package rosetracker.handlers.rosetrackerdataservice;

import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
import com.sap.cloud.sdk.service.prov.api.response.ReadResponse;
import com.sap.cloud.sdk.service.prov.api.response.UpdateResponse;
import org.json.JSONObject;

import rosetracker.api.blockchain.ProofOfHistoryAPI;
import rosetracker.api.database.DatabaseAPI;
import rosetracker.comparators.BCOwnerChangeComparator;
import rosetracker.converter.JsonConverter;
import rosetracker.converter.OwnerChangeExpressionConverter;
import rosetracker.dataclasses.BCOwnerChange;

/***
 * Handler class for entity "OwnerBC" of service "RoseTrackerDataService". This
 * handler registers custom handlers for the entity OData operations. For more
 * information, see:
 * https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/6fe3070250ea45b88c35cda209e8324b.html
 */
public class OwnerBCCustomHandler {

	private void WriteToConsole(String msg) {
		System.out.print(" //// " + msg
				+ " \\\\\\\\ ");
	}

	/*
	 * TODO
	 *
	 * Query : implement operations
					- skip: ready for testing
					- top: ready for testing
					- inlineCount: after merge to mta, the request has no method anymore. Why? TODO
					- count: TODO How?
					- filter: comparatos & AND & OR DONE, TODO contains? how? 
					- custom query: TODO
					- oder by: DONE
					- select: DONE : works out of the box
	 * Create : DONE
	 * Read : DONE
	 * Update : DONE (maybe futher testing necessary)
 	 * Delete : DONE
 	 *
	 * TODO: If database is ready:
	 *	- create entry in PackageTable if create or update commit with a unknown Package ID --> ??
	 */


	// ------------------------------------- Data Reading Operations -------------------------------------

	


