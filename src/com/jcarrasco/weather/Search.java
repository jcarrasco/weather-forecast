package com.jcarrasco.weather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jcarrasco.simpleWeatherforecast.R;

public class Search extends Activity {

	private static final int MAX_RESULTS = 5;

	TextView actualPlace;
	TextView txtSearch;
	Button btnSearch;
	ListView lstResultados;

	private ArrayList<ElementosLista> resultados = new ArrayList<ElementosLista>();
	private ListViewAdapter lstViewAdapter;

	Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		intent = getIntent();
		Bundle bundle = intent.getExtras();

		actualPlace = (TextView) findViewById(R.id.txtCurrentPlace);
		txtSearch = (TextView) findViewById(R.id.txtSearch);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		lstResultados = (ListView) findViewById(R.id.LstResultados);

		actualPlace.setText((String) bundle.get("ActualPlace"));

		this.lstViewAdapter = new ListViewAdapter(this, resultados);
		lstResultados.setAdapter(lstViewAdapter);

		btnSearch.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {

				// Hide keyboard
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);
				// Hide keyboard

				getResult();
			}
		});

		lstResultados.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				String place = getListItem(position);

				listClickEvent(place, true);
			}
		});
	}

	private void listClickEvent(final String place, final boolean isSearch) {
		intent.putExtra("isSearch", isSearch);
		intent.putExtra("returnedData", place);
		setResult(RESULT_OK, intent);

		finish();
	}

	private String getListItem(int position) {
		ElementosLista item = (ElementosLista) (lstResultados
				.getItemAtPosition(position));

		return item.getLocality();
	}

	protected void getResult() {
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());

		try {
			List<Address> resultados = geocoder.getFromLocationName(txtSearch
					.getText().toString(), MAX_RESULTS);

			actualizaElementosLista(resultados);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void actualizaElementosLista(final List<Address> res) {
		ElementosLista eLista = new ElementosLista();

		// Limpio la anterior lista
		if (null != resultados)
			resultados.clear();

		// Limpio el anterior adaptador
		lstViewAdapter.clear();

		for (Address address : res) {
			eLista.setLocality(address.getLocality());
			eLista.setAdminArea(address.getAdminArea());
			eLista.setCountryName(address.getCountryName());
			eLista.setSubAdminArea(address.getSubAdminArea());

			resultados.add(eLista);
		}

		lstViewAdapter.notifyDataSetChanged();
	}

	public List<Address> getResult(String place, Context context)
			throws IOException {
		Geocoder geocoder = new Geocoder(context);

		List<Address> address = geocoder
				.getFromLocationName(place, MAX_RESULTS);

		address.get(0).getLocality();
		address.get(0).getSubAdminArea();
		address.get(0).getAdminArea();
		address.get(0).getCountryName();

		return geocoder.getFromLocationName(place, MAX_RESULTS);
	}

	private class ElementosLista {
		String locality;
		String subAdminArea;
		String AdminArea;
		String CountryName;

		public String getLocality() {
			return locality;
		}

		public void setLocality(String locality) {
			this.locality = locality;
		}

		public void setSubAdminArea(String subAdminArea) {
			this.subAdminArea = subAdminArea;
		}

		public void setAdminArea(String adminArea) {
			AdminArea = adminArea;
		}

		public void setCountryName(String countryName) {
			CountryName = countryName;
		}

		public String getLocalityArea() {
			StringBuilder sb = new StringBuilder();

			sb.append(subAdminArea).append(", ").append(AdminArea).append(", ")
					.append(CountryName);

			return sb.toString();
		}
	}

	public class ListViewAdapter extends ArrayAdapter<ElementosLista> {
		private ArrayList<ElementosLista> items;

		public ListViewAdapter(Context context, ArrayList<ElementosLista> items) {
			super(context, R.layout.icon_list_elements, items);
			this.items = items;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = layoutInflater.inflate(R.layout.list_elements, null);
			}

			ElementosLista elementosLista = items.get(position);
			if (elementosLista != null) {

				// poblamos la lista de elementos
				final TextView txtLocality = (TextView) view
						.findViewById(R.id.txtLocality);

				TextView txtLocalityExtended = (TextView) view
						.findViewById(R.id.txtLocalityExtended);

				ImageView btnAdd = (ImageView) view.findViewById(R.id.btnAdd);

				txtLocality.setText(elementosLista.getLocality());
				txtLocalityExtended.setText(elementosLista.getLocalityArea());

				btnAdd.setOnClickListener(new OnClickListener() {
					public void onClick(View view) {
						String place = getListItem(position);
								//txtLocality.getText().toString();

						addBookmark(place);
					}
				});
			}
			return view;
		}

		void addBookmark(String place) {
			XmlPlaces xmlPlaces = XmlPlaces
					.getXmlPlaces(getApplicationContext());

			int totalPlaces = xmlPlaces.getTotalPlaces();

			xmlPlaces.addPlace(place, totalPlaces + 1);

			listClickEvent(place, false);
		}
	}
}