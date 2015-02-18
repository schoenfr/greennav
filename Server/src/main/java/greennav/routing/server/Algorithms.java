package greennav.routing.server;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.gson.Gson;

/**
 * This controller describes the REST API of the GreenNav web service, it uses
 * the model facade to forward requests.
 */
@Controller
@RequestMapping(value = "/greennav")
public class Algorithms {

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

	@RequestMapping(value = "/algorithms")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String getAlgorithmListNames() throws UnsupportedEncodingException {
		return gson.toJson(greennav.getAlgorithmList());
	}

	@RequestMapping(value = "/algorithms/{algorithm}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String getAlgorithm(@PathVariable("algorithm") String algorithm) {
		return gson.toJson(greennav.getAlgorithm(algorithm));
	}

}
