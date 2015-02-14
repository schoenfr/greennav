package greennav.routing.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(value = "/greennav")
public class Map {

	/**
	 * This is an automatically instantiated GreenNav model realizing the model
	 * facade.
	 */
	@Autowired
	private Server greennav;

	/**
	 * Find nearest vertex to given coordinates and return its ID. The regular
	 * expression for the last parameter (lon) is a hack for not truncating the
	 * "file extension", which in this case would be significant digits.
	 * 
	 * @param lat
	 *            Latitude.
	 * @param lon
	 *            Longitude.
	 * @return The ID of the vertex, or -1 if no vertex was found.
	 */
	@RequestMapping(value = "/vertices/nearest", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public long getNearestVertex(@RequestParam("lat") double lat,
			@RequestParam("lon") double lon) {
		long id = greennav.vertex(lat, lon);
		if (id < 0)
			throw new ResourceNotFoundException("No vertex near (" + lat + ", "
					+ lon + ") could be found.");
		return id;
	}
}
