namespace rosetracker.database.views;

using rosetracker.database.entities as entities from '../db/entities';


// ----------------------------------- Tracking View START -----------------------------------

view TrackingView as select from 
entities.SensorInput as sinp {
	
	key sinp.id_package as PackageID,
	key sinp.start_time as TimeStamp,
	sinp.min_temp as MinTemp,
	sinp.max_temp as MaxTemp,
	sinp.min_humi as MinHum,
	sinp.max_humi as MaxHum,
	sinp.latitude as CurrentLat,
	sinp.longitude as CurrentLong,
	sinp.incident as Incident 
};

// ------------------------------------ Tracking View END ------------------------------------


// ------------------------------------ Package View START ------------------------------------

view packOwner as select from 
packagewithinicdentstatus as pack join
entities.Owner as owner 
on pack.id_currentOwner = owner.id_owner {
	key pack.id_package,
	pack.id_recipent,	
	pack.id_rosetype,
	pack.id_currentOwner,
	pack.lastOwnerChangeLat,
	pack.lastOwnerChangeLon,
	pack.lastOwnerChangeTime,	
	pack.delivery_active,
	pack.incident_status,
	owner.ownername
};

view PackageView as select from 
packOwner as paOW join
entities.Rose as rose 
on paOW.id_rosetype = rose.id_rosetype {
	
	key paOW.id_package as PackageID,
	rose.rosetype as RoseType,
	paOW.ownername as OwnerName,
	paOW.id_recipent as ReceipientID,
	paOW.incident_status as IncidentStatus,
	paOW.delivery_active as ActiveStatus,
	rose.opening_time as OpenSpeed,
	rose.vase_life as Vaselife,
	rose.color as Color,
	rose.fragrance as Fragrantcy,
	rose.min_temp as MinTemperature,
	rose.max_temp as MaxTemperature,
	rose.min_humid as MinHumidity,
	rose.max_humid as MaxHumidity,
	paOW.id_recipent as OwnerID,
	paOW.lastOwnerChangeLat as OwnerLatitude,
	paOW.lastOwnerChangeLon as OwnerLongitude,
	paOW.lastOwnerChangeTime as OwnerTimestamp
	
};

// ------------------------------------- Package View END -------------------------------------


// --------------------------------- OrderDataView View START ---------------------------------

view OrderDataViewAll as 
select from packagewithinicdentstatus as pack{
	
	key pack.incident_status
	
	 
	
	
	
} group by pack.incident_status;

// TOOD just one with id all (maybe) 
view OrderDataView as 
select from packagewithinicdentstatus {

	key id_recipent as recipentID,
	key incident_status as Status,
	
	(
		select from packagewithinicdentstatus innerpack{
		
			count(id_package) as count
		
		} where id_recipent = innerpack.id_recipent and
				delivery_active = TRUE
	
	) as openCount : Integer,
	(
		select from packagewithinicdentstatus innerpack{
		
			count(id_package) as count
		
		} where id_recipent = innerpack.id_recipent and
				delivery_active = FALSE
	
	) as closedCount : Integer 
} group by packagewithinicdentstatus.id_recipent,
		   packagewithinicdentstatus.incident_status;
		   
// ---------------------------------- OrderDataView View END ----------------------------------


// ----------------------------------- Support View START ------------------------------------

view PackageDeliveryStatus as select from 
entities.Package as pack {
	
	key pack.id_package,
	pack.id_recipent,
	pack.id_rosetype,
	pack.id_currentOwner,
	pack.lastOwnerChangeLat,
	pack.lastOwnerChangeLon,
	pack.lastOwnerChangeTime,
	case 
		when id_recipent = id_currentOwner then false
		else true
	end as delivery_active : Boolean
};

view incidents as select from 
TrackingView as trac {

	key trac.PackageID,
	key trac.TimeStamp,
	trac.MinTemp,
	trac.MaxTemp,
	trac.MinHum,
	trac.MaxHum,
	trac.CurrentLat,
	trac.CurrentLong,
	trac.Incident
} where trac.Incident = TRUE;

view tracpack as select from 
incidents as trac right outer join
PackageDeliveryStatus as pack 
on trac.PackageID = pack.id_package {

	key pack.id_package,
	pack.id_recipent,	
	pack.id_rosetype,	
	pack.id_currentOwner,
	pack.lastOwnerChangeLat,
	pack.lastOwnerChangeLon,
	pack.lastOwnerChangeTime,
	pack.delivery_active,
	count(trac.Incident) as incidentCount : Integer
	
} group by  pack.id_package,
			pack.id_recipent,	
			pack.id_rosetype,	
			pack.id_currentOwner,
			pack.lastOwnerChangeLat,
			pack.lastOwnerChangeLon,
			pack.lastOwnerChangeTime,
			pack.delivery_active;
					
					
						
view packagewithinicdentstatus as select from 
tracpack {
	
	key id_package,
	id_recipent,	
	id_rosetype,
	id_currentOwner,
	lastOwnerChangeLat,
	lastOwnerChangeLon,
	lastOwnerChangeTime,	
	delivery_active,
	case 
		when incidentCount = 0 then 'Success'
		when incidentCount = 1 then 'Warning'
		else 'Error'
	end as incident_status : String
};

// ------------------------------------ Support View END -------------------------------------
