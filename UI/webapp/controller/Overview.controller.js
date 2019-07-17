sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/m/MessageBox",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator",
	"sap/ui/model/FilterType",
	"sap/ui/core/routing/History",
	"../model/reverseGeocoding"

], function (Controller, MessageBox, Utilities, History, Filter, FilterType, reverseGeocoding) {
	"use strict";

	return Controller.extend("rosetracker.RoseTracker.controller.Overview", {

		//1.1 onBeforeRendering
		//LOAD AND FILTER DATA BEFORE RENDERING 
		onBeforeRendering: function () {

			//*Optional: User Management 
			//implement user management and filter by customerID
			var user = "cust2";
			var userrole = "nocustomer";

			//DEFINE OVIEW TO ACCESS THE OVERVIEW VIEW
			var oView;
			oView = this.getView();

			
			//1.1.1 FILTER FOR THE CURRENT / OPEN PACKAGES
			var ofilterCurrent = new sap.ui.model.Filter({
				//binding path
				path: "ActiveStatus",
				//filter operation
				operator: sap.ui.model.FilterOperator.EQ,
				value1: true
			});
				
			// 1.1.1. FILTER FOR THE CLOSED / PAST PACKAGES
			var ofilterPast = new sap.ui.model.Filter({
				//binding path
				path: "ActiveStatus",
				//filter operation
				operator: sap.ui.model.FilterOperator.EQ,
				value1: false
			});
			
			//1.1.2. Prepare the sorter 
				var oSorter = new sap.ui.model.Sorter({
				path: 'RoseType',
				group: true
			});

			//1.1.3. Binding the items in the controller: current and past packages
			var currentPackages = oView.byId("currentPackages");
		
			currentPackages.bindItems({
				path: "/PackageData",
				template: currentPackages.getBindingInfo("items").template,
				sorter: oSorter,
				filters: [ofilterCurrent]
			});
			var pastPackages = oView.byId("pastPackages");
			pastPackages.bindItems({
				path: "/PackageData",
				template: currentPackages.getBindingInfo("items").template,
				sorter: oSorter,
				filters: [ofilterPast]
			});
			
			//1.1.4. Prepare the Filter for the Donut Chart
			//Make sure that the donut chart gets the values for all packages
			var ofilterCust = new sap.ui.model.Filter({
						//binding path
						path: "CustomerID",
						//filter operation
						operator: sap.ui.model.FilterOperator.EQ,
						value1: 'all'
					});
			
			//Apply the filter to the
			oView.byId("idDonutChart").getDataset().getBinding("data").filter(ofilterCust);

			//*Optional: User Management
			// only filter if logged in user is from type customer 
			if (userrole === 'customer') {

				//use multiple filters to get only relevant table entries
				var curruser = new sap.ui.model.Filter({
					filters: [
						new sap.ui.model.Filter({
							path: 'ReceipientID',
							operator: sap.ui.model.FilterOperator.EQ,
							value1: user
						}),
						new sap.ui.model.Filter({
							path: 'ActiveStatus',
							operator: sap.ui.model.FilterOperator.EQ,
							value1: true
						})

					],
					and: true
				});

				currentPackages.getBinding("items").filter(curruser);

				var pastuser = new sap.ui.model.Filter({
					filters: [
						new sap.ui.model.Filter({
							path: 'ReceipientID',
							operator: sap.ui.model.FilterOperator.EQ,
							value1: user
						}),
						new sap.ui.model.Filter({
							path: 'ActiveStatus',
							operator: sap.ui.model.FilterOperator.EQ,
							value1: false
						})

					],
					and: true
				});

				pastPackages.getBinding("items").filter(pastuser);
			
	//if a user is set also the donut chart should display the relevant values
				var ofilterCustU = new sap.ui.model.Filter({
				//binding path
				path: "CustomerID",
				//filter operation
				operator: sap.ui.model.FilterOperator.EQ,
				value1: user
			});
			
			//Apply the filter to the
			oView.byId("idDonutChart").getDataset().getBinding("data").filter(ofilterCustU);
			}
			
	
			
			
		},
		
		convertCoordinatesToAddress: function (latInput, lngInput) {

			
			return new Promise((resolve, reject) => {
						resolve(reverseGeocoding.geocodeLatLng(latInput, lngInput));
	
			})

		},
			
		
		
	
		
		
		//1.2 OnInit
		onInit: function () {

		},
		//1.3. OnFilterPackages
		//for filtering the table by packageID using the searchfield
		onFilterPackages: function (oEvent) {

			// build filter array
			var aFilter = [],
				sQuery = oEvent.getParameter("query"),
				// retrieve list control
				oList = this.getView().byId("currentPackages"),
			
				//currentPackages
				// get binding for aggregation 'items'
				oBinding = oList.getBinding("items");

			if (sQuery) {
				aFilter.push(new sap.ui.model.Filter("PackageID", sap.ui.model.FilterOperator.Contains, sQuery));
			}

			// apply filter. an empty filter array simply removes the filter
			// which will make all entries visible again
			oBinding.filter(aFilter);

		},
			onFilterPackagesPast        : function (oEvent) {

			// build filter array
			var aFilter = [],
				sQuery = oEvent.getParameter("query"),
				// retrieve list control
				oList = this.getView().byId("pastPackages"),
			
				//currentPackages
				// get binding for aggregation 'items'
				oBinding = oList.getBinding("items");

			if (sQuery) {
				aFilter.push(new sap.ui.model.Filter("PackageID", sap.ui.model.FilterOperator.Contains, sQuery));
			}
			// apply filter. an empty filter array simply removes the filter
			// which will make all entries visible again
			oBinding.filter(aFilter);
		},

		//1.4. HandleListItemPress
		//Navigation to Details
		handleListItemPress: function (oEvent) {
			// get the router
			var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
			// get the selected packageID from the clikced column
			var selectedPackageId = oEvent.getSource().getBindingContext().getProperty("PackageID");
			//navigate to the path details and give over the packageid as the navigation parameter
			oRouter.navTo("details", {
				packageID: selectedPackageId
			});
		},

		//1.5. onPressOwnership
		//Navigation to Formula-Page which includes the scanner for the ownership change
		onPressOwnership: function (oEvent) {
			var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
			oRouter.navTo("formula", true);
		},

		

		onAfterRendering: function () {},


		onExit: function () {

		}

	});

});