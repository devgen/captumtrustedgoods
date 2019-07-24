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

	// ------------------------------------------------- START CUSTOWN.1 read -------------------------------------------------

	// overrides read operation on OwnerBC table
	// read is triggered, if a specific data entry is requested
	// this can be achieved with the following pattern: ODataService/Table(PRIMARY_KEY='VALUE')
	@Read(entity = "OwnerBC", serviceName = "RoseTrackerDataService")
	public ReadResponse readOwnerBC(ReadRequest req, ExtensionHelper extensionHelper) {

		try {
			
			// Get ID from request
			Map<String, Object> reqKeys = req.getKeys();
	
			String packageID = (String) reqKeys.get("PackageID");
			GregorianCalendar timestamp = (GregorianCalendar) reqKeys.get("TimeStamp");
	
			// Get Data from API
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
			 
			 // adds a new field, as requested from the UI
			 // combines the geocoordinates from two successive data entries for easier presentation of routes in the UI
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
	
	// ------------------------------------------------- END CUSTOWN.1 read -------------------------------------------------


	// ------------------------------------------------- START CUSTOWN.2 data manipulation -------------------------------------------------

	// overrides create operation on OwnerBC table 
	// create is triggered by a post request to the OData service on a table
	@Create(entity = "OwnerBC", serviceName = "RoseTrackerDataService")
	public CreateResponse createOwnerBC(CreateRequest req, ExtensionHelper extensionHelper) {
		
		try {
			
			// get data from request
			EntityData entityToCreate = req.getData();
			
			Map<String,Object> reqMap = entityToCreate.getMap();
			
			// execute data insert into Blockchain
			InsertOwnerChangeDataInBC(reqMap);
			
			// parse Map to entityData 
			EntityData mappedEntity = EntityData.createFromMap(reqMap, BCOwnerChange.GetKeys(), "OwnerBC");
			
			// return repsonse
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
	
	
	// overrides update operation on OwnerBC table 
	// update is triggered by a patch (or put) request to the OData service on a specific data entry
	// since a Blockchain runs in the Background, updating existing data is not possible, therefore update acts the same as create
	// differences to the implementation of create origin in the different method definition (given parameters and requested return format)
	@Update(entity = "OwnerBC", serviceName = "RoseTrackerDataService")
	public UpdateResponse updateOwnerBC(UpdateRequest req, ExtensionHelper extensionHelper) {
		
		try {
			
			// Retrieve keys
			Map<String, Object> keys = req.getMapData();
			
			// execute data insert into Blockchain
			InsertOwnerChangeDataInBC(keys);

			// return repsonse
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
	
	// implementation of adding data to the Blockchian, called by create and update
	private void InsertOwnerChangeDataInBC(Map<String, Object> data) {
		
		// getting the data of the given map
		String packageID = (String) data.get("PackageID");
		String ownerID = (String) data.get("OwnerID");
		BigDecimal currLatBD = (BigDecimal) data.get("CurrentLatitude");
		BigDecimal currLonBD = (BigDecimal) data.get("CurrentLongitude");
		
		// transforming the decimal values in doubles, for easier usage
		Double currLat = 0.0;
		if(currLatBD != null) {
			currLat = currLatBD.doubleValue();
		}
		Double currLon = 0.0;
		if(currLonBD != null) {
			currLon = currLonBD.doubleValue();
		}
		
		// Create ID in Blockchain, if it does not exist already
		// to prevent a 404 repsonse from patch to PoH API
		// for further information see the API description of the PoH API
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
		
		// PATCH JSON
		ProofOfHistoryAPI.PatchForPackage(packageID, jsonObj.toString());
		
	}
	
	// overrites delete operation on OwnerBC table
	// called with a delete call on a specific data entry
	// implemented just to prevent that the OData service wants to access the table
	@Delete(entity = "OwnerBC", serviceName = "RoseTrackerDataService")
	public DeleteResponse deleteOwnerBC(DeleteRequest req, ExtensionHelper extensionHelper) {
		
		try {
			
			// Do Nothing. It's a Blockchain, there is no deleting. 
			// -- Potentially, PoH supports deleting, but we don't want to support this in our use case
			
			return DeleteResponse.setSuccess().response();
			
		} catch (Exception e) {
			ErrorResponse errorResponse = ErrorResponse.getBuilder()
					.setMessage(e.getMessage())
					.setStatusCode(500)
					.setCause(e)
					.response();
			return DeleteResponse.setError(errorResponse);
			
		}
	}
	
	// ------------------------------------------------- START CUSTOWN.3 query  -------------------------------------------------

	// called after the query on OwnerBC table
	// since query cannot be overriten, the OData service executes query on the database table, but the result is not used and is overriten here
	// is called when by navigating to a table or performing a get request to a table
	@AfterQuery(entity = "OwnerBC", serviceName = "RoseTrackerDataService")
	public QueryResponse afterQueryOrders(QueryRequest req, QueryResponseAccessor res, ExtensionHelper h) {

		try {
			
			// get all PackageIDs from the database
			List<String> pckIDs = DatabaseAPI.GetPackageIDsFromDB(h);
			
			List<BCOwnerChange> data = new LinkedList<BCOwnerChange>();
			
			// checking if just one entity is requested
			// is done for performance purposes since every PoH API call coses time and resources
			ExpressionNode expNode = null;
				
			if(req.getQueryExpression() != null) {
				expNode = req.getQueryExpression().getNodes().get(0);
			}
				
			if(expNode != null && OwnerChangeExpressionConverter.CheckIfFilterIsSingle(expNode)) {
				
				pckIDs.clear();
				
				String foundPackage = OwnerChangeExpressionConverter.ExecuteSingleFilterID(expNode);
				
				pckIDs.add(foundPackage);
				
			} 
	
			// getting for each PackageID the data out of the Blockchian
			for(String id : pckIDs) {
	
				String jsonString = ProofOfHistoryAPI.GetDataForPackage(id);
		
				JsonConverter con = new JsonConverter();
				data.addAll(con.GetBCOwnerChangesFromJSONString(jsonString));
	
			}
			 
			// calculate RouteToNextOwner for the UI (same as in read)
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
			
			// Apply top, skip, orderBy and filter
			data = applyQueryArguments(req, data);
	
	
			// build response
			List<EntityData> responseData = new LinkedList<EntityData>();
			
			for(BCOwnerChange change : data) {
	
				EntityData entityChange = EntityData.createFromMap(change.ToMap(),
				BCOwnerChange.GetKeys() , "OwnerBC");
		
				responseData.add(entityChange);
	
			}
			
			// NOTE: Currently count and inline count are not supported due to implementation problems
			
			return QueryResponse.setSuccess().setData(responseData).response();
			
		} catch (Exception e) {
			
			ErrorResponse errorResponse = ErrorResponse.getBuilder()
				.setMessage(e.getMessage())
				.setStatusCode(500)
				.setCause(e)
				.response();
			return QueryResponse.setError(errorResponse);
			
		}

	}
	
	// implementation of order by
	private List<BCOwnerChange> sortBCOwnerChange(List<BCOwnerChange> data, List<OrderByExpression> orderBys) {

		BCOwnerChangeComparator comparator = new BCOwnerChangeComparator();
		comparator.setOrderByExpression(orderBys);
		
		data.sort(comparator);
		
		return data;
	}
	
	// implementation of data filtering methods
	private List<BCOwnerChange> applyQueryArguments(QueryRequest req, List<BCOwnerChange> data) {
		
		Expression filterExp = req.getQueryExpression();
		
		if(filterExp != null) {
			
			List<ExpressionNode> exNodes = filterExp.getNodes();
			
			data = OwnerChangeExpressionConverter.executeExpressionNodeOnData(exNodes.get(0), data);
			
		}
		 
		// order by 
		data = sortBCOwnerChange(data, req.getOrderByProperties());
		
		
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
	
	// ------------------------------------------------- END CUSTOWN.3 query  -------------------------------------------------
}