package lunartools.audiocutter.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.ImageTools;
import lunartools.ObservableJFrame;
import lunartools.audiocutter.AudioCutterController;
import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.SimpleEvents;
import lunartools.audiocutter.gui.mediainfopanel.MediaInfoPanel;
import lunartools.audiocutter.gui.sectionpanel.TablePanel;
import lunartools.audiocutter.gui.statuspanel.StatusMessage;

public class AudioCutterView extends ObservableJFrame implements Observer{
	private static Logger logger = LoggerFactory.getLogger(AudioCutterView.class);

	private AudioCutterModel model;
	private AudioCutterController controller;
	private MenubarController menubarController;
	private JSplitPane jSplitPaneHorizontal;
	private JSplitPane jSplitPaneVertical;

	private final int tableSectionWithMin=350;
	private final int audiodataViewWithMin=530;


	public AudioCutterView(AudioCutterModel model,AudioCutterController controller) {
		super.setTitle(AudioCutterModel.PROGRAMNAME+" "+AudioCutterModel.determineProgramVersion());
		setLayout(new BorderLayout());
		setBounds(model.getFrameBounds());
		setResizable(true);
		setMinimumSize(new Dimension(tableSectionWithMin+audiodataViewWithMin,500));
		this.model=model;
		this.model.addObserver(this);
		this.controller=controller;

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent event){
				sendMessage(SimpleEvents.EXIT);
			}
		});

		setDropTarget(new AudioCutterDropTarget(model,controller));

		this.menubarController=new MenubarController(this.model,this.controller,this);
		setMenuBar(this.menubarController.createMenubar());

		int x=10;
		int y=0;

		jSplitPaneHorizontal = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		add(jSplitPaneHorizontal);
		jSplitPaneHorizontal.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent e) {
				int divider=(int)e.getNewValue();
				if(divider==0) {
					return;
				}
				Rectangle frameBounds=model.getFrameBounds();
				int tabeleSectionWith=frameBounds.width-divider;
				if(tabeleSectionWith<tableSectionWithMin) {
					tabeleSectionWith=tableSectionWithMin;
					divider=frameBounds.width-tabeleSectionWith;
					jSplitPaneHorizontal.setDividerLocation(divider);
				}else if(divider<audiodataViewWithMin) {
					divider=audiodataViewWithMin;
					tabeleSectionWith=frameBounds.width-divider;
					jSplitPaneHorizontal.setDividerLocation(divider);

				}
				model.setSectionTableWidth(tabeleSectionWith);
				model.setAudiodataViewWidth(divider);
			}
		});

		jSplitPaneHorizontal.setBounds(0, 0,this.getWidth()-24,AudioCutterModel.DEFAULT_FRAME_WIDTH-80);

		jSplitPaneHorizontal.setDividerLocation(model.getHorizontalDividerPosition());

		JPanel panelLeft=new LeftPanel(model,controller);
		panelLeft.setLocation(x, y);
		jSplitPaneHorizontal.setLeftComponent(panelLeft);

		jSplitPaneVertical=new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		jSplitPaneVertical.setDividerLocation(model.getVerticalDividerPosition());
		jSplitPaneVertical.setEnabled(false);
		jSplitPaneHorizontal.setRightComponent(jSplitPaneVertical);

		x=0;
		y=0;
		int marginX=4;
		int marginY=4;
		MediaInfoPanel mediaInfoPanel=new MediaInfoPanel(model,controller);
		mediaInfoPanel.setLocation(x+marginX, y+marginY);
		jSplitPaneVertical.setTopComponent(mediaInfoPanel);

		TablePanel tablePanel=new TablePanel(model);
		tablePanel.setOpaque(true);
		tablePanel.setLocation(x, y);
		jSplitPaneVertical.setBottomComponent(tablePanel);

		setIconImages();

		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {}

			@Override
			public void componentResized(ComponentEvent e) {
				model.setFrameBounds(e.getComponent().getBounds());
			}

			@Override
			public void componentMoved(ComponentEvent e) {}

			@Override
			public void componentHidden(ComponentEvent e) {}

		});

		refreshGui();
	}

	@Override
	public void update(Observable observable, Object object) {
		if(object==SimpleEvents.MODEL_AUDIODATACHANGED) {
			refreshGui();
		}else if(object==SimpleEvents.MODEL_ZOOMCHANGED) {
			refreshGui();
		}else if(object==SimpleEvents.MODEL_SELECTIONCHANGED) {
			refreshGui();
		}else if(object==SimpleEvents.MODEL_ZOOMRANGECHANGED) {
			refreshGui();
		}else if(object==SimpleEvents.MODEL_CURSORCHANGED) {
			refreshGui();
		}else if(object==SimpleEvents.MODEL_PLAYCURSORCHANGED) {
			refreshGui();
		}else if(object==SimpleEvents.MODEL_AUDIOSECTIONSCHANGED) {
			refreshGui();
		}else if(object==SimpleEvents.MODEL_SELECTEDSECTIONSCHANGED) {
			refreshGui();
		}else if(object==SimpleEvents.MODEL_FRAMESIZECHANGED) {
			Rectangle bounds=model.getFrameBounds();
			int sectionTableWidth=model.getSectionTableWidth();
			int dividerPosition=bounds.width-sectionTableWidth;
			jSplitPaneHorizontal.setDividerLocation(dividerPosition);
			refreshGui();
		}else if(object instanceof StatusMessage) {
			StatusMessage statusMessage=(StatusMessage)object;
			if(statusMessage.getType()==StatusMessage.Type.FFMPEGVERSION) {
				refreshGui();
			}
		}
	}

	public void sendMessage(Object message) {
		setChanged();
		notifyObservers(message);
	}

	public void refreshGui() {
		this.repaint();
	}

	public void showMessageboxAbout() {
		About.showAboutDialog(this);
	}

	private void setIconImages() {
		try {
			List<Image> icons=new ArrayList<Image>();
			icons.add(ImageTools.createImageFromResource("/icons/ProgramIcon64x64.png"));
			icons.add(ImageTools.createImageFromResource("/icons/ProgramIcon56x56.png"));
			icons.add(ImageTools.createImageFromResource("/icons/ProgramIcon48x48.png"));
			icons.add(ImageTools.createImageFromResource("/icons/ProgramIcon40x40.png"));
			icons.add(ImageTools.createImageFromResource("/icons/ProgramIcon32x32.png"));
			icons.add(ImageTools.createImageFromResource("/icons/ProgramIcon24x24.png"));
			icons.add(ImageTools.createImageFromResource("/icons/ProgramIcon16x16.png"));
			this.setIconImages(icons);
		} catch (IOException e) {
			logger.error("error loading icon image",e);
		}
	}

}
