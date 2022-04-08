package lunartools.audiocutter.gui.preferencespanel;

import java.awt.AWTEvent;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import lunartools.ImageTools;
import lunartools.audiocutter.AudioCutterModel;

public class PreferencesDialog extends JDialog implements AWTEventListener{
	private final int dialogWidth=500;
	private final int dialogHeight=(int)(dialogWidth/AudioCutterModel.SECTIOAUREA);
	private PreferencesController preferencesController;
	
	JTextField textfield;
	JButton buttonSelectFfmpegExe;

	JButton buttonOK;
	JButton buttonCancel;

	public PreferencesDialog(AudioCutterModel model,PreferencesController preferencesController,JFrame jframe) {
		super(jframe,"Preferences");
		this.preferencesController=preferencesController;
		setLayout(null);
		setModal(true);
		setResizable(false);
		setIconImage(jframe.getIconImage());
		Rectangle rectangle=jframe.getBounds(getBounds());
		int offsetX=rectangle.x+((rectangle.width-dialogWidth)>>1);
		int offsetY=rectangle.y+((rectangle.height-dialogHeight)>>1);
		setBounds(offsetX,offsetY,dialogWidth,dialogHeight);

		int margin=4;
		int column1X=20;
		int column1W=130;
		int column2X=column1X+column1W+margin;
		int column2W=300;
		int column3X=column2X+column2W+margin;
		int column3W=24;
		int y=50;
		int lineHeight=18;

		JLabel label=new JLabel("FFmpeg executable:");
		label.setBounds(column1X,y,column1W,20);
		add(label);

		textfield=new JTextField(50);
		textfield.setBounds(column1X,y,column2W,25);
		textfield.setBounds(column2X,y,300,20);
		String ffmpegPath=model.getFFmpegExecutablePath();
		if(ffmpegPath!=null) {
			textfield.setText(ffmpegPath);
		}
		add(textfield);

		buttonSelectFfmpegExe=new JButton();
		buttonSelectFfmpegExe.setBounds(column3X, y, column3W, lineHeight);
		buttonSelectFfmpegExe.setIcon(ImageTools.createImageIcon("/icons/Open16.gif"));
		buttonSelectFfmpegExe.setBorder(null);
		buttonSelectFfmpegExe.setContentAreaFilled(false);
		buttonSelectFfmpegExe.addActionListener(preferencesController);
		add(buttonSelectFfmpegExe);

		int buttonWidth=100;
		int buttonGap=40;
		int buttonDistanceX=(dialogWidth-buttonWidth*2-buttonGap)/2;
		buttonOK=new JButton("Ok");
		buttonOK.setBounds(buttonDistanceX,dialogHeight-80,buttonWidth,25);
		buttonOK.addActionListener(preferencesController);
		add(buttonOK);

		buttonCancel=new JButton("Cancel");
		buttonCancel.setBounds(buttonDistanceX+buttonWidth+buttonGap,dialogHeight-80,buttonWidth,25);
		buttonCancel.addActionListener(preferencesController);
		add(buttonCancel);
		
		long eventMask = AWTEvent.KEY_EVENT_MASK;
		Toolkit.getDefaultToolkit().addAWTEventListener(this, eventMask);
	}

	@Override
	public void dispose() {
		Toolkit.getDefaultToolkit().removeAWTEventListener(this);
		super.dispose();
	}
	
	@Override
	public void eventDispatched(AWTEvent event) {
		if(event instanceof KeyEvent) {
			KeyEvent keyEvent=(KeyEvent)event;
			if(keyEvent.getID()!=KeyEvent.KEY_RELEASED) {
				return;
			}
			if(keyEvent.getKeyCode()==27) {
				preferencesController.actionCancel();
			}else if(keyEvent.getKeyCode()==10) {
				preferencesController.actionOk();
			}
		}
	}

}
