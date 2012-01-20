package com.jcarrasco.weather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.jcarrasco.simpleWeatherforecast.R;
import com.jcarrasco.weatherData.DataProccess;
import com.jcarrasco.weatherData.WeatherData;
import com.jcarrasco.weatherData.WeatherDataPlaces;
import com.jcarrasco.weatherData.WeatherForecastCondition;
import com.jcarrasco.widget.WeatherUtils;
import com.jcarrasco.widget.iGlobals;

public class SimpleWeatherForecast2Activity extends Activity implements
		OnGestureListener, TextToSpeech.OnInitListener, iGlobals {
	TextToSpeech tts;

	ViewFlipper flipper;

	ProgressDialog dialog;
	GestureDetector gestureScanner;

	AdView adView;

	int position = 0;
	String places[];
	boolean isSearch = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		flipper = (ViewFlipper) findViewById(R.id.flipper);

		gestureScanner = new GestureDetector(this);
		tts = new TextToSpeech(this, this);

		// Start Location Service
		startLocationService();

		getPlacesForecast();

		// Put advertising
		// loadAdvertising();
	}

	private void getPlacesForecast() {
		startDialog();
		
		getPlaces();

		if (places.length > 0) {
			new DescargarPrevision().execute(places);
		}
	}

	void getPlaces() {
		XmlPlaces xmlPlaces = XmlPlaces.getXmlPlaces(this);

		int totalPlaces = xmlPlaces.getTotalPlaces();

		places = new String[totalPlaces];

		for (int i = 0; i < totalPlaces; i++) {
			places[i] = xmlPlaces.getPlace(String.valueOf(i + 1));
		}
	}

	private void startLocationService() {
		if (false == isServiceRunning()) {
			Intent intent = new Intent(this,
					com.jcarrasco.Location.WidgetLocation.class);
			startService(intent);
		}
	}

	private boolean isServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (this.getClass().getName()
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 0) {
			String place = data.getStringExtra("returnedData");
			isSearch = data.getBooleanExtra("isSearch", false);

			String placeToSearch[] = { place };

			new DescargarPrevision().execute(placeToSearch);
		}
	}

	// Animation code
	private Animation inFromRightAnimation() {

		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(500);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}

	private Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(500);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

	private Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(500);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}

	private Animation outToRightAnimation() {
		Animation outtoRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoRight.setDuration(500);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}

	// Animation code
	// Menu Code
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.setup_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		switch (item.getItemId()) {
		case R.id.mnuConfig:
			intent = new Intent(this.getBaseContext(),
					PreferencesActivity.class);
			startActivity(intent);

			break;

		case R.id.mnuUpdate:
			// Call to update data process
//			intent = new Intent();
//			intent.setAction("PARSE_DATA");
//
//			this.sendOrderedBroadcast(intent, null);
			
			getPlacesForecast();

			break;

		case R.id.mnuDeletePlace:
			intent = new Intent(this.getBaseContext(), Places.class);
			startActivity(intent);

			break;
		}

		return true;
	}

	// Preferences Changed Listener
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if (KEY_DEGRESS.equals(key)) {
			// Read preferences file
			WeatherUtils.getDataPreferences(this);

			// Update views
			// updateViewForecast();

			// updateWidget();
		} else if (WIDGET_COLOR_CHOOSE.equals(key)) {
			updateWidget();
		}
	}

	// End Menu Code

	// Update Widget
	private void updateWidget() {
		Intent intentWidget = new Intent();

		intentWidget.setAction("com.jcarrasco.weather.UPDATE_WIDGET");
		this.sendBroadcast(intentWidget);
	}

	private void startDialog() {
		dialog = new ProgressDialog(this);
		dialog.setMessage("Descargando...");
		dialog.setTitle("Progreso");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);

		dialog.show();
	}

	private void loadForecastWeather(WeatherData wd) {
		ArrayList<WeatherForecastCondition> weatherForecastCondition = wd
				.getArrayListwfc();

		TextView dayofWeek1 = (TextView) findViewById(R.id.day_of_week1);
		TextView dayofWeek2 = (TextView) findViewById(R.id.day_of_week2);
		TextView dayofWeek3 = (TextView) findViewById(R.id.day_of_week3);
		TextView dayofWeek4 = (TextView) findViewById(R.id.day_of_week4);

		TextView temp1 = (TextView) findViewById(R.id.temp1);
		TextView temp2 = (TextView) findViewById(R.id.temp2);
		TextView temp3 = (TextView) findViewById(R.id.temp3);
		TextView temp4 = (TextView) findViewById(R.id.temp4);

		TextView condition1 = (TextView) findViewById(R.id.condition1);
		TextView condition2 = (TextView) findViewById(R.id.condition2);
		TextView condition3 = (TextView) findViewById(R.id.condition3);
		TextView condition4 = (TextView) findViewById(R.id.condition4);

		ImageView icon1 = (ImageView) findViewById(R.id.icon1);
		ImageView icon2 = (ImageView) findViewById(R.id.icon2);
		ImageView icon3 = (ImageView) findViewById(R.id.icon3);
		ImageView icon4 = (ImageView) findViewById(R.id.icon4);
		ImageView imageView_Search = (ImageView) findViewById(R.id.imageView_Search);

		// dayofWeek1.setTypeface(font);
		// dayofWeek2.setTypeface(font);
		// dayofWeek3.setTypeface(font);
		// dayofWeek4.setTypeface(font);
		// temp1.setTypeface(font);
		// temp2.setTypeface(font);
		// temp3.setTypeface(font);
		// temp4.setTypeface(font);
		// condition1.setTypeface(font);
		// condition2.setTypeface(font);
		// condition3.setTypeface(font);
		// condition4.setTypeface(font);

		if (position == 0)
			imageView_Search.setVisibility(View.VISIBLE);
		else
			imageView_Search.setVisibility(View.GONE);

		final Intent intent = new Intent(this, Search.class);
		intent.putExtra("ActualPlace", wd.getWcc().getPlace());

		imageView_Search.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				startActivityForResult(intent, 0);
			}
		});

		WeatherForecastCondition wfc = weatherForecastCondition.get(0);

		dayofWeek1.setText(wfc.getDayofWeek());
		temp1.setText(getTemperatures(wfc.getTempMin(), wfc.getTempMax(),
				WeatherUtils.xmlDataCelsious));
		condition1.setText(wfc.getCondition());
		icon1.setImageResource(WeatherUtils.getIconDay(wfc.getIconName()));

		wfc = weatherForecastCondition.get(1);

		dayofWeek2.setText(wfc.getDayofWeek());
		temp2.setText(getTemperatures(wfc.getTempMin(), wfc.getTempMax(),
				WeatherUtils.xmlDataCelsious));
		condition2.setText(wfc.getCondition());
		icon2.setImageResource(WeatherUtils.getIconDay(wfc.getIconName()));

		wfc = weatherForecastCondition.get(2);

		dayofWeek3.setText(wfc.getDayofWeek());
		temp3.setText((getTemperatures(wfc.getTempMin(), wfc.getTempMax(),
				WeatherUtils.xmlDataCelsious)));
		condition3.setText(wfc.getCondition());
		icon3.setImageResource(WeatherUtils.getIconDay(wfc.getIconName()));

		wfc = weatherForecastCondition.get(3);

		dayofWeek4.setText(wfc.getDayofWeek());
		temp4.setText(getTemperatures(wfc.getTempMin(), wfc.getTempMax(),
				WeatherUtils.xmlDataCelsious));
		condition4.setText(wfc.getCondition());
		icon4.setImageResource(WeatherUtils.getIconDay(wfc.getIconName()));
	}

	private void loadCurrentWeather(final WeatherData wd) {
		StringBuilder sb = new StringBuilder();
		final String txtWeatherForecast;
		
		ImageView currentIco = (ImageView) findViewById(R.id.currentIco);
		ImageView Speaker = (ImageView) findViewById(R.id.speaker);
		TextView CurrentPlace = (TextView) findViewById(R.id.CurrentPlace);
		TextView CurrentTemp = (TextView) findViewById(R.id.CurrentTemp);
		TextView CurrentHumedity = (TextView) findViewById(R.id.CurrentHumedity);
		TextView CurrentWind = (TextView) findViewById(R.id.CurrentWind);
		TextView CurrentCondition = (TextView) findViewById(R.id.CurrentCondition);
		TextView SunSet = (TextView) findViewById(R.id.sunset);
		TextView SunRise = (TextView) findViewById(R.id.sunrise);

		// CurrentPlace.setTypeface(font);
		// CurrentTemp.setTypeface(font);
		// CurrentHumedity.setTypeface(font);
		// CurrentWind.setTypeface(font);
		// CurrentCondition.setTypeface(font);
		// SunSet.setTypeface(font);
		// SunRise.setTypeface(font);
		// Today.setTypeface(font);
		// Forecast.setTypeface(font);

		CurrentPlace.setText(wd.getWcc().getPlace());		
		sb.append(wd.getWcc().getPlace()).append(",");
				
		sb.append(getString(R.string.current_temperature)).append(
				String.valueOf(wd.getWcc().getTempCelcius()));
		
		if (WeatherUtils.isCelsious)
		{
			CurrentTemp.setText(String.valueOf(wd.getWcc().getTempCelcius())
					+ " ºC");
			sb.append(getString(R.string.degrees_celsius));
		}
		else
		{
			CurrentTemp.setText(String.valueOf(wd.getWcc().getTempFahrenheit())
					+ " ºF");
			sb.append(getString(R.string.degrees_Fahrenheit));
		}
		
		CurrentCondition.setText(wd.getWcc().getCondition());
		sb .append(wd.getWcc().getCondition());
		
		CurrentHumedity.setText(wd.getWcc().getHumidity());
		sb.append(getString(R.string.moisture)).append(wd.getWcc().getHumidity());
		
		CurrentWind.setText(wd.getWcc().getWindCondition());
		sb.append(getString(R.string.wind)).append(wd.getWcc().getWindCondition());
		
		SunSet.setText(WeatherUtils
				.formatDate(wd.getWcc().getSunSet(), "HH:mm"));
		SunRise.setText(WeatherUtils.formatDate(wd.getWcc().getSunRise(),
				"HH:mm"));
		
		sb.append(getString(R.string.sunraise)).append(
				WeatherUtils.formatDate(wd.getWcc().getSunRise(),
						"HH:mm"));
		sb.append(getString(R.string.sunset)).append(
				(WeatherUtils.formatDate(wd.getWcc().getSunSet(),
						"HH:mm")));
		
		//Texto de la previson para textToSpeak  
		txtWeatherForecast = sb.toString();
		
		if (WeatherUtils.isLigthDay(wd.getWcc().getSunRise(), wd.getWcc()
				.getSunSet()))
			currentIco.setImageResource(WeatherUtils.getIconDay(wd.getWcc()
					.getIconName()));
		else
			currentIco.setImageResource(WeatherUtils.getIconNight(wd.getWcc()
					.getIconName()));

		currentIco.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				textToSpeak(txtWeatherForecast);
			}
		});
		
		Speaker.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				textToSpeak(txtWeatherForecast);
			}
		});
	}

	private void textToSpeak(String textToRead) {
		tts.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null);
	}

	private String getTemperatures(int min, int max, boolean xmlDataCelsious) {
		// User choose celsious unit and data send fahrenheit
		if (WeatherUtils.isCelsious && !xmlDataCelsious) {
			min = WeatherUtils.fahrenheitToCelsius(min);
			max = WeatherUtils.fahrenheitToCelsius(max);
		}

		// User choose fahrenheit unit and data send celsious
		if (!WeatherUtils.isCelsious && xmlDataCelsious) {
			min = WeatherUtils.celsiusToFahrenheit(min);
			max = WeatherUtils.celsiusToFahrenheit(max);
		}

		StringBuilder sb = new StringBuilder();

		sb.append(String.valueOf(min));
		sb.append("º/");
		sb.append(String.valueOf(max));
		sb.append("º");

		return sb.toString();
	}

	// private void savePlacesToPreferences(int position, String place) {
	// SharedPreferences pref = getSharedPreferences(PREFS_PLACES,
	// MODE_PRIVATE);
	// SharedPreferences.Editor editor = pref.edit();
	//
	// editor.putInt("totalPlaces", position);
	// editor.putString(String.valueOf(position), place);
	//
	// editor.commit();
	// }
	//
	// private void readPlacesFromPreferences() {
	// SharedPreferences pref = getSharedPreferences(PREFS_PLACES,
	// MODE_PRIVATE);
	//
	// places = new String[pref.getInt("totalPlaces", 0)];
	//
	// for (int i = 1; i <= places.length; i++) {
	// places[i] = pref.getString(String.valueOf(i), "");
	// }
	// }

	private void loadAdvertising() {
		try {
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.RelativeLayout_Layout);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			Log.i(LOG_TITLE, "load ad");

			// Create the adView
			adView = new AdView(this, AdSize.BANNER, AD_KEY);
			adView.setLayoutParams(params);
			// Add the adView to it
			layout.addView(adView);

			// Initiate a generic request to load it with an ad
			adView.loadAd(new AdRequest());
		} catch (Exception e) {
			Log.e(LOG_TITLE, "publi: " + e.getMessage());
		}
	}

	// Control de gestos

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		return gestureScanner.onTouchEvent(me);
	}

	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		// if(velocityY < 0)
		// //Arriba
		// else
		// //abajo

		if (velocityX < 0) {
			// Dcha
			if (position < WeatherDataPlaces.getSize() - 1) {
				position++;

				flipper.setInAnimation(inFromRightAnimation());
				flipper.setOutAnimation(outToLeftAnimation());
				flipper.showNext();
			}

			loadCurrentWeather(WeatherDataPlaces.getPlace(position));
			loadForecastWeather(WeatherDataPlaces.getPlace(position));
		} else if (velocityX > 0) {
			// Izda
			if (position <= 0)
				position = 0;
			else {
				position--;

				flipper.setInAnimation(inFromLeftAnimation());
				flipper.setOutAnimation(outToRightAnimation());
				flipper.showPrevious();
			}
			
			loadCurrentWeather(WeatherDataPlaces.getPlace(position));
			loadForecastWeather(WeatherDataPlaces.getPlace(position));
		}

		return true;
	}

	public void onLongPress(MotionEvent e) {
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	public void onShowPress(MotionEvent e) {
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	// Control de gestos

	private class DescargarPrevision extends
			AsyncTask<String, Void, WeatherData> {
		@Override
		protected WeatherData doInBackground(String... params) {
			DataProccess dp = new DataProccess();
			WeatherData wd = null;			
			
			for (int i = 0; i < params.length; i++) {
				try {
					wd = dp.processData(params[i], isSearch, getBaseContext());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (true == isSearch)
				return wd;
			else
				return null;
		}

		protected void onPostExecute(WeatherData dtos) {
			isSearch = false;

			if (null == dtos) {
				loadCurrentWeather(WeatherDataPlaces.getPlace(position));
				loadForecastWeather(WeatherDataPlaces.getPlace(position));
			} else {
				loadCurrentWeather(dtos);
				loadForecastWeather(dtos);
			}
			
			dialog.dismiss();
		}
	}

	// testToSpeak
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.getDefault());

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {

				Toast msg = Toast.makeText(getApplicationContext(),
						"This Language is not supported", Toast.LENGTH_SHORT);

				msg.show();

				Log.e("TTS", "This Language is not supported");
			}
		} else {
			Log.e("TTS", "Initilization Failed!");
		}

	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown tts!
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}
	// testToSpeak
}