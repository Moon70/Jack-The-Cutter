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
import lunartools.SwingTools;
import lunartools.audiocutter.common.action.ActionFactory;
import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.common.service.AudioPlayer;
import lunartools.audiocutter.common.ui.Dialogs;
import lunartools.audiocutter.core.model.StatusMessage;
import lunartools.audiocutter.core.view.FileDropHandler;
import lunartools.audiocutter.gui.About;
import lunartools.audiocutter.gui.LeftPanel;
import lunartools.audiocutter.gui.MenuModel;
import lunartools.audiocutter.gui.mediainfopanel.MediaInfoPanel;
import lunartools.audiocutter.gui.sectionpanel.TablePanel;
import lunartools.audiocutter.menu.MenuView;
import lunartools.swing.HasParentFrame;

public class AudioCutterView implements HasParentFrame{
	private static Logger logger = LoggerFactory.getLogger(AudioCutterView.class);

	private final AudioCutterModel audioCutterModel;
	private final JFrame jFrame;
	
	private LeftPanel panelLeft;
	
	private JSplitPane jSplitPaneHorizontal;
	private JSplitPane jSplitPaneVertical;

	private final int tableSectionWithMin=350;
	private final int audiodataViewWithMin=530;

	public AudioCutterView(AudioCutterModel audioCutterModel) {
		this.audioCutterModel=Objects.requireNonNull(audioCutterModel);
		jFrame = new JFrame(AudioCutterModel.getProgramNameAndVersion());
		jFrame.setMinimumSize(new Dimension(tableSectionWithMin+audiodataViewWithMin,500));
		jFrame.setResizable(true);
		jFrame.setLayout(new BorderLayout());
		jFrame.setBounds(audioCutterModel.getFrameBounds());
		jFrame.setIconImages(SwingTools.getDefaultIconImages());

		audioCutterModel.addChangeListener(this::updateModelChanges);

		jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
//		addWindowListener(new WindowAdapter(){
//			public void windowClosing(WindowEvent event){
//				sendMessage(SimpleEvents.EXIT);
//			}
//		});

		jSplitPaneHorizontal = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		jFrame.add(jSplitPaneHorizontal);
		jSplitPaneHorizontal.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent e) {
				int divider=(int)e.getNewValue();
				if(divider==0) {
					return;
				}
				Rectangle frameBounds=audioCutterModel.getFrameBounds();
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
				audioCutterModel.setSectionTableWidth(tabeleSectionWith);
				audioCutterModel.setAudiodataViewWidth(divider);
			}
		});

//		jSplitPaneHorizontal.setBounds(0, 0,jFrame.getWidth()-24,AudioCutterModel.DEFAULT_FRAME_WIDTH-80);

		jSplitPaneHorizontal.setDividerLocation(audioCutterModel.getHorizontalDividerPosition());

		jFrame.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {}

			@Override
			public void componentResized(ComponentEvent e) {
				audioCutterModel.setFrameBounds(e.getComponent().getBounds());
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
		jFrame.setDropTarget(new DropTarget(jFrame,new DropTargetAdapter() {
			
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
						audioCutterModel.setStatusMessage(new StatusMessage(StatusMessage.Type.WARNING,"only drag single files to Jack, please"));
						return;
					}
					if(audioCutterModel.isProjectDirty() && Dialogs.userCanceledUnsavedChangesDialogue()){
						return;
					}
					AudioPlayer.getInstance().action_stop();
					audioCutterModel.closeProject();
					File file=droppedFiles.get(0);
					fileDropHandler.processDroppedFile(file);
				} catch (Exception e) {
					logger.error("Error when drag´n´drop file on Jack",e);
				}
			}
		}));
	}
	
	public void temporaryInjectController(AudioCutterController controller) {

		panelLeft=new LeftPanel(audioCutterModel,controller);
		panelLeft.setLocation(10, 0);
		jSplitPaneHorizontal.setLeftComponent(panelLeft);

		jSplitPaneVertical=new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		jSplitPaneVertical.setDividerLocation(audioCutterModel.getVerticalDividerPosition());
		jSplitPaneVertical.setEnabled(false);
		jSplitPaneHorizontal.setRightComponent(jSplitPaneVertical);

		int x=0;
		int y=0;
		int marginX=4;
		int marginY=4;
		MediaInfoPanel mediaInfoPanel=new MediaInfoPanel(audioCutterModel);
		mediaInfoPanel.setLocation(x+marginX, y+marginY);
		jSplitPaneVertical.setTopComponent(mediaInfoPanel);

		TablePanel tablePanel=new TablePanel(audioCutterModel);
		tablePanel.setOpaque(true);
		tablePanel.setLocation(x, y);
		jSplitPaneVertical.setBottomComponent(tablePanel);

		refreshGui();
	}

	public void setMenuView(MenuView menuView) {
		jFrame.setJMenuBar(menuView.getMenuBar());
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
			Rectangle bounds=audioCutterModel.getFrameBounds();
			int sectionTableWidth=audioCutterModel.getSectionTableWidth();
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

	public void refreshGui() {
		jFrame.repaint();
	}

	public void showMessageboxAbout() {
		About.showAboutDialog(jFrame);
	}

	@Override
	public JFrame getJFrame() {
		return jFrame;
	}

	public LeftPanel getPanelLeft() {
		return panelLeft;
	}

	
}
