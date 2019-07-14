package rosetracker.converter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.concurrent.ThreadLocalRandom;
import java.sql.Timestamp;

import org.json.JSONObject;
import rosetracker.dataclasses.BCIncident;

public class IoTDataGenerator {

	private static List<double[]> route1 = new LinkedList<double[]>();
	private static List<double[]> route2 = new LinkedList<double[]>();
	// private static List<String> timestamps = new LinkedList<String>();
	private static List<Timestamp> timestamps = new LinkedList<Timestamp>();

	private static List<BCIncident> initialHistory = new LinkedList<>();

	public static void main(String[] args) {

		// TODO hier bräuchte ich noch passende PackageIDs
		String id1 = "78";
		String id2 = "8";

		generateRoutes();
		generateTimestamps();
		generateAllData(id1);

		for (BCIncident historyEntry : initialHistory) {

			//this will probably still cause an error due to the missing incidentdescription field in the current odata service
			postTracking(historyEntry);

			// in case of an incident, we will write this dataset into the blockchain
			if (!historyEntry.incidentDescription.equals("")) {
				postBCIncident(historyEntry);
			}
			
			

		}
	}

	public static void generateAllData(String id) {
		int counter = 0;
		for (double[] coordinates : route1) {

			for (int i = 1; i < 6; i++) {
				BCIncident tracking = new BCIncident();

				double[] temperatures = generateTemperature();
				double[] humidities = generateHumidity();

				tracking.MinTemperature = temperatures[0];
				tracking.MaxTemperature = temperatures[1];

				tracking.MinHumidity = humidities[0];
				tracking.MaxHumidity = humidities[1];

				tracking.CurrentLatitude = coordinates[0];
				tracking.CurrentLongitude = coordinates[1];

				tracking.PackageID = id;
				tracking.TimeStamp = timestamps.get(counter);

				checkIncident(tracking);

				initialHistory.add(tracking);

				System.out.println(tracking.MinTemperature + "\t " + tracking.MaxTemperature + "\t "
						+ tracking.MinHumidity + "\t " + tracking.MaxHumidity + "\t " + tracking.CurrentLatitude + "\t "
						+ tracking.CurrentLongitude + "\t " + tracking.PackageID + "\t ");
				counter++;

			}

		}
	}

	public static void checkIncident(BCIncident incident) {
		String incidentDescription = "";

		if (incident.MaxHumidity > 0.85) {
			incidentDescription = "Humidity Incident";
		}

		if (incident.MinTemperature < 2.0 || incident.MaxTemperature > 8.0) {
			if (incidentDescription.equals("")) {
				incidentDescription = "Temperature Incident";
			} else {
				incidentDescription += "; Temperature Incident";
			}
		}
	}

