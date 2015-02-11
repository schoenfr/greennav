package greennav.visualization;

import greennav.model.computations.ComputationManager;
import greennav.model.data.DataManager;
import greennav.model.data.GraphManager;
import greennav.visualization.controller.Controller;
import greennav.visualization.model.Model;
import greennav.visualization.view.View;

/**
 * This is the main class of the visualization project for starting up the
 * model-view-controller pattern.
 */
public class Visualization {

	public static void main(String[] args) throws Exception {

		// Create a view
		View view = new View();

		// Create a model
		Model model = new Model();

		// Connect view with model
		view.setModel(model);

		// Set loading status
		view.getStatusBarThread().start();
		view.getStatusBarThread().setStatus("Loading Data", true);

		// Load data and create computation manager
		DataManager dataManager = new DataManager(new GraphManager());
		ComputationManager computationManager = new ComputationManager(
				dataManager);

		// Connect model with computation manager
		model.setComputationManager(computationManager);

		// Stop loading status
		view.getStatusBarThread().interrupt();

		// Instantiate a controller
		new Controller(model, view);
	}

}
