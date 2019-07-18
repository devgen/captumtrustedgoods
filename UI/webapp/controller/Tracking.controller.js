sap.ui.define([
"sap/m/MessageToast", "sap/ui/core/mvc/Controller", "sap/ui/model/json/JSONModel"
], function (MessageToast, Controller, JSONModel) {
	"use strict";

	return Controller.extend("rosetracker.RoseTracker.controller.Tracking", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf rosetracker.RoseTracker.view.Tracking
		 */
		onInit: function () {
  var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
  oRouter.getRoute("Tracking").attachMatched(this._onRouteMatched, this);
  
  // set mock data
			//var sPath = jQuery.sap.getModulePath("rosetracker.RoseTracker", "model/SampleData.json");
				var sampleDatajson = new sap.ui.model.json.JSONModel("model/SampleData.json");
			//var oModel = new sap.ui.model.json.JSONModel(sPath);
			this.getView().setModel(sampleDatajson);
  
		},
		
		
		_onRouteMatched : function (oEvent) {
  var oArgs, oView;
  oArgs = oEvent.getParameter("arguments");
  oView = this.getView();
  oView.bindElement({
    path : "/PackageID_1(" + oArgs.productId + ")",
    events : {
      dataRequested: function () {
        oView.setBusy(true);
      },
      dataReceived: function () {
        oView.setBusy(false);
      }
    }
  });
},
handleNavButtonPress : function (evt) {
  var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
  oRouter.navTo("formula");
}

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf rosetracker.RoseTracker.view.Tracking
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf rosetracker.RoseTracker.view.Tracking
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf rosetracker.RoseTracker.view.Tracking
		 */
		//	onExit: function() {
		//
		//	}

	});

});