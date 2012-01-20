package com.jcarrasco.weather;

import com.jcarrasco.weatherData.WeatherDataPlaces;

import android.content.Context;
import android.content.SharedPreferences;

public class XmlPlaces {
	static private XmlPlaces xmlPlaces = null;
	private SharedPreferences sharedPreferences;

	private XmlPlaces(Context context) {
		sharedPreferences = context.getSharedPreferences("places", 0);
	}

	static public XmlPlaces getXmlPlaces(Context context) {

		if (xmlPlaces == null) {
			xmlPlaces = new XmlPlaces(context);
		}

		return xmlPlaces;
	}

	public void addPlace(String place, int position) {
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putInt("TotalPlaces", position);
		editor.putString(String.valueOf(position), place);

		editor.commit();
	}

	public void removePlace(String place, int position) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		
		editor.putInt("TotalPlaces", getTotalPlaces() - 1);
		editor.remove(String.valueOf(position));

		editor.commit();

		WeatherDataPlaces.removePlace(place);
	}

	public void clearPlaces() {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.clear();

		editor.commit();
	}

	public String getPlace(String position) {
		return sharedPreferences.getString(position, "");
	}

	public int getTotalPlaces() {
		return sharedPreferences.getInt("TotalPlaces", 0);
	}
}
