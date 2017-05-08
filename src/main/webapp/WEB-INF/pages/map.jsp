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
    <script src="/resources/env_map.js" type="text/javascript"></script>

    <title>Moscow Open::Map</title>
    <jsp:include page="navbar.jsp"/>
</head>
<body style="padding-top: 50px">
    <div class="container well">
        <ul class="nav nav-tabs">
            <li class="active"><a data-toggle="tab" href="#env_map">Map</a></li>
            <li><a data-toggle="tab" href="#env_time">Timeline</a></li>
            <li><a data-toggle="tab" href="#env_summary">Summary</a></li>
        </ul>
        <div class="tab-content">
            <div id="env_map" class="tab-pane fade in active">
                <h3>Environment Map</h3>
                <div>
                    <span id="filterMonth" style="font-size: 120%">Month: <span id="filterMonthValue">05.2017</span></span>
                    <input id="monthSlider" type="text" data-slider-min="-20" data-slider-max="0" data-slider-step="1" data-slider-value="0"/>
                </div>

                <div id="map" class="container" style="width: 100%; height: 80%; padding-top: 20px">
                </div>

                <div id="descriptionDiv" align="left" style="visibility: hidden;">
                </div>
            </div>
            <div id="env_summary" class="tab-pane fade">
                <h3>Environment Summary</h3>
            </div>
            <div id="env_time" class="tab-pane fade">
                <h3>Environment Timeline</h3>
            </div>
        </div>
    </div>
</body>
</html>
