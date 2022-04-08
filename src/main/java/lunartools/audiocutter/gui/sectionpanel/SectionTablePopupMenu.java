package lunartools.audiocutter.gui.sectionpanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.ImageTools;
import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.AudioSection;
import lunartools.audiocutter.player.AudioPlayer;

public class SectionTablePopupMenu extends JPopupMenu implements ActionListener{
	private static Logger logger = LoggerFactory.getLogger(SectionTablePopupMenu.class);
	private AudioCutterModel model;
	private TablePanel tablePanel;
	JMenuItem menuitemEditStart;
	JMenuItem menuitemEditEnd;
	private JMenuItem menuitemPlay;
	private JMenuItem menuitemSelectAndZoom;
	private JMenuItem menuitemRemove;

	public SectionTablePopupMenu(AudioCutterModel model,final TablePanel tablePanel) {
		super();
		this.model=model;
		this.tablePanel=tablePanel;

		menuitemPlay=new JMenuItem("play");
		this.add(menuitemPlay);
		menuitemPlay.addActionListener(this);
		menuitemPlay.setIcon(ImageTools.createImageIcon("/icons/Play.png"));

		menuitemSelectAndZoom=new JMenuItem("zoom");
		this.add(menuitemSelectAndZoom);
		menuitemSelectAndZoom.addActionListener(this);
		menuitemSelectAndZoom.setIcon(ImageTools.createImageIcon("/icons/Button_zoomSelection.png"));

		menuitemEditStart=new JMenuItem("edit start position");
		this.add(menuitemEditStart);
		menuitemEditStart.addActionListener(this);
		menuitemEditStart.setIcon(ImageTools.createImageIcon("/icons/EditStartPos.png"));

		menuitemEditEnd=new JMenuItem("edit end position");
		this.add(menuitemEditEnd);
		menuitemEditEnd.addActionListener(this);
		menuitemEditEnd.setIcon(ImageTools.createImageIcon("/icons/EditEndPos.png"));

		this.add(new JPopupMenu.Separator());

		menuitemRemove=new JMenuItem("remove");
		this.add(menuitemRemove);
		menuitemRemove.addActionListener(this);
		menuitemRemove.setIcon(ImageTools.createImageIcon("/icons/DeleteSection.png"));
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if(logger.isTraceEnabled()) {
			logger.trace("actionPerformed: "+actionEvent);
		}
		Object object=actionEvent.getSource();
		int selectedRow=tablePanel.table.getSelectedRow();
		selectedRow=tablePanel.table.convertRowIndexToModel(selectedRow);
		if(object==menuitemEditStart) {
			ArrayList<AudioSection> audioSections=model.getAudioSections();
			AudioSection audioSection=audioSections.get(selectedRow);
			int startpositionOfSelectedSection=audioSection.getPosition();
			int startSelection=startpositionOfSelectedSection-50000;
			if(startSelection<0) {
				startSelection=0;
			}
			int endSelection=startpositionOfSelectedSection+50000;
			if(endSelection>model.getAudiodataLengthInSamples()) {
				endSelection=model.getAudiodataLengthInSamples();
			}
			model.setSelectionRangeInSamples(startSelection,endSelection);
			model.setViewRangeInSamples(startSelection,endSelection);
		}else if(object==menuitemEditEnd) {
			ArrayList<AudioSection> audioSections=model.getAudioSections();
			AudioSection audioSection=audioSections.get(selectedRow+1);
			int startpositionOfSelectedSection=audioSection.getPosition();
			int startSelection=startpositionOfSelectedSection-50000;
			if(startSelection<0) {
				startSelection=0;
			}
			int endSelection=startpositionOfSelectedSection+50000;
			if(endSelection>model.getAudiodataLengthInSamples()) {
				endSelection=model.getAudiodataLengthInSamples();
			}
			model.setSelectionRangeInSamples(startSelection,endSelection);
			model.setViewRangeInSamples(startSelection,endSelection);
		}else if(object==menuitemPlay) {
			AudioPlayer.getInstance().playSection(selectedRow);
		}else if(object==menuitemSelectAndZoom) {
			AudioSection audioSection=model.getAudioSection(selectedRow);
			AudioSection audioSectionNext=model.getAudioSection(selectedRow+1);
			if(audioSectionNext==null) {
				model.setViewRangeInSamples(audioSection.getPosition(),model.getAudiodataLengthInSamples());
			}else {
				model.setViewRangeInSamples(audioSection.getPosition(),audioSectionNext.getPosition());
			}

		}else if(object==menuitemRemove) {
			ArrayList<AudioSection> audioSections=model.getAudioSections();
			audioSections.remove(selectedRow);
			model.setAudioSections(audioSections);
		}
	}

}
