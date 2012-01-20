package com.jcarrasco.weatherData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jcarrasco.SunsetTools.SunriseSunset;
import com.jcarrasco.widget.WeatherUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

public class DataProccess extends BroadcastReceiver {
	final String LOG_TITLE = this.getClass().getName();

	final String countryCode = Locale.getDefault().getCountry();

	XMLParsingDOM xmlParse = new XMLParsingDOM();
	WeatherData dtos = null;

	public void processData(Location loc) {
		StringBuilder queryString = new StringBuilder();

		// Convert Latitude & Longitude on E6 format
		int lat = (int) (loc.getLatitude() * 1E6);
		int lng = (int) (loc.getLongitude() * 1E6);

		dtos = new WeatherData();

		queryString.append("http://www.google.com/ig/api?weather=,,,")
				.append(lat).append(",").append(lng).append("&hl=")
				.append(countryCode);

		try {
			getWeatherPlaceData(queryString.toString());

			// Update name.
			ResolveName place = new ResolveName();
			dtos.getWcc().setPlace(place.getLocationName(loc));

			// Update time to SunRise and SunSet
			updateSunRiseSunSet(dtos.getWcc(), loc);

			// La posicion actual, siempre es la primera posicion del vector
			WeatherDataPlaces.addActualPlace(dtos);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public WeatherData processData(String place, boolean isSearch, Context context) throws IOException {
		// Si no hay conexion a internet, Se muestra un msg, pero no se procesa
		// nada
		if (WeatherUtils.isOnline(context)) {
			dtos = new WeatherData();
			StringBuilder queryString = new StringBuilder();

			queryString.append("http://www.google.com/ig/api?weather=,,,")
					.append(StringNormalizer(place)).append("&hl=")
					.append(countryCode);

			getWeatherPlaceData(queryString.toString());

			// Si no encuentra prediccion para el lugar, no crea el obj.
			if (null != dtos) {
				// Update name.
				dtos.getWcc().setPlace(place);

				// Update time to SunRise and SunSet
				ResolveLocation rl = new ResolveLocation();
				updateSunRiseSunSet(dtos.getWcc(),
						rl.getLocation(place, context));

				if(false == isSearch)
					WeatherDataPlaces.addPlace(dtos);
				else
					return dtos;
			}
		} else {
			// TODO mensaje no hay conexion
		}
		
		return null;
	}

	private void getWeatherPlaceData(String urlPlace)
			throws MalformedURLException {
		/* Replace blanks with HTML-Equivalent. */
		URL url = new URL(urlPlace.replace(" ", "%20"));

		// parse data
		dtos = xmlParse.parse(url);
	}

	private void updateSunRiseSunSet(WeatherCurrentCondition wcc, Location loc) {
		try {
			Log.i(LOG_TITLE, "Update time to sunrise, sunset");

			if (null != loc) {
				SunriseSunset ss = new SunriseSunset(loc.getLatitude(),
						loc.getLongitude(), new Date(), 0);

				wcc.setSunRise(ss.getSunrise());
				wcc.setSunSet(ss.getSunset());
			} else {
				wcc.setSunRise(null);
				wcc.setSunSet(null);
			}
		} catch (Exception e) {
			Log.e(LOG_TITLE, "updateSunRiseSunSet: " + e.getMessage());
		}
	}

	/**
	 * FunciÛn que elimina acentos y caracteres especiales de una cadena de
	 * texto.
	 * 
	 * @param input
	 * @return cadena de texto limpia de acentos y caracteres especiales.
	 */
	private String StringNormalizer(String input) {
		// Cadena de caracteres original a sustituir.
		String original = "·‡‰ÈËÎÌÏÔÛÚˆ˙˘uÒ¡¿ƒ…»ÀÕÃœ”“÷⁄Ÿ‹—Á«";

		// Cadena de caracteres ASCII que reemplazar·n los originales.
		String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";

		String output = input;

		if (null != output) {
			for (int i = 0; i < original.length(); i++) {
				// Reemplazamos los caracteres especiales.
				output = output.replace(original.charAt(i), ascii.charAt(i));
			}
			return output;
		} else
			return "";
	}

	@Override
	public void onReceive(Context arg0, Intent intent) {
		Bundle bundle = intent.getExtras();

		if (null != bundle) {
			Location loc = (Location) bundle.getParcelable("Location");
			processData(loc);
		} else
			Log.e("dataProcess", "bundle = null");
	}

	private class ResolveLocation {
		private static final int MAX_RESULTS = 1;
		Geocoder geocoder;

		private Location getLocation(String place, Context context)
				throws IOException {
			geocoder = new Geocoder(context);

			List<Address> address = geocoder.getFromLocationName(place,
					MAX_RESULTS);

			if (null != address.get(0)) {
				Location loc = new Location("");

				loc.setLatitude(address.get(0).getLatitude());
				loc.setLongitude(address.get(0).getLongitude());

				return loc;
			}

			return null;
		}
	}

	private class ResolveName {
		private JSONObject getObjJson(String url) {
			HttpClient httpclient = new DefaultHttpClient();

			// Prepare a request object
			HttpGet httpget = new HttpGet(url);

			// Execute the request
			HttpResponse response;

			JSONObject json = new JSONObject();

			try {
				response = httpclient.execute(httpget);

				HttpEntity entity = response.getEntity();

				if (entity != null) {

					// A Simple JSON Response Read
					InputStream instream = entity.getContent();
					String result = convertStreamToString(instream);

					json = new JSONObject(result);

					instream.close();
				}

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return json;
		}

		public String convertStreamToString(InputStream is) {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return sb.toString();
		}

		private String getLocationName(Location loc) {
			JSONObject jsonObject;

			String url = "http://where.yahooapis.com/geocode?q="
					+ loc.getLatitude() + "+" + loc.getLongitude()
					+ "&flags=J&gflags=R";

			try {
				// Recoge el obj ResultSet
				jsonObject = getObjJson(url).getJSONObject("ResultSet");

				// Recoge el array Results
				JSONArray jsonContent = jsonObject.getJSONArray("Results");

				// Recoge el unico elemento del array Results
				JSONObject item = jsonContent.getJSONObject(0);

				return item.getString("city");

			} catch (JSONException e) {
				e.printStackTrace();
				return "";
			}
		}
	}
}
