namespace rosetracker.database;

// entities 1 to 1 from service, since no currenlty no views


entity OrderData {
	key CustomerID : String;
	key Status : String;
	openCount : Integer; 
	closedCount : Integer;
}

entity PackageData {
	key PackageID : String;
	RoseType : String;
	OwnerName : String;
	ReceipientID : String;
	IncidentStatus : String;
	ActiveStatus : Boolean;
	OpenSpeed : Integer;
	Vaselife : Integer;
	Color : String;
	Fragrantcy : String;
	MinTemperature : Decimal(8,4);
	MaxTemperature : Decimal(8,4);
	MinHumidity : Decimal(8,4);
	MaxHumidity : Decimal(8,4);
	CurrentLatitude : Decimal(8,4);
	CurrentLongitude : Decimal(8,4);
	CurrentOwnerID : String;
	OwnerLatitude : Decimal(8,4);
	OwnerLongitude : Decimal(8,4);
	OwnerTimestamp : DateTime; 
}

entity IncidentBC {
	key PackageID : String ;
	key TimeStamp : DateTime; 
	CurrentLatitude : Decimal(8,4) ;
	CurrentLongitude : Decimal(8,4) ;
	MinTemperature : Decimal(8,4) ;
	MaxTemperature : Decimal(8,4) ;
	MinHumidity : Decimal(8,4) ;
	MaxHumidity : Decimal(8,4) ;
}

entity OwnerBC {
	key PackageID : String;
	key TimeStamp : DateTime;
	OwnerID : String;
	CurrentLatitude : Decimal(8,4);
	CurrentLongitude : Decimal(8,4);
	RouteToNextOwner : String;
}

entity Tracking {
	key PackageID : String;
	key TimeStamp : DateTime;
	MinTemp : Decimal(8,4);
	MaxTemp : Decimal(8,4);
	MinHumidity : Decimal(8,4);
	MaxHumidity : Decimal(8,4);
	CurrentLat : Decimal(8,4);
	CurrentLong : Decimal(8,4);
}
