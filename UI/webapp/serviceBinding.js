function initModel() {
	//var sUrl = "/RoseTrackerDummyDataV2/odata/v2/RoseTrackerDataService/";
	var sUrl = "/RoseTrackerDummyDataV2/odata/v2/RoseTrackerDataService/";
	var oModel = new sap.ui.model.odata.ODataModel(sUrl, true);
	sap.ui.getCore().setModel(oModel);
}