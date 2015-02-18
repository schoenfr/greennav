// ------------- the states -------------

/*
 * Add the 'route' event to the 'empty' state going to state 'translationStart'
 */
GreenNav.states.empty.events.route = 'translationStart';

/*
 * This function validates the input form and triggers the routing process.
 */
GreenNav.calculateRoute = function () {
    GreenNav.clear();
    GreenNav.log('Cleared<br />');
    if (GreenNav.validate_form('route')) {
        GreenNav.consumeEvent('route');
    } else {
        GreenNav.log('Route: Validation failed<br />');
    }
};

/*
 * 
 * On entering the first routing state, Google's Geocoder is asked for a
 * translation of the start address. The result is put into
 * GreenNav.states.translationStart.resolved.
 */
GreenNav.states.translationStart = {
    events: {
        resolve: 'translationDestination',
        error: 'error'
    },
    onEnter: function () {
        var startNodeAddress = GreenNav.get_address('#route_start');
        GreenNav.geocoder.geocode(startNodeAddress, function (results, status) {
            if (status === google.maps.GeocoderStatus.OK) {
                GreenNav.states.translationStart.resolved = results[0].geometry.location;
                GreenNav.log(
                        'Route: Resolved first address to<br />'
                        + results[0].geometry.location + '<br />');
                GreenNav.consumeEvent('resolve');
            } else {
                GreenNav.log(
                        'Route: Failed to resolve first address!<br />');
                GreenNav.consumeEvent('error', 'Can not geocode: ' + status);
            }
        });
    }
};

/*
 * On entering the second routing state, Google's Geocoder is asked for a
 * translation of the destination address. The result is put into
 * GreenNav.states.translationDestination.resolved.
 */
GreenNav.states.translationDestination = {
    events: {
        resolve: 'interpolatingStart',
        error: 'error'
    },
    onEnter: function () {
        var targetNodeAddress = GreenNav.get_address('#route_dest');
        GreenNav.geocoder.geocode(targetNodeAddress, function (results, status) {
            if (status === google.maps.GeocoderStatus.OK) {
                GreenNav.states.translationDestination.resolved = results[0].geometry.location;
                GreenNav.map.setCenter(results[0].geometry.location);
                GreenNav.log(
                        'Route: Resolved second address to<br />'
                        + results[0].geometry.location + '<br />');
                GreenNav.consumeEvent('resolve');
            } else {
                GreenNav.log(
                        'Route: Failed to resolve second address!<br />');
                GreenNav.consumeEvent('error', 'Can not geocode: ' + status);
            }
        });
    }
};

/*
 * On entering the third routing state, GreenNav is asked for the corresponding
 * vertex near the given coordinates determined by Google's Geocoder in the
 * first routing step.
 */
GreenNav.states.interpolatingStart = {
    events: {
        vertex: 'interpolatingDestination'
    },
    onEnter: function () {
        var lat = GreenNav.states.translationStart.resolved.lat();
        var lon = GreenNav.states.translationStart.resolved.lng();
        var request = GreenNav.server + "/greennav/vertices/nearest?lat=" + lat
                + "&lon=" + lon;
        GreenNav.get(request, function (from) {
            GreenNav.log(
                    'Route: Resolved first vertex to<br />' + from + '<br />');
            GreenNav.states.interpolatingStart.vertex = from;
            GreenNav.consumeEvent('vertex');
        });
    }
};

/*
 * On entering the fourth routing state, GreenNav is asked for the corresponding
 * vertex near the given coordinates determined by Google's Geocoder in the
 * second routing step.
 */
GreenNav.states.interpolatingDestination = {
    events: {
        vertex: 'routeGreenNavServer',
        charging: 'chargingPath'
    },
    onEnter: function () {
        var lat = GreenNav.states.translationDestination.resolved.lat();
        var lon = GreenNav.states.translationDestination.resolved.lng();
        var charging = document.getElementById('chargingStations');
        var request = GreenNav.server + "/greennav/vertices/nearest?lat=" + lat
                + "&lon=" + lon;
        GreenNav.get(request, function (to) {
            GreenNav.log(
                    'Route: Resolved second vertex to<br />' + to + '<br />');
            GreenNav.states.interpolatingDestination.vertex = to;
            if (charging.checked) {
                GreenNav.log('Use Charging Algorithm<br />');
                GreenNav.consumeEvent('charging');
            } else {
                GreenNav.log('Use Standard Algorithm<br />');
                GreenNav.consumeEvent('vertex');
            }
        });
    }
};

/**
 * Draw a path on the map. The coordinates are expected to be a list of
 * JavaScript maps, each containing a "latitude" and a "longitude" key with
 * corresponding values in degree.
 * 
 * Example: [{"latitude": 1.2345, "longitude": 1.2345}, ...]
 */
GreenNav.turnTranslation = {
    1: "gerade aus",
    2: "links abbiegen",
    3: "rechts abbiegen",
    4: "leicht links abbiegen",
    5: "leicht rechts abbiegen",
    6: "scharf links abbiegen",
    7: "scharf rechts abbiegen",
    20: "in den Kreisverkehr einbiegen",
    21: "den Kreisverkehr verlassen"
};

