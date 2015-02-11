package greennav.visualization.controller;

import greennav.mapviewer.controller.DefaultMapController;
import greennav.mapviewer.structures.XY;
import greennav.model.data.structs.ENGVertex;
import greennav.osmosis.structs.LatLon;

import java.awt.event.MouseEvent;
import java.util.Locale;

/**
 * This map controller is built upon the default map controller for manipulating
 * the map, but it additionally adds the functionality of choosing start or
 * destination vertices.
 */
public class MapController extends DefaultMapController {

	/**
	 * Reference to the parent controller.
	 */
	private Controller parent;

	/**
	 * The constructor automatically registers itself to the map viewer
	 * component.
	 * 
	 * @param parent
	 *            The map viewer component to control.
	 */
	public MapController(Controller parent) {
		super(parent.getView().getObservationMap());
		this.parent = parent;
	}

	private String coordinateToText(LatLon request) {
		return String.format(Locale.ENGLISH, "%.4f, %.4f",
				request.getLatitude(), request.getLongitude());
	}

	/**
	 * On a mouse click, choose (if flags are set to true) either the start or
	 * the destination vertex (or none). Also write the chosen coordinates to
	 * the corresponding text fields.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		LatLon request = parent.getView().getObservationMap().getMercator()
				.mousePointToLatlon(new XY(e.getPoint()));
		ENGVertex vertex = parent.getModel().getDataManager().getGraphManager()
				.getNodeMatcher().getNearestVertex(request);
		if (vertex != null) {
			if (parent.isChoosingStart()) {
				parent.getModel().setStart(vertex);
				parent.setChoosingStart(false);
				parent.getView().getPreparationPanel().getChooseStartField()
						.setText(coordinateToText(request));
			} else if (parent.isChoosingDestination()) {
				parent.getModel().setDestination(vertex);
				parent.setChoosingDestination(false);
				parent.getView().getPreparationPanel()
						.getChooseDestinationField()
						.setText(coordinateToText(request));
			} else {
				super.mouseClicked(e);
			}
		} else {
			parent.getView().setStatus(
					"Invalid coordinates, no vertex found near coordinates "
							+ request + ".");
		}
	}
}
