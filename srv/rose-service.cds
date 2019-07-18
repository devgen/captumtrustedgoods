using rosetracker.database.entities as entities from '../db/entities';
using rosetracker.database.views as views from '../db/views';
using rosetracker.database.dummyentities as dummy from '../db/dummyentities';

service RoseTrackerDataService {

	entity TrackingView as projection on views.TrackingView;
	entity PackageView as projection on views.PackageView;
	entity OrderDataView as projection on views.OrderDataView;
	
	
	entity PackageDeliveryStatus as projection on views.PackageDeliveryStatus;
	entity tracpack as projection on views.tracpack;
	entity packagewithinicdentstatus as projection on views.packagewithinicdentstatus;
	entity packOwner as projection on views.packOwner;
	
	
	
	entity SensorInput as projection on entities.SensorInput;
	//entity SensorPackage as projection on entities.SensorPackage;
	entity Owner as projection on entities.Owner;
	entity Rose as projection on entities.Rose;
	entity Rose_owner as projection on entities.Rose_owner;
	//entity StorageMapping as projection on entities.StorageData;
	entity Package as projection on entities.Package;
	//entity Storage_history as projection on entities.Storage_history;
	
	entity OwnerBC as projection on dummy.OwnerBC;
	entity IncidentBC as projection on dummy.IncidentBC;
	
}