package org.greennavigation.utils;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * Utility class for methods used in the application.
 */
public class Util {
	/*
	 * Converts java.util.Collection into an array.
	 */
	public static <T> T[] collectionToArray(Class<T[]> clazz, Collection<T> collection) {
		T[] result = null;
		if (collection != null) {
			result = clazz.cast(Array.newInstance(clazz.getComponentType( ), collection.size()));
			if (collection != null) {
				int i = 0;
				for (T geopoint : collection) {
					result[i] = geopoint;
					i++;
				}
			}
		}

		return result;
	}
}
