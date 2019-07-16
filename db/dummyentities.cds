namespace rosetracker.database.dummyentities;

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