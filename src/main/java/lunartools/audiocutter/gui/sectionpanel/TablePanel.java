package lunartools.audiocutter.gui.sectionpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

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

import lunartools.audiocutter.AudioCutterModel;

public class TablePanel extends JPanel{
	private static final int COLUMNWIDTH_TRACKNUMBER = 30;
	private static final int COLUMNWIDTH_POSITION = 85;
	private static final int COLUMNWIDTH_LENGTH = 65;

	private SectionTableModel sectionTableModel;

	JTable table;
	private JScrollPane scrollPane;

	public TablePanel(AudioCutterModel model) {
		super(new BorderLayout());

		sectionTableModel=new SectionTableModel(model);
		table = new JTable(sectionTableModel);
		table.setFillsViewportHeight(true);
		table.setPreferredScrollableViewportSize(getSize());

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionModel selectionModel = table.getSelectionModel();
		selectionModel.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e){
				ListSelectionModel selectionModel=(ListSelectionModel)e.getSource();
				model.setSelectedAudioSection(selectionModel.getMinSelectionIndex());
			}

		});

		table.addMouseListener(new SectionTableMouseListener(model,this));

		Dimension dim = new Dimension(8,2);
		table.setIntercellSpacing(new Dimension(dim));
		table.setRowHeight(24);
		table.setSelectionBackground(new Color(0xfff0e1));
		table.setSelectionForeground(Color.BLACK);
		TableCellRenderer rendererFromHeader = table.getTableHeader().getDefaultRenderer();
		JLabel headerLabel = (JLabel) rendererFromHeader;
		headerLabel.setHorizontalAlignment(JLabel.CENTER);

		TableColumn tableColumn=table.getColumnModel().getColumn(0);
		tableColumn.setResizable(false);
		tableColumn.setPreferredWidth(COLUMNWIDTH_TRACKNUMBER);
		tableColumn.setMinWidth(COLUMNWIDTH_TRACKNUMBER);
		tableColumn.setMaxWidth(COLUMNWIDTH_TRACKNUMBER);
		DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
		defaultTableCellRenderer.setHorizontalAlignment(JLabel.CENTER);
		tableColumn.setCellRenderer(defaultTableCellRenderer);

		tableColumn=table.getColumnModel().getColumn(1);
		tableColumn.setResizable(true);
		defaultTableCellRenderer = new DefaultTableCellRenderer();
		defaultTableCellRenderer.setHorizontalAlignment(JLabel.LEFT);
		tableColumn.setCellRenderer(defaultTableCellRenderer);

		tableColumn=table.getColumnModel().getColumn(2);
		tableColumn.setResizable(false);
		tableColumn.setPreferredWidth(COLUMNWIDTH_POSITION); 
		tableColumn.setMinWidth(COLUMNWIDTH_POSITION);
		tableColumn.setMaxWidth(COLUMNWIDTH_POSITION);
		defaultTableCellRenderer = new DefaultTableCellRenderer();
		defaultTableCellRenderer.setHorizontalAlignment(JLabel.RIGHT);
		tableColumn.setCellRenderer(defaultTableCellRenderer);

		tableColumn=table.getColumnModel().getColumn(3);
		tableColumn.setResizable(false);
		tableColumn.setPreferredWidth(COLUMNWIDTH_LENGTH); 
		tableColumn.setMinWidth(COLUMNWIDTH_LENGTH);
		tableColumn.setMaxWidth(COLUMNWIDTH_LENGTH);
		defaultTableCellRenderer = new DefaultTableCellRenderer();
		defaultTableCellRenderer.setHorizontalAlignment(JLabel.RIGHT);
		tableColumn.setCellRenderer(defaultTableCellRenderer);

		scrollPane = new JScrollPane(table,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane,BorderLayout.CENTER);
		scrollPane.setBorder(null);
	}

}
