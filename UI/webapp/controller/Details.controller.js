sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/core/routing/History",
	"sap/ui/model/FilterOperator",
	"sap/ui/model/json/JSONModel",
	"sap/ui/Device",
	"sap/ui/vbm/AnalyticMap",
		"../model/reverseGeocoding"

], function (Controller, JSONModel, Device, History, FilterOperator, AnalyticMap, reverseGeocoding) {
	"use strict";

	//AnalyticMap.GeoJSONURL  =  "test-resources/sap/ui/vbm/demokit/media/analyticmap/L0.json";

	return Controller.extend("rosetracker.RoseTracker.controller.Details", {
		//2.1. OnInit
		onInit: function () {
			var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
			oRouter.getRoute("details").attachPatternMatched(this._onRouteMatched, this);

		},
		
			convertCoordinatesToAddress: function (latInput, lngInput) {


			
			return new Promise((resolve, reject) => {
						resolve(reverseGeocoding.geocodeLatLng(latInput, lngInput));
			
			})

		},
		
		
		
		
		
		//2.2. _onRouteMatched
			_onRouteMatched: function (oEvent) {
			var oArgs, oView;
			oArgs = oEvent.getParameter("arguments");
			oView = this.getView();

			//GENERATE FILTER FOR PACKAGEID
			var ofilter = new sap.ui.model.Filter({
				path: "PackageID",
				operator: sap.ui.model.FilterOperator.EQ,
				value1: oArgs.packageID
			});
			
				
  			//GET THE CORRESPONDING ELEMENTS/TABLES FROM THE DETAILS XML VIEW AND APPLY THE FILTER TO THE TABLE ITEMS
			oView.byId("OwnershipTable").getBinding("items").filter(ofilter);
			oView.byId("packagemaster").getBinding("items").filter(ofilter);
			oView.byId("incident").getBinding("items").filter(ofilter);

			// 2.2.1. MAP 
			var incidentSpots = oView.byId("incidentSpots");
			incidentSpots.getBinding("items").filter(ofilter);
				var ownerSpots = oView.byId("ownerSpots");
			ownerSpots.getBinding("items").filter(ofilter);
					var routes = oView.byId("routes");
			routes.getBinding("items").filter(ofilter);
			//FILTER MAP BY PACKAGES
			 oView.byId("routes").getBinding("items").filter(ofilter);
			 	 oView.byId("incidentSpots").getBinding("items").filter(ofilter);
			 	  oView.byId("ownerSpots").getBinding("items").filter(ofilter);

			//2.2.2. LINECHART
			//BIND AND FILTER LINE GRAPH
			// GET THE LINE CHART FROM THE VIEW BY THE ID OF THE VIZFRAME
			var tempLineChart = this.getView().byId("idLineGraph");

			//BIND THE CHART TO THE CORRESPONDING DATA TABLE OF THE SERVICE: IN THIS CASE THE TRACKING DATA
			tempLineChart.bindElement({
				path: "/TrackingView"
			});

			// APPLY THE FILTER THAT WAS CREATED EARLIER TO FILTER THE LIECHART BY PACKAGEID
			oView.byId("idLineGraph").getDataset().getBinding("data").filter(ofilter);

			//REPEAT THIS FOT THE HUMIDITY LINE CHART - 1 GET ELEMENT FROM VIEW
			var humLineChart = this.getView().byId("idLineGraph2");

			// BIND IT TO THE DATATABLE
			humLineChart.bindElement({
				path: "/TrackingView"
			});

			// APPLY THE FILTER
			oView.byId("idLineGraph2").getDataset().getBinding("data").filter(ofilter);

		},

		//2.3. Back Navigation to Overview Page
		onBack: function () {
			var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
			oRouter.navTo("Overview", true);

		},
		//2.4. onClichRoutr
		onClickRoute: function (oEvent) {
			sap.m.MessageToast.show("Routedetails " + oEvent.getParameter("position"));
		}

	});

});