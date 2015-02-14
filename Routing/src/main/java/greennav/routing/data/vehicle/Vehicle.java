package greennav.routing.data.vehicle;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "vehicle")
public class Vehicle implements Serializable {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 9208556281102675611L;

	/**
	 * The vehicle type comprises information about technical data common to all
	 * vehicles of that type.
	 */
	private VehicleType type;

	/**
	 * Weight of the additional payload (passengers, baggage).
	 */
	private double payload = 0;

	/**
	 * Subtracts the default payload and adds the dynamically given real payload
	 * to the whole weight of the vehicle.
	 * 
	 * @param payload
	 *            The dynamic payload in kg
	 */
	public Vehicle(VehicleType type, double payload) {
		this.type = type;
		this.payload = payload;
	}

	public void setPayload(double payload) {
		this.payload = payload;
	}

	public double getPayload() {
		return payload;
	}

	public double getTotalWeight() {
		return type.getEmptyWeight() + payload;
	}

	public VehicleType getType() {
		return type;
	}

	// /**
	// * Modifies the vehicle attributes by current weather situation
	// */
	// public void modifyByWeather() {
	// // get current temperature
	// Amount<Temperature> currentTemp = Environment.currentTemp; // in Celsius
	// // modify C
	// double capacityRatio = 1.0; // in %
	// double powerRatio = 1.0; // in %
	// // TODO: remove constants, what are the references?
	// if (currentTemp.isLessThan(Amount.valueOf(15, SI.CELSIUS))) {
	// capacityRatio = 0.01 * currentTemp.doubleValue(SI.CELSIUS) + 0.85;
	// powerRatio = -0.02 * currentTemp.doubleValue(SI.CELSIUS) + 1.30;
	// }
	// C = C.times(capacityRatio);
	// auxiliaryPower = auxiliaryPower.times(powerRatio);
	// }
}
