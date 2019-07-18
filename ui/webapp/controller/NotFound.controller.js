sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/core/routing/History"
], function (Controller, History) {
	"use strict";

	return Controller.extend("rosetracker.RoseTracker.controller.NotFound", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf rosetracker.RoseTracker.view.NotFound
		 */
		onInit: function () {

		},
				 onBack: function()  {
		  var oHistory = History.getInstance();
		  var sPreviousHash = oHistory.getPreviousHash();
		  if (sPreviousHash !== undefined) {
		  window.history.go(-1);
		  } else {
		  var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		  oRouter.navTo("Overview", true);
		  }
		  }

	

	});

});