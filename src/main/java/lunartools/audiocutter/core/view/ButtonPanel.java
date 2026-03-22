package lunartools.audiocutter.core.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import lunartools.audiocutter.common.action.ActionFactory;
import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.core.AudioCutterModel;

public class ButtonPanel extends JPanel{
	private final AudioCutterModel model;
	private final Dimension BUTTON_DIMENSION=new Dimension(32,30);

	private JButton jbuttonPlayCursor;
	private JButton jbuttonPlaySelection;
	private JButton jbuttonPause;
	private JButton jbuttonStop;
	private JButton jbuttonPrev;
	private JButton jbuttonNext;

	private JButton jbuttonZoomIn;
	private JButton jbuttonZoomOut;
	private JButton jbuttonZoomSelection;
	private JButton jbuttonFitProject;
	private JToggleButton toggleButtonAmplitudeZoom;

	private JButton jbuttonCut;

	public ButtonPanel(AudioCutterModel audioCutterModel) {
		this.model=Objects.requireNonNull(audioCutterModel);
		this.setLayout(new FlowLayout(FlowLayout.LEFT,4,2));

		add(jbuttonPlayCursor=createJButton());
		add(jbuttonPlaySelection=createJButton());
		add(jbuttonPause=createJButton());
		add(jbuttonStop=createJButton());
		add(jbuttonPrev=createJButton());
		add(jbuttonNext=createJButton());
		add(jbuttonZoomIn=createJButton());
		add(jbuttonZoomOut=createJButton());
		add(jbuttonZoomSelection=createJButton());
		add(jbuttonFitProject=createJButton());

		toggleButtonAmplitudeZoom=new JToggleButton();
		toggleButtonAmplitudeZoom.setPreferredSize(BUTTON_DIMENSION);
		toggleButtonAmplitudeZoom.setMinimumSize(BUTTON_DIMENSION);
		add(toggleButtonAmplitudeZoom);

		add(jbuttonCut=createJButton());

		Dimension pref = getPreferredSize();
		setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));
		setAlignmentX(Component.LEFT_ALIGNMENT);

		updateEnabledState();
		audioCutterModel.addChangeListener(this::updateModelChanges);

		//setBackground(new Color(0xccffcc));
	}

	private JButton createJButton() {
		JButton jButton=new JButton();
		jButton.setPreferredSize(BUTTON_DIMENSION);
		jButton.setMinimumSize(BUTTON_DIMENSION);
		return jButton;
	}

	public void setActionFactory(ActionFactory actionFactory) {
		jbuttonPlayCursor.setAction(actionFactory.createPlayCursorAction());
		jbuttonPlaySelection.setAction(actionFactory.createPlaySelectionAction());
		jbuttonPause.setAction(actionFactory.createPauseAction());
		jbuttonStop.setAction(actionFactory.createStopAction());
		jbuttonPrev.setAction(actionFactory.createPreviousAction());
		jbuttonNext.setAction(actionFactory.createNextAction());
		jbuttonZoomIn.setAction(actionFactory.createZoomInAction());
		jbuttonZoomOut.setAction(actionFactory.createZoomOutAction());
		jbuttonZoomSelection.setAction(actionFactory.createZoomSelectionAction());
		jbuttonFitProject.setAction(actionFactory.createFitProjectAction());
		toggleButtonAmplitudeZoom.setAction(actionFactory.createAmplitudeZoomAction());
		jbuttonCut.setAction(actionFactory.createCutAction());
		updateEnabledState();
	}

	public void updateModelChanges(Object object) {
		if(object==SimpleEvents.MODEL_AUDIODATACHANGED) {
			updateEnabledState();
		}else if(object==SimpleEvents.MODEL_ZOOMFACTORCHANGED) {
			updateEnabledState();
		}else if(object==SimpleEvents.MODEL_SELECTIONCHANGED) {
			updateEnabledState();
		}else if(object==SimpleEvents.MODEL_AUDIOSECTIONSCHANGED) {
			updateEnabledState();
		}
	}

	private void updateEnabledState() {
		boolean hasAudiodata=model.hasAudiodata();
		boolean hasSelections=model.hasAudioSections();
		boolean hasSelection=model.hasSelection();
		jbuttonPlayCursor.setEnabled(hasAudiodata);
		jbuttonPause.setEnabled(hasAudiodata);
		jbuttonStop.setEnabled(hasAudiodata);
		jbuttonZoomIn.setEnabled(hasAudiodata && model.getZoom()<AudioCutterModel.ZOOM_MAX);
		jbuttonZoomOut.setEnabled(hasAudiodata && model.getZoom()>AudioCutterModel.ZOOM_MIN);
		jbuttonFitProject.setEnabled(hasAudiodata);
		toggleButtonAmplitudeZoom.setEnabled(hasAudiodata);
		jbuttonCut.setEnabled(hasAudiodata);

		jbuttonPlaySelection.setEnabled(hasSelection);
		jbuttonPrev.setEnabled(hasSelections);
		jbuttonNext.setEnabled(hasSelections);
		jbuttonZoomSelection.setEnabled(hasSelection);
	}

}
