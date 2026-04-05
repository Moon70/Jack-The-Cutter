package lunartools.audiocutter.core.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Objects;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import lunartools.audiocutter.common.action.ActionFactory;
import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.core.AudioCutterModel;

public class ButtonPanel extends JPanel{
	private final AudioCutterModel audioCutterModel;
	private final Dimension BUTTON_DIMENSION=new Dimension(32,30);

	private final JButton jbuttonPlayCursor;
	private final JButton jbuttonPlaySelection;
	private final JButton jbuttonPause;
	private final JButton jbuttonStop;
	private final JButton jbuttonPrev;
	private final JButton jbuttonNext;

	private final JButton jbuttonZoomIn;
	private final JButton jbuttonZoomOut;
	private final JButton jbuttonZoomSelection;
	private final JButton jbuttonFitProject;
	private final JToggleButton toggleButtonAmplitudeZoom;

	private JButton jbuttonCut;

	public ButtonPanel(AudioCutterModel audioCutterModel) {
		this.audioCutterModel=Objects.requireNonNull(audioCutterModel);
		this.setLayout(new FlowLayout(FlowLayout.LEFT,4,2));

		add(jbuttonPlayCursor=createJButton());
		add(jbuttonPlaySelection=createJButton());
		add(jbuttonPause=createJButton());
		add(jbuttonStop=createJButton());
		add(jbuttonPrev=createJButton());
		add(jbuttonNext=createJButton());
		add(Box.createHorizontalStrut(16));
		add(jbuttonZoomIn=createJButton());
		add(jbuttonZoomOut=createJButton());
		add(jbuttonZoomSelection=createJButton());
		add(jbuttonFitProject=createJButton());

		toggleButtonAmplitudeZoom=new JToggleButton();
		toggleButtonAmplitudeZoom.setPreferredSize(BUTTON_DIMENSION);
		toggleButtonAmplitudeZoom.setMinimumSize(BUTTON_DIMENSION);
		add(toggleButtonAmplitudeZoom);

		add(Box.createHorizontalStrut(16));
		add(jbuttonCut=createJButton());

		Dimension preferredSize = getPreferredSize();
		setMaximumSize(new Dimension(Integer.MAX_VALUE, preferredSize.height));
		setAlignmentX(Component.LEFT_ALIGNMENT);

		updateEnabledState();
		audioCutterModel.addChangeListener(this::updateModelChanges);
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
		boolean hasAudiodata=audioCutterModel.hasAudiodata();
		boolean hasSelections=audioCutterModel.hasAudioSections();
		boolean hasSelection=audioCutterModel.hasSelection();
		jbuttonPlayCursor.setEnabled(hasAudiodata);
		jbuttonPause.setEnabled(hasAudiodata);
		jbuttonStop.setEnabled(hasAudiodata);
		jbuttonZoomIn.setEnabled(hasAudiodata && audioCutterModel.getZoom()<AudioCutterModel.ZOOM_MAX);
		jbuttonZoomOut.setEnabled(hasAudiodata && audioCutterModel.getZoom()>AudioCutterModel.ZOOM_MIN);
		jbuttonFitProject.setEnabled(hasAudiodata);
		toggleButtonAmplitudeZoom.setEnabled(hasAudiodata);
		jbuttonCut.setEnabled(hasAudiodata);

		jbuttonPlaySelection.setEnabled(hasSelection);
		jbuttonPrev.setEnabled(hasSelections);
		jbuttonNext.setEnabled(hasSelections);
		jbuttonZoomSelection.setEnabled(hasSelection);
	}

}
