package greennav.routing.data.vehicle;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The type of a vehicle is a description of the properties of a family of
 * vehicles.
 */
@XmlRootElement
public class VehicleType implements Serializable {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -518129363419951966L;

	/**
	 * Name of the vehicle type.
	 */
	private String name;

	/**
	 * Rotational inertia dimensionless
	 */
	private double lambda;

	/**
	 * Weight of the vehicle (excluding additional payload as passengers,
	 * baggage).
	 */
	private double emptyWeight;

	/**
	 * Drag coefficient (cw-Wert) dimensionless.
	 */
	private double cw;

	/**
	 * Max speed.
	 */
	private double vMax;

	/**
	 * Efficiency for discharge.
	 */
	private double etaMDischarge;

	/**
	 * Efficiency for recuperation.
	 */
	private double etaMRecuperation;

	/**
	 * Front surface of car.
	 */
	private double surfaceA;

	/**
	 * The battery capacity in kWh (the maximal amount which can be stored).
	 */
	private double capacity;

	/**
	 * The basis power of all auxiliary loads in the car.
	 */
	private double auxiliaryPower;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	public double getEmptyWeight() {
		return emptyWeight;
	}

	public void setEmptyWeight(double emptyWeight) {
		this.emptyWeight = emptyWeight;
	}

	public double getCw() {
		return cw;
	}

	public void setCw(double cw) {
		this.cw = cw;
	}

	public double getvMax() {
		return vMax;
	}

	public void setvMax(double vMax) {
		this.vMax = vMax;
	}

	public double getEtaMDischarge() {
		return etaMDischarge;
	}

	public void setEtaMDischarge(double etaMDischarge) {
		this.etaMDischarge = etaMDischarge;
	}

	public double getEtaMRecuperation() {
		return etaMRecuperation;
	}

	public void setEtaMRecuperation(double etaMRecuperation) {
		this.etaMRecuperation = etaMRecuperation;
	}

	public double getSurfaceA() {
		return surfaceA;
	}

	public void setSurfaceA(double surfaceA) {
		this.surfaceA = surfaceA;
	}

	/**
	 * Get the capacity of this vehicle type in kWh.
	 * 
	 * @return capacity in kWh
	 */
	public double getCapacity() {
		return capacity;
	}

	/**
	 * Set the capacity of this vehicle type in kWh.
	 * 
	 * @param capacity
	 *            in kWh
	 */
	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	public double getAuxiliaryPower() {
		return auxiliaryPower;
	}

	public void setAuxiliaryPower(double auxiliaryPower) {
		this.auxiliaryPower = auxiliaryPower;
	}

	/**
	 * Naive implementation of an equals-method. Compares only the name of the
	 * vehicles.
	 */
	@Override
	public boolean equals(Object other) {
		return other != null && other instanceof VehicleType
				&& this.name.equals(((VehicleType) other).name);
	}
}
