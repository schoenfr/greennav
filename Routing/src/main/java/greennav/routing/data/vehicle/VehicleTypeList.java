package greennav.routing.data.vehicle;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class provides a manager for the Vehicle data structure. It allows other
 * classes to get all or just a certain vehicle object.
 */
@XmlRootElement
public class VehicleTypeList {

	/**
	 * List of vehicles
	 */
	private Map<String, VehicleType> types = null;

	/**
	 * Initialization method, reads the vehicle file and passes it to the
	 * parameterized version of this method
	 */
	public VehicleTypeList() throws Exception {
		types = new HashMap<String, VehicleType>();
	}

	/**
	 * The real initialization method. Loads the file specified in the
	 * parameter, extracts the vehicle information and creates the vehicle
	 * instances
	 * 
	 * @throws Exception
	 */
	public static VehicleTypeList init() throws Exception {
		VehicleTypeList list = new VehicleTypeList();
		Document doc = null;
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			doc = docBuilder.parse("vehicles.xml");
		} catch (ParserConfigurationException e) {
			throw new Exception(
					"Reading of vehicleTypes XML could no be executed (1).", e);
		} catch (IOException e) {
			throw new Exception(
					"Reading of vehicleTypes XML could no be executed (2).", e);
		}
		NodeList nodeList = doc.getElementsByTagName("vehicleType");
		for (int i = 0; i < nodeList.getLength(); i++) {
			list.add(parseVehicleFromXMLNode(nodeList.item(i)));
		}
		return list;
	}

	public void add(VehicleType type) {
		types.put(type.getName(), type);
	}

	/**
	 * Extracts a single vehicle from an XML node
	 * 
	 * @param node
	 * @param defaultPayload
	 * @return
	 */
	private static VehicleType parseVehicleFromXMLNode(Node node) {
		VehicleType car = new VehicleType();
		Element element = (Element) node;
		car.setName(getTextContent(element, "name"));
		car.setLambda(Double.parseDouble(getTextContent(element,
				"accelerationFactor")));
		car.setEmptyWeight(Double
				.parseDouble(getTextContent(element, "weight")));
		car.setCw(Double.parseDouble(getTextContent(element, "cwValue")));
		car.setvMax(Double.parseDouble(getTextContent(element, "maxVelocity")) / 3.6);
		car.setEtaMDischarge(Double.parseDouble(getTextContent(element,
				"efficiencyFactorDischarge")));
		car.setEtaMRecuperation(Double.parseDouble(getTextContent(element,
				"efficiencyFactorRecuperation")));
		car.setSurfaceA(Double.parseDouble(getTextContent(element,
				"frontSurface")));
		car.setCapacity(Double.parseDouble(getTextContent(element,
				"batteryCapacity")) * 1000 * 3600);
		car.setAuxiliaryPower(Double.parseDouble(getTextContent(element,
				"auxiliaryPower")));
		return car;
	}

	/**
	 * Returns the content of a tag specified by the tagName as string.
	 * 
	 * @param element
	 *            XML Node
	 * @param tagName
	 *            Name of the tag which contains the desired content
	 * @return Content of the tag as String
	 */
	public static String getTextContent(Element element, String tagName) {
		return element.getElementsByTagName(tagName).item(0).getTextContent();
	}

	/**
	 * Returns a vehicle instance of the car specified by the name
	 * 
	 * @param name
	 *            Name of the vehicle
	 * @return Vehicle instance with the given name or throwing an exception if
	 *         not found.
	 */
	public VehicleType getVehicleTypeByName(String name) {
		return types.get(name);
	}

	@XmlElement
	public Collection<VehicleType> getTypes() {
		return types.values();
	}

	public Collection<String> getNames() {
		return types.keySet();
	}
}
