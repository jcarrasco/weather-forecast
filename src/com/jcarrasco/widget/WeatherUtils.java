package com.jcarrasco.widget;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jcarrasco.simpleWeatherforecast.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

public class WeatherUtils implements iGlobals {
	public static boolean xmlDataCelsious = true;
	public static boolean isCelsious = false;
	private static int timeToUpdate = 0;
	private static String color = "";

	public static int getColor() {
		int _color = Color.rgb(Integer.valueOf(color.substring(1, 3), 16),
				Integer.valueOf(color.substring(3, 5), 16),
				Integer.valueOf(color.substring(5, 7), 16));

		return _color;
	}

	public static int getTimeToUpdate() {
		return timeToUpdate;
	}

	private static void setTimeToUpdate(int _timeToUpdate) {
		WeatherUtils.timeToUpdate = _timeToUpdate;
	}

	public static String getTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		Date date = new Date();

		return dateFormat.format(date);
	}

	private static SharedPreferences getPreferences(Context context) {
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		return prefs;
		// /data/data/com.jcarrasco.weatherWidget/shared_prefs/com.jcarrasco.weatherWidget_preferences.xml
	}

	/** Useful Utility in working with temperatures. (conversions). */
	public static int fahrenheitToCelsius(int tFahrenheit) {
		return (int) ((5.0f / 9.0f) * (tFahrenheit - 32));
	}

	public static int celsiusToFahrenheit(int tCelsius) {
		return (int) ((9.0f / 5.0f) * tCelsius + 32);
	}

	public static int getIconNight(String ico) {
		if (ico.equals("chance_of_rain"))
			return R.drawable.luna_chance_of_rain;
		if (ico.equals("chance_of_snow"))
			return R.drawable.luna_chance_of_snow;
		if (ico.equals("chance_of_storm"))
			return R.drawable.luna_chance_of_storm;
		if (ico.equals("chance_of_tstorm"))
			return R.drawable.luna_chance_of_tstorm;
		if (ico.equals("sunny"))
			return R.drawable.luna_despejado;
		if (ico.equals("dust"))
			return R.drawable.dust;
		if (ico.equals("rain"))
			return R.drawable.rain;
		if (ico.equals("mist"))
			return R.drawable.chance_of_rain;
		if (ico.equals("fog"))
			return R.drawable.fog;
		if (ico.equals("cloudy") || ico.equals("mostly_sunny")
				|| ico.equals("partly_cloudy"))
			return R.drawable.luna_nubes1;
		if (ico.equals("mostly_cloudy"))
			return R.drawable.luna_nubes;
		if (ico.equals("sleet"))
			return R.drawable.sleet;

		return R.drawable.no_disponible;
	}

	public static int getIconDay(String ico) {
		if (ico.equals("chance_of_rain"))
			return R.drawable.chance_of_rain;
		if (ico.equals("chance_of_snow"))
			return R.drawable.chance_of_snow;
		if (ico.equals("chance_of_storm"))
			return R.drawable.chance_of_tstorm;
		if (ico.equals("chance_of_tstorm"))
			return R.drawable.chance_of_tstorm;
		if (ico.equals("cloudy"))
			return R.drawable.cloudy;
		if (ico.equals("dust"))
			return R.drawable.dust;
		if (ico.equals("fog"))
			return R.drawable.fog;
		if (ico.equals("icy"))
			return R.drawable.icy;
		if (ico.equals("mist"))
			return R.drawable.chance_of_rain;
		if (ico.equals("mostly_cloudy"))
			return R.drawable.mostly_cloudy;
		if (ico.equals("mostly_sunny"))
			return R.drawable.mostly_sunny;
		if (ico.equals("partly_cloudy"))
			return R.drawable.partly_cloudy;
		if (ico.equals("rain"))
			return R.drawable.rain;
		if (ico.equals("sleet"))
			return R.drawable.sleet;
		if (ico.equals("smoke"))
			return R.drawable.smoke;
		if (ico.equals("snow"))
			return R.drawable.snow;
		if (ico.equals("storm"))
			return R.drawable.storm;
		if (ico.equals("sunny"))
			return R.drawable.sunny;
		if (ico.equals("thunderstorm"))
			return R.drawable.thunderstorm;
		if (ico.equals("wind"))
			return R.drawable.wind;
		return R.drawable.no_disponible;
	}

	public static void getDataPreferences(Context context) {
		getDegressUnit(context);
		getTimeOfChange(context);
		getColor(context);
	}

	private static void getColor(Context context) {
		String colorChoose = WeatherUtils.getPreferences(context).getString(
				"color_choose", "0");

		switch (Integer.valueOf(colorChoose)) {
		case 0:
			color = "0007700";
			break;
		case 1:
			color = "0DB4B25";
			break;
		case 2:
			color = "0BFC728";
			break;
		case 3:
			color = "0135EAD";
			break;
		case 4:
			color = "07D13AD";
			break;
		case 5:
			color = "09C1451";
			break;
		case 6:
			color = "0000000";
			break;
		case 7:
			color = "0FFFFFF";
		}

	}

	// get from properties the degrees unit
	private static void getDegressUnit(Context context) {
		String degrees_unit = WeatherUtils.getPreferences(context).getString(
				"degress", "100");

		// By default, celsious
		switch (Integer.valueOf(degrees_unit)) {
		case FARENHEIT:
			isCelsious = false;
			break;

		default:
			isCelsious = true;
		}
	}

	// get from properties time between updates
	private static void getTimeOfChange(Context context) {
		String timeChoose = WeatherUtils.getPreferences(context).getString(
				"update_time", "0");

		switch (Integer.valueOf(timeChoose)) {
		case 1:
			// 1 hour
			setTimeToUpdate(1000 * 60 * 60);
			break;
		case 2:
			// 2 hours
			setTimeToUpdate(1000 * 60 * 60 * 2);
			break;
		case 3:
			// 4 hours
			setTimeToUpdate(1000 * 60 * 60 * 4);
			break;
		case 4:
			// 8 hours
			setTimeToUpdate(1000 * 60 * 60 * 8);
			break;
		case 5:
			// 12 hours
			setTimeToUpdate(1000 * 60 * 60 * 12);
			break;
		default:
			// 30 min
			setTimeToUpdate(1000 * 60 * 30);
		}
	}

	public static boolean isLigthDay(final Date sunRise, final Date sunSet) {
		if (null != sunRise && null != sunSet) {
			long now = new Date().getTime();

			return sunRise.getTime() < now && sunSet.getTime() > now;
		} else {
			return false;
		}
	}

	public static String formatDate(Date date, String format) {
		SimpleDateFormat formato = new SimpleDateFormat(format);

		return formato.format(date);
	}

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netInfo = cm.getActiveNetworkInfo();

		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}

		return false;
	}
}
