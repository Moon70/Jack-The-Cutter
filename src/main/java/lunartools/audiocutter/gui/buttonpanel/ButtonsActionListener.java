package lunartools.audiocutter.gui.buttonpanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.AudioSection;
import lunartools.audiocutter.player.AudioPlayer;

public class ButtonsActionListener implements ActionListener{
	private AudioCutterModel model;
	private ButtonPanel view;

	public ButtonsActionListener(AudioCutterModel model,ButtonPanel audioCutterView) {
		this.model=model;
		this.view=audioCutterView;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source=e.getSource();
		if(source==view.jbuttonPlayCursor) {
			AudioPlayer.getInstance().action_playFromCursorPosition();
		}else if(source==view.jbuttonPlaySelection) {
			AudioPlayer.getInstance().action_playSelection();
		}else if(source==view.jbuttonPause) {
			AudioPlayer.getInstance().action_pause();
		}else if(source==view.jbuttonStop) {
			AudioPlayer.getInstance().action_stop();
		}else if(source==view.jbuttonPrev) {
			AudioPlayer.getInstance().action_PrevSection();
		}else if(source==view.jbuttonNext) {
			AudioPlayer.getInstance().action_NextSection();
		}else if(source==view.jbuttonZoomIn) {
			action_zoomIn();
		}else if(source==view.jbuttonZoomOut) {
			action_zoomOut();
		}else if(source==view.jbuttonFitSelection) {
			action_fitSelection();
		}else if(source==view.jbuttonFitProject) {
			action_fitProject();
		}else if(source==view.toggleButtonAmplitudeZoom) {
			model.setAmplitudeZoom(view.toggleButtonAmplitudeZoom.isSelected());
		}else if(source==view.jbuttonCut) {
			action_cut();
		}
	}

	public void action_zoomIn() {
		model.setZoom(model.getZoom()+1);
	}

	public void action_zoomOut() {
		model.setZoom(model.getZoom()-1);
	}

	public void action_fitSelection() {
		model.setViewRangeInSamples(model.getSelectionStartInSamples(),model.getSelectionEndInSamples());
	}

	public void action_fitProject() {
		model.setViewRangeInSamples(0,model.getAudiodataLengthInSamples());
		model.setZoom(0);
	}

	private void action_cut() {
		ArrayList<AudioSection> audioSections=model.getAudioSections();
		int cursorPos=model.getCursorPositionSampleNumber();
		if(audioSections.size()==0) {
			audioSections.add(new AudioSection(0));
			audioSections.add(new AudioSection(cursorPos));
			model.setAudioSections(audioSections);
			return;
		}else {
			for(int i=0;i<audioSections.size();i++) {
				AudioSection audioSection=audioSections.get(i);
				if(audioSection.getPosition()==cursorPos) {
					return;
				}
				if(audioSection.getPosition()>cursorPos) {
					audioSections.add(i, new AudioSection(cursorPos));
					model.setAudioSections(audioSections);
					return;
				}
			}
			audioSections.add(new AudioSection(cursorPos));
			model.setAudioSections(audioSections);
		}
	}

}
