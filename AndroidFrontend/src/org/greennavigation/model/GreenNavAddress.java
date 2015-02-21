package org.greennavigation.model;

import org.mapsforge.core.GeoPoint;

import android.util.Log;

/**
 * this class presents a Address model.
 * 
 */
public class GreenNavAddress extends GeoPoint {

	private String inhabitant;
	public String postcode;
	public String country;
	public String houseNumber;
	public String street;
	private String city;
	private String federalLand;

	private boolean isEmpty;

	/**
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public GreenNavAddress(double latitude, double longitude) {
		super(latitude, longitude);
		this.setEmpty(true);
	}

	public GreenNavAddress() {
		this(0, 0);
		this.setEmpty(true);
	}

	/**
	 * 
	 * @param latitude
	 * @param longitude
	 * @param street
	 * @param houseNumber
	 * @param city
	 * @param postcode
	 * @param country
	 */
	public GreenNavAddress(double latitude, double longitude, String street,
			String houseNumber, String city, String postcode, String country) {
		this(latitude, longitude);
		setStreet(street);
		setCity(city);
		setHouseNumber(houseNumber);
		setPostcode(postcode);
		setCountry(country);

		this.setEmpty(false);
	}

	/**
	 * 
	 * @param latitude
	 * @param longitude
	 * @param street
	 * @param houseNumber
	 * @param city
	 * @param postcode
	 * @param country
	 * @param federalLand
	 */
	public GreenNavAddress(double latitude, double longitude, String street,
			String houseNumber, String city, String postcode, String country,
			String federalLand) {
		this(latitude, longitude, street, houseNumber, city, postcode, country);
		setFederalLand(federalLand);
		this.setEmpty(false);
	}

	/**
	 * 
	 * @param latitude
	 * @param longitude
	 * @param street
	 * @param houseNumber
	 * @param city
	 * @param postcode
	 * @param country
	 * @param federalLand
	 * @param inhabitant
	 */
	public GreenNavAddress(double latitude, double longitude, String street,
			String houseNumber, String city, String postcode, String country,
			String federalLand, String inhabitant) {
		this(latitude, longitude, street, houseNumber, city, postcode, country,
				federalLand);
		setInhabitant(inhabitant);
		this.setEmpty(false);
	}

	/**
	 * 
	 * @param street
	 * @param houseNumber
	 * @param postcode
	 * @param city
	 * @param country
	 * @param federalLand
	 * @param inhabitant
	 */
	public GreenNavAddress(String street, String houseNumber, String postcode,
			String city, String country, String federalLand, String inhabitant) {
		this(0, 0, street, houseNumber, postcode, city, country, federalLand);
		setInhabitant(inhabitant);
		this.isEmpty = false;
	}

	/**
	 * @param street
	 *            the street to set
	 */
	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * @return the street
	 */
	public String getStreet() {
		return this.street;
	}

	/**
	 * @param houseNumber
	 *            the houseNumber to set
	 */
	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	/**
	 * @return the houseNumber
	 */
	public String getHouseNumber() {
		return this.houseNumber;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return this.country;
	}

	/**
	 * @param postcode
	 *            the postcode to set
	 */
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	/**
	 * @return the postcode
	 */
	public String getPostcode() {
		return this.postcode;
	}

	/**
	 * @param federalLand
	 *            the federalLand to set
	 */
	public void setFederalLand(String federalLand) {
		this.federalLand = federalLand;
	}

	/**
	 * @return the federalLand
	 */
	public String getFederalLand() {
		return this.federalLand;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	public GeoPoint getGeoPoint() {
		return new GeoPoint(this.getLatitude(), this.getLongitude());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (street != null) {
			sb.append(street);
			if (houseNumber != null) {
				sb.append(" ");
				sb.append(houseNumber);
			}
			sb.append(", ");
		}
		if (city != null) {
			sb.append(city);
			sb.append(", ");
		}
		if (postcode != null) {
			sb.append(postcode);
			sb.append(", ");
		}
		if (federalLand != null) {
			sb.append(federalLand);
			sb.append(", ");
		}
		if (country != null) {
			sb.append(country);
		}
		String string = sb.toString();
		try {
			string = string.replaceAll("null", "");
		} catch (NullPointerException e) {
			Log.e(getClass().getSimpleName(), string);

		}

		return string;
	}

	public String getAddresseString() {

		StringBuilder sb = new StringBuilder();
		if (street != null) {
			sb.append(street);
			if (houseNumber != null) {
				sb.append(" , ");
				sb.append(houseNumber);
			}
			sb.append(" ");
		}
		if (city != null) {
			sb.append(city);
			sb.append(" , ");
		}
		if (postcode != null) {
			sb.append(postcode);
			sb.append(" ");
		}
		if (federalLand != null) {
			sb.append(federalLand);
			sb.append(" ");
		}
		if (country != null) {
			sb.append(country);
		}
		String string = sb.toString();
		try {
			string = string.replaceAll("null", "");
		} catch (NullPointerException e) {
			Log.e(getClass().getSimpleName(), string);

		}

		return string;
	}

	/**
	 * @param inhabitant
	 *            the inhabitant to set
	 */
	public void setInhabitant(String inhabitant) {
		this.inhabitant = inhabitant;
	}

	/**
	 * @return the inhabitant
	 */
	public String getInhabitant() {
		return inhabitant;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	private void setEmpty(boolean b) {
		this.isEmpty = b;

	}

}
