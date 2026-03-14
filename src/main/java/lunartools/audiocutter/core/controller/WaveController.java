package lunartools.audiocutter.core.controller;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import lunartools.audiocutter.core.AudioCutterModel;
import lunartools.audiocutter.core.view.AWavePanel;
import lunartools.audiocutter.core.view.WavePanelZoom;

public abstract class WaveController implements MouseListener,MouseMotionListener{
	AudioCutterModel audioCutterModel;
	AWavePanel wavePanel;

	private boolean createSelection;
	private int selectedSection=-1;
	private boolean moveSectionPosition;
	private int selectionBegin;

	public WaveController(AudioCutterModel audioCutterModel,AWavePanel wavePanel) {
		this.audioCutterModel=audioCutterModel;
		this.wavePanel=wavePanel;
	}

	public abstract int getViewStartInSamples();

	public abstract int getViewEndInSamples();

	public abstract void resetSectionMarks();

	public abstract void addSectionMark(int i, int sectionPosPixel);

	abstract int getSelectedSection(int sectionPosPixel);

	@Override
	public void mouseClicked(MouseEvent e) {
		if(!audioCutterModel.hasAudiodata()) {
			return;
		}
		int sampleNumber=convertScreenPositionToSampleNumber(e.getX());
		if(e.getClickCount()==2) {
			int selectionStartInSamples=audioCutterModel.getSelectionStartInSamples();
			int selectionEndInSamples=audioCutterModel.getSelectionEndInSamples();
			if(selectionStartInSamples<=sampleNumber && selectionEndInSamples>=sampleNumber) {
				zoomSelection();
			}
		}else {
			audioCutterModel.setCursorPositionSampleNumber(sampleNumber);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(!audioCutterModel.hasAudiodata()) {
			return;
		}
		createSelection=false;
		moveSectionPosition=false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(!audioCutterModel.hasAudiodata()) {
			return;
		}
		int sampleNumber=convertScreenPositionToSampleNumber(e.getX());
		if(sampleNumber<0) {
			sampleNumber=0;
		}else if(sampleNumber>=audioCutterModel.getAudiodataLengthInSamples()-1) {
			sampleNumber=audioCutterModel.getAudiodataLengthInSamples()-1;
		}
		if(!(createSelection | moveSectionPosition)) {
			if(selectedSection!=-1) {
				moveSectionPosition=true;
			}else {
				createSelection=true;
				selectionBegin=sampleNumber;
			}
		}
		if(createSelection) {
			if(selectionBegin<sampleNumber) {
				audioCutterModel.setSelectionRangeInSamples(selectionBegin, sampleNumber);
			}else {
				audioCutterModel.setSelectionRangeInSamples(sampleNumber,selectionBegin);
			}
		}else if(moveSectionPosition) {
			audioCutterModel.setAudioSectionPosition(selectedSection, sampleNumber);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {
		audioCutterModel.setMousePositionSampleNumber(0);
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(!audioCutterModel.hasAudiodata()) {
			return;
		}
		if(e.getSource() instanceof WavePanelZoom) {
			selectedSection=getSelectedSection(e.getX());
			if(selectedSection!=-1) {
				wavePanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}else {
				selectedSection=-1;
				wavePanel.setCursor(Cursor.getDefaultCursor());
			}
		}
		int sampleNumber=convertScreenPositionToSampleNumber(e.getX());
		audioCutterModel.setMousePositionSampleNumber(sampleNumber);

	}

	public int convertScreenPositionToSampleNumber(int pixel) {
		int viewStartInSamples=getViewStartInSamples();
		int viewEndInSamples=getViewEndInSamples();
		int delta=viewEndInSamples-viewStartInSamples;
		double pixelFactor=(double)delta/(double)wavePanel.getWidth();
		int result=viewStartInSamples+(int)(pixelFactor*pixel);
		return result;
	}

	public int convertSampleNumberToScreenPosition(int samples) {
		int viewStartInSamples=getViewStartInSamples();
		int viewEndInSamples=getViewEndInSamples();
		int samplesInView=viewEndInSamples-viewStartInSamples;
		double pixelFactor=(double)samplesInView/(double)wavePanel.getWidth();
		int result=(int)((samples-viewStartInSamples)/pixelFactor);
		return result;
	}

	private void zoomSelection() {
		int selectionStartInSamplesCopy=audioCutterModel.getSelectionStartInSamples();
		int selectionEndInSamplesCopy=audioCutterModel.getSelectionEndInSamples();

		int delta=selectionEndInSamplesCopy-selectionStartInSamplesCopy;

		int audiodataLengthInSamples=audioCutterModel.getAudiodataLengthInSamples();
		int rangeMax=audiodataLengthInSamples-(wavePanel.getWidth()>>2);//maximum zoom means 1 sample takes 4 pixel on screen
		for(int i=AudioCutterModel.ZOOM_MIN;i<=AudioCutterModel.ZOOM_MAX;i++) {
			int zoom=AudioCutterModel.ZOOM_MAX-i;
			zoom=zoom*zoom;
			double zoomFactor=(double)zoom/AudioCutterModel.ZOOM_MAX;
			int viewDeltaNew=(int)(zoomFactor*rangeMax/(double)AudioCutterModel.ZOOM_MAX);
			if(delta>=viewDeltaNew) {
				audioCutterModel.setZoom(i);
				break;
			}
		}
		audioCutterModel.setViewRangeInSamples(selectionStartInSamplesCopy, selectionEndInSamplesCopy);
	}

}
