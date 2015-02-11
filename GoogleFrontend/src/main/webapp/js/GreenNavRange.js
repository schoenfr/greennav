// Extend the state machine by the following states:

/*
 * Add the 'range' event to the 'empty' state going to state 'range1'
 */
GreenNav.states.empty.events.range = 'range1';

/*
 * Add the 'range1' state with event 'resolve' going to 'range2'.
 * 
 * On entering this state, Google's Geocoder is asked for a
 * translation of the start address. The result is put into
 * GreenNav.states.range1.resolved.
 */
GreenNav.states.range1 = {
	events : {
		resolve : 'range2'
	},
	onEnter : function() {
		$('#log').before('Range: Start<br />');
		var startNodeAddress = GreenNav.get_address('#range_address');
		GreenNav.geocoder.geocode(startNodeAddress, function(results, status) {
			if (status === google.maps.GeocoderStatus.OK) {
				GreenNav.states.range1.resolved = results[0].geometry.location;
				$('#log').before(
						'Range: Resolved address to<br />'
						+ results[0].geometry.location + '<br />');
				GreenNav.consumeEvent('resolve');
			} else {
				$('#log').before('Range: Failed to resolve address!<br />');
				GreenNav.consumeEvent('error', 'Can not geocode: ' + status);
			}
		});
	}
};

/*
 * Add the 'range2' state with event 'vertex' going to 'range3',
 * 
 * On entering this state, GreenNav is asked for the corresponding
 * vertex near the given coordinates determined by Google's Geocoder in the
 * first range step.
 */
GreenNav.states.range2 = {
	events : {
		vertex : 'range3'
	},
	onEnter : function() {
		var lat = GreenNav.states.range1.resolved.lat();
		var lon = GreenNav.states.range1.resolved.lng();
		var request = GreenNav.server + "/greennav/vertices/nearest?lat=" + lat
		+ "&lon=" + lon;
		GreenNav.get(request, function(from) {
			$('#log').before('Range: Resolved vertex to<br />' + from + '<br />');
			GreenNav.states.range2.vertex = from;
			GreenNav.consumeEvent('vertex');
		});
	}
};

/*
 * Add the 'range3' state with event 'show' going to 'range4'.
 * 
 * On entering this state, GreenNav is asked to compute the range
 * from the start vertex. It produces a Polygon (google.maps) object that is put
 * onto the map, its reference is saved in GreenNav.states.range3.polygon.
 */
GreenNav.states.range3 = {
	events : {
		show : 'range4'
	},
	onEnter : function() {
		var from = GreenNav.states.range2.vertex;
		var battery = $('#range_energy').val();
		var vehicle = $('#range_vehicle_type').val();
		var request = GreenNav.server + '/greennav/vehicles/' + vehicle
				+ '/ranges/' + from + "?battery=" + battery;

		GreenNav.get(request, function(data) {
			$('#log').before('Range: Successfully computed the range<br />');
			GreenNav.consumeEvent('show');
			var range = data;
			var nodes = range.rangePoints;
			GreenNav.states.range3.polygon = GreenNav.mk_polygon(nodes);
			google.maps.event.addListener(GreenNav.polygon, 'click',
					GreenNav.processClick);
		});
	}
};

/*
 * Add the 'range4' state with event 'reset' going back to initial state 'empty'
 */
GreenNav.states.range4 = {
	events : {
		reset : 'empty'
	}
};

/*
 * This function reads the range form, translates addresses and sends a request
 * to server, the corresponding callback function is responsible for
 * interpreting the results.
 */
GreenNav.calculateRange = function() {
	GreenNav.clear();
	$('#log').before('Cleared<br />');
	if (!GreenNav.validate_form("range")) {
		$('#log').before('Range: Validation failed<br />');
		return;
	}
	GreenNav.consumeEvent('range');
};

GreenNav.rangeLatLon = function(lat, lon, battery) {
	// TODO: remember markers
	var googlelatlon = new google.maps.LatLng(lat, lon);
	GreenNav.marker = new google.maps.Marker({
		position : googlelatlon
	});

	GreenNav.marker.setMap(GreenNav.map);
	GreenNav.map.setCenter(googlelatlon);
};

/*
 * Helper function for drawing a polygon on the map. The coordinates are
 * expected to be a list of JavaScript maps, each containing a "latitude"
 * and a "longitude" key and corresponding values in degree.
 * 
 * Example: [{"latitude": 1.2345, "longitude": 1.2345}, ...]
 */
GreenNav.mk_polygon = function(coordinates) {
	var nodes = [];
	for (var i = 0; i < coordinates.length; i += 1) {
		nodes[i] = new google.maps.LatLng(coordinates[i].latitude,
				coordinates[i].longitude);
	}
	var polygon = new google.maps.Polygon({
		paths : nodes,
		strokeColor : '#FF0000',
		strokeOpacity : 1,
		strokeWeight : 2,
		fillColor : '#FF0000',
		fillOpacity : 0.33
	});
	polygon.setMap(GreenNav.map);
	return polygon;
};