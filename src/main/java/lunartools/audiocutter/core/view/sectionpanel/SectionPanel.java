package lunartools.audiocutter.core.view.sectionpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import lunartools.audiocutter.core.AudioCutterModel;

public class SectionPanel extends JPanel{
	private static final int COLUMNWIDTH_TRACKNUMBER = 30;
	private static final int COLUMNWIDTH_POSITION = 85;
	private static final int COLUMNWIDTH_LENGTH = 65;

	private final SectionTableModel sectionTableModel;
	private final JScrollPane scrollPane;

	private final JTable jTable;
	private final SectionTableMouseListener sectionTableMouseListener;
	
	public SectionPanel(AudioCutterModel audioCutterModel) {
		setLayout(new BorderLayout());

		sectionTableModel=new SectionTableModel(audioCutterModel);
		jTable = new JTable(sectionTableModel);
		jTable.setFillsViewportHeight(true);
		jTable.setPreferredScrollableViewportSize(getSize());

		jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionModel selectionModel = jTable.getSelectionModel();
		selectionModel.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e){
				ListSelectionModel selectionModel=(ListSelectionModel)e.getSource();
				audioCutterModel.setSelectedAudioSection(selectionModel.getMinSelectionIndex());
			}

		});

		sectionTableMouseListener=new SectionTableMouseListener(audioCutterModel,jTable);
		jTable.addMouseListener(sectionTableMouseListener);

		Dimension dim = new Dimension(8,2);
		jTable.setIntercellSpacing(new Dimension(dim));
		jTable.setRowHeight(24);
		jTable.setSelectionBackground(new Color(0xfff0e1));
		jTable.setSelectionForeground(Color.BLACK);
		TableCellRenderer rendererFromHeader = jTable.getTableHeader().getDefaultRenderer();
		JLabel headerLabel = (JLabel) rendererFromHeader;
		headerLabel.setHorizontalAlignment(JLabel.CENTER);

		TableColumn tableColumn=jTable.getColumnModel().getColumn(0);
		tableColumn.setResizable(false);
		tableColumn.setPreferredWidth(COLUMNWIDTH_TRACKNUMBER);
		tableColumn.setMinWidth(COLUMNWIDTH_TRACKNUMBER);
		tableColumn.setMaxWidth(COLUMNWIDTH_TRACKNUMBER);
		DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
		defaultTableCellRenderer.setHorizontalAlignment(JLabel.CENTER);
		tableColumn.setCellRenderer(defaultTableCellRenderer);

		tableColumn=jTable.getColumnModel().getColumn(1);
		tableColumn.setResizable(true);
		defaultTableCellRenderer = new DefaultTableCellRenderer();
		defaultTableCellRenderer.setHorizontalAlignment(JLabel.LEFT);
		tableColumn.setCellRenderer(defaultTableCellRenderer);

		tableColumn=jTable.getColumnModel().getColumn(2);
		tableColumn.setResizable(false);
		tableColumn.setPreferredWidth(COLUMNWIDTH_POSITION); 
		tableColumn.setMinWidth(COLUMNWIDTH_POSITION);
		tableColumn.setMaxWidth(COLUMNWIDTH_POSITION);
		defaultTableCellRenderer = new DefaultTableCellRenderer();
		defaultTableCellRenderer.setHorizontalAlignment(JLabel.RIGHT);
		tableColumn.setCellRenderer(defaultTableCellRenderer);

		tableColumn=jTable.getColumnModel().getColumn(3);
		tableColumn.setResizable(false);
		tableColumn.setPreferredWidth(COLUMNWIDTH_LENGTH); 
		tableColumn.setMinWidth(COLUMNWIDTH_LENGTH);
		tableColumn.setMaxWidth(COLUMNWIDTH_LENGTH);
		defaultTableCellRenderer = new DefaultTableCellRenderer();
		defaultTableCellRenderer.setHorizontalAlignment(JLabel.RIGHT);
		tableColumn.setCellRenderer(defaultTableCellRenderer);

		scrollPane = new JScrollPane(jTable,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane,BorderLayout.CENTER);
		scrollPane.setBorder(null);
	}

	public JTable getJTable() {
		return jTable;
	}

	public SectionTableMouseListener getSectionTableMouseListener() {
		return sectionTableMouseListener;
	}
	
}
