package lunartools.audiocutter.gui.scrollbarspanel;

import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import lunartools.audiocutter.AudioCutterModel;

public class ScrollbarAdjustmentListener implements AdjustmentListener{
	private AudioCutterModel model;
	private ScrollbarsPanel view;

	public ScrollbarAdjustmentListener(AudioCutterModel model,ScrollbarsPanel view) {
		this.model=model;
		this.view=view;
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		Scrollbar scrollbar=(Scrollbar)e.getSource();
		if(scrollbar==view.scrollbarWave) {
			int value=scrollbar.getValue();
			int viewStartInSamples=model.getViewStartInSamples();
			int viewEndInSamples=model.getViewEndInSamples();
			int delta=viewEndInSamples-viewStartInSamples;
			model.setViewRangeInSamples(value, value+delta);
		}else if(scrollbar==view.scrollbarZoom) {
			model.setZoom(e.getValue());
		}
	}

}
