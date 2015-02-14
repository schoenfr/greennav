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
public class Ranges {

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
	 * The range function computes a range prediction for a particular vehicle.
	 * It is one of the two main features of GreenNav besides routing
	 * computations.
	 * 
	 * @param vehicle
	 *            The vehicle's name.
	 * @param battery
	 *            The battery state in percentage from 0 to 100.
	 * @param from
	 *            The ID of the start vertex.
	 * @return Range prediction.
	 * @throws ComputationException
	 */
	@RequestMapping(value = "vehicles/{vehicle}/ranges/{from}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String range(@PathVariable("vehicle") String vehicle,
			@RequestParam("battery") double battery,
			@PathVariable("from") long from) {
		Vehicle v = new Vehicle(greennav.getVehicleTypeList()
				.getVehicleTypeByName(vehicle), 0);
		return gson.toJson(greennav.range(v, battery, from));
	}
}
