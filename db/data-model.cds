namespace rosetracker.database;

context cdsContext {

	// -------------------------------------------------- START TRACKINGVIEW -------------------------------------------------- 

	view trackingView as 
		select from 
			sensor_package as spck join 
			sensor_input as sinp 
			on spck.ID_DELIVERY = sinp.ID_DELIVERY {
				
				key spck.ID_PACKAGE as PackageID,
				key sinp.START_TIME as TimeStamp,
				sinp.MIN_TEMP as MinTemp,
				sinp.MAX_TEMP as MaxTemp,
				sinp.MIN_HUMID as MinHumidity,
				sinp.MAX_HUMID as MaxHumidity,
				sinp.LATITUDE as CurrentLat,
				sinp.LONGITUDE as CurrentLong,
				sinp.INCIDENT as Incident
				
			};
	
	// --------------------------------------------------- END TRACKINGVIEW -------------------------------------------------- 
	
	// ----------------------------------------------- START SUPPORTING VIEWS ------------------------------------------------ 
	
	view tracpack as 
		select from 
			trackingView as trac join
			package as pack 
			on trac.PackageID = pack.ID_PACKAGE {
			
				pack.ID_PACKAGE,
				pack.ID_RECIPIENT,	
				pack.ID_ROSETYPE,	
				pack.DELIVERY_ACTIVE,
				count(trac.Incident) as INCIDENTCOUNT
				
			} group by  pack.ID_PACKAGE,
						pack.ID_RECIPIENT,	
						pack.ID_ROSETYPE,	
						pack.DELIVERY_ACTIVE;
					
					
						
	view packagewithinicdentstatus as 
		select from 
			tracpack {
				
				ID_PACKAGE,
				ID_RECIPIENT,	
				ID_ROSETYPE,	
				DELIVERY_ACTIVE,
				case 
					when INCIDENTCOUNT = 0 then 'Success'
					when INCIDENTCOUNT = 1 then 'Warning'
					else 'Error'
				end as INCIDENT_STATUS : String(20)
			};
	
	// ------------------------------------------------ END SUPPORTING VIEWS ------------------------------------------------- 
	
	// -------------------------------------------------- START PACKAGEVIEW -------------------------------------------------- 
	
	view shistowner as 
		select from 
			storage_history as shist join
			owner as owner 
			on shist.ID_OWNER = owner.ID_OWNER {
				
				key shist.ID_PACKAGE,
				owner.OWNERNAME,
				//shist.INCIDENT_STATUS,
				shist.ID_OWNER,
				shist.CHANGE_LAT,
				shist.CHANGE_LONG,
				shist.CHANGE_TIME
				
			};
			
	view showpack as 
		select from 
			shistowner as show join
			packagewithinicdentstatus as pack 
			on show.ID_PACKAGE = pack.ID_PACKAGE {
				
				key pack.ID_PACKAGE,
				pack.ID_ROSETYPE,
				show.OWNERNAME,
				pack.INCIDENT_STATUS,
				pack.DELIVERY_ACTIVE,
				show.ID_OWNER,
				show.CHANGE_LAT,
				show.CHANGE_LONG,
				show.CHANGE_TIME,
				pack.ID_RECIPIENT,
			};


	view packageView as 
		select from 
			showpack as sopa join
			rose as rose 
			on sopa.ID_ROSETYPE = rose.ID_ROSETYPE {
				
				key sopa.ID_PACKAGE as PackageID,
				rose.ROSETYPE as RoseType,
				sopa.OWNERNAME as OwnerName,
				sopa.ID_RECIPIENT as ReceipientID,
				sopa.INCIDENT_STATUS as IncidentStatus,
				sopa.DELIVERY_ACTIVE as ActiveStatus,
				rose.OPENING_TIME as OpenSpeed,
				rose.VASE_LIFE as Vaselife,
				rose.COLOR as Color,
				rose.FRAGRANCE as Fragrantcy,
				rose.MIN_TEMP as MinTemperature,
				rose.MAX_TEMP as MaxTemperature,
				rose.MIN_HUMID as MinHumidity,
				rose.MAX_HUMID as MaxHumidity,
				sopa.ID_OWNER as OwnerID,
				sopa.CHANGE_LAT as OwnerLatitude,
				sopa.CHANGE_LONG as OwnerLongitude,
				sopa.CHANGE_TIME as OwnerTimestamp
				
			};
	
	// --------------------------------------------------- END PACKAGEVIEW -------------------------------------------------- 
	
	
	// --------------------------------------------------- START ORDERVIEW -------------------------------------------------- 

	view orderDataView as 
		select from packagewithinicdentstatus {
		
			key ID_RECIPIENT as recipentID,
			key INCIDENT_STATUS as Status,
			
			(
				select from packagewithinicdentstatus innerpack{
				
					count(ID_PACKAGE) as count
				
				} where ID_RECIPIENT = innerpack.ID_RECIPIENT and
						DELIVERY_ACTIVE = TRUE
			
			) as openCount : Integer,
			
			(
				select from packagewithinicdentstatus innerpack{
				
					count(ID_PACKAGE) as count
				
				} where ID_RECIPIENT = innerpack.ID_RECIPIENT and
						DELIVERY_ACTIVE = FALSE
			
			) as closedCount : Integer  
			
			
		} group by ID_RECIPIENT,
				   INCIDENT_STATUS;
	
	// ---------------------------------------------------- END ORDERVIEW --------------------------------------------------- 
	
	
	// ----------------------------------------------------- START TABLES --------------------------------------------------- 

    entity owner {
        key ID_OWNER : String(8) not null;
            OWNERNAME: String(20);
            CITY     : String(20);
            COUNTRY  : String(20);
            ROLE     : String(20);
            owner_roseowner : association[1, 0..*] to cdsContext.rose_owner on owner_roseowner.ID_OWNER = ID_OWNER;
            owner_storage : association[1, 0..*] to cdsContext.storage on owner_storage.ID_OWNER = ID_OWNER;
            owner_packagerecipient : association[1, 1..*] to cdsContext.package on owner_packagerecipient.ID_RECIPIENT = ID_OWNER;
            owner_storagehistory : association[1, 1..*] to cdsContext.storage_history on owner_storagehistory.ID_OWNER = ID_OWNER;
    };

    entity rose {
        key ID_ROSETYPE  : String(8) not null;
            ROSETYPE     : String(20);
            OPENING_TIME : String(20);
            HEAD_SHAPE   : String(20);
            COLOR        : String(20);
            FRAGRANCE    : Boolean;
            VASE_LIFE    : String(20);
            MAX_TEMP	 : Double;
            MIN_TEMP	 : Double;
            MAX_HUMID	 : Double;
            MIN_HUMID	 : Double;
            rose_roseowner : association[1, 0..*] to cdsContext.rose_owner on rose_roseowner.ID_ROSETYPE = ID_ROSETYPE;
            rose_package : association[1, 0..*] to cdsContext.package on rose_package.ID_ROSETYPE = ID_ROSETYPE;
    };
    
    entity rose_owner {
    	key ID_ROSETYPE : String(8) not null;
    	key ID_OWNER : String(8) not null;
    };
    
	entity storage {
		key ID_STORAGE	: String(8) not null;
			ID_OWNER	: String(8) not null;
			STORAGE_TYPE	: String(20);
			storage_storagehistory : association[1, 0..*] to cdsContext.storage_history on storage_storagehistory.ID_OWNER = ID_OWNER;
			storage_sensorinput : association[1, 0..*] to cdsContext.sensor_input on storage_sensorinput.ID_STORAGE = ID_STORAGE;
	};
	
	entity package {
		key ID_PACKAGE	: String(8) not null;
			ID_RECIPIENT	: String(8);
			ID_ROSETYPE 	: String(8);
			DELIVERY_ACTIVE 	: Boolean;
			package_storagehistory : association[1, 0..*] to cdsContext.storage_history on package_storagehistory.ID_PACKAGE = ID_PACKAGE;
			package_sensorpackage : association[1, 0..*] to cdsContext.sensor_package on package_sensorpackage.ID_PACKAGE = ID_PACKAGE;
	};

	entity storage_history {
		key ID_OWNER	: String(8) not null;
		key ID_PACKAGE	: String(8) not null;
			CHANGE_TIME	: DateTime;
			CHANGE_LAT	 : Decimal(8,4);
			CHANGE_LONG	 : Decimal(8,4);
			//INCIDENT_STATUS 	: String(10);
	};
	
	entity sensor_input {
		key ID_DELIVERY	: String(8) not null;
			ID_STORAGE	: String(8);
		key	START_TIME	: DateTime not null;
			END_TIME	: DateTime;
			LATITUDE	: Decimal(8,4);
			LONGITUDE	: Decimal(8,4);
			MAX_TEMP	: Double; 
			MIN_TEMP	: Double;
			AVG_TEMP	: Double;
			MAX_HUMID	: Double;
			MIN_HUMID	: Double;
			AVG_HUMID	: Double;
			INCIDENT	: Boolean; 
			sensorinput_sensorpackage : association[1, 0..*] to cdsContext.sensor_package on sensorinput_sensorpackage.ID_DELIVERY = ID_DELIVERY;
	};
	
	entity sensor_package {
		key ID_DELIVERY	: String(8) not null;
		key ID_PACKAGE	: String(8) not null;
	};
	
	// ------------------------------------------------------ END TABLES ----------------------------------------------------
	
	
	// ----------------------------------------------------- DUMMY TABLES ---------------------------------------------------
	// ----------------------------------------------------- for Blokchain --------------------------------------------------
	
	entity IncidentBC {
		key PackageID : String ;
		key TimeStamp : DateTime; 
		CurrentLatitude : Decimal(8,4) ;
		CurrentLongitude : Decimal(8,4) ;
		MinTemperature : Decimal(8,4) ;
		MaxTemperature : Decimal(8,4) ;
		MinHumidity : Decimal(8,4) ;
		MaxHumidity : Decimal(8,4) ;
		IncidentDesc : String;
	};
	
	entity OwnerBC {
		key PackageID : String;
		key TimeStamp : DateTime;
		OwnerID : String;
		CurrentLatitude : Decimal(8,4);
		CurrentLongitude : Decimal(8,4);
		RouteToNextOwner : String;
	};
}