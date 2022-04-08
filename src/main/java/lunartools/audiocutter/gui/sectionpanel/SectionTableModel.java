package lunartools.audiocutter.gui.sectionpanel;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.AudioSection;
import lunartools.audiocutter.Calculator;
import lunartools.audiocutter.SimpleEvents;

public class SectionTableModel extends AbstractTableModel implements Observer{
	private static Logger logger = LoggerFactory.getLogger(SectionTableModel.class);
	private AudioCutterModel model;

	private static final String[] COLUMNNAMES = {
			"#",
			"Section name",
			"Position",
			"Length"
	};

	public SectionTableModel(AudioCutterModel model) {
		this.model=model;
		model.addObserver(this);
	}

	public int getColumnCount() {
		return COLUMNNAMES.length;
	}

	public int getRowCount() {
		ArrayList<AudioSection> audioSections=model.getAudioSections();
		return audioSections==null?0:audioSections.size();
	}

	public String getColumnName(int column) {
		return COLUMNNAMES[column];
	}

	public Object getValueAt(int row, int column) {
		AudioSection audioSection=model.getAudioSections().get(row);
		switch(column) {
		case 0:
			return ""+(row<9?"0"+(row+1):(row+1));
		case 1:
			return audioSection.getName();
		case 2:
			return Calculator.convertNumberOfSamplesToHourMinuteSecondFractionString(audioSection.getPosition());
		case 3:
			int end;
			if(model.getAudioSections().size()-1>row) {
				end=model.getAudioSections().get(row+1).getPosition();
			}else {
				end=model.getAudiodataLengthInSamples();
			}
			return Calculator.convertNumberOfSamplesToHourMinuteSecondString(end-audioSection.getPosition());
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
		ArrayList<AudioSection> audioSections=model.getAudioSections();
		AudioSection audioSection=audioSections.get(row);
		if(audioSection.getName()==null || !audioSection.getName().equals(value)) {
			audioSection.setName((String)value);
			model.setProjectIsDirty(true);
		}
		fireTableCellUpdated(row, column);
	}

	@Override
	public void update(Observable observable, Object object) {
		if(object==SimpleEvents.MODEL_MEDIAFILECHANGED) {
			fireTableDataChanged();
		}
	}

}