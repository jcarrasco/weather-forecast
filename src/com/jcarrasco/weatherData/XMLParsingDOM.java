package com.jcarrasco.weatherData;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLParsingDOM {
	public WeatherData parse(URL url) {
		WeatherCurrentCondition wcc = new WeatherCurrentCondition();
		ArrayList<WeatherForecastCondition> arrayListwfc = new ArrayList<WeatherForecastCondition>();

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;

			db = dbf.newDocumentBuilder();

			InputStreamReader isr = new InputStreamReader(url.openStream(), "iso-8859-1");
			Document doc = db.parse(new InputSource(isr));
			doc.getDocumentElement().normalize();
			
			isr.close();

			NodeList nodeList = doc.getElementsByTagName("current_conditions");
			Node node = nodeList.item(0);

			// En caso de que no se encuentre la localidad, el obj nodeList sera
			// null
			if (null != node) {
				Element fstElmnt = (Element) node;

				wcc.setCondition(getValue(fstElmnt, "condition"));
				wcc.setTempCelcius(getValue(fstElmnt, "temp_c"));
				wcc.setTempFahrenheit(getValue(fstElmnt, "temp_f"));
				wcc.setHumidity(getValue(fstElmnt, "humidity"));
				wcc.setIconURL(getValue(fstElmnt, "icon"));
				wcc.setWindCondition(getValue(fstElmnt, "wind_condition"));

				nodeList = doc.getElementsByTagName("forecast_conditions");

				for (int i = 0; i < nodeList.getLength(); i++) {
					node = nodeList.item(i);
					fstElmnt = (Element) node;

					WeatherForecastCondition wfc = new WeatherForecastCondition();

					wfc.setDayofWeek(getValue(fstElmnt, "day_of_week"));
					wfc.setTempMin(Integer.parseInt(getValue(fstElmnt, "low")));
					wfc.setTempMax(Integer.parseInt(getValue(fstElmnt, "high")));
					wfc.setIconURL(getValue(fstElmnt, "icon"));
					wfc.setCondition(getValue(fstElmnt, "condition"));

					arrayListwfc.add(wfc);
				}
			}
			else
				return null;
			
		} catch (ParserConfigurationException e) {
			wcc = null;
			arrayListwfc = null;

			e.printStackTrace();
		} catch (SAXException e) {
			wcc = null;
			arrayListwfc = null;

			e.printStackTrace();
		} catch (IOException e) {
			wcc = null;
			arrayListwfc = null;

			e.printStackTrace();
		}

		WeatherData dtos = new WeatherData();

		dtos.setWcc(wcc);
		dtos.setArrayListwfc(arrayListwfc);

		return dtos;
	}

	private String getValue(final Element fstElmnt, final String tag) {
		NodeList nodeList;
		Element nodeElement;

		nodeList = fstElmnt.getElementsByTagName(tag);
		nodeElement = (Element) nodeList.item(0);
		if(null != nodeElement)
			return nodeElement.getAttribute("data");
		else
			return "";
	}
}
