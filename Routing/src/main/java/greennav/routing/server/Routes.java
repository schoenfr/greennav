package greennav.routing.server;

import greennav.routing.data.vehicle.Vehicle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.gson.Gson;

@Controller
@RequestMapping(value = "/greennav")
public class Routes {

	/**
	 * This is an automatically instantiated GreenNav model realizing the model
	 * facade.
	 */
	@Autowired
	private Server greennav;

	/**
	 * Gson is used to convert java objects to Json objects.
	 */
	private Gson gson = new Gson();

	/**
	 * The routing function computes an optimal path for a particular vehicle.
	 * It is one of the two main features of GreenNav besides range
	 * computations.
	 * 
	 * @param vehicle
	 *            The vehicle's name.
	 * @param battery
	 *            The battery state in percentage from 0 to 100.
	 * @param from
	 *            The ID of the start vertex.
	 * @param to
	 *            The ID of the destination vertex.
	 * @param optimization
	 *            The optimization criteria, either 'distance', 'time' or
	 *            'energy'.
	 * @return An optimal route.
	 * @throws ComputationException
	 */
	@RequestMapping(value = "/vehicles/{vehicle}/routes/{from}-{to}/opt/{for}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String route(
			@PathVariable("vehicle") String vehicle,
			@PathVariable("from") long from,
			@PathVariable("to") long to,
			@PathVariable("for") String optimization,
			@RequestParam(value = "battery", defaultValue = "100") double battery,
			@RequestParam(value = "algorithm", required = false) String algorithm,
			@RequestParam(value = "turns", defaultValue = "false") boolean turns) {
		if (algorithm == null || algorithm.isEmpty())
			algorithm = "EnergyAStar-wgp-" + optimization;
		Vehicle v = new Vehicle(greennav.getVehicleTypeList()
				.getVehicleTypeByName(vehicle), 0);
		return gson.toJson(greennav.route(v, battery, from, to, optimization,
				algorithm, turns));
	}

}
