<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
    <script src="http://api-maps.yandex.ru/2.1/?lang=ru_RU" type="text/javascript"></script>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-slider/9.8.0/css/bootstrap-slider.css" type="text/css">
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-slider/9.8.0/bootstrap-slider.js"></script>

    <script type="text/javascript">
    var pollution = "<c:out value='${pollution}' />";
    </script>
    <script src="<c:url value="/resources/map.js" />"></script>

    <script type="text/javascript">
        $(document).ready(function() {
            $("#monthSlider").slider();

            $("#monthSlider").on("slide", function(slideEvt) {
                var d = new Date();
                d.setMonth(d.getMonth() + slideEvt.value);
                var strFilterDate = d.getMonth() + "." + d.getFullYear();
                $("#filterMonthValue").text(strFilterDate);
            });

            var firstVal;
            $("#monthSlider").on("slideStart", function(slideEvt) {
                firstVal = slideEvt.value;
            });
            $("#monthSlider").on("slideStop", function(slideEvt) {
                if (firstVal != slideEvt.value) {
                    alert("!")
                    var d = new Date();
                    d.setMonth(d.getMonth() + slideEvt.value);
                    var strFilterDate = d.getMonth() + "." + d.getFullYear();

                    var responseJson;

                    $.ajax({
                        type: "GET",
                        url: '/get_air_quality_points?filter_date=' + strFilterDate,
                        success: function(resp) {
                            responseJson = resp;
                        }
                    })


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
            <input id="monthSlider" type="text" data-slider-min="-20" data-slider-max="0" data-slider-step="1" data-slider-value="0"/&t
            <span id="filterMonth">Month: <span id="filterMonthValue">05-2017</span></span>
        </div>

        <div id="map" class="container" style="width: 100%; height: 80%; padding-top: 20px">
        </div>
    </div>
</body>
</html>