GreenNav.turnTranslationIcon = function (turnNumber, from, to, w) {

    var width = 24;

    var translations = {
        1: "<img src=\"images/straight.png\" width=\"" + width + "\"/>",
        2: "<img src=\"images/left.png\" width=\"" + width + "\"/>",
        3: "<img src=\"images/right.png\" width=\"" + width + "\"/>",
        4: "<img src=\"images/20left.png\" width=\"" + width + "\"/>",
        5: "<img src=\"images/20right.png\" width=\"" + width + "\"/>",
        6: "<img src=\"images/left.png\" width=\"" + width + "\"/>",
        7: "<img src=\"images/right.png\" width=\"" + width + "\"/>",
        20: "<img src=\"images/roundabout.png\" width=\"" + width + "\"/>",
        21: "<img src=\"images/roundabout-out.png\" width=\"" + width + "\"/>"
    }

//    // left turn
//    if (turnNumner === 2 || turnNumber === 4 || turnNumber === 6) {
//        
//    }

    return translations[turnNumber] + " " + to;
}

/**
 * Parses the numeric Values of a hex-color string
 * can also be used for blue, red, green
 * @param {String} color
 * @returns {Array}
 */
GreenNav.parseColor = function (color) {
    
    if (color === 'blue')
        color = "#0000ff";
    else if (color === 'green')
        color = "#00ff00";
    else if (color === 'red')
        color = "#ff0000";

    var colorValues = [];
    //red
    colorValues[0] = parseInt(color.substr(1, 2), 16);
    //green
    colorValues[1] = parseInt(color.substr(3, 2), 16);
    //blue
    colorValues[2] = parseInt(color.substr(5, 2), 16);
    return colorValues;
}

/**
 * Takes a list of coordinates and creates a path line and driving directions from them
 * @param {type} coordinates
 * @param {type} color
 * @returns {google.maps.Polyline|StateMachine.mk_path.path|GreenNav.mk_path.path}
 */
GreenNav.mk_path = function (coordinates, color, optForString) {

    var lastCoordinate = "Start";
    var nodes = [];
    if (optForString == null)
        optForString = "Route";
    
    // Get
    var colorValues = GreenNav.parseColor(color);
    var rgbaColorString = "rgba(" + colorValues[0] + "," + colorValues[1] + "," + colorValues[2] + ",0.4)";
    
    // SVG Symbol Triangle
    var triangleIcon = {
        path: google.maps.SymbolPath.BACKWARD_CLOSED_ARROW,
        fillColor: color,
        fillOpacity: 0.4,
        scale: 2,
        strokeColor: "black",
        strokeOpacity: 0.4,
        strokeWeight: 1
    };
    
    // Directions: create an unordered list in the directions-div for every route
    $("#directions").append("<ul id=\"routeColor" + color + "\"></ul>");
    // style and append a list; 
    var routeList = $("#routeColor" + color).css({'border': "none " + color, 'padding': "10px",
        'background-color': rgbaColorString})
            .append("<h4>"+optForString+"</h4>");

    for (var i = 0; i < coordinates.length; i += 1) {
        nodes[i] = new google.maps.LatLng(coordinates[i].latitude,
                coordinates[i].longitude);
        if (coordinates[i].turn > 0) {
            var m = new google.maps.Marker({
                position: nodes[i],
                map: GreenNav.map,
                title: GreenNav.turnTranslation[coordinates[i].turn] + " auf "
                        + coordinates[i].street,
                icon: triangleIcon
            });
            //Adds the Marker to the global GreenNav Marker Array
            //(for forther access and deletion)
            GreenNav.routemarker.push(m);
//            $("#directions").append("<p> <div class=\"direction\" onclick=\"GreenNav.linkDirection("+i+"); \">" + "" + lastCoordinate + " " + m['title'] + "</div> </p>");
            routeList.append("<li class=\"direction\" onclick=\"GreenNav.map.setCenter( { lat:"
                    + nodes[i].lat() + ", lng:" + nodes[i].lng() + "}); GreenNav.map.setZoom(15)\">"
                    //+ m['title'] 
                    + GreenNav.turnTranslationIcon(coordinates[i].turn, lastCoordinate, coordinates[i].street, 24)
                    + "</li>");
            $('.direction').css({'list-style': 'none'});
            lastCoordinate = coordinates[i].street;
        }
    }
    
    var path = new google.maps.Polyline({
        path: nodes,
        strokeColor: color,
        strokeOpacity: 0.6,
        strokeWeight: 2.5,
        map: GreenNav.map
    });
    // Adds the path to the GreenNav path (mainly for deletion)
    GreenNav.routes.push(path);
    return path;
};

/*
 * On entering the fifth routing state, GreenNav is asked to route from start to
 * destination. It produces a Polyline (google.maps) object that is put onto the
 * map, its reference is saved in GreenNav.states.route5.path.
 */
