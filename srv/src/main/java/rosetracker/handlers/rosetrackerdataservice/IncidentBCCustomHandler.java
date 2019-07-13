package rosetracker.handlers.rosetrackerdataservice;

import java.util.List;
import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.sap.cloud.sdk.service.prov.api.EntityData;
import com.sap.cloud.sdk.service.prov.api.ExtensionHelper;
import com.sap.cloud.sdk.service.prov.api.annotations.AfterQuery;
import com.sap.cloud.sdk.service.prov.api.filter.Expression;
import com.sap.cloud.sdk.service.prov.api.filter.ExpressionNode;
import com.sap.cloud.sdk.service.prov.api.operations.Create;
import com.sap.cloud.sdk.service.prov.api.operations.Delete;
import com.sap.cloud.sdk.service.prov.api.operations.Read;
import com.sap.cloud.sdk.service.prov.api.operations.Update;
import com.sap.cloud.sdk.service.prov.api.request.CreateRequest;
import com.sap.cloud.sdk.service.prov.api.request.DeleteRequest;
import com.sap.cloud.sdk.service.prov.api.request.OrderByExpression;
import com.sap.cloud.sdk.service.prov.api.request.QueryRequest;
import com.sap.cloud.sdk.service.prov.api.request.ReadRequest;
import com.sap.cloud.sdk.service.prov.api.request.UpdateRequest;
import com.sap.cloud.sdk.service.prov.api.response.CreateResponse;
import com.sap.cloud.sdk.service.prov.api.response.DeleteResponse;
import com.sap.cloud.sdk.service.prov.api.response.ErrorResponse;
import com.sap.cloud.sdk.service.prov.api.response.QueryResponse;
import com.sap.cloud.sdk.service.prov.api.response.QueryResponseAccessor;
import com.sap.cloud.sdk.service.prov.api.response.QueryResponseBuilder;
import com.sap.cloud.sdk.service.prov.api.response.ReadResponse;
import com.sap.cloud.sdk.service.prov.api.response.UpdateResponse;

import org.json.JSONObject;

import rosetracker.api.blockchain.ProofOfHistoryAPI;
import rosetracker.comparators.BCIncidentComparator;
import rosetracker.converter.IncidentExpressionConverter;
import rosetracker.converter.JsonConverter;
import rosetracker.dataclasses.BCIncident;

public class IncidentBCCustomHandler {
	
	private void WriteToConsole(String msg) {
		System.out.print(" //// " + msg
				+ " \\\\\\\\ ");
	}

