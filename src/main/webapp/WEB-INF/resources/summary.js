$(document).ready(function() {
    $('.nav-tabs a[href="#env_summary"]').on('shown.bs.tab', function() {
        $.getJSON("/get_summary", function(data) {
            var stationsCaption = "<h4>Five most polluted stations:</h4>";
            for (var i = 0; i < 5; i++) {// data["stationNames"].length; i++) {
                stationsCaption += (i + 1) + ". " + data["stationNames"][i]["name"]
                + ": <b>" + data["stationNames"][i]["pollution"] + "</b><br>";
            }

            document.getElementById("mostPollutedStations").innerHTML = stationsCaption;

            var districtsCaption = "<h4>Five most polluted districts:</h4>";
            for (var i = 0; i < 5; i++) { // data["districts"].length; i++) {
                districtsCaption += (i + 1) + ". " + data["districts"][i]["name"]
                + ": <b>" + data["districts"][i]["pollution"] + "</b><br>";
            }

            document.getElementById("mostPollutedDistricts").innerHTML = districtsCaption;
        });
    });
});