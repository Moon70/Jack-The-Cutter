package lunartools.audiocutter.core.view;

import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import lunartools.ByteTools;
import lunartools.audiocutter.common.model.AudioSectionModel;
import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.common.ui.ColorManager;
import lunartools.audiocutter.common.ui.util.SampleUtils;
import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.core.controller.WaveController;

public abstract class AWavePanel extends JPanel{
	AudioCutterModel audioCutterModel;
	WaveController waveController;

	private final int CHANNEL_MARGIN=6;
	private final int SCALE_HEIGHT=20;
	private int scaleOffset;

	final int zoomMarkHeight=16;
	int zoomMarkOffset;

	private int channelHeight;
	int channelHeightHalve;
	int channel1CenterY;
	int channel2CenterY;
	private int channel1Top;
	private int channel1Bot;
	private int channel2Top;
	private int channel2Bot;

	public AWavePanel(AudioCutterModel audioCutterModel) {
		this.audioCutterModel=audioCutterModel;
		this.setLayout(null);
		calculateSizes();
		audioCutterModel.addChangeListener(this::updateModelChanges);
	}

	public void updateModelChanges(Object object) {
		if(object==SimpleEvents.MODEL_FRAMESIZECHANGED) {
			calculateSizes();
		}
	}

	private void calculateSizes() {
		int panelHeight=getHeight();
		this.channelHeight=(panelHeight-SCALE_HEIGHT)/2-CHANNEL_MARGIN*2;

		this.channelHeightHalve=channelHeight>>1;
		this.channel1CenterY=CHANNEL_MARGIN+channelHeightHalve;
		this.channel2CenterY=CHANNEL_MARGIN+channelHeight+CHANNEL_MARGIN+CHANNEL_MARGIN+channelHeightHalve;

		this.channel1Top=channel1CenterY-channelHeightHalve;
		this.channel1Bot=channel1CenterY+channelHeightHalve;
		this.channel2Top=channel2CenterY-channelHeightHalve;
		this.channel2Bot=channel2CenterY+channelHeightHalve;

		this.scaleOffset=channel2Bot;

		this.zoomMarkOffset=scaleOffset+1;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int width=getWidth();

		if(!audioCutterModel.hasAudiodata()) {
			drawCoordinates(g);
			return;
		}

		drawSelectedSectionBackground(g);
		drawSelectionBackground(g,width);
		drawCoordinates(g);

		byte[] audioDataBytes=audioCutterModel.getAudiodata();
		int index=0;
		int viewStartInSamples=waveController.getViewStartInSamples();
		int viewEndInSamples=waveController.getViewEndInSamples();
		double step=(double)(viewEndInSamples-viewStartInSamples)/(double)width;

		int previousSample1=0;
		int previousSample2=0;
		g.setColor(ColorManager.wave());
		for(int i=0;i<width;i++) {
			index=viewStartInSamples+(int)(step*i);
			index=index<<2;
			int sample1=ByteTools.littleEndianBytesToSignedWord(audioDataBytes, index);
			int sample2=ByteTools.littleEndianBytesToSignedWord(audioDataBytes, index+2);

			drawAmplitudeZoom(g,i,sample1,sample2,previousSample1,previousSample2);

			sample1=((sample1*channelHeightHalve)>>15);
			sample2=((sample2*channelHeightHalve)>>15);
			g.drawLine(i-1, channel1CenterY-previousSample1, i, channel1CenterY-sample1);
			g.drawLine(i-1, channel2CenterY-previousSample2, i, channel2CenterY-sample2);
			previousSample1=sample1;
			previousSample2=sample2;
		}

		int cursorPosSample=audioCutterModel.getCursorPositionSampleNumber();
		if(cursorPosSample>=viewStartInSamples && cursorPosSample<=viewEndInSamples) {
			int cursorPosPixel=waveController.convertSampleNumberToScreenPosition(cursorPosSample);
			g.setColor(ColorManager.cursor());
			g.drawLine(cursorPosPixel, channel1CenterY-channelHeightHalve, cursorPosPixel, channel2CenterY+channelHeightHalve);
		}

		ArrayList<AudioSectionModel> audiosections=audioCutterModel.getAudioSections();
		waveController.resetSectionMarks();
		if(audiosections!=null && audiosections.size()>1) {
			for(int i=0;i<audiosections.size();i++) {
				AudioSectionModel audioSection=audiosections.get(i);
				int audioSectionPos=audioSection.getPosition();
				if(audioSectionPos>=viewStartInSamples && audioSectionPos<=viewEndInSamples) {
					int sectionPosPixel=waveController.convertSampleNumberToScreenPosition(audioSectionPos);
					if(i>0) {
						waveController.addSectionMark(i,sectionPosPixel);
					}
					g.setColor(ColorManager.selectionStart());
					g.drawLine(sectionPosPixel, channel1CenterY-channelHeightHalve, sectionPosPixel, channel2CenterY+channelHeightHalve);
				}
			}
		}

		int playPosSample=audioCutterModel.getPlayPositionSampleNumber();
		if(playPosSample>=viewStartInSamples && playPosSample<=viewEndInSamples) {
			int playPosPixel=waveController.convertSampleNumberToScreenPosition(playPosSample);
			g.setColor(ColorManager.cursorPlay());
			g.drawLine(playPosPixel, channel1CenterY-channelHeightHalve, playPosPixel, channel2CenterY+channelHeightHalve);
		}

		drawScale(g);
	}

