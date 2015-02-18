package greennav.routing.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.gson.Gson;

@Controller
@RequestMapping(value = "/greennav")
public class Vehicles {

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

	@RequestMapping(value = "/vehicles")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String getVehicleTypeListNames() {
		return gson.toJson(greennav.getVehicleTypeList().getNames());
	}

	@RequestMapping(value = "/vehicles/properties")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String getVehicleTypeList() {
		return gson.toJson(greennav.getVehicleTypeList());
	}

	@RequestMapping(value = "/vehicles/{vehicle}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String getVehicleType(@PathVariable("vehicle") String vehicle) {
		return gson.toJson(greennav.getVehicleTypeList().getVehicleTypeByName(
				vehicle));
	}
}
