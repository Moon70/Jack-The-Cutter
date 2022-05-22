package lunartools.audiocutter.gui.wavepanel;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import lunartools.ByteTools;
import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.AudioSection;
import lunartools.audiocutter.Calculator;
import lunartools.audiocutter.SimpleEvents;

public abstract class WavePanel extends JPanel implements Observer{
	AudioCutterModel model;
	WaveController waveController;

	static final Color COLOR_BACKGROUND_WAVE_FULL=new Color(0xC6E8FF);
	static final Color COLOR_BACKGROUND_WAVE_ZOOM=new Color(0xFFFFE4);
	static final Color COLOR_EXTRA_ZOOM=new Color(0xcccccc);
	static final Color COLOR_SECTION=new Color(0xFFE4D5);
	static final Color COLOR_SELECTION=new Color(0xdddddd);
	static final Color COLOR_CURSOR=new Color(0x0099ff);
	static final Color COLOR_CURSOR_PLAY=Color.BLACK;
	static final Color COLOR_SELECTION_START=Color.RED;

	final int channelOffsetX=0;
	private final int channelOffsetY=10;
	private final int channelMarginY=10;
	final int scaleHeight=20;

	int panelHeight;
	int channelHeight;
	int channelHeightHalve;
	int channel1CenterY;
	int channel2CenterY;
	protected int panelHeightWithoutScale;
	private int channel1Top;
	private int channel1Bot;
	private int channel2Top;
	int channel2Bot;

	private int audioViewWidth;

	public WavePanel(AudioCutterModel audioCutterModel) {
		this.model=audioCutterModel;
		this.setLayout(null);
		calculateSizes();
		model.addObserver(this);
	}

	abstract int getViewStartInSamples();

	abstract int getViewEndInSamples();

	@Override
	public void update(Observable observable, Object object) {
		if(object==SimpleEvents.MODEL_FRAMESIZECHANGED) {
			calculateSizes();
		}
	}

	private void calculateSizes() {
		int frameHeight=model.getFrameBounds().height;
		int magic=248;
		this.panelHeight=(frameHeight-magic)>>1;
		this.channelHeight=(panelHeight-channelOffsetY-channelMarginY-channelMarginY-scaleHeight)>>1;
		this.channelHeightHalve=channelHeight>>1;
		this.channel1CenterY=channelOffsetY+(channelHeight>>1);
		this.channel2CenterY=channelOffsetY+channelHeight+channelMarginY+(channelHeight>>1);
		this.panelHeightWithoutScale=panelHeight-scaleHeight;
		this.channel1Top=channel1CenterY-(channelHeight>>1);
		this.channel1Bot=channel1Top+channelHeight;
		this.channel2Top=channel2CenterY-(channelHeight>>1);
		this.channel2Bot=channel2CenterY+(channelHeight>>1);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		audioViewWidth=model.getAudiodataViewWidth();
		g.setColor(getBackground());
		g.fillRect(0,0,audioViewWidth,panelHeightWithoutScale);

		if(!model.hasAudiodata()) {
			g.setColor(Color.GRAY);
			g.drawLine(channelOffsetX, channel1CenterY, channelOffsetX+audioViewWidth, channel1CenterY);
			g.drawLine(channelOffsetX, channel2CenterY, channelOffsetX+audioViewWidth, channel2CenterY);
			return;
		}

		drawSelectedSectionBackground(g);
		drawSelectionBackground(g);

		g.setColor(Color.GRAY);
		g.drawLine(channelOffsetX, channel1CenterY, channelOffsetX+audioViewWidth, channel1CenterY);
		g.drawLine(channelOffsetX, channel2CenterY, channelOffsetX+audioViewWidth, channel2CenterY);

		byte[] audioDataBytes=model.getAudiodata();
		int index=0;
		int viewStartInSamples=getViewStartInSamples();
		int viewEndInSamples=getViewEndInSamples();
		double step=(double)(viewEndInSamples-viewStartInSamples)/(double)audioViewWidth;

		int previousSample1=0;
		int previousSample2=0;
		g.setColor(Color.BLACK);
		for(int i=0;i<audioViewWidth;i++) {
			index=viewStartInSamples+(int)(step*i);
			index=index<<2;
			int sample1=ByteTools.lBytearrayToSignedWord(audioDataBytes, index);
			int sample2=ByteTools.lBytearrayToSignedWord(audioDataBytes, index+2);

			drawAmplitudeZoom(g,i,sample1,sample2,previousSample1,previousSample2);

			sample1=(sample1*(channelHeight>>1))>>15;
		sample2=(sample2*(channelHeight>>1))>>15;
		g.drawLine(channelOffsetX+i-1, channel1CenterY-previousSample1, channelOffsetX+i, channel1CenterY-sample1);
		g.drawLine(channelOffsetX+i-1, channel2CenterY-previousSample2, channelOffsetX+i, channel2CenterY-sample2);
		previousSample1=sample1;
		previousSample2=sample2;
		}

		int cursorPosSample=model.getCursorPositionSampleNumber();
		if(cursorPosSample>=viewStartInSamples && cursorPosSample<=viewEndInSamples) {
			int cursorPosPixel=waveController.convertSampleNumberToScreenPosition(this, cursorPosSample);
			g.setColor(COLOR_CURSOR);
			g.drawLine(cursorPosPixel, channel1CenterY-(channelHeight>>1), cursorPosPixel, channel2CenterY+(channelHeight>>1));
		}

		ArrayList<AudioSection> audiosections=model.getAudioSections();
		waveController.resetSectionMarks();
		if(audiosections!=null && audiosections.size()>1) {
			for(int i=0;i<audiosections.size();i++) {
				AudioSection audioSection=audiosections.get(i);
				int audioSectionPos=audioSection.getPosition();
				if(audioSectionPos>=viewStartInSamples && audioSectionPos<=viewEndInSamples) {
					int sectionPosPixel=waveController.convertSampleNumberToScreenPosition(this, audioSectionPos);
					if(i>0) {
						waveController.addSectionMark(i,sectionPosPixel);
					}
					g.setColor(COLOR_SELECTION_START);
					g.drawLine(sectionPosPixel, channel1CenterY-(channelHeight>>1), sectionPosPixel, channel2CenterY+(channelHeight>>1));
				}
			}
		}

		int playPosSample=model.getPlayPositionSampleNumber();
		if(playPosSample>=viewStartInSamples && playPosSample<=viewEndInSamples) {
			int playPosPixel=waveController.convertSampleNumberToScreenPosition(this, playPosSample);
			g.setColor(COLOR_CURSOR_PLAY);
			g.drawLine(playPosPixel, channel1CenterY-(channelHeight>>1), playPosPixel, channel2CenterY+(channelHeight>>1));
		}

		drawScale(g);
	}

