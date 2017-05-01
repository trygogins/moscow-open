ymaps.ready(function () {

    // myPlacemark = new ymaps.Placemark([55.76, 37.64], { hintContent: json.cells.MonthlyAverage, balloonContent: 1 > 0 ? 1 : 0 });

    var myMap = new ymaps.Map('map', {
            center: [55.751574, 37.573856],
            zoom: 9
        }, {
            searchControlProvider: 'yandex#search'
        }),

        myPlacemark = new ymaps.Placemark([55.76, 37.64], { hintContent: pollution, balloonContent: parseInt(pollution) > 1 ? "X" : "O" });

    myMap.geoObjects.add(myPlacemark);
});