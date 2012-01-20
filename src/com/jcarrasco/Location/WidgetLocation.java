package com.jcarrasco.Location;

import com.jcarrasco.simpleWeatherforecast.R;
import com.jcarrasco.widget.WeatherUtils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class WidgetLocation extends Service implements LocationListener {

	private final int DISTANCE_TO_UPDATE = 1000; // 1km
	private final int CHECK_POSITION_TIME = 5 * 60 * 1000; // 5 min

	private static LocationManager locationManager;
//	private static Location loc = new Location("");

	// Get the best location provider
	String locationBestProvider = "";

	private final String LOG_TITLE = this.getClass().getName();

	@Override
	public void onCreate() {
		Log.i(LOG_TITLE, "OnCreate");

		super.onCreate();

		_startService();
	}

	private void _startService() {

		Log.i(LOG_TITLE, "Start service");

		if (null == locationManager) {
			initLocationManager();
		}

		// If location by network is not enabled, not start service
		if (locationManager.isProviderEnabled(locationBestProvider)) {
			// Read and configure user preferences.
			setupPreferences();

			// Ejecuta proceso en intervalos programados por el usuario
			timeSchedule();
		} else {
			// Show warning no provider
			showNotification();
		}
	}

	@Override
	public void onDestroy() {
		Log.i(LOG_TITLE, "Stop service");
		// Finally we should call removeUpdates that will stop handling further
		// activity from the locationManager instance, as we do not need the
		// service anymore.
		locationManager.removeUpdates(this);

		super.onDestroy();
	}

	private void initLocationManager() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationBestProvider = locationManager.getBestProvider(getCriteria(),
				true);

		locationManager.requestLocationUpdates(locationBestProvider,
				CHECK_POSITION_TIME, DISTANCE_TO_UPDATE, this);
	}

	private void setupPreferences() {
		// Read preferences file
		WeatherUtils.getDataPreferences(this);
	}

	private void timeSchedule() {
		Intent intent = new Intent();

		intent.setAction("PARSE_DATA");
		intent.putExtra("Location", getLocation());

		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				intent, 0);
		
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		alarmManager.setRepeating(AlarmManager.RTC,
				SystemClock.elapsedRealtime(), WeatherUtils.getTimeToUpdate(),
				pendingIntent);
	}

	Location getLocation() {
		// Get the last known position of the best location provider
		return locationManager.getLastKnownLocation(locationBestProvider);
	}

	private Criteria getCriteria() {
		// Options to choose the best location provider
		Criteria criteria = new Criteria();

		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		return criteria;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void onLocationChanged(Location _loc) {
		Log.i(LOG_TITLE, "Location Changed");

		Intent intent = new Intent();
		intent.setAction("PARSE_DATA");
		intent.putExtra("Location", getLocation());
		
		this.sendOrderedBroadcast(intent, null);
	}

	public void onProviderDisabled(String arg0) {
		Log.i(LOG_TITLE, "ProviderDisabled");

		showNotification();
	}

	private void showNotification() {
		String noProvider = this.getResources().getString(R.string.no_provider);

		String start_provider = this.getResources().getString(
				R.string.start_provider);

		// Obtenemos una referencia al servicio de notificaciones
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager notManager = (NotificationManager) getSystemService(ns);

		// Configuramos la notificación
		int icono = android.R.drawable.stat_sys_warning;
		CharSequence textoEstado = this.getResources().getString(
				R.string.warning);
		long hora = System.currentTimeMillis();

		Notification notif = new Notification(icono, textoEstado, hora);

		// Configuramos el Intent
		Context contexto = getApplicationContext();
		CharSequence titulo = noProvider;
		CharSequence descripcion = start_provider;

		// Al pulsar el notification, abre la ventana de activar localizacion
		Intent notIntent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

		PendingIntent contIntent = PendingIntent.getActivity(contexto, 0,
				notIntent, 0);

		notif.setLatestEventInfo(contexto, titulo, descripcion, contIntent);

		// AutoCancel: cuando se pulsa la notificaión ésta desaparece
		notif.flags |= Notification.FLAG_AUTO_CANCEL;

		// Enviar notificación
		notManager.notify(1, notif);
	}

	public void onProviderEnabled(String arg0) {
		Log.i(LOG_TITLE, "ProviderEnabled");

		_startService();
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}
}
