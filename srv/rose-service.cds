using rosetracker.database.cdsContext as database from '../db/data-model';

service RoseTrackerDataService {

	// for inserting test data
	
	entity owner_FORTESTDATAINSERT as projection on database.owner;
	entity rose_FORTESTDATAINSERT as projection on database.rose;
	entity rose_owner_FORTESTDATAINSERT as projection on database.rose_owner;
	entity storage_FORTESTDATAINSERT as projection on database.storage;
	entity package_FORTESTDATAINSERT as projection on database.package;
	entity storage_history_FORTESTDATAINSERT as projection on database.storage_history;
	entity sensor_input_FORTESTDATAINSERT as projection on database.sensor_input;
	entity sensor_package_FORTESTDATAINSERT as projection on database.sensor_package;

	// database views
	
	entity Tracking as projection on database.trackingView;
	entity PackageData as projection on database.packageView;
	entity OrderData as projection on database.orderDataView;
	
	// blockchain views
	
    entity IncidentBC as projection on database.IncidentBC;
    entity OwnerBC as projection on database.OwnerBC;
    
    
}