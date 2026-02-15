package lunartools.audiocutter.gui.scrollbarspanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.AdjustmentListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.core.AudioCutterModel;

public class ScrollbarsPanel extends JPanel{
	private static Logger logger = LoggerFactory.getLogger(ScrollbarsPanel.class);
	private AudioCutterModel model;
	public JScrollBar scrollbarWave;
	public JScrollBar scrollbarZoom;

	public ScrollbarsPanel(AudioCutterModel model) {
		this.model=model;
		model.addChangeListener(this::updateModelChanges);
		this.setLayout(new GridBagLayout());

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;

		AdjustmentListener adjustmentlistener=new ScrollbarAdjustmentListener(model,this);
		
		JLabel labelMove=new JLabel("Move");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		add(labelMove, gridBagConstraints);

		scrollbarWave=new JScrollBar(JScrollBar.HORIZONTAL);
		scrollbarWave.setBackground(Color.DARK_GRAY);
		scrollbarWave.addAdjustmentListener(adjustmentlistener);
		scrollbarWave.setEnabled(false);
		gridBagConstraints.gridx = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(scrollbarWave, gridBagConstraints);

		
		gridBagConstraints.gridy = 1;
		JLabel labelZoom=new JLabel("Zoom");
		add(labelZoom);

		gridBagConstraints.gridx = 0;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		add(labelZoom, gridBagConstraints);

		scrollbarZoom=new JScrollBar(JScrollBar.HORIZONTAL);
		scrollbarZoom.setBackground(Color.DARK_GRAY);
		scrollbarZoom.addAdjustmentListener(adjustmentlistener);
		scrollbarZoom.setEnabled(false);
		gridBagConstraints.gridx = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(scrollbarZoom, gridBagConstraints);

		
//		setSize(new Dimension(1000, 50));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		//setAlignmentY(Component.TOP_ALIGNMENT);
		//setMinimumSize(new Dimension(100, 50));
		setPreferredSize(new Dimension(1000, 50));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));


		//setBackground(new Color(0xccccff));
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		//setSize(model.getAudiodataViewWidth(),getHeight());
		//scrollbarWave.setBounds(scrollbarWave.getLocation().x,scrollbarWave.getLocation().y,getScrollbarWidth(),scrollbarWave.getHeight());
		//scrollbarZoom.setBounds(scrollbarZoom.getLocation().x,scrollbarZoom.getLocation().y,getScrollbarWidth(),scrollbarZoom.getHeight());
	}

	public void updateModelChanges(Object object) {
		if(object==SimpleEvents.MODEL_ZOOMRANGECHANGED) {
			refresh();
			scrollbarZoom.setValue(model.getZoom());
		}else if(object==SimpleEvents.MODEL_ZOOMCHANGED) {
			scrollbarZoom.setValue(model.getZoom());
		}
	}

	public void refresh() {
		if(!model.hasAudiodata()) {
			scrollbarWave.setEnabled(false);
			scrollbarZoom.setEnabled(false);
			return;
		}
		int viewStartInSamples=model.getViewStartInSamples();
		int viewEndInSamples=model.getViewEndInSamples();
		int delta=viewEndInSamples-viewStartInSamples;
		scrollbarWave.setMinimum(0);
		scrollbarWave.setMaximum(model.getAudiodataLengthInSamples()-delta);

		int audioViewWidth=model.getAudiodataViewWidth();
		double step=(double)(viewEndInSamples-viewStartInSamples)/(double)audioViewWidth;

		scrollbarWave.setBlockIncrement(((int)step)<<4);
		scrollbarWave.setUnitIncrement(((int)step)<<4);
		scrollbarWave.setValue(viewStartInSamples);

		scrollbarWave.setEnabled(true);
		scrollbarZoom.setEnabled(true);
	}

}
