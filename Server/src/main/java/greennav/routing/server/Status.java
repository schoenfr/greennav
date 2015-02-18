package greennav.routing.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class Status {

	/**
	 * This is an automatically instantiated GreenNav model realizing the model
	 * facade.
	 */
	@Autowired
	private Server greennav;

	@RequestMapping(value = "/greennav", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String introduction() {
		return "<html>" + "<head>"
				+ "<title>GreenNav - RESTful Web Service</title>" + "</head>"
				+ "<body>" + "<h3>GreenNav - RESTful Web Service</h3>"
				+ greennav.getStatus() + "</body>" + "</html>";
	}
}
