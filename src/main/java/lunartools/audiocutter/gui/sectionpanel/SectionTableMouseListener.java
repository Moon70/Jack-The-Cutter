package lunartools.audiocutter.gui.sectionpanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTable;

import lunartools.audiocutter.AudioCutterModel;

public class SectionTableMouseListener extends MouseAdapter{
	private AudioCutterModel model;
	private TablePanel tablePanel;
	private JPopupMenu popupMenu;

	public SectionTableMouseListener(AudioCutterModel model,TablePanel tablePanel) {
		this.model=model;
		this.tablePanel=tablePanel;
		this.popupMenu=new SectionTablePopupMenu(model,tablePanel);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int row = tablePanel.table.rowAtPoint(e.getPoint());
		if (row >= 0 && row < tablePanel.table.getRowCount()) {
			tablePanel.table.setRowSelectionInterval(row, row);
		} else {
			tablePanel.table.clearSelection();
		}

		int rowindex = tablePanel.table.getSelectedRow();
		if (rowindex < 0){
			return;
		}

		if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
			((SectionTablePopupMenu)popupMenu).menuitemEditStart.setEnabled(row>0);
			((SectionTablePopupMenu)popupMenu).menuitemEditEnd.setEnabled(row<model.getAudioSections().size()-1);
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}
}
