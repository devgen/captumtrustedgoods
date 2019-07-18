sap.ui.define([], function () {
	"use strict";
	return {
		statusText: function (sStatus) {
			var resourceBundle = this.getView().getModel("i18n").getResourceBundle();
			switch (sStatus) {
				case "Success":
					return "sap-icon://sys-enter";
				case "Warning":
					return resourceBundle.getText("Warning");
				case "Error":
					return resourceBundle.getText("Error");
				default:
					return "hi";
			}
		}
	};
});