/*
 * Green Navigation
 * http://www.isp.uni-luebeck.de/research/projects/green-navigation
 * 
 * This file contains the client functionality for GreenNav. The two main
 * features are route calculation and range prediction. It is based on the
 * Google API for translating coordinates and drawing maps.
 */


/**
 * A state machine is used for reacting on ajax callbacks, such that heavily
 * nested function definitions can be avoided.
 * 
 * @param {type} states
 * @param {type} initial
 * @returns {StateMachine}
 */
function StateMachine(states, initial) {

	// Remember the set of states
	this.states = states;

	// Choose the current state to be the initial state
	this.currentState = initial;

	/*
	 * Consuming and event means to transit to the next state. The data variable
	 * is used to provide arguments to the onEnter function of the next state.
	 */
	this.consumeEvent = function(event, data) {
		// If the event is defined for the current state
		if (this.states[this.currentState].events[event]) {
			// Leave the current state
			if (this.states[this.currentState].onExit) {
				this.states[this.currentState].onExit();
			}
			// Choose the next state
			this.currentState = this.states[this.currentState].events[event];
			// Write to log
			// $('#log').before(
			// 'State: Consumed ' + event + ', now in state '
			// + this.currentState + '<br />');
			// Enter next state
			if (this.states[this.currentState].onEnter) {
				this.states[this.currentState].onEnter(data);
			}
		}
	};

	/*
	 * Getter for the current state name.
	 */
	this.getCurrentStateName = function() {
		return this.currentState;
	};

	/*
	 * Getter for the current state.
	 */
	this.getCurrentState = function() {
		return this.states[this.currentState];
	};

	/*
	 * Getter for some state identified by name x.
	 */
	this.getState = function(x) {
		return this.states[x];
	};

};

/*
 * The following set of states contains an empty and an error state.
 */
var initStates = {
	// The state name is 'empty'
	empty : {
		// The set of events for 'empty' is defined as follows
		events : {
			// On a 'reset' event, go to the 'empty' state
			reset : 'empty'
		}
	},
	// The state name is 'error'
	error : {
		// The set of events for 'empty' is defined as follows
		events : {
			// On a 'reset' event, go to the 'empty' state
			reset : 'empty'
		}
	}
};

function getUrlParameter(sParam) {
	var sPageURL = window.location.search.substring(1);
	var sURLVariables = sPageURL.split('&');
	for (var i = 0; i < sURLVariables.length; i++) {
		var sParameterName = sURLVariables[i].split('=');
		if (sParameterName[0] == sParam) {
			return sParameterName[1];
		}
	}
};

/*
 * GreenNav namespace: The namespace dictionary contains the following
 * functions: calculate_route, calculate_range, send_request. These functions
 * are called when the calculate buttons are clicked. They read the input
 * fields, send a request to the server and display the result of the request. -
 * get_address, validate_form, add_input_step To read/validate input and create
 * new input fields. - show_error, show_dialog To display text within the error
 * <div> or within a dialog. - mk_path, mk_polygon To draw a path or polygon
 * onto the map. - clear To remove the path/polygon from the map and hide all
 * messages.
 */
var GreenNav = new StateMachine(initStates, 'empty');

/*
 * The initialization function is called, when the document is ready. It
 * initializes the map to given coordinates. It also connects the UI components
 * with corresponding JavaScript functions.
 */
