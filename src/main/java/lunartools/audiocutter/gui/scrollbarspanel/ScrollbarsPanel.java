package lunartools.audiocutter.gui.scrollbarspanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lunartools.audiocutter.AudioCutterModel;
import lunartools.audiocutter.SimpleEvents;

public class ScrollbarsPanel extends JPanel implements Observer{
	private static Logger logger = LoggerFactory.getLogger(ScrollbarsPanel.class);
	private AudioCutterModel model;

	public Scrollbar scrollbarWave;
	public Scrollbar scrollbarZoom;

	private int marginX=4;
	private int marginY=4;
	private int lineHeight=18;
	private int column1X=10;
	private int column1Width=35;
	private int scrollbarX=column1X+column1Width+marginX;

	public ScrollbarsPanel(AudioCutterModel model) {
		this.model=model;
		model.addObserver(this);
		this.setLayout(null);

		AdjustmentListener adjustmentlistener=new ScrollbarAdjustmentListener(model,this);

		int y=marginY;
		int scrollbarWidth=getScrollbarWidth();

		JLabel labelMove=new JLabel("Move");
		labelMove.setBounds(column1X,y,column1Width,lineHeight);
		add(labelMove);

		scrollbarWave=new Scrollbar(Scrollbar.HORIZONTAL);
		scrollbarWave.setBounds(scrollbarX,y,scrollbarWidth,lineHeight);
		scrollbarWave.setBackground(Color.DARK_GRAY);
		scrollbarWave.addAdjustmentListener(adjustmentlistener);
		scrollbarWave.setEnabled(false);
		add(scrollbarWave);

		y+=lineHeight+marginY;

		JLabel labelZoom=new JLabel("Zoom");
		labelZoom.setBounds(column1X,y,column1Width,lineHeight);
		add(labelZoom);

		scrollbarZoom=new Scrollbar(Scrollbar.HORIZONTAL,0,1,0,101);
		scrollbarZoom.setBounds(scrollbarX,y,scrollbarWidth,lineHeight);
		scrollbarZoom.setBackground(Color.DARK_GRAY);
		scrollbarZoom.addAdjustmentListener(adjustmentlistener);
		scrollbarZoom.setEnabled(false);
		add(scrollbarZoom);

		y+=lineHeight+marginY;

		Dimension size=new Dimension(model.getAudiodataViewWidth(),y);
		setSize(size);

		//setBackground(new Color(0xffffcc));
	}

	private int getScrollbarWidth() {
		int viewWidth=model.getAudiodataViewWidth();
		int width=viewWidth-scrollbarX-marginX;
		return width;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		setSize(model.getAudiodataViewWidth(),getHeight());
		scrollbarWave.setBounds(scrollbarWave.getLocation().x,scrollbarWave.getLocation().y,getScrollbarWidth(),scrollbarWave.getHeight());
		scrollbarZoom.setBounds(scrollbarZoom.getLocation().x,scrollbarZoom.getLocation().y,getScrollbarWidth(),scrollbarZoom.getHeight());
	}

	@Override
	public void update(Observable observable, Object object) {
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
