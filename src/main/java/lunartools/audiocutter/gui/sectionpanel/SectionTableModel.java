package lunartools.audiocutter.gui.sectionpanel;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.common.model.AudioSectionModel;
import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.common.ui.util.SampleUtils;
import lunartools.audiocutter.core.AudioCutterModel;

public class SectionTableModel extends AbstractTableModel{
	private static Logger logger = LoggerFactory.getLogger(SectionTableModel.class);
	private AudioCutterModel model;

	private static final String[] COLUMNNAMES = {
			"#",
			"Section name",
			"Position",
			"Length"
	};

	public SectionTableModel(AudioCutterModel audioCutterModel) {
		this.model=audioCutterModel;
		audioCutterModel.addChangeListener(this::updateModelChanges);
	}

	public int getColumnCount() {
		return COLUMNNAMES.length;
	}

	public int getRowCount() {
		ArrayList<AudioSectionModel> audioSections=model.getAudioSections();
		return audioSections==null?0:audioSections.size();
	}

	public String getColumnName(int column) {
		return COLUMNNAMES[column];
	}

	public Object getValueAt(int row, int column) {
		AudioSectionModel audioSection=model.getAudioSections().get(row);
		switch(column) {
		case 0:
			return ""+(row<9?"0"+(row+1):(row+1));
		case 1:
			return audioSection.getName();
		case 2:
			return SampleUtils.convertNumberOfSamplesToHourMinuteSecondFractionString(audioSection.getPosition());
		case 3:
			int end;
			if(model.getAudioSections().size()-1>row) {
				end=model.getAudioSections().get(row+1).getPosition();
			}else {
				end=model.getAudiodataLengthInSamples();
			}
			return SampleUtils.convertNumberOfSamplesToHourMinuteSecondString(end-audioSection.getPosition());
		}
		throw new RuntimeException("Illegal column: "+column);
	}

	public Class getColumnClass(int c) {
		return String.class;
	}

	public boolean isCellEditable(int row, int column) {
		return column==1;
	}

	public void setValueAt(Object value, int row, int column) {
		ArrayList<AudioSectionModel> audioSections=model.getAudioSections();
		AudioSectionModel audioSection=audioSections.get(row);
		if(audioSection.getName()==null || !audioSection.getName().equals(value)) {
			audioSection.setName((String)value);
			model.setProjectDirty(true);
		}
		fireTableCellUpdated(row, column);
	}

	public void updateModelChanges(Object object) {
//System.out.println("updateModelChanges");
		if(object==SimpleEvents.MODEL_MEDIAFILECHANGED) {
//System.out.println("fireTableDataChanged");
			fireTableDataChanged();
		}
	}

}