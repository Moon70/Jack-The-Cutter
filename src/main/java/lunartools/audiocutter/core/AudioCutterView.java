package lunartools.audiocutter.core;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.ImageTools;
import lunartools.ObservableJFrame;
import lunartools.audiocutter.common.action.ActionFactory;
import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.common.service.AudioPlayer;
import lunartools.audiocutter.common.ui.Dialogs;
import lunartools.audiocutter.core.view.FileDropHandler;
import lunartools.audiocutter.gui.About;
import lunartools.audiocutter.gui.LeftPanel;
import lunartools.audiocutter.gui.MenuModel;
import lunartools.audiocutter.gui.mediainfopanel.MediaInfoPanel;
import lunartools.audiocutter.gui.sectionpanel.TablePanel;
import lunartools.audiocutter.gui.statuspanel.StatusMessage;
import lunartools.audiocutter.menu.MenuView;
import lunartools.swing.HasParentFrame;

public class AudioCutterView extends JFrame implements HasParentFrame{
	private static Logger logger = LoggerFactory.getLogger(AudioCutterView.class);

	private final AudioCutterModel model;
	private MenuModel menuModel;
	private AudioCutterController controller;
	private MenuModel menubarController;
	private JSplitPane jSplitPaneHorizontal;
	private JSplitPane jSplitPaneVertical;

	private final int tableSectionWithMin=350;
	private final int audiodataViewWithMin=530;


	public AudioCutterView(AudioCutterModel model) {
		super.setTitle(AudioCutterModel.getProgramNameAndVersion());
		setLayout(new BorderLayout());
		setBounds(model.getFrameBounds());
		setResizable(true);
		setMinimumSize(new Dimension(tableSectionWithMin+audiodataViewWithMin,500));
		this.model=Objects.requireNonNull(model);
		this.model.addChangeListener(this::updateModelChanges);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
//		addWindowListener(new WindowAdapter(){
//			public void windowClosing(WindowEvent event){
//				sendMessage(SimpleEvents.EXIT);
//			}
//		});



//		int x=10;
//		int y=0;

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
	
	public void setDropTargetHandler(FileDropHandler fileDropHandler) {
		Objects.requireNonNull(fileDropHandler);
//		setDropTarget(new AudioCutterDropTarget(model,controller));
		setDropTarget(new DropTarget(this,new DropTargetAdapter() {
			
			@Override
			public void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_LINK);
					Transferable transferable=evt.getTransferable();
					Object object=transferable.getTransferData(DataFlavor.javaFileListFlavor);
					if(!(object instanceof List<?>)) {
						return;
					}
					List<File> droppedFiles = (List<File>)object;
					if(droppedFiles.size()>1) {
						model.setStatusMessage(new StatusMessage(StatusMessage.Type.WARNING,"only drag single files to Jack, please"));
						return;
					}
					if(model.isProjectDirty() && Dialogs.userCanceledUnsavedChangesDialogue()){
						return;
					}
					AudioPlayer.getInstance().action_stop();
					model.closeProject();
					File file=droppedFiles.get(0);
					fileDropHandler.processDroppedFile(file);
				} catch (Exception e) {
					logger.error("Error when drag´n´drop file on Jack",e);
				}
			}
		}));
	}
	
	public void temporaryInjectController(AudioCutterController controller) {
		this.controller=controller;
		

		JPanel panelLeft=new LeftPanel(model,controller);
		panelLeft.setLocation(10, 0);
		jSplitPaneHorizontal.setLeftComponent(panelLeft);

		jSplitPaneVertical=new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		jSplitPaneVertical.setDividerLocation(model.getVerticalDividerPosition());
		jSplitPaneVertical.setEnabled(false);
		jSplitPaneHorizontal.setRightComponent(jSplitPaneVertical);

		int x=0;
		int y=0;
		int marginX=4;
		int marginY=4;
		MediaInfoPanel mediaInfoPanel=new MediaInfoPanel(model);
		mediaInfoPanel.setLocation(x+marginX, y+marginY);
		jSplitPaneVertical.setTopComponent(mediaInfoPanel);

		TablePanel tablePanel=new TablePanel(model);
		tablePanel.setOpaque(true);
		tablePanel.setLocation(x, y);
		jSplitPaneVertical.setBottomComponent(tablePanel);

		refreshGui();
	}

	public void setMenuView(MenuView menuView) {
		this.setJMenuBar(menuView.getMenuBar());
	}

	public void updateModelChanges(Object object) {
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

//	public void sendMessage(Object message) {
//		setChanged();
//		notifyObservers(message);
//	}

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

	@Override
	public JFrame getJFrame() {
		return this;
	}

}
