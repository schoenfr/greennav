package greennav.visualization.controller;

import greennav.visualization.view.View;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * ChangeListener to perform the change event for the show route check box.
 * Actually, this has nothing to do with the model, this controller is only used
 * to hide or show some information, namely the current route.
 */
public class ShowRouteChangeListener implements ItemListener {

	/**
	 * The view.
	 */
	private View view;

	/**
	 * The constructor registers itself to the corresponding check box within
	 * the view.
	 * 
	 * @param parent
	 *            The parent controller containing a reference to the view.
	 */
	public ShowRouteChangeListener(Controller parent) {
		view = parent.getView();
		view.getObservationPanel().getShowRouteCheckBox().addItemListener(this);
	}

	/**
	 * If the state of the check box changes, the route is either shown or
	 * hidden.
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		view.getObservationMap().showRoute(
				e.getStateChange() == ItemEvent.SELECTED);
	}
}
