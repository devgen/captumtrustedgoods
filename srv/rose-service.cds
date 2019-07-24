using rosetracker.database.entities as entities from '../db/entities';
using rosetracker.database.views as views from '../db/views';
using rosetracker.database.dummyentities as dummy from '../db/dummyentities';

service RoseTrackerDataService {

	entity TrackingView as projection on views.TrackingView;
	entity PackageView as projection on views.PackageView;
	entity OrderDataViewAll as projection on views.OrderDataViewAll;
	
	entity SensorInput as projection on entities.SensorInput;
	entity Owner as projection on entities.Owner;
	entity Rose as projection on entities.Rose;
	entity Rose_owner as projection on entities.Rose_owner;
	entity Package as projection on entities.Package;
	
	entity OwnerBC as projection on dummy.OwnerBC;
	entity IncidentBC as projection on dummy.IncidentBC;
	
}