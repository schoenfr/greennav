/*
 * This is an event handler for mouse clicks on the map (or a polygon), adding
 * locations to the route and showing the remaining range. EXPERIMENTAL
 */
GreenNav.processClick = function(e) {
	if (GreenNav.routemarker.length > GreenNav.routes.length + 1)
		return;
	var m = new google.maps.Marker({
		position : e.latLng,
		map : GreenNav.map
	});
	GreenNav.routemarker[GreenNav.routemarker.length] = m;
	if (GreenNav.routemarker.length < 2)
		return;
	var xM = GreenNav.routemarker[GreenNav.routemarker.length - 2]
			.getPosition();
	var yM = GreenNav.routemarker[GreenNav.routemarker.length - 1]
			.getPosition();
	var x = GreenNav.asLatLon([ xM ])[0];
	var y = GreenNav.asLatLon([ yM ])[0];
	var request = {
		algorithm : "TUM-A*-energy",
		batteryStatus : GreenNav.remaining[GreenNav.remaining.length - 1],
		optimization : "energy",
		payload : 0,
		startNode : x,
		stopoverNode : [],
		targetNode : y,
		vehicleType : $('#route_vehicle_type').val()
	};
	GreenNav.sendRequest(request, "route", function(data) {
		GreenNav.remaining[GreenNav.remaining.length] = data['batteryStatus'];
		GreenNav.show_error(data['batteryStatus']);
		var nodes = data['route'];
		GreenNav.routes[GreenNav.routes.length] = GreenNav.mk_path(nodes);
		if (GreenNav.polygon !== null)
			GreenNav.polygon.setMap(null);
		var requestObject = {
			algorithm : "TUM-A*-energy",
			batteryStatus : data['batteryStatus'],
			dynamicPayload : 0,
			optimization : "energy",
			startNode : y,
			vehicleType : $('#range_vehicle_type').val()
		};
		GreenNav.sendRequest(requestObject, "range", GreenNav.rangeCallback);
	});
};