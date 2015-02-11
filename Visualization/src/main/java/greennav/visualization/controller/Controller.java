package greennav.visualization.controller;

import greennav.visualization.model.Model;
import greennav.visualization.view.View;

/**
 * The main controller initializes all of its sub controllers. The sub
 * controllers may communicate with each other via this super controller. An
 * example is setting the start vertex: First, the user clicks on a button in
 * the left panel, then the user clicks on the map. Both controllers need to
 * work together in some way. This is done using a boolean value in the super
 * controller, that describes if the user wants to set a new starting vertex.
 */
public class Controller {

	/**
	 * The model comprises the data, the routing manager and the actual
	 * information determined by the observing aspects.
	 */
	private Model model;

	/**
	 * The view comprises all sub views.
	 */
	private View view;

	/**
	 * True, if the user currently wants to choose the starting vertex via mouse
	 * click on the map.Actually, if both of these values are set, then the
	 * first click is recognized as start vertex and the second click is
	 * recognized as the destination vertex.
	 */
	private boolean choosingStart = false;
	/**
	 * True, if the user currently wants to choose the destination vertex via
	 * mouse click on the map. Actually, if both of these values are set, then
	 * the first click is recognized as start vertex and the second click is
	 * recognized as the destination vertex.
	 */
	private boolean choosingDestination = false;

	/**
	 * The constructor initializes all of its sub controllers. Each sub
	 * controller then registers automatically.
	 * 
	 * @param model
	 *            The model to observe.
	 * @param view
	 *            The view comprising all of its sub views.
	 */
	public Controller(Model model, View view) {
		this.model = model;
		this.view = view;

		new MapController(this);
		new ShowRouteChangeListener(this);
		new ShowStartDestChangeListener(this);
		new StartStopController(this);
		new ChooseVertexController(this);
		new PreparationItemChangeListener(this);
	}

	/**
	 * This non-trivial setter enables or disables the text field and the button
	 * for choosing the start vertex, if the user currently wants to choose the
	 * vertex via mouse click on the map.
	 * 
	 * @param choosingStart
	 *            True, if the user wants to choose a start on the map.
	 */
	public void setChoosingStart(boolean choosingStart) {
		this.choosingStart = choosingStart;
		view.getPreparationPanel().getChooseStartButton()
				.setEnabled(!choosingStart);
		view.getPreparationPanel().getChooseStartField()
				.setEditable(!choosingStart);
		if (choosingStart) {
			view.getPreparationPanel().getChooseStartField()
					.setText("Click on map to choose");
		}
	}

	/**
	 * This non-trivial setter enables or disables the text field and the button
	 * for choosing the destination vertex, if the user currently wants to
	 * choose the vertex via mouse click on the map.
	 * 
	 * @param choosingDestination
	 *            True, if the user wants to choose a destination on the map.
	 */
	public void setChoosingDestination(boolean choosingDestination) {
		this.choosingDestination = choosingDestination;
		view.getPreparationPanel().getChooseDestinationButton()
				.setEnabled(!choosingDestination);
		view.getPreparationPanel().getChooseDestinationField()
				.setEditable(!choosingDestination);
		if (choosingDestination) {
			view.getPreparationPanel().getChooseDestinationField()
					.setText("Click on map to choose");
		}
	}

	/**
	 * True, if the user wants to choose a starting vertex.
	 * 
	 * @return Currently choosing starting vertex?
	 */
	public boolean isChoosingStart() {
		return choosingStart;
	}

	/**
	 * True, if the user wants to choose a destination vertex.
	 * 
	 * @return Currently choosing destination vertex?
	 */
	public boolean isChoosingDestination() {
		return choosingDestination;
	}

	/**
	 * Get the model.
	 * 
	 * @return The model.
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * Get the view.
	 * 
	 * @return The view.
	 */
	public View getView() {
		return view;
	}

}
