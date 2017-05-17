var myChart;

$(document).ready(function() {
    $('.nav-tabs a[href="#env_time"]').on('shown.bs.tab', function() {
        $("#groupBySelector").val("StationName");
        $("#groupBySelector").trigger("change");
    });
});

$(function () {
    // creating chart
    myChart = Highcharts.chart('chart_container', {
        title: {
            text: 'Pollution Timeline'
        },
        yAxis: {
            title: {
                text: 'Pollution, %'
            }
        },
        series: []
    });
});

var timeline_data;

$(document).ready(function() {
    $("#groupValuesSelector").selectpicker('refresh');

    $("#groupBySelector").change(function() {
        // getting data for charts
        $.getJSON("/get_timeline_data?group_by=" + $("#groupBySelector").val(), function(data) {
            $("#groupValuesSelector").find("option").remove().end();
            for (var key in data) {
                $("#groupValuesSelector").append('<option>' + key + '</option>');
            }

            $("#groupValuesSelector").selectpicker('refresh');

            timeline_data = data;
        });

        $("#groupValuesSelector").trigger("change");
    });

    $("#groupValuesSelector").change(function() {
        var mySeries = [];
        var this_timeline = timeline_data[$("#groupValuesSelector").val()];

        var substances = Object.keys(this_timeline);

        for (var i = 0; i < substances.length; i++) {
            var dataline = this_timeline[substances[i]];
            var vals = [];

            var dates = Object.keys(dataline);

            for (var j = 0; j < dates.length; j++) {
                vals.push(Object.values(dataline[dates[j]]));
            }

            mySeries.push({"name" : substances[i], "data" : vals})
        }

        myChart = Highcharts.chart('chart_container', {
            title: {
                text: 'Pollution Timeline'
            },

            yAxis: {
                title: {
                    text: 'Pollution, %'
                }
            },

            xAxis: {
                categories: ['02.2016', '03.2016', '04.2016', '05.2016', '06.2016', '07.2016', '08.2016', '09.2016', '10.2016',
                    '11.2016', '12.2016', '01.2017', '02.2017', '03.2017']
            },

            series : mySeries
        });
    });
});