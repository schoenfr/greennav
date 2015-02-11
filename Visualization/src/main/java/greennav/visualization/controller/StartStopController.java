package greennav.visualization.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ActionListener for the "Start Visualization" button and "Stop Visualization"
 * button; on click, it will trigger the start or the stop of the routing
 * algorithm.
 */
public class StartStopController implements ActionListener {

	/**
	 * The parent controller.
	 */
	private Controller parent;

	/**
	 * The constructor takes its parent and registers itself automatically to
	 * the the corresponding start button.
	 * 
	 * @param parent
	 *            The parent controller.
	 */
	public StartStopController(Controller parent) {
		this.parent = parent;
		parent.getView().getPreparationPanel().getStartVisualizationButton()
				.addActionListener(this);
		parent.getView().getObservationPanel().getStopVisualizationButton()
				.addActionListener(this);
	}

	/**
	 * On clicking the start button, clear the view and start the routing. On
	 * clicking the stop button, stop the routing and return to the preparation
	 * state.
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource().equals(
				parent.getView().getPreparationPanel()
						.getStartVisualizationButton())) {
			parent.getView().getObservationMap().getShapes().clear();
			parent.getView().getObservationMap().getQueue().clear();
			parent.getView().getObservationMap().getQueueMirror().clear();
			parent.getView().getObservationMap().getRouteLineShapes().clear();
			parent.getModel().startRouting();
		} else if (e.getSource().equals(
				parent.getView().getObservationPanel()
						.getStopVisualizationButton())) {
			parent.getModel().stopRouting();
		}
	}
}
