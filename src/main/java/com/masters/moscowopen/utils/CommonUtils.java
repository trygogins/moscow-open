package com.masters.moscowopen.utils;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Georgii Ovsiannikov
 * @since 5/17/17
 */
public class CommonUtils {

    public static int compareDates(String date1, String date2) {
        DateTime given = DateTimeFormat.forPattern("MM.YYYY").parseDateTime(date1);
        DateTime filterDate = DateTimeFormat.forPattern("MM.YYYY").parseDateTime(date2);

        return given.compareTo(filterDate);
    }

    public static void main(String[] args) throws IOException {
        JSONArray jsonData = new JSONArray(IOUtils.toString(new FileInputStream("/Users/georgii/Dropbox/master_program/2nd/coursework/moscow-open/src/main/webapp/WEB-INF/resources/dataset.txt")));
        Set<String> allParameters = new HashSet<>();
        for (Object m : jsonData) {
            allParameters.add(((JSONObject) m).getJSONObject("Cells").getString("Parameter"));
        }

        System.out.println(allParameters);

        Map<String, List<JSONObject>> temp = new TreeMap<>(CommonUtils::compareDates);
        for (Object o : jsonData) {
            String timePeriod = ((JSONObject) o).getJSONObject("Cells").getString("Period");
            if (!temp.containsKey(timePeriod)) {
                temp.put(timePeriod, new ArrayList<>());
            }

            temp.get(timePeriod).add((JSONObject) o);
        }

        List<String> params = Arrays.asList("Оксид углерода", "Диоксид азота", "Взвешенные частицы РМ10", "Взвешенные частицы РМ2.5");
        Map<String, Map<String, Double>> globalResult = new TreeMap<>();
        for (String param : params) {
            Map<String, Double> result = new TreeMap<>(CommonUtils::compareDates);
            for (String timePeriod : temp.keySet()) {
                double monthAverage = temp.get(timePeriod).stream()
                        .filter(p -> p.getJSONObject("Cells").getString("Parameter").equals(param))
                        .collect(Collectors.averagingDouble(p -> p.getJSONObject("Cells").getDouble("MonthlyAveragePDKss")));
                result.put(timePeriod, ((double) Math.round(monthAverage * 100)) / 100);
            }

            globalResult.put(param, result);
        }

        System.out.println(globalResult);
    }
}
