package lunartools.audiocutter.gui.buttonpanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.AudioSection;
import lunartools.audiocutter.player.AudioPlayer;

public class ButtonMouseListener implements MouseListener{
	private AudioCutterModel model;
	private ButtonPanel view;

	public ButtonMouseListener(AudioCutterModel model,ButtonPanel view) {
		this.model=model;
		this.view=view;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton()!=MouseEvent.BUTTON3) {
			return;
		}

		Object source=e.getSource();
		if(source==view.jbuttonPrev) {
			int cursorPos=model.getCursorPositionSampleNumber();
			ArrayList<AudioSection> audioSections=model.getAudioSections();
			for(int i=audioSections.size()-1;i>=0;i--) {
				AudioSection audioSection=audioSections.get(i);
				if(audioSection.getPosition()<cursorPos) {
					zoomAndPlay(audioSection.getPosition());
					break;
				}
			}
		}else if(source==view.jbuttonNext) {
			int cursorPos=model.getCursorPositionSampleNumber();
			ArrayList<AudioSection> audioSections=model.getAudioSections();
			for(int i=0;i<audioSections.size();i++) {
				AudioSection audioSection=audioSections.get(i);
				if(audioSection.getPosition()>cursorPos) {
					zoomAndPlay(audioSection.getPosition());
					break;
				}
			}
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	private void zoomAndPlay(int cursorPos) {
		model.setCursorPositionSampleNumber(cursorPos);
		int audiodataLength=model.getAudiodataLengthInSamples();
		int start=cursorPos-44100*2;
		if(start<0) {
			start=0;
		}
		int end=cursorPos+44100*5;
		if(end>audiodataLength) {
			end=audiodataLength;
		}
		model.setViewRangeInSamples(start, end);

		start=cursorPos-44100;
		if(start<0) {
			start=0;
		}
		model.setSelectionRangeInSamples(start, end);
		AudioPlayer.getInstance().action_playSelection();
	}
}
