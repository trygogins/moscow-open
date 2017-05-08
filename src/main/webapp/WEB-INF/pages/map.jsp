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
