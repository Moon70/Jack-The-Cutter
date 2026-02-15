package lunartools.audiocutter.gui.buttonpanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import lunartools.ImageTools;
import lunartools.audiocutter.common.action.ActionFactory;
import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.core.AudioCutterController;
import lunartools.audiocutter.core.AudioCutterModel;

public class ButtonPanel extends JPanel{
	private AudioCutterModel model;

	JButton jbuttonPlayCursor;
	JButton jbuttonPlaySelection;
	JButton jbuttonPause;
	JButton jbuttonStop;
	JButton jbuttonPrev;
	JButton jbuttonNext;

	JButton jbuttonZoomIn;
	JButton jbuttonZoomOut;
	JButton jbuttonZoomSelection;
	JButton jbuttonFitProject;
	JToggleButton toggleButtonAmplitudeZoom;

	JButton jbuttonCut;

	public ButtonPanel(AudioCutterModel model,AudioCutterController controller) {
		this.model=model;
		this.setLayout(new FlowLayout(FlowLayout.LEFT,4,2));

		ActionListener buttonActionListener=new ButtonsActionListener(model,controller,this);
		MouseListener buttonMouseListener=new ButtonMouseListener(model,this);

		int buttonWidth=32;
		int buttonHeight=30;

		Dimension buttonDimension=new Dimension(buttonWidth,buttonHeight);
		jbuttonPlayCursor=new JButton();
		jbuttonPlayCursor.setPreferredSize(buttonDimension);
		jbuttonPlayCursor.setMinimumSize(buttonDimension);
		jbuttonPlayCursor.setEnabled(false);
		add(jbuttonPlayCursor);

		jbuttonPlaySelection=new JButton();
		jbuttonPlaySelection.setPreferredSize(buttonDimension);
		jbuttonPlaySelection.setMinimumSize(buttonDimension);
		jbuttonPlaySelection.setEnabled(false);
		add(jbuttonPlaySelection);

		jbuttonPause=new JButton();
		jbuttonPause.setPreferredSize(buttonDimension);
		jbuttonPause.setMinimumSize(buttonDimension);
		jbuttonPause.setEnabled(false);
		add(jbuttonPause);

		jbuttonStop=new JButton();
		jbuttonStop.setPreferredSize(buttonDimension);
		jbuttonStop.setMinimumSize(buttonDimension);
		jbuttonStop.setEnabled(false);
		add(jbuttonStop);

		jbuttonPrev=new JButton();
		jbuttonPrev.setPreferredSize(buttonDimension);
		jbuttonPrev.setMinimumSize(buttonDimension);
		jbuttonPrev.addMouseListener(buttonMouseListener);
		jbuttonPrev.setEnabled(false);
		add(jbuttonPrev);

		jbuttonNext=new JButton();
		jbuttonNext.setPreferredSize(buttonDimension);
		jbuttonNext.setMinimumSize(buttonDimension);
		jbuttonNext.addMouseListener(buttonMouseListener);
		jbuttonNext.setEnabled(false);
		add(jbuttonNext);

		jbuttonZoomIn=new JButton();
		jbuttonZoomIn.setPreferredSize(buttonDimension);
		jbuttonZoomIn.setMinimumSize(buttonDimension);
		jbuttonZoomIn.setEnabled(false);
		add(jbuttonZoomIn);

		jbuttonZoomOut=new JButton();
		jbuttonZoomOut.setPreferredSize(buttonDimension);
		jbuttonZoomOut.setMinimumSize(buttonDimension);
		jbuttonZoomOut.setEnabled(false);
		add(jbuttonZoomOut);

		jbuttonZoomSelection=new JButton();
		jbuttonZoomSelection.setPreferredSize(buttonDimension);
		jbuttonZoomSelection.setMinimumSize(buttonDimension);
		jbuttonZoomSelection.setEnabled(false);
		add(jbuttonZoomSelection);

		jbuttonFitProject=new JButton();
		jbuttonFitProject.setPreferredSize(buttonDimension);
		jbuttonFitProject.setMinimumSize(buttonDimension);
		jbuttonFitProject.setEnabled(false);
		add(jbuttonFitProject);

		toggleButtonAmplitudeZoom=new JToggleButton();
		toggleButtonAmplitudeZoom.setPreferredSize(buttonDimension);
		toggleButtonAmplitudeZoom.setMinimumSize(buttonDimension);
		toggleButtonAmplitudeZoom.setSelectedIcon(ImageTools.createImageIcon("/icons/Button_AmplitudeZoomPressed.png"));
		toggleButtonAmplitudeZoom.setEnabled(false);
		add(toggleButtonAmplitudeZoom);

		jbuttonCut=new JButton();
		jbuttonCut.setPreferredSize(buttonDimension);
		jbuttonCut.setMinimumSize(buttonDimension);
		jbuttonCut.setEnabled(false);
		add(jbuttonCut);

		Dimension pref = getPreferredSize();
		setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));
		setAlignmentX(Component.LEFT_ALIGNMENT);

		model.addChangeListener(this::updateModelChanges);

		//setBackground(new Color(0xccffcc));
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
	}

	public void updateModelChanges(Object object) {
		if(object==SimpleEvents.MODEL_AUDIODATACHANGED) {
			refresh();
		}else if(object==SimpleEvents.MODEL_ZOOMCHANGED) {
			refresh();
		}else if(object==SimpleEvents.MODEL_ZOOMRANGECHANGED) {
			refresh();
		}else if(object==SimpleEvents.MODEL_SELECTIONCHANGED) {
			refresh();
		}else if(object==SimpleEvents.MODEL_AUDIOSECTIONSCHANGED) {
			refresh();
		}
	}

	private void refresh() {
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
