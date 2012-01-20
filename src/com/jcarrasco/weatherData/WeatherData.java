package com.jcarrasco.weatherData;

import java.util.ArrayList;

public class WeatherData {
	WeatherCurrentCondition wcc;
	ArrayList<WeatherForecastCondition> arrayListwfc;

	public WeatherCurrentCondition getWcc() {
		return wcc;
	}

	public void setWcc(WeatherCurrentCondition wcc) {
		this.wcc = wcc;
	}

	public ArrayList<WeatherForecastCondition> getArrayListwfc() {
		return arrayListwfc;
	}

	public void setArrayListwfc(ArrayList<WeatherForecastCondition> arrayListwfc) {
		this.arrayListwfc = arrayListwfc;
	}
}
