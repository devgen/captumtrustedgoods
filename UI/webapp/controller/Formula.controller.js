jQuery.sap.require("sap.ndc.BarcodeScanner");
sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/core/routing/History",
	"sap/ui/model/FilterOperator",
	'sap/m/MessageToast'
	
], function (Controller, History, FilterOperator) {
	"use strict";

	let location = 'no value';
	let	latitude = '19.99';
	let	longitude = '50.99';

	return Controller.extend("rosetracker.RoseTracker.controller.Formula", {

		onBeforeRendering: function () {
		},

		onInit: function () {
		},
		

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
					var	packageID = mResult.text;

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
		
		validateOwner: function(oEvent){
		
  
		},
		
		//PRESS THE SUBMIT BUTTON
		onPressSubmit: function (oEvent) {
		
			var PackageIDInput = this.getView().byId("PackageIDChange").getText();
			console.log(PackageIDInput)
			var OwnerInput = this.getView().byId("OwnerChange").getValue();
			var LocationInput = this.getView().byId("LocationChange").getText();
			var TimeStampInput = this.getView().byId("TimestampChange").getText();
			
			console.log(PackageIDInput+'  '+ OwnerInput+' '+LocationInput+' '+TimeStampInput)

			var empty = false;
			//CHECK IF VALUES ARE NOT FILLED AND SET A FLAG
			if (PackageIDInput === "") {
				empty = true;
			}
			if (OwnerInput  === "") {
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
			//var oModel = new  sap.ui.model.odata.v2.ODataModel("https://fon6pom573dmdpz1kerdummyodata-srv.cfapps.eu10.hana.ondemand.com/odata/v2/RoseTrackerDataService/");
			var oModel = this.getView().getModel();
			console.log(oModel)
			
			
			var lat = latitude.toFixed(4);
				var long = longitude.toFixed(4);

		
		var oData = {
			PackageID: PackageIDInput,
			TimeStamp: new Date(), 
			OwnerID: OwnerInput, 
			CurrentLatitude: lat,
			CurrentLongitude: long
 
		}
		
			//Give feedback to the user by showing a message
			var msg = 'no message' ;
		
		
		oModel.create("/OwnerBC", oData, {
			success:  function() {
	  sap.m.MessageToast.show('Success: '+PackageIDInput+' was handed over to '+OwnerInput);  
	},
    error: function() {
	  sap.m.MessageToast.show('Error: Ownershipchange did not succeed');  }
		});
	
		
		// Update Current Owner in PackageData
		//First define the values that you want to submit
			var oData = {
			PackageID: PackageIDInput,
			CurrentOwnerID: OwnerInput,
			OwnerLatitude: lat,
			OwnerLongitude: long,
			OwnerTimestamp: new Date()
		}
		// Update the corresponding entry for the scanned PackageID in the PackageData Table
		oModel.update("/PackageData('"+PackageIDInput+"')", oData, {
			success:  function() {
	  sap.m.MessageToast.show('Success: '+PackageIDInput+' was handed over to '+OwnerInput);  
	},
    error: function() {
	  sap.m.MessageToast.show('Error: Ownershipchange did not succeed');  }
		});
		
		


			}
		
		}

	});

});