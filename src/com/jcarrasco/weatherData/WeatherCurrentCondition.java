package com.jcarrasco.weatherData;

import java.util.Date;

import android.util.Log;

/**
 * Holds the information between the <current_conditions>-tag of what the Google
 * Weather API returned.
 */
public class WeatherCurrentCondition {

	// ===========================================================
	// Fields
	// ===========================================================

	private static final int FILE_EXTENSION = 4;
	private String place = null;
	private String tempCelcius = null;
	private String tempFahrenheit = null;
	private String iconURL = null;
	private String condition = null;
	private String windCondition = null;
	private String humidity = null;
	private Date sunSet = null;
	private Date sunRise = null;
	
	private final static String LOG_TITLE = "Weather widget";

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public String getTempCelcius() {
		return tempCelcius;
	}

	public void setTempCelcius(String tempCelcius) {
		this.tempCelcius = tempCelcius;
	}

	public String getTempFahrenheit() {
		return tempFahrenheit;
	}

	public void setTempFahrenheit(String tempFahrenheit) {
		this.tempFahrenheit = tempFahrenheit;
	}

	/**
	 * @return the place
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * @param place
	 *            the place to set
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	public String getIconURL() {
		return this.iconURL;
	}

	public void setIconURL(String iconURL) {
		this.iconURL = iconURL;
	}

	public String getCondition() {
		return this.condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getWindCondition() {
		return this.windCondition;
	}

	public void setWindCondition(String windCondition) {
		this.windCondition = onlyData(windCondition);
	}

	public String getHumidity() {
		return this.humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = onlyData(humidity);
	}

	public Date getSunSet() {
		return sunSet;
	}

	public void setSunSet(Date sunSet) {
		this.sunSet = sunSet;
	}

	public Date getSunRise() {
		return sunRise;
	}

	public void setSunRise(Date sunRise) {
		this.sunRise = sunRise;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	public String getIconName() {
		try {
			return iconURL.substring(iconURL.lastIndexOf("/") + 1,
					iconURL.length() - FILE_EXTENSION);
		} catch (StringIndexOutOfBoundsException e) {
			Log.e(LOG_TITLE, "getIconName: " + iconURL);
			return "no_disponible";
		}
	}

	private String onlyData(String data) {
		if(!data.equals(""))
			return (String) data.subSequence(data.indexOf(": ") + 2, data.length());
		else
			return "";
	}
}
