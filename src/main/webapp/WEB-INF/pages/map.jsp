<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
    <script src="http://api-maps.yandex.ru/2.1/?lang=ru_RU" type="text/javascript"></script>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-slider/9.8.0/css/bootstrap-slider.css" type="text/css">
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-slider/9.8.0/bootstrap-slider.js"></script>

    <script type="text/javascript">
    </script>
    <script type="text/javascript">
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
                }
            });
        });

        $(document).ready(function() {
            $("#monthSlider").slider();

            $("#monthSlider").on("slide", function(slideEvt) {
                var d = new Date();
                d.setMonth(d.getMonth() + slideEvt.value);
                if (d.getMonth() == 0) d.setMonth(1);
                var strFilterDate = (d.getMonth() > 9 ? d.getMonth() : "0" + d.getMonth()) + "." + d.getFullYear();
                $("#filterMonthValue").text(strFilterDate);
            });

            var firstVal;
            $("#monthSlider").on("slideStart", function(slideEvt) {
                firstVal = slideEvt.value;
            });
            $("#monthSlider").on("slideStop", function(slideEvt) {
                if (firstVal != slideEvt.value) {
                    document.getElementById("descriptionDiv").innerHTML = "";
                    $("#descriptionDiv").css('visibility', 'visible');

                    var d = new Date();
                    d.setMonth(d.getMonth() + slideEvt.value);
                    if (d.getMonth() == 0) d.setMonth(1);
                    var strFilterDate = (d.getMonth() > 9 ? d.getMonth() : "0" + d.getMonth()) + "." + d.getFullYear();

                    $.getJSON('/get_air_quality_points?filter_date=' + strFilterDate, function(data) {
                        myMap.geoObjects.removeAll();

                        for (var key in data) {
                            let measurementInfo = data[key];

                            ymaps.geocode(key).then(function(res) {
                                var geoObject = res.geoObjects.get(0);

                                var captionArray = [],
                                        green = 0,
                                        black = 0;
                                for (var i = 0; i < measurementInfo.length && measurementInfo[i]['Cells']['MonthlyAveragePDKss'] != null; i++) {
                                    var isNotPolluted = measurementInfo[i]['Cells']['MonthlyAveragePDKss'] < 1;
                                    var redColor = isNotPolluted ? ""
                                            : "<p style=\"color:#FF0000\">";

                                    var items = redColor + "<b>" + measurementInfo[i]['Cells']['Parameter']
                                    + "</b>: " + measurementInfo[i]['Cells']['MonthlyAveragePDKss'] + (isNotPolluted ? "" : "</p>");

                                    captionArray.push(items);

                                    isNotPolluted ?
                                            green ++
                                            : black ++;
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
                                            iconPieChartRadius: 30,
                                            iconPieChartCoreRadius: 10,
                                            iconPieChartCoreFillStyle: '#ffffff',
                                            iconPieChartStrokeStyle: '#ffffff',
                                            iconPieChartStrokeWidth: 3
                                        });

                                captionArray.sort();
                                var caption = captionArray.join('<br>');

                                myPlacemark.events.add("click", function(e) {
                                    if (e.get('target') == prev) {
                                        return;
                                    }
                                    document.getElementById("descriptionDiv").innerHTML = caption;
                                    $("#descriptionDiv").css('visibility', 'visible');
                                    e.get('target').options.set("iconPieChartStrokeStyle", "#C0C0C0");
                                    e.get('target').options.set("iconPieChartRadius", 40);

                                    if (prev != null) {
                                        prev.options.set("iconPieChartStrokeStyle", "#ffffff");
                                        prev.options.set("iconPieChartRadius", 30);
                                    }

                                    prev = e.get('target');
                                });

                                myMap.geoObjects.add(myPlacemark);
                            });
                        }
                    });
                }
            });
        });
    </script>

    <title>Moscow Open Data::Map</title>
    <jsp:include page="navbar.jsp"/>
</head>
<body style="padding-top: 50px">
    <div class="container well" align="center" style="width: 50%;">
        <div>
            <span id="filterMonth" style="font-size: 120%">Month: <span id="filterMonthValue">05.2017</span></span>
            <input id="monthSlider" type="text" data-slider-min="-20" data-slider-max="0" data-slider-step="1" data-slider-value="0"/>
        </div>

        <div id="map" class="container" style="width: 100%; height: 80%; padding-top: 20px">
        </div>

        <div id="descriptionDiv" align="left" style="visibility: hidden;">
        </div>
    </div>
</body>
</html>
