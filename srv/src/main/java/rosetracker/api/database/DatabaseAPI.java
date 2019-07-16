package rosetracker.api.database;

import java.util.LinkedList;
import java.util.List;

import com.sap.cloud.sdk.hana.connectivity.cds.CDSQuery;
import com.sap.cloud.sdk.hana.connectivity.cds.CDSSelectQueryBuilder;
import com.sap.cloud.sdk.hana.connectivity.cds.CDSSelectQueryResult;
import com.sap.cloud.sdk.service.prov.api.DataSourceHandler;
import com.sap.cloud.sdk.service.prov.api.EntityData;
import com.sap.cloud.sdk.service.prov.api.ExtensionHelper;
import com.sap.cloud.sdk.service.prov.rt.cds.CDSHandler;

public class DatabaseAPI {
	
	public static List<String> GetPackageIDsFromDB(ExtensionHelper h) {
		
		List<String> pckIDs = new LinkedList<String>();
		
		try {
		
			DataSourceHandler dshandler = h.getHandler();
		
			CDSHandler cdsHandler = (CDSHandler) dshandler;
			
			CDSQuery cdsQuery = new CDSSelectQueryBuilder("RoseTrackerDataService.Package")
						                .selectColumns("id_package")
						                .build();
						                
			CDSSelectQueryResult cdsSelectQueryResult = cdsHandler.executeQuery(cdsQuery);
			
			List<EntityData> result = cdsSelectQueryResult.getResult();
			
			for(EntityData ed : result) {
				
				pckIDs.add((String) ed.getMap().get("id_package"));
				
			}
			
		} catch (Exception e) {
			
		}
		
		return pckIDs;
	}

}
