using RoseTracker.RoseTrackerDB as database from '../RoseTrackerDB/src/cdsArtifact.hdbcds';


service RoseTrackerDataService {

    entity PackageData as projection on database.PackageData;
    entity IncidentBC as projection on database.IncidentBC;
    entity OwnerBC as projection on database.OwnerBC;
    entity Tracking as projection on database.Tracking;
    entity OrderData as projection on database.OrderData;
    
}