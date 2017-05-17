var myMap;
var prev; // previously clicked placemark (for usability)
ymaps.ready(function () {

    myMap = new ymaps.Map('map', {
        center: [55.751574, 37.573856],
        zoom: 10
    }, {
        searchControlProvider: 'yandex#search'
    });

    myMap.events.add('click', function(e) {
        document.getElementById("descriptionDiv").innerHTML = "";
        $("#descriptionDiv").css('visibility', 'visible');

        if (prev != null) {
            prev.options.set("iconPieChartStrokeStyle", "#ffffff");
            prev.options.set("iconPieChartRadius", 30);
            prev = null;
        }
    });

    onSlideStop(null);
});

var onSlideStop = function (slideEvt) {
    if (slideEvt != null) {
        valueOnStop = slideEvt.value;
    }
    if (slideEvt == null || valueOnStart != valueOnStop || valueOnStop != prevStop) {
        document.getElementById("descriptionDiv").innerHTML = "";
        $("#descriptionDiv").css('visibility', 'visible');

        var d = new Date();
        d.setMonth(d.getMonth() + (slideEvt == null ? 0 : slideEvt.value));
        if (d.getMonth() == 0) d.setMonth(1);
        var strFilterDate = (d.getMonth() > 9 ? d.getMonth() : "0" + d.getMonth()) + "." + d.getFullYear();
        $("#filterMonthValue").text(strFilterDate);

        $.getJSON('/get_air_quality_points?filter_date=' + strFilterDate, processResponse);
    }
};
var valueOnStart;
var prevStop;
var valueOnStop;

$(document).ready(function() {
    $("#monthSlider").slider();

    $("#monthSlider").on("slide", function(slideEvt) {
        var d = new Date();
        d.setMonth(d.getMonth() + slideEvt.value);
        if (d.getMonth() == 0) d.setMonth(1);
        var strFilterDate = (d.getMonth() > 9 ? d.getMonth() : "0" + d.getMonth()) + "." + d.getFullYear();
        $("#filterMonthValue").text(strFilterDate);
    });

    $("#monthSlider").on("slideStart", function(slideEvt) {
        valueOnStart = slideEvt.value;
        prevStop = valueOnStop;
    });

    $("#monthSlider").on("slideStop", onSlideStop);
});

var processResponse = function (data) {
    myMap.geoObjects.removeAll();

    for (var key in data) {
        let measurementInfo = data[key];

        ymaps.geocode(key).then(function (res) {
            var geoObject = res.geoObjects.get(0);

            var captionArray = [],
                green = 0,
                black = 0;
            for (var i = 0; i < measurementInfo.length && measurementInfo[i]['Cells']['MonthlyAveragePDKss'] != null; i++) {
                var isNotPolluted = measurementInfo[i]['Cells']['MonthlyAveragePDKss'] < 1;
                var redColor = isNotPolluted ? ""
                    : "<p style=\"color:#FF0000\">";

                var items = redColor + "<b>" + measurementInfo[i]['Cells']['Parameter']
                    + "</b>: " + measurementInfo[i]['Cells']['MonthlyAverage']
                    + " (" + Math.round(measurementInfo[i]['Cells']['MonthlyAveragePDKss'] * 100) + (isNotPolluted ? "%)" : "%)</p>");
                console.log(items);
                captionArray.push(items);

                isNotPolluted ?
                    green++
                    : black++;
            }

            if (green + black == 0) {
                return;
            }

            // creating PieChart:
            var data = [{weight: green, color: '#00FF00'}, {weight: black, color: '#000000'}];
            if (green * black == 0) {
                if (green > 0) {
                    data = [{weight: green, color: '#00FF00'}];
                } else {
                    data = [{weight: black, color: '#000000'}];
                }
            }

            var myPlacemark = new ymaps.Placemark(geoObject.geometry._coordinates,
                {
                    data: data
                }, {
                    iconLayout: 'default#pieChart',
                    iconPieChartRadius: 25,
                    iconPieChartCoreRadius: 10,
                    iconPieChartCoreFillStyle: '#ffffff',
                    iconPieChartStrokeStyle: '#ffffff',
                    iconPieChartStrokeWidth: 3
                });

            captionArray.sort();
            var caption = captionArray.join('<br>');

            myPlacemark.events.add("click", function (e) {
                if (e.get('target') == prev) {
                    return;
                }
                document.getElementById("descriptionDiv").innerHTML = caption;
                $("#descriptionDiv").css('visibility', 'visible');
                e.get('target').options.set("iconPieChartStrokeStyle", "#C0C0C0");
                e.get('target').options.set("iconPieChartRadius", 35);

                if (prev != null) {
                    prev.options.set("iconPieChartStrokeStyle", "#ffffff");
                    prev.options.set("iconPieChartRadius", 30);
                }

                prev = e.get('target');
            });

            myMap.geoObjects.add(myPlacemark);
        });
    }
};

