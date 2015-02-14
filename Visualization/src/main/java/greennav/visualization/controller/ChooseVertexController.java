package greennav.visualization.controller;

import greennav.osmosis.structs.LatLon;
import greennav.routing.data.Graph.Vertex;
import greennav.visualization.view.PreparationPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This controller is responsible for choosing the start and destination vertex.
 * The input is done either using text fields or by clicking on the map if
 * activated by clicking the corresponding button.
 */
public class ChooseVertexController implements ActionListener {

	/**
	 * The parent controller.
	 */
	private Controller parent;

	/**
	 * The preparation panel this controller is instantiated for.
	 */
	private PreparationPanel preparationPanel;

	/**
	 * The constructor saves its parent and automatically registers as an
	 * observer to corresponding input buttons and vields.
	 * 
	 * @param parent
	 *            The parent controller.
	 */
	public ChooseVertexController(Controller parent) {
		this.parent = parent;
		this.preparationPanel = parent.getView().getPreparationPanel();
		preparationPanel.getChooseStartButton().addActionListener(this);
		preparationPanel.getChooseDestinationButton().addActionListener(this);
		preparationPanel.getChooseStartField().addActionListener(this);
		preparationPanel.getChooseDestinationField().addActionListener(this);
	}

	/**
	 * On submitting text to the start text field, the coordinates are read and
	 * a corresponding vertex (to be set as starting vertex) is determined.
	 */
	private void actionChooseStartField() {
		String text = preparationPanel.getChooseStartField().getText();
		String[] components = text.split(",");
		if (components.length == 2 && Double.valueOf(components[0]) != null
				&& Double.valueOf(components[1]) != null) {
			double lat = Double.valueOf(components[0]);
			double lon = Double.valueOf(components[1]);
			LatLon request = new LatLon(lat, lon);
			Vertex vertex = parent.getModel().getServer().graph
					.getVertexByLatLon(lat, lon);
			if (vertex != null) {
				parent.getModel().setStart(vertex);
				parent.setChoosingStart(false);
			} else {
				parent.getView().setStatus(
						"Invalid coordinates, no vertex found near coordinates "
								+ request + ".");
			}
		} else {
			parent.getView()
					.setStatus(
							"Invalid coordinates, the format you need to use is for example \"51.056, 11.02356\".");
		}
	}

	/**
	 * On submitting text to the destination text field, the coordinates are
	 * read and a corresponding vertex (to be set as destination vertex) is
	 * determined.
	 */
	private void actionChooseDestinationField() {
		String text = preparationPanel.getChooseDestinationField().getText();
		String[] components = text.split(",");
		if (components.length == 2 && Double.valueOf(components[0]) != null
				&& Double.valueOf(components[1]) != null) {
			double lat = Double.valueOf(components[0]);
			double lon = Double.valueOf(components[1]);
			LatLon request = new LatLon(lat, lon);
			Vertex vertex = parent.getModel().getServer().graph
					.getVertexByLatLon(lat, lon);
			if (vertex != null) {
				parent.getModel().setDestination(vertex);
				parent.setChoosingDestination(false);
			} else {
				parent.getView().setStatus(
						"Invalid coordinates, no vertex found near coordinates "
								+ request + ".");
			}
		} else {
			parent.getView()
					.setStatus(
							"Invalid coordinates, the format you need to use is for example \"51.056, 11.02356\".");
		}
	}

	/**
	 * If either the button was clicked or the text in the field was submitted,
	 * this methods places either the start or the destination vertex.
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource().equals(preparationPanel.getChooseStartButton())) {
			parent.setChoosingStart(true);
		} else if (e.getSource().equals(
				preparationPanel.getChooseDestinationButton())) {
			parent.setChoosingDestination(true);
		} else if (e.getSource().equals(preparationPanel.getChooseStartField())) {
			actionChooseStartField();
		} else if (e.getSource().equals(
				preparationPanel.getChooseDestinationField())) {
			actionChooseDestinationField();
		}
	}
}
