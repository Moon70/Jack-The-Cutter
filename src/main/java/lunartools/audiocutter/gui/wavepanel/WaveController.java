package lunartools.audiocutter.gui.wavepanel;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.Calculator;

public abstract class WaveController implements MouseListener,MouseMotionListener{
	AudioCutterModel model;
	WavePanel view;

	private boolean createSelection;
	private int selectedSection=-1;
	private boolean moveSectionPosition;
	private int selectionBegin;

	public WaveController(AudioCutterModel model,WavePanel view) {
		this.model=model;
		this.view=view;
	}

	abstract int getViewStartInSamples();

	abstract int getViewEndInSamples();

	abstract void resetSectionMarks();

	abstract void addSectionMark(int i, int sectionPosPixel);

	abstract int getSelectedSection(int sectionPosPixel);

	@Override
	public void mouseClicked(MouseEvent e) {
		if(!model.hasAudiodata()) {
			return;
		}
		int sampleNumber=convertScreenPositionToSampleNumber(e.getX());
		if(e.getClickCount()==2) {
			int selectionStartInSamples=model.getSelectionStartInSamples();
			int selectionEndInSamples=model.getSelectionEndInSamples();
			if(selectionStartInSamples<=sampleNumber && selectionEndInSamples>=sampleNumber) {
				zoomSelection();
			}
		}else {
			model.setCursorPositionSampleNumber(sampleNumber);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(!model.hasAudiodata()) {
			return;
		}
		createSelection=false;
		moveSectionPosition=false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(!model.hasAudiodata()) {
			return;
		}
		int sampleNumber=convertScreenPositionToSampleNumber(e.getX());
		if(sampleNumber<0) {
			sampleNumber=0;
		}else if(sampleNumber>=model.getAudiodataLengthInSamples()-1) {
			sampleNumber=model.getAudiodataLengthInSamples()-1;
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
				model.setSelectionRangeInSamples(selectionBegin, sampleNumber);
			}else {
				model.setSelectionRangeInSamples(sampleNumber,selectionBegin);
			}
		}else if(moveSectionPosition) {
			model.setAudioSectionPosition(selectedSection, sampleNumber);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {
		model.setMousePositionSampleNumber(0);
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(!model.hasAudiodata()) {
			return;
		}
		if(e.getSource() instanceof WavePanelZoom) {
			selectedSection=getSelectedSection(e.getX());
			if(selectedSection!=-1) {
				view.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}else {
				selectedSection=-1;
				view.setCursor(Cursor.getDefaultCursor());
			}
		}
		int sampleNumber=convertScreenPositionToSampleNumber(e.getX());
		model.setMousePositionSampleNumber(sampleNumber);

	}

	int convertScreenPositionToSampleNumber(int pixel) {
		int viewStartInSamples=getViewStartInSamples();
		int viewEndInSamples=getViewEndInSamples();
		int delta=viewEndInSamples-viewStartInSamples;
		double pixelFactor=(double)delta/(double)model.getAudiodataViewWidth();
		int result=viewStartInSamples+(int)(pixelFactor*pixel);
		return result;
	}

	int convertSampleNumberToScreenPosition(WavePanel wavePanel, int samples) {
		int viewStartInSamples=wavePanel.getViewStartInSamples();
		int viewEndInSamples=wavePanel.getViewEndInSamples();
		int samplesInView=viewEndInSamples-viewStartInSamples;
		double pixelFactor=(double)samplesInView/(double)wavePanel.model.getAudiodataViewWidth();
		int result=(int)((samples-viewStartInSamples)/pixelFactor);
		return result;
	}

	private void zoomSelection() {
		int selectionStartInSamplesCopy=model.getSelectionStartInSamples();
		int selectionEndInSamplesCopy=model.getSelectionEndInSamples();

		int delta=selectionEndInSamplesCopy-selectionStartInSamplesCopy;

		int audiodataLengthInSamples=model.getAudiodataLengthInSamples();
		int rangeMax=audiodataLengthInSamples-(model.getAudiodataViewWidth()>>2);//maximum zoom means 1 sample takes 4 pixel on screen
		for(int i=AudioCutterModel.ZOOM_MIN;i<=AudioCutterModel.ZOOM_MAX;i++) {
			int zoom=AudioCutterModel.ZOOM_MAX-i;
			zoom=zoom*zoom;
			double zoomFactor=(double)zoom/AudioCutterModel.ZOOM_MAX;
			int viewDeltaNew=(int)(zoomFactor*rangeMax/(double)AudioCutterModel.ZOOM_MAX);
			if(delta>=viewDeltaNew) {
				model.setZoom(i);
				break;
			}
		}
		model.setViewRangeInSamples(selectionStartInSamplesCopy, selectionEndInSamplesCopy);
	}

}
