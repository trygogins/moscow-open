package com.masters.moscowopen.controllers;

import com.google.common.collect.ImmutableMap;
import com.masters.moscowopen.model.SummaryPair;
import com.masters.moscowopen.utils.CommonUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class EnvironmentalDataController {

	@Value("${open.data.mos.api_key}")
	private String apiKey;
	@Value("${com.macbook.local_path}")
	private String localFileAddress;

	@RequestMapping(value = "/get_air_quality_points", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	public String getPoints(@RequestParam("filter_date") String filterDate) throws IOException {
		String jsonString = getJsonString(2453);
		JSONArray jsonData = new JSONArray(jsonString);
		Map<String, List<JSONObject>> result = new HashMap<>();

		for (Object o : jsonData) {
			JSONObject jsonItem = (JSONObject) o;
			String dateStr = jsonItem.getJSONObject("Cells").getString("Period");

			if (isEqual(dateStr, filterDate)) {
				String location = jsonItem.getJSONObject("Cells").getString("Location");
				if (!result.containsKey(location)) {
					result.put(location, new ArrayList<>());
				}

				result.get(location).add(jsonItem);
			}
		}

		return new JSONObject(result).toString();
	}

	@RequestMapping(value = "/get_timeline_data", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	public String getTimeline(@RequestParam("group_by") String groupParameter) throws IOException {
		if (!groupParameter.equals("StationName") && !groupParameter.equals("District")) {
			return new JSONObject(ImmutableMap.of("state", "error", "message", "Illegal Argument!")).toString();
		}

		Map<String, Map<String, List<Map<String, Double>>>> result = getTimelineMap(groupParameter);
		return new JSONObject(result).toString();
	}

	private Map<String, Map<String, List<Map<String, Double>>>> getTimelineMap(String groupParameter) throws IOException {
		String jsonString = getJsonString(2453);
		JSONArray jsonData = new JSONArray(jsonString);
		Map<String, List<JSONObject>> unpreparedResult = new HashMap<>();

		for (Object o : jsonData) {
			JSONObject jsonItem = (JSONObject) o;
			String groupByName = jsonItem.getJSONObject("Cells").getString(groupParameter);

			if (!unpreparedResult.containsKey(groupByName)) {
				unpreparedResult.put(groupByName, new ArrayList<>());
			}

			unpreparedResult.get(groupByName).add(jsonItem);
		}

		for (String groupByName : unpreparedResult.keySet()) {
			unpreparedResult.get(groupByName).sort((o1, o2) -> {
				String date1 = o1.getJSONObject("Cells").getString("Period");
				String date2 = o2.getJSONObject("Cells").getString("Period");

				return CommonUtils.compareDates(date1, date2);
			});
		}

		Map<String, Map<String, List<Map<String, Double>>>> result = new HashMap<>();
		for (String groupByName : unpreparedResult.keySet()) {
			List<JSONObject> jsonObjects = unpreparedResult.get(groupByName);
			Map<String, List<Map<String, Double>>> groupedBySubstance = new HashMap<>();
			for (JSONObject measurement : jsonObjects) {
				if (measurement.getJSONObject("Cells").isNull("MonthlyAveragePDKss")) {
					continue;
				}

				String substanceName = measurement.getJSONObject("Cells").getString("Parameter");
				List<Map<String, Double>> list = groupedBySubstance.get(substanceName);
				if (list == null) {
					groupedBySubstance.put(substanceName, new ArrayList<>());
				}

				groupedBySubstance.get(substanceName).add(ImmutableMap.of(
						measurement.getJSONObject("Cells").getString("Period"),
						measurement.getJSONObject("Cells").getDouble("MonthlyAveragePDKss")));
			}

			Map<String, List<Map<String, Double>>> temp = new HashMap<>();
			for (String substance : groupedBySubstance.keySet()) {
				if (groupedBySubstance.get(substance).size() < 14) {
					continue;
				}

				temp.put(substance, groupedBySubstance.get(substance));
			}

			if (!temp.isEmpty()) {
				result.put(groupByName, temp);
			}
		}
		return result;
	}

	@RequestMapping(value = "/get_summary", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	public String getSummary() throws IOException {
		Map<String, Map<String, List<Map<String, Double>>>> byStationName = getTimelineMap("StationName");
		Map<String, Map<String, List<Map<String, Double>>>> byDistrict = getTimelineMap("District");

		Map<String, Double> resultStationNames = getAverages(byStationName);
		Map<String, Double> resultDistricts = getAverages(byDistrict);

		List<SummaryPair> dResult = new ArrayList<>();
		List<SummaryPair> snResult = new ArrayList<>();
		dResult.addAll(resultDistricts.entrySet()
				.stream()
				.map(districtPollutionPair -> SummaryPair.of(districtPollutionPair.getKey(), districtPollutionPair.getValue()))
				.collect(Collectors.toList()));

		snResult.addAll(resultStationNames.entrySet()
				.stream()
				.map(stationPollutionPair -> SummaryPair.of(stationPollutionPair.getKey(), stationPollutionPair.getValue()))
				.collect(Collectors.toList()));

		Comparator<SummaryPair> comp = (o1, o2) -> o1.getPollution() < o2.getPollution() ? 1 :
				(o1.getPollution() > o2.getPollution() ? -1 : 0);
		dResult.sort(comp);
		snResult.sort(comp);

		return new JSONObject(ImmutableMap.of("districts", dResult,
				"stationNames", snResult)).toString();
	}

	private Map<String, Double> getAverages(Map<String, Map<String, List<Map<String, Double>>>> groupedMeasurements) {
		Map<String, Double> resultDistricts = new HashMap<>();
		for (String groupName : groupedMeasurements.keySet()) {
			int exceeds = 0,
					count = 0;
			for (String substances : groupedMeasurements.get(groupName).keySet()) {
				for (Map<String, Double> measurement : groupedMeasurements.get(groupName).get(substances)) {
					if ((double) measurement.values().toArray()[0] >= 1.0) {
						exceeds ++;
					}

					count ++;
				}
			}

			resultDistricts.put(groupName, (double) Math.round((double) exceeds * 100 / count) / 100);
		}

		return resultDistricts;
	}

	private String getJsonString(int datasetId) throws IOException {
		if (System.getProperty("com.masters.isDebug").equals("true")) {
			return IOUtils.toString(new FileInputStream(localFileAddress));
		}

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		HttpGet httpGet = new HttpGet("https://apidata.mos.ru/v1/datasets/" + datasetId + "/rows?api_key=" + apiKey);
		httpGet.addHeader("Content-Type", "application/json; charset=utf-8");
		HttpResponse response = httpClient.execute(httpGet);
		return IOUtils.toString(response.getEntity().getContent(), "UTF-8");
	}

	private boolean isEqual(String dateStr, String filterDateStr) {
		return CommonUtils.compareDates(dateStr, filterDateStr) == 0;
	}

}