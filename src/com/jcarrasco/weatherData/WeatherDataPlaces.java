package com.jcarrasco.weatherData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

public class WeatherDataPlaces {
	static Vector<WeatherData> datos = new Vector<WeatherData>();
	static boolean actual_position_insert = false;

	public static void addActualPlace(WeatherData data) {
		// La posicion 0 siempre es para el lugar localizado, si no es la
		// primera vez que se inserta en la pos 0, se elimina el elemento y se
		// inserta
		if (true == actual_position_insert)
			datos.remove(0);
		else
			actual_position_insert = true;

		datos.add(0, data);
	}

	public static void addPlace(WeatherData data) {
		boolean itemFound = false;

		// Comprueba que no existe ya el nuevo lugar en el vector
		for (int i = 0; i < datos.size(); i++) {
			if (datos.get(i).getWcc().getPlace()
					.equals(data.getWcc().getPlace())) {
				itemFound = true;
				i = datos.size();
			}
		}

		// En caso de que no exista anteriormente, lo añade
		if (itemFound == false)
			datos.add(data);
	}

	public static void removePlace(String place) {
		for (int i = 0; i < datos.size(); i++) {
			if (datos.get(i).getWcc().getPlace().equals(place)) {
				datos.remove(i);
				i = datos.size();
			}
		}
	}

	public static WeatherData getPlace(int position) {
		return datos.get(position);
	}

	public static ArrayList<String> getPlacesList() {
		String placesList[] = new String[datos.size()];
		ArrayList<String> alPlacesList = new ArrayList<String>();

		for (int i = 0; i < datos.size(); i++) {
			placesList[i] = datos.get(i).getWcc().getPlace();
		}

		// Copy array to arrayList
		Collections.addAll(alPlacesList, placesList);

		return alPlacesList;
	}

	public static int getSize() {
		return datos.size();
	}
}
