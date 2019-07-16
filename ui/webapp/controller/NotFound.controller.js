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

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf rosetracker.RoseTracker.view.NotFound
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf rosetracker.RoseTracker.view.NotFound
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf rosetracker.RoseTracker.view.NotFound
		 */
		//	onExit: function() {
		//
		//	}

	});

});