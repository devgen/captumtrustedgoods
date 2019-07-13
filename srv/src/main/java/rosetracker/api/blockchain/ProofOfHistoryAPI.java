package rosetracker.api.blockchain;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.Set;

import org.json.JSONObject;

public class ProofOfHistoryAPI {
	
	private static String AccessToken ="";
	
	private static int APIcallCounter = 0;
	private static final int maxAPIcalls = 50;

	private static final String ClientID = "sb-a86798bd-4bec-4bff-8615-18a17208dc39!b15106|na-420adfc9-f96e-4090-a650-0386988b67e0!b1836";
	private static final String ClientSecret = "SCW4k+PTcP25Mewm0RIqNg4akAE=";
	private static final String AuthURL = "https://p2001348379trial.authentication.eu10.hana.ondemand.com/oauth/token?grant_type=client_credentials";

	private static void WriteToConsole(String msg) {
		System.out.print("---------------------------------------------------------\n" + "CUSTOM CODE   ----   ----    "
				+ msg + "    ---    ----  \n" + "---------------------------------------------------------\n");
	}

	private static void UpdateAccessToken() {

		DataOutputStream dataOut = null;
		BufferedReader in = null;

		int responseCode = 0;

		try {

			URL urlobj = new URL(AuthURL);

			HttpURLConnection connection = (HttpURLConnection) urlobj.openConnection();

			String base64Credentials = Base64.getEncoder().encodeToString((ClientID + ":" + ClientSecret).getBytes());

			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Authorization", "Basic " + base64Credentials);
			connection.setDoInput(true);

			responseCode = connection.getResponseCode();

			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer res = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				res.append(inputLine);
			}

			JSONObject response = new JSONObject(res.toString());

			AccessToken = response.getString("access_token");
			WriteToConsole("AccessToken ist: " + AccessToken);

		} catch (Exception e) {
			if (responseCode == 500 && APIcallCounter < maxAPIcalls) {
				APIcallCounter++;
				WriteToConsole("" + APIcallCounter);
				UpdateAccessToken();
			} else {
				// ist der Call durchgegangen setzen wir den Counter wieder zurueck
				// ggf. braucht man das else hier auch gar nicht
				APIcallCounter = 0;
			}
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
	}

	public static String GetAccessToken() {
		if (AccessToken == "") {
			UpdateAccessToken();
		}
		return AccessToken;
	}

	public static Boolean HeadForPackage(String PackageID) {
		DataOutputStream dataOut = null;
		BufferedReader in = null;

		int responseCode = 0;

		try {

			System.out.print("\n \n HEAD-API wurde gestartet \n \n");
			String url = "https://blockchain-service.cfapps.eu10.hana.ondemand.com/blockchain/proofOfHistory/api/v1/histories/"
					+ PackageID;

			URL urlObj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.setRequestMethod("HEAD");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Authorization", "Bearer " + AccessToken);
			connection.setRequestProperty("DataServiceVersion", "2.0");

			connection.setDoInput(true);

			responseCode = connection.getResponseCode();
			WriteToConsole("ResponseCode  von Head ist dieses Mal: " + responseCode);
			
			
			if (responseCode == 401) {
				UpdateAccessToken();
				return HeadForPackage(PackageID);
			}

			if (responseCode == 500 && APIcallCounter < maxAPIcalls) {
				APIcallCounter++;
				WriteToConsole("" + APIcallCounter);
				return HeadForPackage(PackageID);
			} else {
				APIcallCounter = 0;
			}
			return responseCode == 200;

		} catch (Exception e) {
			if (responseCode == 500 && APIcallCounter < maxAPIcalls) {
				APIcallCounter++;
				WriteToConsole("" + APIcallCounter);
				return HeadForPackage(PackageID);
			} else {
				// ist der Call durchgegangen setzen wir den Counter wieder zurueck
				// ggf. braucht man das else hier auch gar nicht
				APIcallCounter = 0;
			}
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
		return false;
	}

	public static String GetDataForPackage(String PackageID) {
		DataOutputStream dataOut = null;
		BufferedReader in = null;

		String back = "Nothing Changed";
		int responseCode = 0;

		try {

			System.out.print("\n \n GET-API wurde gestartet \n \n");
			String url = "https://blockchain-service.cfapps.eu10.hana.ondemand.com/blockchain/proofOfHistory/api/v1/histories/"
					+ PackageID;

			URL urlObj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Authorization", "Bearer " + AccessToken);
			connection.setRequestProperty("DataServiceVersion", "2.0");

			connection.setDoInput(true);

			responseCode = connection.getResponseCode();

			WriteToConsole("ResponseCode von Get ist dieses Mal: " + responseCode);

			if (responseCode == 404) {
				return "";
			}

			if (responseCode == 401) {
				UpdateAccessToken();
				return GetDataForPackage(PackageID);
			}

			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer res = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				res.append(inputLine);
			}

			back = res.toString();

		} catch (Exception e) {

			if (responseCode == 500 && APIcallCounter < maxAPIcalls) {
				APIcallCounter++;
				WriteToConsole("" + APIcallCounter);
				return GetDataForPackage(PackageID);
			} else {

				// ist der Call durchgegangen setzen wir den Counter wieder zurueck
				// ggf. braucht man das else hier auch gar nicht

				APIcallCounter = 0;
			}

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
				System.out.print("Fehler im finally");
				e.printStackTrace();
			}
		}

