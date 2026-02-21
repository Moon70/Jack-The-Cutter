package lunartools.audiocutter.core.view.sectionpanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

import javax.swing.JTable;

import lunartools.audiocutter.core.AudioCutterModel;

public class SectionTableMouseListener extends MouseAdapter{
	private final AudioCutterModel audioCutterModel;
	private final JTable jTable;
	private final SectionTablePopupMenu sectionTablePopupMenu;

	public SectionTableMouseListener(AudioCutterModel audioCutterModel,JTable jTable) {
		this.audioCutterModel=Objects.requireNonNull(audioCutterModel);
		this.jTable=Objects.requireNonNull(jTable);
		this.sectionTablePopupMenu=new SectionTablePopupMenu(jTable);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.isPopupTrigger()) {
			showPopup(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.isPopupTrigger()) {
			showPopup(e);
		}
	}

	private void showPopup(MouseEvent e) {
		int selectedRow = jTable.rowAtPoint(e.getPoint());
		if(selectedRow >= 0) {
			jTable.setRowSelectionInterval(selectedRow, selectedRow);
		}

		int rowindex = jTable.getSelectedRow();
		if(rowindex < 0){
			return;
		}

		sectionTablePopupMenu.menuitemEditStart.setEnabled(selectedRow>0);
		sectionTablePopupMenu.menuitemEditEnd.setEnabled(selectedRow<audioCutterModel.getAudioSections().size()-1);
		sectionTablePopupMenu.menuitemDeleteLeftCutpoint.setEnabled(selectedRow>0);
		sectionTablePopupMenu.menuitemDeleteRightCutpoint.setEnabled(selectedRow<audioCutterModel.getAudioSections().size()-1);
		sectionTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	public SectionTablePopupMenu getSectionTablePopupMenu() {
		return sectionTablePopupMenu;
	}

}