	@AfterQuery(entity = "OwnerBC", serviceName = "RoseTrackerDataService")
	public QueryResponse afterQueryOrders(QueryRequest req, QueryResponseAccessor res, ExtensionHelper h) {

		try {
			
			WriteToConsole("CUSTOM AFTER QUERY start");
			
			// --- get the data from the Blockchain

			// get all IDs that exist
			List<String> pckIDs = DatabaseAPI.GetPackageIDsFromDB(h);
			
			
			// get BCOwnerChangeData based on the retreived IDs
			List<BCOwnerChange> data = new LinkedList<BCOwnerChange>();
	
			for(String id : pckIDs) {
				
				WriteToConsole(" searching for ID : " + id);
	
				String jsonString = ProofOfHistoryAPI.GetDataForPackage(id);
		
				JsonConverter con = new JsonConverter();
				data.addAll(con.GetBCOwnerChangesFromJSONString(jsonString));
	
			}
			 
			// --- calculate RouteToNextOwner for the UI
			for(int i = 0; i < data.size(); i++) {
				 	
				BCOwnerChange change = data.get(i);
				
				// add own Coordinates
				
				String routeString = "";
				routeString += change.CurrentLongitude + ";";
				routeString += change.CurrentLatitude + ";0; ";
				
				if( i < (data.size()-1)) {
					
					// add Coordinates for next owner 
					
					BCOwnerChange nextChange = data.get(i+1);
					routeString += nextChange.CurrentLongitude + ";";
					routeString += nextChange.CurrentLatitude + ";0";
					
				} else {
					
					// add route to itself if last Owner
					
					routeString += change.CurrentLongitude + ";";
					routeString += change.CurrentLatitude + ";0";
					
				}
				
				change.RouteToNextOwner = routeString;
			 	
			}
			
			// -- apply the OData functions
			
			
			// count ( --> isCountCall() - boolean ) --> only return number of entries
			
			if(req.isCountCall()) {
				
				// TODO
				
				// how to return just a number?
				
			} 
			
			// top, skip, orderBy and filter
			data = applyQueryArguments(req, data);
	
	
			// --- build response
			List<EntityData> responseData = new LinkedList<EntityData>();
			
			for(BCOwnerChange change : data) {
	
				EntityData entityChange = EntityData.createFromMap(change.ToMap(),
				BCOwnerChange.GetKeys() , "OwnerBC");
		
				responseData.add(entityChange);
	
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
	
	
	
	private List<BCOwnerChange> sortBCOwnerChange(List<BCOwnerChange> data, List<OrderByExpression> orderBys) {

		BCOwnerChangeComparator comparator = new BCOwnerChangeComparator();
		comparator.setOrderByExpression(orderBys);
		
		data.sort(comparator);
		
		return data;
	}
	
	
	
	private List<BCOwnerChange> applyQueryArguments(QueryRequest req, List<BCOwnerChange> data) {
		
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
		
			data = OwnerChangeExpressionConverter.executeExpressionNodeOnData(exNodes.get(0), data);
		
			WriteToConsole(" HANDLER data size after filter:  " + data.size());
		
		}
		
		
		
		// getCustomQueryOptions() ??? Map<String, String> --> ex /EPMSampleService/Products?Search="abc" --> <Search,abc>
		
		
		
		// --- change order of data
		 
		data = sortBCOwnerChange(data, req.getOrderByProperties());
		
		
		
		
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
	
	
	
	// for testing purposes for read:
	//
	// for general reading (not needed anymore)
	// https://[appID]-srv.cfapps.eu10.hana.ondemand.com/odata/v2/RoseTrackerDataService/OwnerBC(PackageID='PackageID2',TimeStamp=datetimeoffset'2004-05-23T14:25:10Z')
	//
	// for Blockchain testing 
	// (PackageID='700',TimeStamp=datetimeoffset'2019-07-06T14:13:12Z')

	@Read(entity = "OwnerBC", serviceName = "RoseTrackerDataService")
	public ReadResponse readOwnerBC(ReadRequest req, ExtensionHelper extensionHelper) {

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
	
			List<BCOwnerChange> data = con.GetBCOwnerChangesFromJSONString(jsonString);
	
			// Filter Data from Parser after timestamp
	
			BCOwnerChange match = null;
			
			int matchIndex = -1;
	
			for(int i = 0; i < data.size(); i++) {
	
				BCOwnerChange change = data.get(i);
	
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
			
			/*
				Svenjas Request: Add further field: RouteToNextOwner
					"X;Y;0; X;Y;0"
					
				No orderby necessary, since Blockchain saves chronologicaly and is also read out
			
			 */
			 
			 if(matchIndex != -1) {
			 	
			 	// add own Coordinates
				
				String routeString = "";
				routeString += match.CurrentLatitude + ";";
				routeString += match.CurrentLongitude + ";0; ";
				
				if( matchIndex < (data.size()-1)) {
					
					// add Coordinates for next owner 
					
					BCOwnerChange nextChange = data.get(matchIndex+1);
					routeString += nextChange.CurrentLatitude + ";";
					routeString += nextChange.CurrentLongitude + ";0";
					
				} else {
					
					// add route to itself if last Owner
					
					routeString += match.CurrentLatitude + ";";
					routeString += match.CurrentLongitude + ";0";
					
				}
				
				match.RouteToNextOwner = routeString;
			 	
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


	@Create(entity = "OwnerBC", serviceName = "RoseTrackerDataService")
	public CreateResponse createOwnerBC(CreateRequest req, ExtensionHelper extensionHelper) {
		
		// POTENTIAL PROBLEM: return EntityData needs a timestamp. 
		//				-->	dummy is ok 
		
		try {
			
			WriteToConsole("CUSTOM CREATE start");
			
			// get data from request
			
			EntityData entityToCreate = req.getData();
			
			Map<String,Object> reqMap = entityToCreate.getMap();
			
			// debugging
			
			// WriteToConsole("reqMap size: " + reqMap.size());
			
			// Collection<Object> reqValues = reqMap.values();
			
			// for(Object o : reqValues) {
			// 	String oString = o.toString();
			// 	WriteToConsole("CREATE VALUE: " + oString);
			// }
			
			InsertOwnerChangeDataInBC(reqMap);
			
			// parse Map to entityData 
			
			EntityData mappedEntity = EntityData.createFromMap(reqMap, BCOwnerChange.GetKeys(), "OwnerBC");
			
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
	
	
	@Update(entity = "OwnerBC", serviceName = "RoseTrackerDataService")
	public UpdateResponse updateOwnerBC(UpdateRequest req, ExtensionHelper extensionHelper) {
		
		// UNDERSTANDING: 
		//		Check whether all attributes are here (ID, OwnerID, Lat, Lon) necessary? 
		
		try {
			
			WriteToConsole("CUSTOM UPDATE start");
			
			// Retrieve keys
			Map<String, Object> keys = req.getMapData();
			
			WriteToConsole("update keys size: " + keys.size());
			
			// debugging 
			// Collection<Object> reqValues = keys.values();
			// Set<String> keySet = keys.keySet();
			
			// for(String s : keySet) {
			// 	WriteToConsole("UPDATE KEY: " + s);
			// }
			
			// for(Object o : reqValues) {
			// 	if(o != null) {
			// 		String oString = o.toString();
			// 		WriteToConsole("UPDATE VALUE: " + oString);
			// 	} else {
			// 		WriteToConsole("UPDATE VALUE: NULL VALUE WHY?");
			// 	}
			// }
			
			InsertOwnerChangeDataInBC(keys);

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
	
	
	private void InsertOwnerChangeDataInBC(Map<String, Object> data) {
		
		WriteToConsole("INSERT TO OWNER CHANGE TO BC start");
		
		String packageID = (String) data.get("PackageID");
		//GregorianCalendar timestamp = (GregorianCalendar) keys.get("TimeStamp");
		String ownerID = (String) data.get("OwnerID");
		BigDecimal currLatBD = (BigDecimal) data.get("CurrentLatitude");
		BigDecimal currLonBD = (BigDecimal) data.get("CurrentLongitude");
		
		Double currLat = 0.0;
		if(currLatBD != null) {
			currLat = currLatBD.doubleValue();
		}
		
		Double currLon = 0.0;
		if(currLonBD != null) {
			currLon = currLonBD.doubleValue();
		}
		
		// debugging
		// WriteToConsole( " ---- READ VALUES ---- " + 
		// 		"PackageID: " + packageID + " ---- " + 
		// 		"OwnerID:   " + ownerID + " ---- " + 
		// 		"CurrLat:   " + currLat + " ---- " + 
		// 		"CurrLon:   " + currLon + " ---- ");
		
		// Create ID in Blockchain, if it does not exist already
		
		if(!ProofOfHistoryAPI.HeadForPackage(packageID)) {
			
			ProofOfHistoryAPI.PostForPackage(packageID);
		}
		
		// parse map to JSON
		
		Map<String,Object> innerJSON = new HashMap<String,Object>();
		innerJSON.put("OwnerID", ownerID);
		innerJSON.put("CurrentLatitude", currLat);
		innerJSON.put("CurrentLongitude", currLon);
		
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put("OwnerChange",innerJSON);
		
		// debugging
		// WriteToConsole(" ---- BUILD JSON OBJECT ---- " + jsonObj.toString());
		
		// PATCH JSON
		
		ProofOfHistoryAPI.PatchForPackage(packageID, jsonObj.toString());
		
		WriteToConsole("INSERT TO OWNER CHANGE TO BC end");
		
	}
	
	
	// ------------------------------------------ Not supported Methods ------------------------------------------
	
	
	@Delete(entity = "OwnerBC", serviceName = "RoseTrackerDataService")
	public DeleteResponse deleteOwnerBC(DeleteRequest req, ExtensionHelper extensionHelper) {
		
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