$(document).ready(
		function() {

                        GreenNav.log('Init Log:');
                        
			GreenNav.server = "https://greennav.isp.uni-luebeck.de";		

			// Initialize the map
			var options = {
				zoom : 8,
				center : new google.maps.LatLng(53.8344, 10.7042),
				mapTypeId : google.maps.MapTypeId.ROADMAP,
				scaleControl : true
			};

			GreenNav.map = new google.maps.Map(document.getElementById('map'),
					options);

			GreenNav.states.error.onEnter = function() {
				GreenNav.show_error('Error: ' + status);
			};

			// Attributes
			GreenNav.geocoder = new google.maps.Geocoder();

			GreenNav.path = null;
			GreenNav.polygon = null;
			GreenNav.marker = null;

			GreenNav.routemarker = [];
			GreenNav.routes = [];
			GreenNav.remaining = [ 100 ];

			GreenNav.steps = [];
			GreenNav.step_id = 0;

			google.maps.event.addListener(GreenNav.map, 'click',
					GreenNav.processClick);

			$(document).ajaxError(
					function(event, XmlHttpRequest, settings, error) {
						var status = (xhr || {}).status;
						error += 'Error: ' + status;
						GreenNav.show_error(error);
					});

			// Toggle input <div> visibility
			$('#toggle_input').click(function() {
				$('#error').hide();
				$('#input').animate({
					width : 'toggle'
				}, 0, function() {
					google.maps.event.trigger(GreenNav.map, 'resize');
				});
			});

			// Toggle route/range visibility
			$('#show_route').css('color', '#5fc82b').click(function() {
				$(this).css('color', '#5fc82b');
				$('#show_range').css('color', '#0b5c26');
				$('#route_input').show();
				$('#range_input').hide();
                                $('#directions').show();
			});

			$('#show_range').click(function() {
				$(this).css('color', '#5fc82b');
				$('#show_route').css('color', '#0b5c26');
				$('#route_input').hide();
				$('#range_input').show();
                                $('#directions').hide();
			});

			// Bind route/range functions to buttons
			$('#route').click(GreenNav.calculateRoute);
			$('#route_start, #route_dest, #route_energy, div[id^="step"]')
					.keypress(function(event) {
						if (event.which === 13) {
							GreenNav.calculateRoute();
						}
					});
			$('#add_step').click(GreenNav.add_input_step);
			$('#range').click(GreenNav.calculateRange);
			$('#range_address, #range_energy').keypress(function(event) {
				if (event.which === 13) {
					GreenNav.calculateRange();
				}
			});

			// Register loading <div> for AJAX callbacks
			$('#loading').ajaxStart(function() {
				$(this).show();
			}).ajaxStop(function() {
				$(this).hide();
			});

			// Info/Kontakt dialogs
			$('#info').click(function() {
				GreenNav.show_dialog('info_greennav');
			});
			$('#contact').click(function() {
				GreenNav.show_dialog('contact_greennav');
			});
			$('#bg, .close_dialog').click(function() {
				$('#bg, .dialog').hide();
			});
			$.when(GreenNav.readVehicles()).done(function () {
				// Check for GET variables for predefined request
				var from = getUrlParameter('from');
				var to = getUrlParameter('to');
				var vehicle = getUrlParameter('vehicle');
				var battery = getUrlParameter('battery');
				$('#route_start > input').eq(0).val(from);
				$('#route_dest > input').eq(0).val(to);
				$('#route_vehicle_type').val(vehicle);
				$('#route_energy').val(battery);
				if (from != null) {
					GreenNav.calculateRoute();
				}
			});
        
                        /********************************
                         * FOOLING AROUND
                         */
                        
                        //GreenNav.server
                        var serveraddr = GreenNav.server+"/greennav/vehicles";
                        GreenNav.log("Server: "+serveraddr);
                        $.ajax(        
                                {
                                        type : "GET",
                                        url :  serveraddr,
                                        dataType : "json",
                                        error : function(xhr, textStatus, errorThrown) {        
                                            GreenNav.log(" -No connection to the server!");},
                                        success : function(data, textStatus, XMLHttpRequest) {     
                                            if (data.length !== 0) {
                                                GreenNav.log("Server found");
                                                GreenNav.log(GreenNav.server);
                                            }}
                                    });
		});

/*
 * This function initializes the select box for vehicle types.
 */
GreenNav.readVehicles = function() {
	GreenNav.log('Init: Start to read vehicles<br />');
	GreenNav.log(GreenNav.server + "/greennav/vehicles");
	return GreenNav.get(GreenNav.server + "/greennav/vehicles", function(data) {
		$("#route_vehicle_type").empty();
		for (var i = 0; i < data.length; i++) {
			var vhc = data[i];
			$("<option/>").val(vhc).text(vhc).appendTo("#route_vehicle_type");
		}
		$("#range_vehicle_type").empty();
		for (var i = 0; i < data.length; i++) {
			var vhc = data[i];
			$("<option/>").val(vhc).text(vhc).appendTo("#range_vehicle_type");
		}
		GreenNav.log('Init: Read ' + data.length + ' vehicles<br />');
	});
};

/*
 * Removes the markeres by assigning them a null map
 * @returns {undefined}
 */
function removeMarkers() {
    for (var i = 0; i < GreenNav.routemarker.length; i++) {        
        GreenNav.routemarker[i].setMap(null);
    }
}

function removeMapPaths() {
    for (var i = 0; i < GreenNav.routes.length; i++) {
        GreenNav.routes[i].setMap(null);
    }
}
/*
 * Remove previously drawn path or polygon from the map and hide all messages.
 */
GreenNav.clear = function() {
	GreenNav.consumeEvent('reset');
	$('#error').hide();
        $('#log').empty();
	$('#route_details').empty().hide();
        $('#directions').empty();

        removeMarkers();
        removeMapPaths();
//        for (var i; i < GreenNav.routes.length; i++) {
//            GreenNav.routes[i].setMap(null);
//        }
        
        GreenNav.routes = [];

	GreenNav.remaining = [ 100 ];

	if (GreenNav.path !== null) {
		GreenNav.path.setMap(null);
		GreenNav.path = null;
	}

	if (GreenNav.polygon !== null) {
		GreenNav.polygon.setMap(null);
		GreenNav.polygon = null;
	}

	if (GreenNav.marker !== null) {
		GreenNav.marker.setMap(null);
		GreenNav.marker = null;
	}
};

