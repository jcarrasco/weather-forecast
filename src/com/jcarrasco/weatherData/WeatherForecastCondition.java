package com.jcarrasco.weatherData;

/**
 * Holds the information between the <forecast_conditions>-tag of what the
 * Google Weather API returned.
 */
public class WeatherForecastCondition {

	// ===========================================================
	// Fields
	// ===========================================================

	private static final int FILE_EXTENSION = 4;
	
	private String dayofWeek = null;
	private int tempMin = 0;
	private int tempMax = 0;
	private String iconURL = null;
	private String condition = null;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public String getDayofWeek() {
		return dayofWeek;
	}

	public void setDayofWeek(String dayofWeek) {
		this.dayofWeek = dayofWeek;
	}
	
	public int getTempMin() {
		return tempMin;
	}

	public void setTempMin(int tempMin) {
		this.tempMin = tempMin;
	}

	public int getTempMax() {
		return tempMax;
	}

	public void setTempMax(int tempMax) {
		this.tempMax = tempMax;
	}
	
	public String getIconURL() {
		return iconURL;
	}

	public void setIconURL(String iconURL) {
		this.iconURL = iconURL;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
		
	// ===========================================================
	// Methods
	// ===========================================================
	public String getIconName() {
		return iconURL.substring(iconURL.lastIndexOf("/") + 1, iconURL.length() - FILE_EXTENSION);
	}
}