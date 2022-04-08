package lunartools.audiocutter.gui.buttonpanel;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import lunartools.ImageTools;
import lunartools.audiocutter.AudioCutterController;
import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.SimpleEvents;

public class ButtonPanel extends JPanel implements Observer{
	private AudioCutterModel model;

	JButton jbuttonPlayCursor;
	JButton jbuttonPlaySelection;
	JButton jbuttonPause;
	JButton jbuttonStop;
	JButton jbuttonPrev;
	JButton jbuttonNext;

	JButton jbuttonZoomIn;
	JButton jbuttonZoomOut;
	JButton jbuttonFitSelection;
	JButton jbuttonFitProject;
	JToggleButton toggleButtonAmplitudeZoom;

	JButton jbuttonCut;

	public ButtonPanel(AudioCutterModel model,AudioCutterController controller) {
		this.model=model;
		this.setLayout(null);

		ActionListener buttonActionListener=new ButtonsActionListener(model,this);

		int buttonWidth=32;
		int buttonHeight=30;
		int buttonMargin=8;
		int groupMargin=80;
		int x=0;
		int y=4;

		jbuttonPlayCursor=new JButton();
		jbuttonPlayCursor.setBounds(x, y, buttonWidth,buttonHeight);
		jbuttonPlayCursor.setIcon(ImageTools.createImageIcon("/icons/Button_playCursor.png"));
		jbuttonPlayCursor.setToolTipText("play from cursor position");
		jbuttonPlayCursor.addActionListener(buttonActionListener);
		jbuttonPlayCursor.setEnabled(false);
		add(jbuttonPlayCursor);


		x+=buttonWidth+buttonMargin;

		jbuttonPlaySelection=new JButton();
		jbuttonPlaySelection.setBounds(x, y, buttonWidth,buttonHeight);
		jbuttonPlaySelection.setIcon(ImageTools.createImageIcon("/icons/Button_playSelection.png"));
		jbuttonPlaySelection.setToolTipText("play selection");
		jbuttonPlaySelection.addActionListener(buttonActionListener);
		jbuttonPlaySelection.setEnabled(false);
		add(jbuttonPlaySelection);

		x+=buttonWidth+buttonMargin;

		jbuttonPause=new JButton();
		jbuttonPause.setBounds(x, y, buttonWidth,buttonHeight);
		jbuttonPause.setIcon(ImageTools.createImageIcon("/icons/Button_pause.png"));
		jbuttonPause.setToolTipText("pause");
		jbuttonPause.addActionListener(buttonActionListener);
		jbuttonPause.setEnabled(false);
		add(jbuttonPause);

		x+=buttonWidth+buttonMargin;

		jbuttonStop=new JButton();
		jbuttonStop.setBounds(x, y, buttonWidth,buttonHeight);
		jbuttonStop.setIcon(ImageTools.createImageIcon("/icons/Button_stop.png"));
		jbuttonStop.setToolTipText("stop");
		jbuttonStop.addActionListener(buttonActionListener);
		jbuttonStop.setEnabled(false);
		add(jbuttonStop);

		x+=buttonWidth+buttonMargin;

		jbuttonPrev=new JButton();
		jbuttonPrev.setBounds(x, y, buttonWidth,buttonHeight);
		jbuttonPrev.setIcon(ImageTools.createImageIcon("/icons/Button_previousSection.png"));
		jbuttonPrev.setToolTipText("goto previous section");
		jbuttonPrev.addActionListener(buttonActionListener);
		jbuttonPrev.setEnabled(false);
		add(jbuttonPrev);

		x+=buttonWidth+buttonMargin;

		jbuttonNext=new JButton();
		jbuttonNext.setBounds(x, y, buttonWidth,buttonHeight);
		jbuttonNext.setIcon(ImageTools.createImageIcon("/icons/Button_nextSection.png"));
		jbuttonNext.setToolTipText("goto next section");
		jbuttonNext.addActionListener(buttonActionListener);
		jbuttonNext.setEnabled(false);
		add(jbuttonNext);

		x+=buttonWidth+buttonMargin+groupMargin;

		jbuttonZoomIn=new JButton();
		jbuttonZoomIn.setBounds(x, y, buttonWidth,buttonHeight);
		jbuttonZoomIn.setIcon(ImageTools.createImageIcon("/icons/Button_zoomIn.png"));
		jbuttonZoomIn.setToolTipText("zoom in");
		jbuttonZoomIn.addActionListener(buttonActionListener);
		jbuttonZoomIn.setEnabled(false);
		add(jbuttonZoomIn);

		x+=buttonWidth+buttonMargin;

		jbuttonZoomOut=new JButton();
		jbuttonZoomOut.setBounds(x, y, buttonWidth,buttonHeight);
		jbuttonZoomOut.setIcon(ImageTools.createImageIcon("/icons/Button_zoomOut.png"));
		jbuttonZoomOut.setToolTipText("zoom out");
		jbuttonZoomOut.addActionListener(buttonActionListener);
		jbuttonZoomOut.setEnabled(false);
		add(jbuttonZoomOut);

		x+=buttonWidth+buttonMargin;

		jbuttonFitSelection=new JButton();
		jbuttonFitSelection.setBounds(x, y, buttonWidth,buttonHeight);
		jbuttonFitSelection.setIcon(ImageTools.createImageIcon("/icons/Button_zoomSelection.png"));
		jbuttonFitSelection.setToolTipText("zoom selection");
		jbuttonFitSelection.addActionListener(buttonActionListener);
		jbuttonFitSelection.setEnabled(false);
		add(jbuttonFitSelection);

		x+=buttonWidth+buttonMargin;

		jbuttonFitProject=new JButton();
		jbuttonFitProject.setBounds(x, y, buttonWidth,buttonHeight);
		jbuttonFitProject.setIcon(ImageTools.createImageIcon("/icons/Button_fitProject.png"));
		jbuttonFitProject.setToolTipText("fit project");
		jbuttonFitProject.addActionListener(buttonActionListener);
		jbuttonFitProject.setEnabled(false);
		add(jbuttonFitProject);

		x+=buttonWidth+buttonMargin;

		toggleButtonAmplitudeZoom=new JToggleButton();
		toggleButtonAmplitudeZoom.setBounds(x, y, buttonWidth,buttonHeight);
		toggleButtonAmplitudeZoom.setIcon(ImageTools.createImageIcon("/icons/Button_AmplitudeZoom.png"));
		toggleButtonAmplitudeZoom.setSelectedIcon(ImageTools.createImageIcon("/icons/Button_AmplitudeZoomPressed.png"));
		toggleButtonAmplitudeZoom.setToolTipText("zoom amplitude");
		toggleButtonAmplitudeZoom.addActionListener(buttonActionListener);
		toggleButtonAmplitudeZoom.setEnabled(false);
		add(toggleButtonAmplitudeZoom);

		x+=buttonWidth+buttonMargin+groupMargin;

		jbuttonCut=new JButton();
		jbuttonCut.setBounds(x, y, buttonWidth,buttonHeight);
		jbuttonCut.setIcon(ImageTools.createImageIcon("/icons/ProgramIcon24x24.png"));
		jbuttonCut.setToolTipText("cut at cursor position");
		jbuttonCut.addActionListener(buttonActionListener);
		jbuttonCut.setEnabled(false);
		add(jbuttonCut);

		x+=buttonWidth;

		Dimension size=new Dimension(x, buttonHeight+4);
		setSize(size);
		setMinimumSize(size.getSize());
		setMaximumSize(size.getSize());
		setPreferredSize(size.getSize());

		model.addObserver(this);

		//setBackground(new Color(0xccffcc));
	}

	@Override
	public void update(Observable observable, Object object) {
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
		jbuttonFitSelection.setEnabled(hasSelection);
	}

}
