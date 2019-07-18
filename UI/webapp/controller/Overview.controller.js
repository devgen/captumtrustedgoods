sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/m/MessageBox",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator",
	"sap/ui/model/FilterType",
	"sap/ui/core/routing/History",
	"../model/formatter"

], function (Controller, MessageBox, Utilities, History, formatter, Filter, FilterType) {
	"use strict";

	return Controller.extend("rosetracker.RoseTracker.controller.Overview", {
		formatter: formatter,

		//LOAD AND FILTER DATA BEFORE RENDERING 
		onBeforeRendering: function () {

			//dummy customer receipient 1
			var user = "ReceipientID 1";
			var userrole = "nocustomer";

			//DEFINE OVIEW TO ACCESS THE OVERVIEW VIEW
			var oView;
			oView = this.getView();

			//GENERATE FILTER FOR recipientid (USER) -- to apply for customers only / wholesaler should see all -- 
			var ofilterUser = new sap.ui.model.Filter({
				//binding path
				path: "ReceipientID",
				//filter operation
				operator: sap.ui.model.FilterOperator.EQ,
				//filter value: TODO GET AS VARIABLE FROM USER MANAGEMENT OR SET WHEN SWITCHING ROLES
				value1: user
			});
			
			//FILTER FOR THE CURRENT / OPEN PACKAGES
			var ofilterCurrent = new sap.ui.model.Filter({
				//binding path
				path: "ActiveStatus",
				//filter operation
				operator: sap.ui.model.FilterOperator.EQ,
				value1: true
			});

				// FILTER FOR THE CLOSED / PAST PACKAGES
			var ofilterPast = new sap.ui.model.Filter({
				//binding path
				path: "ActiveStatus",
				//filter operation
				operator: sap.ui.model.FilterOperator.EQ,
				value1: false
			});

			//binding the items in the controller: current and past packages
			var currentPackages = oView.byId("currentPackages");
			// prepare the sorter for the tables
			var oSorter = new sap.ui.model.Sorter({
				path: 'RoseType',
				group: true
			});
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

			}
		},

		onInit: function () {

		},

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

		//Navigation to Formula-Page which includes the scanner for the ownership change
		onPressOwnership: function (oEvent) {
			var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
			oRouter.navTo("formula", true);
		},

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

		onAfterRendering: function () {},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf rosetracker.RoseTracker.view.LineChart
		 */
		onExit: function () {

		}

	});

});