	public static int postBCIncident(BCIncident incident) {
		DataOutputStream dataOut = null;
		BufferedReader in = null;

		int responseCode = 0;

		try {

			System.out.println("POST-API for BCIncident started");
			String url = "https://fon6pom573dmdpz1kerdummyodata-srv.cfapps.eu10.hana.ondemand.com/odata/v2/RoseTrackerDataService/IncidentBC";

			JSONObject jsonObject = new JSONObject();

			// inserted to make it fit for the service
			String timestamp = incident.TimeStamp.toString();
			timestamp = timestamp.replace(" ", "T");
			timestamp += "Z";

			jsonObject.put("PackageID", incident.PackageID);
			// jsonObject.put("TimeStamp", incident.TimeStamp.toString());
			jsonObject.put("TimeStamp", timestamp);
			jsonObject.put("CurrentLatitude", Double.toString(incident.CurrentLatitude));
			jsonObject.put("CurrentLongitude", Double.toString(incident.CurrentLongitude));
			jsonObject.put("MinTemperature", Double.toString(incident.MinTemperature));
			jsonObject.put("MaxTemperature", Double.toString(incident.MaxTemperature));
			jsonObject.put("MinHumidity", Double.toString(incident.MinHumidity));
			jsonObject.put("MaxHumidity", Double.toString(incident.MaxHumidity));

			URL urlObj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("DataServiceVersion", "2.0");

			connection.setDoInput(true);

			connection.setDoOutput(true);
			OutputStream os = connection.getOutputStream();
			os.write(jsonObject.toString().getBytes("UTF-8"));

			os.flush();
			os.close();

			responseCode = connection.getResponseCode();
			System.out.println("ResponseCode: " + responseCode);
			System.out.println("POST Response Message : " + connection.getResponseMessage());

			return responseCode;

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			try {
				if (dataOut != null) {
					dataOut.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	public static int postTracking(BCIncident incident) {

		DataOutputStream dataOut = null;
		BufferedReader in = null;

		int responseCode = 0;

		try {

			System.out.println("POST-API for Tracking started");
			String url = "https://fon6pom573dmdpz1kerdummyodata-srv.cfapps.eu10.hana.ondemand.com/odata/v2/RoseTrackerDataService/Tracking";

			JSONObject jsonObject = new JSONObject();

			// inserted to make it fit for the service
			String timestamp = incident.TimeStamp.toString();
			timestamp = timestamp.replace(" ", "T");
			timestamp += "Z";

			jsonObject.put("PackageID", incident.PackageID);
			// jsonObject.put("TimeStamp", incident.TimeStamp.toString());
			jsonObject.put("TimeStamp", timestamp);
			jsonObject.put("CurrentLat", Double.toString(incident.CurrentLatitude));
			jsonObject.put("CurrentLong", Double.toString(incident.CurrentLongitude));
			jsonObject.put("MinTemp", Double.toString(incident.MinTemperature));
			jsonObject.put("MaxTemp", Double.toString(incident.MaxTemperature));
			jsonObject.put("MinHumidity", Double.toString(incident.MinHumidity));
			jsonObject.put("MaxHumidity", Double.toString(incident.MaxHumidity));
			// maybe comment out to test against the test odata service
			jsonObject.put("incidentDescription", incident.incidentDescription);

			URL urlObj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("DataServiceVersion", "2.0");

			connection.setDoInput(true);

			connection.setDoOutput(true);
			OutputStream os = connection.getOutputStream();
			os.write(jsonObject.toString().getBytes("UTF-8"));

			os.flush();
			os.close();

			responseCode = connection.getResponseCode();
			System.out.println("ResponseCode: " + responseCode);
			System.out.println("POST Response Message : " + connection.getResponseMessage());

			return responseCode;

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			try {
				if (dataOut != null) {
					dataOut.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	public static void generateRoutes() {
		// wir generieren 5 Einträge zu jeder Location
		// Route 1
		double[] station0 = { -0.937263, -78.613653 };
		double[] station1 = { -0.260159, -79.167500 };
		double[] station2 = { -0.204248, -78.489706 };
		double[] station3 = { 50.050819, 8.564596 };
		double[] station4 = { 48.773573, 9.168711 };
		double[] station5 = { 48.135728, 11.690204 };

		route1.add(station0);
		route1.add(station1);
		route1.add(station2);
		route1.add(station3);
		route1.add(station4);
		route1.add(station5);

		// Round
		for (double[] coordinates : route1) {
			coordinates[0] = Math.round(10000.0 * coordinates[0]) / 10000.0;
			coordinates[1] = Math.round(10000.0 * coordinates[1]) / 10000.0;
		}

		// Route 2
		double[] station10 = { -4.009331, -79.204257 };
		double[] station11 = { -2.200339, -79.888952 };
		double[] station12 = { -0.182976, -78.478845 };
		double[] station13 = { 48.351981, 11.770076 };
		double[] station14 = { 49.460963, 11.080001 };
		double[] station15 = { 52.520203, 13.419610 };

		route2.add(station10);
		route2.add(station11);
		route2.add(station12);
		route2.add(station13);
		route2.add(station14);
		route2.add(station15);

		for (double[] coordinates : route2) {
			coordinates[0] = Math.round(10000.0 * coordinates[0]) / 10000.0;
			coordinates[1] = Math.round(10000.0 * coordinates[1]) / 10000.0;
		}
	}

	public static void generateTimestamps() {
		// wir generieren fuer jeden Eintrag in der Route einen Timestamp --> 5*6=30

		long start = 1557871200000L; // 15.05.201
		long interval = 32400000L; // 9hours

		for (int i = 0; i < 30; i++) {
			// String timestamp = new Timestamp(start + i * interval).toString();
			Timestamp timestamp = new Timestamp(start + i * interval);
			// System.out.println(timestamp);
//			timestamp = timestamp.replace(" ", "T");
//			timestamp += "Z";
			timestamps.add(timestamp);
		}
	}

	public static double[] generateTemperature() {

		double[] result = new double[2];

		// take values between 0 and 10 degree celsius
		// ideal temperatur btw. 2 and 8 degree celsius
		double number1 = ThreadLocalRandom.current().nextDouble(0, 10.1);
		number1 = Math.round(100.0 * number1) / 100.0;
		double number2 = ThreadLocalRandom.current().nextDouble(0, 10.1);
		number2 = Math.round(100.0 * number2) / 100.0;

		if (number1 < number2) {
			result[0] = number1;
			result[1] = number2;
		} else {
			result[0] = number2;
			result[1] = number1;
		}

		return result;
	}

	public static double[] generateHumidity() {

		double[] result = new double[2];

		// take values between 0% and 100%
		// ideal humidity below btw. 50% and 85%
		double number1 = ThreadLocalRandom.current().nextDouble(0.3, 1);
		number1 = Math.round(100.0 * number1) / 100.0;
		double number2 = ThreadLocalRandom.current().nextDouble(0.3, 1);
		number2 = Math.round(100.0 * number2) / 100.0;

		if (number1 < number2) {
			result[0] = number1;
			result[1] = number2;
		} else {
			result[0] = number2;
			result[1] = number1;
		}

		return result;
	}
}
