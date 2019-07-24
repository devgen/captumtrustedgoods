namespace rosetracker.database.entities;

entity Owner {
	key id_owner: String;
	
	ownername	: String;
	city    	: String;
	country 	: String;
	role    	: String;
};

entity Rose {
	key id_rosetype  : String;
	
	rosetype     : String;
	opening_time : String;
	head_shape   : String;
	color        : String;
	fragrance    : Boolean;
	vase_life    : String;
	max_temp	 : Decimal(8,4);
	min_temp	 : Decimal(8,4);
	max_humid	 : Decimal(8,4);
	min_humid	 : Decimal(8,4);
};
    
entity Rose_owner {
	key id_rosetype : String;
	key id_owner	: String;
};

entity Package {
	key id_package	: String;
	
	id_recipent		: String;
	id_rosetype 	: String;
	
	id_currentOwner : String;
	lastOwnerChangeLat : Decimal(8,4);
	lastOwnerChangeLon : Decimal(8,4);
	lastOwnerChangeTime : DateTime;
};

entity SensorInput {
	key id_package : String;
	key start_time  : DateTime;
	
	end_time		: DateTime;
	latitude		: Decimal(8,4);
	longitude		: Decimal(8,4);
	max_temp		: Decimal(8,4);
	min_temp		: Decimal(8,4);
	avg_temp		: Decimal(8,4);
	max_humi		: Decimal(8,4);
	min_humi		: Decimal(8,4);
	avg_humi		: Decimal(8,4);
	incident		: Boolean; 
};