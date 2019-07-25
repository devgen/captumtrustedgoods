sap.ui.define([], function () {
	"use strict";
	return {
		// API call to location IQ
		// translates the longtitude and latitude into an address and returns the response
		geocodeLatLng: function (latInput, lngInput) {
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