package lunartools.audiocutter.core.view.sectionpanel;

import java.util.Objects;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import lunartools.audiocutter.common.action.ActionFactory;

public class SectionTablePopupMenu extends JPopupMenu{
	private final JTable jTable;

	JMenuItem menuitemEditStart;
	JMenuItem menuitemEditEnd;
	private JMenuItem menuitemPlay;
	private JMenuItem menuitemSelectAndZoom;
	JMenuItem menuitemDeleteLeftCutpoint;
	JMenuItem menuitemDeleteRightCutpoint;

	public SectionTablePopupMenu(JTable jTable) {
		super();
		this.jTable=Objects.requireNonNull(jTable);

		this.add(menuitemPlay=new JMenuItem());
		this.add(menuitemSelectAndZoom=new JMenuItem());
		this.add(menuitemEditStart=new JMenuItem());
		this.add(menuitemEditEnd=new JMenuItem());

		this.add(new JPopupMenu.Separator());

		this.add(menuitemDeleteLeftCutpoint=new JMenuItem());
		this.add(menuitemDeleteRightCutpoint=new JMenuItem());
	}

	public void setActionFactory(ActionFactory actionFactory) {
		menuitemPlay.setAction(actionFactory.createPopupPlayAction(jTable));
		menuitemSelectAndZoom.setAction(actionFactory.createPopupSelectAndZoomAction(jTable));
		menuitemEditStart.setAction(actionFactory.createPopupEditStartPositionAction(jTable));
		menuitemEditEnd.setAction(actionFactory.createPopupEditEndPositionAction(jTable));
		menuitemDeleteLeftCutpoint.setAction(actionFactory.createPopupDeleteLeftCutpointAction(jTable));
		menuitemDeleteRightCutpoint.setAction(actionFactory.createPopupDeleteRightCutpointAction(jTable));
	}

}