	void drawSelectedSectionBackground(Graphics g) {
		int selectedSection=model.getSelectedAudioSection();
		if(selectedSection>=0) {
			AudioSection audioSection=model.getAudioSection(selectedSection);
			final int sectionStartInSamples=audioSection.getPosition();
			final int sectionEndInSamples;
			if(selectedSection==model.getAudioSectionsSize()-1) {
				sectionEndInSamples=model.getAudiodataLengthInSamples();
			}else {
				audioSection=model.getAudioSection(selectedSection+1);
				sectionEndInSamples=audioSection.getPosition();
			}
			final int sectionStartInPixel=waveController.convertSampleNumberToScreenPosition(this, sectionStartInSamples);
			final int sectionEndInPixel=waveController.convertSampleNumberToScreenPosition(this, sectionEndInSamples);

			g.setColor(COLOR_SECTION);
			g.fillRect(sectionStartInPixel,channel1Top+1,sectionEndInPixel-sectionStartInPixel+1,channelHeight*2+channelMarginY);
		}
	}

	void drawSelectionBackground(Graphics g) {
		final int selectionStartInPixel=waveController.convertSampleNumberToScreenPosition(this, model.getSelectionStartInSamples());
		final int selectionEndInPixel=waveController.convertSampleNumberToScreenPosition(this, model.getSelectionEndInSamples());

		g.setColor(COLOR_SELECTION);
		g.fillRect(selectionStartInPixel,channel1Top+1,selectionEndInPixel-selectionStartInPixel+1,channelHeight-1);
		g.fillRect(selectionStartInPixel,channel2Top+1,selectionEndInPixel-selectionStartInPixel+1,channelHeight-1);

		g.setColor(Color.GRAY);
		g.drawLine(channelOffsetX, channel1Top, 	channelOffsetX+audioViewWidth, channel1Top);
		g.drawLine(channelOffsetX, channel1Bot, 	channelOffsetX+audioViewWidth, channel1Bot);
		g.drawLine(channelOffsetX, channel2Top, 	channelOffsetX+audioViewWidth, channel2Top);
		g.drawLine(channelOffsetX, channel2Bot, 	channelOffsetX+audioViewWidth, channel2Bot);
	}

	void drawAmplitudeZoom(Graphics g,int index, int sample1,int sample2, int previousSample1,int previousSample2) {}

	void drawScale(Graphics g) {
		g.setColor(Color.BLACK);
		int magic=38;
		for(int i=0;i<audioViewWidth;i+=magic) {
			if((i%(5*magic))==0) {
				g.drawLine(i, channel2Bot, i, channel2Bot+24);
				int posInSamples=waveController.convertScreenPositionToSampleNumber(i);
				String timestamp=Calculator.convertNumberOfSamplesToHourMinuteSecondString(posInSamples);
				g.drawString(timestamp, i+4, channel2Bot+22);
			}else {
				g.drawLine(i, channel2Bot, i, channel2Bot+4);
			}
		}
	}

}