		return back;

	}

	public static Boolean PostForPackage(String PackageID) {
		DataOutputStream dataOut = null;
		BufferedReader in = null;

		int responseCode = 0;

		try {

			System.out.print("\n \n POST-API wurde gestartet \n \n");
			String url = "https://blockchain-service.cfapps.eu10.hana.ondemand.com/blockchain/proofOfHistory/api/v1/histories/"
					+ PackageID;

			URL urlObj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Authorization", "Bearer " + AccessToken);
			connection.setRequestProperty("DataServiceVersion", "2.0");

			connection.setDoInput(true);

			connection.setDoOutput(true);

			responseCode = connection.getResponseCode();
			WriteToConsole("ResponseCode ist dieses Mal: " + responseCode);

			if (responseCode == 401) {
				WriteToConsole("AccessToken will be updated!");
				UpdateAccessToken();
				return PostForPackage(PackageID);
			}

			return responseCode == 201;

		} catch (Exception e) {
			if (responseCode == 500 && APIcallCounter < maxAPIcalls) {
				APIcallCounter++;
				WriteToConsole("" + APIcallCounter);
				return PostForPackage(PackageID);
			} else {
				// ist der Call durchgegangen setzen wir den Counter wieder zurueck
				// ggf. braucht man das else hier auch gar nicht
				APIcallCounter = 0;
			}
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
		return false;
	}

	public static Boolean PatchForPackage(String PackageID, String Update) {
		DataOutputStream dataOut = null;
		BufferedReader in = null;

		int responseCode = 0;

		try {

			System.out.print("\n \n PATCH-API wurde gestartet \n \n");
			String url = "https://blockchain-service.cfapps.eu10.hana.ondemand.com/blockchain/proofOfHistory/api/v1/histories/"
					+ PackageID;

			URL urlObj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			try {
				connection.setRequestMethod("PATCH");
			} catch (ProtocolException ex) {
				allowMethods("PATCH");
				return PatchForPackage(PackageID, Update);
			}
			connection.setRequestProperty("Content-Type", "application/json");

			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Authorization", "Bearer " + AccessToken);
			connection.setRequestProperty("DataServiceVersion", "2.0");

			connection.setDoInput(true);

			connection.setDoOutput(true);
			dataOut = new DataOutputStream(connection.getOutputStream());
			dataOut.writeBytes(Update);
			dataOut.flush();

			responseCode = connection.getResponseCode();
			WriteToConsole("ResponseCode ist dieses Mal: " + responseCode);

			if (responseCode == 401) {
				UpdateAccessToken();
				return PatchForPackage(PackageID, Update);
			}

			if (responseCode == 500 && APIcallCounter < maxAPIcalls) {
				APIcallCounter++;
				WriteToConsole("" + APIcallCounter);
				return PatchForPackage(PackageID, Update);
			} else {
				// ist der Call durchgegangen setzen wir den Counter wieder zurueck
				// ggf. braucht man das else hier auch gar nicht
				APIcallCounter = 0;
			}

			return responseCode == 204;

		} catch (Exception e) {
			if (responseCode == 500 && APIcallCounter < maxAPIcalls) {
				APIcallCounter++;
				WriteToConsole("" + APIcallCounter);
				return PatchForPackage(PackageID, Update);
			} else {
				// ist der Call durchgegangen setzen wir den Counter wieder zurueck
				// ggf. braucht man das else hier auch gar nicht
				APIcallCounter = 0;
			}
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
		return false;
	}

	private static void allowMethods(String... methods) {
		try {
			Field methodsField = HttpURLConnection.class.getDeclaredField("methods");

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

			methodsField.setAccessible(true);

			String[] oldMethods = (String[]) methodsField.get(null);
			Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
			methodsSet.addAll(Arrays.asList(methods));
			String[] newMethods = methodsSet.toArray(new String[0]);

			methodsField.set(null/* static field */, newMethods);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}
}