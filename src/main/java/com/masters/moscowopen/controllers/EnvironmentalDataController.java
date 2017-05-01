package com.masters.moscowopen.controllers;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class EnvironmentalDataController {

	private static final Logger logger = LoggerFactory.getLogger(EnvironmentalDataController.class);

	@Value("${open.data.mos.api_key}")
	private String apiKey;

	@RequestMapping(value = "/get_air_quality_points", method = RequestMethod.GET)
	public String getPoints(@RequestParam("filter_date") String filterDate) throws IOException {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		HttpGet httpGet = new HttpGet("https://apidata.mos.ru/v1/datasets/2453/rows?api_key=" + apiKey);
		HttpResponse response = httpClient.execute(httpGet);
		String jsonString = IOUtils.toString(response.getEntity().getContent());

		JSONArray jsonData = new JSONArray(jsonString);
		List<JSONObject> result = new ArrayList<>();

		for (Object measurement : jsonData) {
			JSONObject jsonItem = (JSONObject) measurement;
			String dateStr = jsonItem.getJSONObject("Cells").getString("Period");

			if (in(dateStr, filterDate)) {
				result.add(jsonItem);
			}
		}

		return new JSONArray(result).toString();
	}

	private boolean in(String dateStr, String filterDateStr) {
		DateTime given = DateTimeFormat.forPattern("MM.YYYY").parseDateTime(dateStr);
		DateTime filterDate = DateTimeFormat.forPattern("MM.YYYY").parseDateTime(filterDateStr);

		return given.equals(filterDate);
	}
}