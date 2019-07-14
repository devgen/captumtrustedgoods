sap.ui.define([], function () {
	"use strict";
	return {

		// Google Geocoding API
		// geocodeLatLng: function (geocoder, infowindow, input) {

		// 	console.log("geocodeLatLng wurde gestartet");
		// 	var latlngStr = input.split(',', 2);
		// 	var latlng = {
		// 		lat: parseFloat(latlngStr[0]),
		// 		lng: parseFloat(latlngStr[1])
		// 	};
		// 	geocoder.geocode({
		// 		'location': latlng
		// 	}, function (results, status) {
		// 		if (status === 'OK') {
		// 			if (results[0]) {
		// 				console.log("Ergebnis ist: ");
		// 				console.log(results[0].formatted_address)
		// 			} else {
		// 				console.log("No results found");
		// 			}
		// 		} else {
		// 			window.alert('Geocoder failed due to: ' + status);
		// 		}
		// 	});
		// }

		geocodeLatLng: function (latInput, lngInput) {
			//make it async again and work with promise/callback
			// var latlngStr = latlng.split(',', 2);
			//var lat = parseFloat(latlngStr[0]);
			//var lng = parseFloat(latlngStr[1]);
			return new Promise((resolve, reject) => {
				var lat = parseFloat(latInput);
				var lng = parseFloat(lngInput);

				var settings = {
					"async": true,
					"crossDomain": true,
					"url": "https://eu1.locationiq.com/v1/reverse.php?key=8356c108c54954&lat=" + lat + "&lon=" + lng + "&format=json",
					"method": "GET"
				}

				$.ajax(settings).done(function (response) {
					//result = response.address.road + " " + response.address.house_number + ", " + response.address.postcode + " " + response.address.city + ", " + response.address.state + ", " + response.address.country;
					var result = response.display_name;
					console.log(result)
					resolve(result)
				});
				
			})

		}
	}
})