	@AfterQuery(entity = "IncidentBC", serviceName = "RoseTrackerDataService")
	public QueryResponse queryIncidentBC(QueryRequest req, QueryResponseAccessor res, ExtensionHelper h) {
		
		try {
			
			WriteToConsole("CUSTOM AFTER QUERY start");

			List<String> pckIDs = new LinkedList<String>();
			
			// TODO Get Package IDs from Package Data Table
			
			// -- dummy
			
			pckIDs.add("700");
			pckIDs.add("705");
			pckIDs.add("706");
			pckIDs.add("707");
			pckIDs.add("708");
			
			// !-- dummy
			
			// TODO handle empty List --> no packages in db
			
	
			List<BCIncident> data = new LinkedList<BCIncident>();
	
			for(String id : pckIDs) {
	
				String jsonString = ProofOfHistoryAPI.GetDataForPackage(id);
		
				JsonConverter con = new JsonConverter();
				data.addAll(con.GetBCIncidentsFromJSONString(jsonString));
	
			}
			
			// count ( --> isCountCall() - boolean ) --> only return number of entries
			
			if(req.isCountCall()) {
				
				// TODO
				
				// how to return just a number?
				
			} 
			
			// Apply top, skip, orderBy and filter
			data = applyQueryArguments(req, data);
	
	
			// build response
			List<EntityData> responseData = new LinkedList<EntityData>();
			
			for(BCIncident change : data) {
	
				EntityData entityIncident = EntityData.createFromMap(change.ToMap(),
				BCIncident.GetKeys() , "IncidentBC");
		
				responseData.add(entityIncident);
	
			}
	
			WriteToConsole("CUSTOM AFTER QUERY before return");
			
			//if(req.isInlineCountCall()) {
				
				//QueryResponseBuilder tmp = QueryResponse.setSuccess().setData(responseData).setInlineCount(responseData.size());
				
				//return tmp.response();
				
			//} else {
				
				return QueryResponse.setSuccess().setData(responseData).response();
				
			//}
	
				
		} catch (Exception e) {
			
			ErrorResponse errorResponse = ErrorResponse.getBuilder()
				.setMessage(e.getMessage())
				.setStatusCode(500)
				.setCause(e)
				.response();
			return QueryResponse.setError(errorResponse);
			
		}
	}
	
	
	private List<BCIncident> sortBCIncident(List<BCIncident> data, List<OrderByExpression> orderBys) {

		BCIncidentComparator comparator = new BCIncidentComparator();
		comparator.setOrderByExpression(orderBys);
		
		data.sort(comparator);
		
		return data;
	}
	
	
	private List<BCIncident> applyQueryArguments(QueryRequest req, List<BCIncident> data) {
		
		// --- alter data list according to params ---
		
		// filter ( --> getQueryExpression() - filterExpression (?) )
		// https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/dac41e0a0c784eab837c7ec9ccad18d3.html
		
		Expression filterExp = req.getQueryExpression();
		
		if(filterExp != null) {
			
			List<ExpressionNode> exNodes = filterExp.getNodes();
			
			for(ExpressionNode e : exNodes) {
				
				WriteToConsole("Node: " + e.toString());
				
			}
			
			WriteToConsole(" HANDLER data size before filter:  " + data.size());
		
			data = IncidentExpressionConverter.executeExpressionNodeOnData(exNodes.get(0), data);
		
			WriteToConsole(" HANDLER data size after filter:  " + data.size());
		
		}
		
		
		
		// getCustomQueryOptions() ??? Map<String, String> --> ex /EPMSampleService/Products?Search="abc" --> <Search,abc>
		
		
		
		// --- change order of data
		 
		data = sortBCIncident(data, req.getOrderByProperties());
		
		
		
		
		// first do skip then top --> skip 1 and top 5 retruns indeces 1-6 
		//			just top 5 returns 0-5
		
		//get skip value from request
		int skipValue = req.getSkipOptionValue();
		
		// check if skip is requested in URI
		if(skipValue != -1)  {
			
			// if skip value exeeds data size, everthing is removed
			if(skipValue >= data.size()) {
				
				data.clear();
				
			} else {
				
				// remove the first [SKIPVALUE] items of the data list
				
				for(int i = 0; i < skipValue; i++) {
					
					data.remove(i);
					
				}
				
			}
			
		}
		
		// get top value from request
		int topValue = req.getTopOptionValue();
		
		// check if top is requested in URI and if data contains something
		if(topValue != -1 && !data.isEmpty()) {
			
			// if top value exceeds the current size of the data list, nothing is done here
			if(topValue < data.size()) {
				
				// remove all list items after the top value
				for(int i = topValue; i < data.size(); i++) {
					
					data.remove(i);
					
				}
				
			}
			
		}
		
		return data;
		
	}

	@Read(entity = "IncidentBC", serviceName = "RoseTrackerDataService")
	public ReadResponse readIncidentBC(ReadRequest req, ExtensionHelper extensionHelper) {
		
		try {
			
			WriteToConsole("CUSTOM READ start");

			// Get ID from request

			Map<String, Object> reqKeys = req.getKeys();
	
			String packageID = (String) reqKeys.get("PackageID");
			GregorianCalendar timestamp = (GregorianCalendar) reqKeys.get("TimeStamp");
	
			// Get Data from API
			
			// TODO: handle empty String --> no resource to id 
	
			String jsonString = ProofOfHistoryAPI.GetDataForPackage(packageID);
	
			// Parse Data from API
	
			JsonConverter con = new JsonConverter();
	
			List<BCIncident> data = con.GetBCIncidentsFromJSONString(jsonString);
	
			// Filter Data from Parser after timestamp
	
			BCIncident match = null;
			
			int matchIndex = -1;
	
			for(int i = 0; i < data.size(); i++) {
	
				BCIncident change = data.get(i);
	
				if(change.TimeStamp.getTime() == timestamp.getTimeInMillis()) {
					match = change;
					matchIndex = i;
					break;
				}
			}
			
			if(match == null) {

				WriteToConsole("CUSTOM READ no match for timestamp");
				
				// TODO return error 
	
			}
			
			WriteToConsole("CUSTOM READ before return");
	
			// return match
			
			return ReadResponse.setSuccess().setData(match.ToMap()).response();
			
		} catch (Exception e) {
			
			ErrorResponse errorResponse = ErrorResponse.getBuilder()
				.setMessage(e.getMessage())
				.setStatusCode(500)
				.setCause(e)
				.response();
			return ReadResponse.setError(errorResponse);
			
		}
	}


	// ------------------------------------- Data Manipulation Operations -------------------------------------


