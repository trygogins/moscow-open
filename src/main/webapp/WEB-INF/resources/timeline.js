$(function () {
    // creating chart
    var myChart = Highcharts.chart('chart_container', {
        chart: {
            type: 'bar'
        },
        title: {
            text: 'Fruit Consumption'
        },
        xAxis: {
            categories: ['Apples', 'Bananas', 'Oranges']
        },
        yAxis: {
            title: {
                text: 'Fruit eaten'
            }
        },
        series: [{
            name: 'Jane',
            data: [1, 0, 4]
        }, {
            name: 'John',
            data: [5, 7, 3]
        }]
    });
});

$(document).ready(function() {
    for (var i = 0; i < 10; i++) {
        $("#groupValuesSelector").append('<option>' + i + '</option>');
    }
    $("#groupValuesSelector").selectpicker('refresh');

    $("#groupBySelector").change(function() {
        // getting data for charts
        $.getJSON("/get_timeline_data?group_by=" + $("#groupBySelector").val(), function(data) {
            $("#groupValuesSelector").find("option").remove().end();
            for (var key in data) {
                $("#groupValuesSelector").append('<option>' + key + '</option>');
            }

            $("#groupValuesSelector").selectpicker('refresh');
        });
    });
});