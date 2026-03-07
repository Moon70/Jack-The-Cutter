package lunartools.audiocutter.core.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Objects;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import lunartools.audiocutter.common.model.SimpleEvents;
import lunartools.audiocutter.core.AudioCutterModel;

public class ScrollbarsPanel extends JPanel{
	private final AudioCutterModel audioCutterModel;
	private final JScrollBar scrollbarMove;
	private final JScrollBar scrollbarZoom;
	private final Dimension SCROLLBARSPANEL_DIMENSION=new Dimension(Integer.MAX_VALUE, 50);

	public ScrollbarsPanel(AudioCutterModel audioCutterModel) {
		this.audioCutterModel=Objects.requireNonNull(audioCutterModel);
		this.setLayout(new GridBagLayout());

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints.anchor = GridBagConstraints.WEST;

		JLabel labelMove=new JLabel("Move");
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		add(labelMove, gridBagConstraints);

		scrollbarMove=new JScrollBar(JScrollBar.HORIZONTAL);
		scrollbarMove.setMinimum(0);
		scrollbarMove.addAdjustmentListener(e -> {
			int value=scrollbarMove.getValue();
			int viewStartInSamples=audioCutterModel.getViewStartInSamples();
			int viewEndInSamples=audioCutterModel.getViewEndInSamples();
			int delta=viewEndInSamples-viewStartInSamples;
			audioCutterModel.setViewRangeInSamples(value, value+delta);
		});
		gridBagConstraints.gridx = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(scrollbarMove, gridBagConstraints);


		JLabel labelZoom=new JLabel("Zoom");
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		add(labelZoom, gridBagConstraints);

		scrollbarZoom=new JScrollBar(JScrollBar.HORIZONTAL);
		scrollbarZoom.setMinimum(AudioCutterModel.ZOOM_MIN);
		scrollbarZoom.setMaximum(AudioCutterModel.ZOOM_MAX);
		scrollbarZoom.setVisibleAmount(1);
		scrollbarZoom.addAdjustmentListener(e -> {
			audioCutterModel.setZoom(e.getValue());
		});
		gridBagConstraints.gridx = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(scrollbarZoom, gridBagConstraints);

		//setAlignmentX(Component.LEFT_ALIGNMENT);
		//setPreferredSize(new Dimension(1000, 50));
		setMaximumSize(SCROLLBARSPANEL_DIMENSION);

		refresh();
		audioCutterModel.addChangeListener(this::updateModelChanges);

		//setBackground(new Color(0xccccff));
	}

	public void updateModelChanges(Object object) {
		if(object==SimpleEvents.MODEL_AUDIODATACHANGED) {
			refresh();
		}else if(object==SimpleEvents.MODEL_ZOOMRANGECHANGED) {
			refresh();
			scrollbarZoom.setValue(audioCutterModel.getZoom());
		}else if(object==SimpleEvents.MODEL_ZOOMCHANGED) {
			scrollbarZoom.setValue(audioCutterModel.getZoom());
		}
	}

	public void refresh() {
		if(!audioCutterModel.hasAudiodata()) {
			scrollbarMove.setEnabled(false);
			scrollbarZoom.setEnabled(false);
			return;
		}
		scrollbarMove.setEnabled(true);
		scrollbarZoom.setEnabled(true);

		int viewStartInSamples=audioCutterModel.getViewStartInSamples();
		int viewEndInSamples=audioCutterModel.getViewEndInSamples();
		int deltaViewInSamples=viewEndInSamples-viewStartInSamples;

		scrollbarMove.setBlockIncrement(deltaViewInSamples>>1);
		scrollbarMove.setUnitIncrement(deltaViewInSamples>>3);

		scrollbarMove.setValues(
				viewStartInSamples,   // value
			    deltaViewInSamples,  // extent
			    0,   // minimum
			    audioCutterModel.getAudiodataLengthInSamples()  // maximum
			);
	}

}
