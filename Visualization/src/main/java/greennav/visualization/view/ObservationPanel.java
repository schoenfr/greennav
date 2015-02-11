package greennav.visualization.view;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

public class ObservationPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Show the current size of the queue.
	 */
	private final JLabel queueSizeLabel = new JLabel();

	/**
	 * Show the maximum size of the queue reached so far.
	 */
	private final JLabel maxQueueSizeLabel = new JLabel();

	/**
	 * The close button stops the algorithm and switches back to preparation
	 * perspective.
	 */
	private final JButton stopVisualizationButton = new JButton(
			"Stop Visualization");

	private final JCheckBox showRouteCheckBox = new JCheckBox("Show route");
	private final JCheckBox startDestCheckBox = new JCheckBox(
			"Show start/destination");
	private final JCheckBox heightInfoCheckBox = new JCheckBox(
			"Show height information");

	public ObservationPanel(View parent) {
		super(new MigLayout("fillx"));

		add(new JLabel("Queue"), "split, span, gaptop 20");
		add(new JSeparator(), "growx, wrap, gaptop 20");

		add(new JLabel("Current size: "), "grow, gap 10");
		add(queueSizeLabel, "wrap");
		add(new JLabel("Maximum size: "), "grow, gap 10");
		add(maxQueueSizeLabel, "wrap");

		add(new JLabel("Controls"), "split, span, gaptop 20");
		add(new JSeparator(), "growx, wrap, gaptop 20");
		add(showRouteCheckBox, "span 2, grow, gap 10 10, wrap");
		add(startDestCheckBox, "span 2, grow, gap 10 10, wrap");
		add(heightInfoCheckBox, "span 2, grow, gap 10 10, wrap");
		add(stopVisualizationButton, "span 2, grow, gap 10 10");

		showRouteCheckBox.setSelected(true);
		startDestCheckBox.setSelected(true);
		heightInfoCheckBox.setSelected(false);

		showRouteCheckBox.setOpaque(false);
		startDestCheckBox.setOpaque(false);
		heightInfoCheckBox.setOpaque(false);
		setOpaque(false);
	}

	public JButton getStopVisualizationButton() {
		return stopVisualizationButton;
	}

	public JLabel getQueueSizeLabel() {
		return queueSizeLabel;
	}

	public JLabel getMaxQueueSizeLabel() {
		return maxQueueSizeLabel;
	}

	public JCheckBox getShowRouteCheckBox() {
		return showRouteCheckBox;
	}

	public JCheckBox getStartDestCheckBox() {
		return startDestCheckBox;
	}

	public JCheckBox getHeightInfoCheckBox() {
		return heightInfoCheckBox;
	}
}