/*
 * Fade in a new error message.
 */
GreenNav.show_error = function(msg) {
	$('#error').hide().html(msg).fadeIn('slow');
};

/*
 * Show log
 */
GreenNav.log = function(msg) {
        $('#log').append('<p>'+msg+'</p>');
        //$('#log').css("border","solid medium");
}


/*
 * Fade in a new dialog.
 */
GreenNav.show_dialog = function(dialog) {
	var div = $('#' + dialog);
	var h = $(document).height();
	var w = $(document).width();
	$('#bg').height(h).width(w).fadeTo(500, 0.64);
	div.css({
		'top' : h / 2 - div.height() / 2,
		'left' : w / 2 - div.width() / 2
	});
	div.fadeTo(1000, 1);
};

/*
 * Test, if all form fields for the request [type] contain text. The parameter
 * 'type' can either be "route" or "range".
 */
GreenNav.validate_form = function(type) {
	var elements = $('#' + type + '_input input.inputfield');

	for (var i = 0; i < elements.length; i += 1) {
		if (elements.eq(i).val() === '') {
			GreenNav.show_error('Nicht alle Felder ausgef&uuml;llt!');
			return false;
		}
	}

	var energyValue = parseInt($('#' + type + '_energy').val(), 10);

	if (isNaN(energyValue)) {
		GreenNav
				.show_error('Der Energiewert muss eine Zahl<br />zwischen 0 und 100 sein!');
		return false;
	}

	if (energyValue < 0 || 100 < energyValue) {
		GreenNav
				.show_error('Der Energiewert muss eine Zahl<br />zwischen 0 und 100 sein!');
		return false;
	}

	return true;
};

/*
 * This function wraps an ajax call sending a request object of a particular
 * type defined by the parameter "requestType". This is done in order to
 * potentially log ajax calls and to log ajax errors.
 */
GreenNav.get = function(url, callback) {
	return $.ajax({
		url : url,
		timeout : 30000,
		success : function(data, textStatus, XMLHttpRequest) {
			callback(data);
		},
		error : function(xhr, textStatus, errorThrown) {
			GreenNav.log('Error: ' + textStatus + '<br />');
                        GreenNav.log(errorThrown);
			GreenNav.show_error(errorThrown);
		},
		dataType : "json"
	});
};

/*
 * Read an address from an input field. The parameter 'div' is used to specify
 * the field, from which to read the address.
 */
GreenNav.get_address = function(div) {
	var addr = $(div + ' > input').eq(0).val();
	return {
		'address' : addr,
		'region' : 'de'
	};
};

/*
 * This function is used to transform a list of nodes (containing coordinates)
 * into a list of latlon-pairs. This is necessary because the GreenNav web
 * service can not handle Google coordinates but only latlon-pairs.
 */
GreenNav.asLatLon = function(resolved) {
	var nodes = [];
	for (var i = 0; i < resolved.length; i++) {
		var node = {
			latitude : resolved[i].lat(),
			longitude : resolved[i].lng()
		};
		nodes[i] = node;
	}
	return nodes;
};

/*
 * This function is used to translate a list of nodes (containing addresses)
 * into a list of coordinates. Notice, that we can not avoid the recursive
 * function behavior because of the callback nature of ajax.
 */
GreenNav.resolveAdresses = function(nodes, callback) {
	var f = function(original, resolved) {
		if (original.length === 0) {
			callback(resolved);
		}
		GreenNav.geocoder.geocode(original.pop(), function(results, status) {
			if (status === google.maps.GeocoderStatus.OK) {
				resolved.push(results[0].geometry.location);
				// TODO: other results?
				f(original, resolved);
			} else {
				GreenNav.show_error('Can not geocode: ' + status);
			}
		});
	};
	f(nodes, []);
};

/*
 * Add HTML code for another 'step' to the route input form. Steps are used to
 * define places to visit by a route from the start to the destination.
 */
GreenNav.add_input_step = function() {
	$('#error').hide();

	GreenNav.step_id += 1;

	// Create new step
	var c = GreenNav.step_id;
	var html = '<div id="step'
			+ c
			+ '">\
          <label><a id="rm_step'
			+ c
			+ '" href="javascript:;">[-]</a> Step:</label>\
          <input type="text" value="" class="inputfield if_long" />\
        </div>';
	$('#steps').append(html);

	// Bind remove function to the new step
	$('#rm_step' + c).click(function() {
		var t = '#step' + c;
		$(t).remove();
		var tmp = [];

		for (var i = 0; i < GreenNav.steps.length; i++) {
			if (GreenNav.steps[i] !== t) {
				tmp.push(GreenNav.steps[i]);
			}
		}
		GreenNav.steps = tmp;
	});

	// Add the new step to the list which is read when a route is calculated
	GreenNav.steps.push('#step' + GreenNav.step_id);
};

