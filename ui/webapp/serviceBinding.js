function initModel() {
	var sUrl = "/RoseTrackerOData/odata/v2/RoseTrackerDataService/";
	var oModel = new sap.ui.model.odata.ODataModel(sUrl, true);
	sap.ui.getCore().setModel(oModel);
}