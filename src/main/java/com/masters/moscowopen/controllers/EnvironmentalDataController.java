package com.masters.moscowopen.controllers;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class EnvironmentalDataController {

	private static final Logger logger = LoggerFactory.getLogger(EnvironmentalDataController.class);

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

		String jsonString = getJsonString(2453);
		JSONArray jsonData = new JSONArray(jsonString);
		Map<String, List<JSONObject>> result = new HashMap<>();

		for (Object o : jsonData) {
			JSONObject jsonItem = (JSONObject) o;
			String groupByName = jsonItem.getJSONObject("Cells").getString(groupParameter);

			if (!result.containsKey(groupByName)) {
				result.put(groupByName, new ArrayList<>());
			}

			result.get(groupByName).add(jsonItem);
		}

		for (String groupByName : result.keySet()) {
			result.get(groupByName).sort((o1, o2) -> {
				String date1 = o1.getJSONObject("Cells").getString("Period");
				String date2 = o2.getJSONObject("Cells").getString("Period");

				return compareDates(date1, date2);
			});
		}

		return new JSONObject(result).toString();
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
		return compareDates(dateStr, filterDateStr) == 0;
	}

	private int compareDates(String date1, String date2) {
		DateTime given = DateTimeFormat.forPattern("MM.YYYY").parseDateTime(date1);
		DateTime filterDate = DateTimeFormat.forPattern("MM.YYYY").parseDateTime(date2);

		return given.compareTo(filterDate);
	}
}