package rosetracker.converter;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import rosetracker.dataclasses.BCIncident;
import rosetracker.dataclasses.BCOwnerChange;

public class JsonConverter {
	
	public List<BCIncident> GetBCIncidentsFromJSONString(String jsonString) {

		List<BCIncident> back = new LinkedList<BCIncident>();
		
	// {
	// 	"key": "PackageID1",
	// 	"value": {
	// 		"timestamp": "2019-07-04T19:48:32Z",
	// 		"update": {
	// 			"OwnerID": "SecondID"
	// 		}
	// 	}
	// }
	// -----------------------------------------------------------------------------
	// /histories
	// {
	// 	"id": "dev_PackageID1",
	// 	"updates": [
	// 		{
	// 			"timestamp": "2019-07-04T17:53:31Z",
	// 			"update": {
	// 				"OwnerChange": {
	// 					"OwnerID": "BC OWNER ID"
	// 					attr
	// 				}
	// 			}
	// 			"timestamp": "2019-07-04T17:53:31Z",
	// 			"update": {
	// 				"Incident": {
	// 					"OwnerID": "BC OWNER ID"
	// 					attr
	// 				}
	// 			}
	// 		}
	// 	]
	// }
	

		try {

			JSONObject jInput = new JSONObject(jsonString);

			String PackageID = jInput.optString("id");
			JSONArray jUpdates = jInput.optJSONArray("updates");
			

			for (int i = 0; i < jUpdates.length(); i++) {
				
				JSONObject elem = jUpdates.getJSONObject(i);
				
				String zeitString = elem.optString("timestamp");
				zeitString = zeitString.replace("T", " ");
				zeitString = zeitString.replace("Z", "");
				Timestamp zeitstempel = Timestamp.valueOf(zeitString);
				

				JSONObject updateObject = elem.optJSONObject("update");
				
				JSONObject eventType = updateObject.optJSONObject("Incident");


				if (eventType != null) {

					BCIncident tmpIncident = new BCIncident();
					
					tmpIncident.TimeStamp = zeitstempel;
					tmpIncident.PackageID = PackageID;
					tmpIncident.CurrentLatitude = eventType.getDouble("CurrentLatitude");
					tmpIncident.CurrentLongitude = eventType.getDouble("CurrentLongitude");
					tmpIncident.MinTemperature = eventType.getDouble("MinTemperature");
					tmpIncident.MaxTemperature = eventType.getDouble("MaxTemperature");
					tmpIncident.MinHumidity = eventType.getDouble("MinHumidity");
					tmpIncident.MaxHumidity = eventType.getDouble("MaxHumidity");	
					
										
					back.add(tmpIncident);
				}
				
			}
						

		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Fehler im JsonConverter bei Incident!!!");
		}

		return back;

	}
	

	public List<BCOwnerChange> GetBCOwnerChangesFromJSONString(String jsonString) {

		List<BCOwnerChange> back = new LinkedList<BCOwnerChange>();

		try {

			JSONObject jInput = new JSONObject(jsonString);

			String PackageID = jInput.optString("id");
			JSONArray jUpdates = jInput.optJSONArray("updates");

			for (int i = 0; i < jUpdates.length(); i++) {

				JSONObject elem = jUpdates.getJSONObject(i);

				String zeitString = elem.optString("timestamp");
				zeitString = zeitString.replace("T", " ");
				zeitString = zeitString.replace("Z", "");
				Timestamp zeitstempel = Timestamp.valueOf(zeitString);

				JSONObject updateObject = elem.optJSONObject("update");
				JSONObject eventType = updateObject.optJSONObject("OwnerChange");

				if (eventType != null) {

					BCOwnerChange tmpOwnerChange = new BCOwnerChange();

					tmpOwnerChange.TimeStamp = zeitstempel;
					tmpOwnerChange.PackageID = PackageID;
					tmpOwnerChange.CurrentOwner = eventType.getString("OwnerID");
					tmpOwnerChange.CurrentLatitude = eventType.getDouble("CurrentLatitude");
					tmpOwnerChange.CurrentLongitude = eventType.getDouble("CurrentLongitude");

					
					back.add(tmpOwnerChange);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Fehler im JsonConverter bei OwnerChange!!!");
		}
		return back;


	}
}
