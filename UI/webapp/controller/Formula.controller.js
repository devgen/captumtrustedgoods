jQuery.sap.require("sap.ndc.BarcodeScanner");
sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/core/routing/History",
	"sap/ui/model/FilterOperator",
	'sap/m/MessageToast'

], function (Controller, History, FilterOperator) {
	"use strict";

	let location = 'no value';
	let latitude = '';
	let longitude = '';

	return Controller.extend("rosetracker.RoseTracker.controller.Formula", {

		onBeforeRendering: function () {

		},

		onInit: function () {},

		//3.1. OnGeoSuccess
		//if geolocation is sucessfully loaded.
		onGeoSuccess: function (position) {
			// todo translate geocode 
			location = (position.coords.latitude + ';' + position.coords.longitude) + ';0';
			//round the coordinates in order to match them with the oData Tableformat
			latitude = position.coords.latitude;
			longitude = position.coords.longitude;

		},
		//IF GEOLOATION IS NOT SUCESSFULLY LOADED
		onGeoError: function () {
			console.log('code: ' + error.code + '\n' + 'message: ' + error.message + '\n');
		},

		// 3.2. OnPress
		//FUNCTION FOR CLICKING THE SCAN BUTTON
		onPress: function (oEvent) {

			//TO GET THE RIGHT CONTEXT AND ACCESS ELEMENTS IN THE VIEW
			var that = this;
			// USE NAVIGATOR TO ACCESS THE CURRENT GEOLOCATION AND CALL THE CORRESPONDING METHOD
			navigator.geolocation.getCurrentPosition(this.onGeoSuccess, this.onGeoError, {
				enableHighAccuracy: true
			});
			// USE NDC DEVICE NATIVE CAMERA TO SCAN THE QR CODE
			sap.ndc.BarcodeScanner.scan(

				// IF SCAN WAS SUCESSFULL; APPLY THE FOLLOWING LOGIC TO THE RESULT
				function (mResult) {

					//FILL IN THE FORM WITH THE RELEVANT DATA
					var packageID = mResult.text;

					//Create Ownership time
					var today = new Date();

					//SET THE VALUES IN THE VIEW ELEMENTS
					that.byId('PackageIDChange').setText(packageID);
					that.byId('LocationChange').setText(location);
					//	that.byId('OwnerIDChange').setText(user);
					that.byId('TimestampChange').setText(today);
					that.byId('PackageIDForm').setText(packageID);

					// Fill In Form for PachageID from scanned pack to show info about the package	
					// RETRIEVE RELEVANT INFO FOR THE SCANNED PACKAGE AND DISPLAY IT TO THE USER
					var currentowner = that.getView().getModel().getObject("/PackageData('" + packageID + "')/CurrentOwnerID");
					var rosetype = that.getView().getModel().getObject("/PackageData('" + packageID + "')/RoseType");
					var color = that.getView().getModel().getObject("/PackageData('" + packageID + "')/Color");

					// SET THOSE VALUES INTO THE CORRESPONSING FIELDS IN THE VIEW
					that.byId('CurrentOwnerIDForm').setText(currentowner);
					that.byId('RoseTypeForm').setText(rosetype);
					that.byId('ColorForm').setText(color);

				},
				// LOG AN ERROR MESSAGE IF THE SCAN WAS NOT SUCESSFULL 
				function (Error) {
					console.log("Scanning failed: " + Error);
				}
			)
		},

	
		//3.3. OnPressSubmit
		//PRESS THE SUBMIT BUTTON
		onPressSubmit: function (oEvent) {

			var PackageIDInput = this.getView().byId("PackageIDChange").getText();
			console.log(PackageIDInput)
			var OwnerInput = this.getView().byId("OwnerChange").getValue();
			var LocationInput = this.getView().byId("LocationChange").getText();
			var TimeStampInput = this.getView().byId("TimestampChange").getText();

			console.log(PackageIDInput + '  ' + OwnerInput + ' ' + LocationInput + ' ' + TimeStampInput)

			var empty = false;
			//CHECK IF VALUES ARE NOT FILLED AND SET A FLAG
			if (PackageIDInput === "") {
				empty = true;
			}
			if (OwnerInput === "") {
				empty = true;
				var OwnerLabel = this.getView().byId("OwnerChange");
				//REMIND THE USER IN CASE HE DID NOT ENTER HIS OWNER ID FOR THE OWNERSHIP CHANGE
				OwnerLabel.setValue("Please enter value");
			}
			if (LocationInput === "") {
				empty = true;
			}
			if (TimeStampInput === "") {
				empty = true;
			}
			// ONCE ALL FIELDS ARE FILLED; SEND THE NEW OWNERSHIP TO THE ODATA SEVICE
			if (!empty) {

				//Render the Odata Service
				var oModel = this.getView().getModel();
				//	console.log(oModel)
				var lastLong = '';
				var lastLat = '';
				var lat = latitude.toFixed(4);
				var long = longitude.toFixed(4);

				// read data 
				oModel.read("/OwnerBC", {
					urlParameters: {
						"$filter": "PackageID eq '"+PackageIDInput+"'",
						"$orderby": "TimeStamp desc",
						"$top": 1,
						"$select": "CurrentLongitude, CurrentLatitude"
					},
					success: function (oData, oResponse) {
							console.log(oData.results[0].CurrentLongitude);
							console.log(oData.results[0].CurrentLatitude);
						lastLong = oData.results[0].CurrentLongitude;
						lastLat = oData.results[0].CurrentLatitude;

						// build the Data Object		
						var oData = {
							PackageID: PackageIDInput,
							TimeStamp: new Date(),
							OwnerID: OwnerInput,
							CurrentLatitude: lat,
							CurrentLongitude: long,
							RouteToNextOwner: lastLong + ";" + lastLat + ";0; " + long + ";" + lat + ";0"
						}

						//create a new entry in the OwnerBC Odata Table --> Blockchain
						oModel.create("/OwnerBC", oData, {
							success: function (oData) {
								sap.m.MessageToast.show('Success: ' + PackageIDInput + ' was handed over to ' + OwnerInput);
								console.log(oData)
							},
							error: function () {
								sap.m.MessageToast.show('Error: Ownershipchange did not succeed');
							}
						});

					},
					error: function () {
						console.log('read error');
					}
				});

				// Update Current Owner in PackageData --> SAP Hana
				// First define the values that you want to submit
				var oData = {
						PackageID: PackageIDInput,
						CurrentOwnerID: OwnerInput,
						OwnerLatitude: lat,
						OwnerLongitude: long,
						OwnerTimestamp: new Date()
					}
			}

		},
		//3.4. OnBack
			//navigate back in browserhistory or to overview
		onBack: function () {
			var oHistory = History.getInstance();
			var sPreviousHash = oHistory.getPreviousHash();
			if (sPreviousHash !== undefined) {
				window.history.go(-1);
			} else {
				var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
				oRouter.navTo("Overview", true);
			}
		},

	});

});