GreenNav.states.routeGreenNavServer = {
    events: {
        show: 'stopRouting'
    },
    onEnter: function () {
        var from = GreenNav.states.interpolatingStart.vertex;
        var to = GreenNav.states.interpolatingDestination.vertex;
        var battery = $('#route_energy').val();
        var vehicle = $('#route_vehicle_type').val();
        //var opt = $('#route_optimization').val().toLowerCase();
        var requestEnergy = GreenNav.server + "/greennav/vehicles/" + vehicle
                + "/routes/" + from * 1 + "-" + to + "/opt/" + "energy" + "?battery="
                + battery + "&turns=true&algorithm=EnergyAStar";
        var requestShortest = GreenNav.server + "/greennav/vehicles/" + vehicle
                + "/routes/" + from * 1 + "-" + to + "/opt/" + "distance" + "?battery="
                + battery + "&turns=true";
        var requestFastest = GreenNav.server + "/greennav/vehicles/" + vehicle
                + "/routes/" + from * 1 + "-" + to + "/opt/" + "time" + "?battery="
                + battery + "&turns=true";

	GreenNav.log(requestEnergy);
	GreenNav.log(requestShortest);
	GreenNav.log(requestFastest);

        function callbackEnergy(data) {
            var answer = data;
            GreenNav.log('Successfully computed energy-efficient route ALG:' + answer.algorithm);
            GreenNav.states.routeGreenNavServer.energyPath = GreenNav.mk_path(answer.route, 'green', "Energieeffiziente Route");
//                        for(var i = 0; i < answer.route.length; i += 1) {
//                            GreenNav.log('Route turn:'+GreenNav.turnTranslation[answer.route[i]['turn'].toString()]);
//                        }
        }
        function callbackShortest(data) {
            var answer = data;
            GreenNav.log('Successfully computed shortest route ALG:' + answer.algorithm);
            GreenNav.states.routeGreenNavServer.shortestPath = GreenNav.mk_path(answer.route, 'blue', "K&uuml;rzeste Route");
        }
        function callbackFastest(data) {
            var answer = data;
            GreenNav.log('Successfully computed fastest route ALG:' + answer.algorithm);
            GreenNav.states.routeGreenNavServer.fastestPath = GreenNav.mk_path(answer.route, 'red', "Schnellste Route");
        }

        GreenNav.log("<a href=\"" + requestEnergy + "\">Energy link</a>");
        var ajax1 = GreenNav.get(requestEnergy, callbackEnergy);
        GreenNav.log("<a href=\"" + requestShortest + "\">Time link</a>");
        var ajax2 = GreenNav.get(requestShortest, callbackShortest);
        GreenNav.log("<a href=\"" + requestFastest + "\">Distance link</a>");
        var ajax3 = GreenNav.get(requestFastest, callbackFastest);

        $.when(ajax1, ajax2, ajax3).done(function () {
            GreenNav.log("Display routes done!");
            GreenNav.consumeEvent('show');
        });
    }
};

/**
 * Draw a path on the map. The coordinates are expected to be a list of
 * JavaScript maps, each containing a "latitude" and a "longitude" key with
 * corresponding values in degree.
 * 
 * Example: [{"latitude": 1.2345, "longitude": 1.2345}, ...]
 */
GreenNav.mk_chargingStations = function (coordinates) {
    for (var i = 0; i < coordinates.length; i += 1) {
        new google.maps.Marker({
            position: new google.maps.LatLng(coordinates[i].latitude,
                    coordinates[i].longitude),
            map: GreenNav.map,
            title: "Charging Station"
        });
    }
    return;
};

GreenNav.states.stopRouting = {
    events: {
        reset: 'empty'
    },
    onEnter: function () {
        $('.direction').toggle();
        $('#directions ul h4').click(function () {
            $(this).parent('ul').children('li').toggle();
        });
    }

};

/**
 * On entering the seventh routing state, GreenNav is asked to route from start
 * to destination using charging stations along the way. It produces a Polyline
 * (google.maps) object that is put onto the map, its reference is saved in
 * GreenNav.states.chargingPath.path.
 */
GreenNav.states.chargingPath = {
    events: {
        show: 'stopRouting'
    },
    onEnter: function () {
        var from = GreenNav.states.interpolatingStart.vertex;
        var to = GreenNav.states.interpolatingDestination.vertex;
        var battery = $('#route_energy').val();
        var vehicle = $('#route_vehicle_type').val();
        var opt = $('#route_optimization').val().toLowerCase();
        var request = GreenNav.server + "/greennav/vehicles/" + vehicle
                + "/chargingroutes/" + from + "-" + to + "/opt/" + opt
                + "?battery=" + battery + "&algorithm=" + "RuegerDijkstra" + "&turns=true";
        var getRequest = $.get(request, function (data) {
            GreenNav.log('Route: Successfully computed the route');
            var answer = JSON.parse(data);
            GreenNav.states.chargingPath.path = GreenNav.mk_path(answer.route, 'red');
            GreenNav.mk_chargingStations(answer.chargingStations);
        }).done(function () {
            alert("DONE");
        }).always(function () {
            alert("ERROR");
        });

        GreenNav.consumeEvent('show');
    }
};