	private void drawCoordinates(Graphics g) {
		g.setColor(ColorManager.scaleLight());
		int audioViewWidth=getWidth();
		g.drawLine(0, channel1CenterY, audioViewWidth, channel1CenterY);
		g.drawLine(0, channel2CenterY, audioViewWidth, channel2CenterY);

		g.drawLine(0, channel1Top, audioViewWidth, channel1Top);
		g.drawLine(0, channel1Bot, audioViewWidth, channel1Bot);
		g.drawLine(0, channel2Top, audioViewWidth, channel2Top);
		g.drawLine(0, channel2Bot, audioViewWidth, channel2Bot);

	}

	public void drawSelectedSectionBackground(Graphics g) {
		int selectedSection=audioCutterModel.getSelectedAudioSection();
		if(selectedSection>=0) {
			AudioSectionModel audioSection=audioCutterModel.getAudioSection(selectedSection);
			final int sectionStartInSamples=audioSection.getPosition();
			final int sectionEndInSamples;
			if(selectedSection==audioCutterModel.getAudioSectionsSize()-1) {
				sectionEndInSamples=audioCutterModel.getAudiodataLengthInSamples();
			}else {
				audioSection=audioCutterModel.getAudioSection(selectedSection+1);
				sectionEndInSamples=audioSection.getPosition();
			}
			final int sectionStartInPixel=waveController.convertSampleNumberToScreenPosition(sectionStartInSamples);
			final int sectionEndInPixel=waveController.convertSampleNumberToScreenPosition(sectionEndInSamples);

			g.setColor(ColorManager.section());
			g.fillRect(sectionStartInPixel,channel1Top+1,sectionEndInPixel-sectionStartInPixel+1,channelHeight*2+CHANNEL_MARGIN);
		}
	}

	public void drawSelectionBackground(Graphics g,int audioViewWidth) {
		final int selectionStartInPixel=waveController.convertSampleNumberToScreenPosition(audioCutterModel.getSelectionStartInSamples());
		final int selectionEndInPixel=waveController.convertSampleNumberToScreenPosition(audioCutterModel.getSelectionEndInSamples());

		g.setColor(ColorManager.selection());
		g.fillRect(selectionStartInPixel,channel1Top+1,selectionEndInPixel-selectionStartInPixel+1,channelHeight-1);
		g.fillRect(selectionStartInPixel,channel2Top+1,selectionEndInPixel-selectionStartInPixel+1,channelHeight-1);
	}

	public void drawAmplitudeZoom(Graphics g,int index, int sample1,int sample2, int previousSample1,int previousSample2) {}

	public void drawScale(Graphics g) {
		final int PIXEL_COUNT_OF_SUB_UNIT=38;
		final int NUMBER_OF_SUB_SEGMENTS_PER_MAIN_SEGMENT=5;
		final int PIXEL_COUNT_OF_MAIN_UNIT=NUMBER_OF_SUB_SEGMENTS_PER_MAIN_SEGMENT*PIXEL_COUNT_OF_SUB_UNIT;
		final int SCALE_MAIN_MARK_HEIGHT=16;
		final int SCALE_SUB_MARK_HEIGHT=4;
		final int SCALE_MAIN_TEXT_OFFSET_X=14;
		final int SCALE_MAIN_TEXT_OFFSET_Y=2;

		g.setColor(ColorManager.scale());
		for(int i=0;i<getWidth();i+=PIXEL_COUNT_OF_SUB_UNIT) {
			if((i%(PIXEL_COUNT_OF_MAIN_UNIT))==0) {
				g.drawLine(i, channel2Bot, i, channel2Bot+SCALE_MAIN_MARK_HEIGHT);
				int posInSamples=waveController.convertScreenPositionToSampleNumber(i);
				String timestamp=SampleUtils.convertNumberOfSamplesToHourMinuteSecondString(posInSamples);
				g.drawString(timestamp, i+SCALE_MAIN_TEXT_OFFSET_Y, channel2Bot+SCALE_MAIN_TEXT_OFFSET_X);
			}else {
				g.drawLine(i, channel2Bot, i, channel2Bot+SCALE_SUB_MARK_HEIGHT);
			}
		}
	}

}
