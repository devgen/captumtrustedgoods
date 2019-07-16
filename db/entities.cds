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
    
// entity StorageData {
// 	key id_storage	: String;
	
// 	id_owner		: String;
// 	storage_type	: String;
// };

entity Package {
	key id_package	: String;
	
	id_recipent		: String;
	id_rosetype 	: String;
	
	id_currentOwner : String;
	lastOwnerChangeLat : Decimal(8,4);
	lastOwnerChangeLon : Decimal(8,4);
	lastOwnerChangeTime : DateTime;
	
	//delivery_active : Boolean;
};


// entity Storage_history {
// 	key id_owner	: String;
// 	key id_package	: String;
	
// 	change_time	 : DateTime;
// 	change_lat	 : Decimal(8,4);
// 	change_lon	 : Decimal(8,4);
// };

entity SensorInput {
	key id_package : String;
	key start_time  : DateTime;
	
	//id_storage		: String;
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

// entity SensorPackage {
// 	key id_delivery : String;
// 	key id_package  : String;
// 	timestamp		: DateTime;
// };