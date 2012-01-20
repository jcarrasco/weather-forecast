package com.jcarrasco.weather;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jcarrasco.simpleWeatherforecast.R;
import com.jcarrasco.weatherData.WeatherDataPlaces;

public class Places extends Activity {

	ListView lstResultados;
	ListViewAdapter lstViewAdapter;

	ArrayList<String> lstPlaces = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.places);

		lstResultados = (ListView) findViewById(R.id.LstResultados);

		lstPlaces = WeatherDataPlaces.getPlacesList();

		this.lstViewAdapter = new ListViewAdapter(this, lstPlaces);
		lstResultados.setAdapter(this.lstViewAdapter);
	}
	
	protected void onPause ()
	{
		super.onPause();
		
		writePlaces();
	}

	private void actualizaElementosLista() {
		// Limpio el anterior adaptador
		lstViewAdapter.clear();

		lstPlaces = WeatherDataPlaces.getPlacesList();

		lstViewAdapter.notifyDataSetChanged();
	}

	private void writePlaces() {
		XmlPlaces xmlPlaces = XmlPlaces.getXmlPlaces(this);
				
		xmlPlaces.clearPlaces();
		
		//La posicion 0 es al actual, por lo que no se guarda
		for(int i = 1; i < lstPlaces.size(); i++)
		{
			xmlPlaces.addPlace(lstPlaces.get(i), i);
		}
	}

	public class ListViewAdapter extends ArrayAdapter<String> {
		private ArrayList<String> items;

		public ListViewAdapter(Context context, ArrayList<String> items) {
			super(context, R.layout.icon_list_elements, items);
			this.items = items;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;

			if (view == null) {
				LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = layoutInflater
						.inflate(R.layout.icon_list_elements, null);
			}

			final String elementoLista = items.get(position);
			if (!elementoLista.equals("")) {

				// poblamos la lista de elementos
				TextView txtLocality = (TextView) view
						.findViewById(R.id.txtLocality);

				ImageView btnRemove = (ImageView) view
						.findViewById(R.id.btnRemove);

				txtLocality.setText(elementoLista);

				btnRemove.setOnClickListener(new OnClickListener() {
					public void onClick(View view) {
						XmlPlaces xmlPlaces = XmlPlaces.getXmlPlaces(getApplicationContext());
						
						xmlPlaces.removePlace(elementoLista, position);

						actualizaElementosLista();
					}
				});
			}
			return view;
		}
	}
}