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
		int value=0;
		int extent=0;
		scrollbarZoom.setValues(value, extent, AudioCutterModel.ZOOM_MIN, AudioCutterModel.ZOOM_MAX);

		gridBagConstraints.gridx = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(scrollbarZoom, gridBagConstraints);

		setMaximumSize(SCROLLBARSPANEL_DIMENSION);

		updateEnabledState();

		//setBackground(new Color(0xccccff));
	}

	public void updateEnabledState() {
		if(!audioCutterModel.hasAudiodata()) {
			scrollbarMove.setEnabled(false);
			scrollbarZoom.setEnabled(false);
			return;
		}
		scrollbarMove.setEnabled(true);
		scrollbarZoom.setEnabled(true);
	}

	public JScrollBar getScrollbarMove() {
		return scrollbarMove;
	}

	public JScrollBar getScrollbarZoom() {
		return scrollbarZoom;
	}

}
