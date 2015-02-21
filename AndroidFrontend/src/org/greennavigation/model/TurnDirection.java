package org.greennavigation.model;

import java.util.Locale;

/**
 * This enum provides values for a turn's direction.
 */
public enum TurnDirection {
	LEFT("left"), RIGHT("right"), STRAIGHT("straight");

	/**
	 * A String-representation of the TurnDirection
	 */
	private String representation;

	private TurnDirection(String representation) {
		this.representation = representation;
	}

	/**
	 * This method returns the appropriate TurnDirection matching the given
	 * String.
	 * 
	 * @param txt
	 *            the name of a TurnDirection-constant
	 * @return the TurnDirection corresponding to the given String
	 */
	public static TurnDirection parseTurnDirection(String txt) {
		txt = txt.replace(' ', '_');
		return TurnDirection.valueOf(txt.toUpperCase(Locale.US));
	}

	@Override
	public String toString() {
		return representation;
	}
	
	

}