	@Create(entity = "IncidentBC", serviceName = "RoseTrackerDataService")
	public CreateResponse createIncidentBC(CreateRequest req, ExtensionHelper extensionHelper) {
		
		
		try {
			
			WriteToConsole("CUSTOM CREATE start");
			
			// get data from request
			
			EntityData entityToCreate = req.getData();
			
			Map<String,Object> reqMap = entityToCreate.getMap();
			
			InsertIncidentDataInBC(reqMap);
			
			// parse Map to entityData 
			
			EntityData mappedEntity = EntityData.createFromMap(reqMap, BCIncident.GetKeys(), "IncidentBC");
			
			// return repsonse
			
			WriteToConsole("CUSTOM CREATE before return");
			
			return CreateResponse.setSuccess().setData(mappedEntity).response();
			
			
		} catch (Exception e) {
			
			ErrorResponse errorResponse = ErrorResponse.getBuilder()
					.setMessage(e.getMessage())
					.setStatusCode(500)
					.setCause(e)
					.response();
			return CreateResponse.setError(errorResponse);
			
		}
	}
	

	@Update(entity = "IncidentBC", serviceName = "RoseTrackerDataService")
	public UpdateResponse updateIncidentBC(UpdateRequest req, ExtensionHelper extensionHelper) {
		
		
		try {
			
			WriteToConsole("CUSTOM UPDATE start");
			
			// Retrieve keys
			Map<String, Object> keys = req.getMapData();
			
			WriteToConsole("update keys size: " + keys.size());
			
			InsertIncidentDataInBC(keys);

			WriteToConsole("CUSTOM UPDATE before return");

			return UpdateResponse.setSuccess().response();
			
		} catch (Exception e) {
			
			ErrorResponse errorResponse = ErrorResponse.getBuilder()
					.setMessage(e.getMessage())
					.setStatusCode(500)
					.setCause(e)
					.response();
			return UpdateResponse.setError(errorResponse);
			
		}
	}
	
	
	private void InsertIncidentDataInBC(Map<String, Object> data) {
		
		WriteToConsole("INSERT TO OWNER CHANGE TO BC start");
		
		String packageID = (String) data.get("PackageID");
		BigDecimal currLatBD = (BigDecimal) data.get("CurrentLatitude");
		BigDecimal currLonBD = (BigDecimal) data.get("CurrentLatitude");
		BigDecimal mintempBD = (BigDecimal) data.get("MinTemperature");
		BigDecimal maxtempBD = (BigDecimal) data.get("MaxTemperature");
		BigDecimal minhumBD = (BigDecimal) data.get("MinHumidity");
		BigDecimal maxhumBD = (BigDecimal) data.get("MaxHumidity");
		String description = (String) data.get("Description");
		
		Double currLat = 0.0;
		if(currLatBD != null) {
			currLat = currLatBD.doubleValue();
		}
		Double currLon = 0.0;
		if(currLonBD != null) {
			currLon = currLonBD.doubleValue();
		}
		Double minTemp = 0.0;
		if(mintempBD != null) {
			minTemp = mintempBD.doubleValue();
		}
		Double maxTemp = 0.0;
		if(maxtempBD != null) {
			maxTemp = maxtempBD.doubleValue();
		}
		Double minHum = 0.0;
		if(minhumBD != null) {
			minHum = minhumBD.doubleValue();
		}
		Double maxHum = 0.0;
		if(maxhumBD != null) {
			maxHum = maxhumBD.doubleValue();
		}
		
		// Create ID in Blockchain, if it does not exist already
		
		if(!ProofOfHistoryAPI.HeadForPackage(packageID)) {
			
			ProofOfHistoryAPI.PostForPackage(packageID);
		}
		
		// parse map to JSON
		
		Map<String,Object> innerJSON = new HashMap<String,Object>();
		innerJSON.put("CurrentLatitude", currLat);
		innerJSON.put("CurrentLongitude", currLon);
		innerJSON.put("MinTemperature", minTemp);
		innerJSON.put("MaxTemperature", maxTemp);
		innerJSON.put("MinHumidity", minHum);
		innerJSON.put("MaxHumidity", maxHum);
		innerJSON.put("Description", description);
		
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put("Incident",innerJSON);
		
		// PATCH JSON
		
		ProofOfHistoryAPI.PatchForPackage(packageID, jsonObj.toString());
		
		WriteToConsole("INSERT TO OWNER CHANGE TO BC end");
		
	}
	
	// ------------------------------------------ Not supported Methods ------------------------------------------
	
	
	@Delete(entity = "IncidentBC", serviceName = "RoseTrackerDataService")
	public DeleteResponse deleteIncidentBC(DeleteRequest req, ExtensionHelper extensionHelper) {
		
		try {
			
			WriteToConsole("CUSTOM DELETE");
			
			// Do Nothing. It's a Blockchain, there is no deleting. 
			// -- Potentially, PoH supports deleting, but we don't want to support this in our use case
			
			return DeleteResponse.setSuccess().response();
			
		} catch (Exception e) {
			
			// Return an instance of DeleteResponse containing the error in case
			// of failure
			ErrorResponse errorResponse = ErrorResponse.getBuilder()
					.setMessage(e.getMessage())
					.setStatusCode(500)
					.setCause(e)
					.response();
			return DeleteResponse.setError(errorResponse);
			
		}
	}

}