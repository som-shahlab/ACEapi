package com.podalv.search.server.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.gson.Gson;
import com.podalv.search.server.api.requests.PatientSearchRequest;
import com.podalv.search.server.api.responses.PatientSearchResponse;

public class PerformanceTest {

  private static final Random random = new Random();

  private static HashMap<String, HashMap<String, Integer>> readStats(final File statsFile) throws IOException {
    final HashMap<String, HashMap<String, Integer>> result = new HashMap<>();
    final BufferedReader reader = new BufferedReader(new FileReader(statsFile));
    String line;
    while ((line = reader.readLine()) != null) {
      if (line.startsWith("ICD9 : ") || line.startsWith("ICD10 : ") || line.startsWith("RX : ") || line.startsWith("CPT : ")) {
        final String type = line.substring(0, line.indexOf(" : "));
        final String code = line.substring(line.indexOf('[') + 1, line.indexOf(']'));
        final String number = line.substring(line.lastIndexOf("=") + 1).trim();
        result.putIfAbsent(type, new HashMap<>());
        final HashMap<String, Integer> map = result.get(type);
        map.put(code, Integer.parseInt(number));
      }
    }
    reader.close();
    return result;
  }

  private static PatientSearchRequest generateRequest(final String command, final int codeCnt, final String ... codes) {
    final PatientSearchRequest request = new PatientSearchRequest();
    final StringBuilder query = new StringBuilder(command + "(");
    for (int x = 0; x < Math.min(codes.length, codeCnt); x++) {
      if (x != 0) {
        query.append(",");
      }
      query.append(codes[x]);
    }
    request.setQuery(query.toString() + "*)");
    request.setReturnSurvivalData(false);
    request.setStatisticsLimit(0);
    return request;
  }

  private static String getRandomCode(final HashMap<String, Integer> map) {
    int rand = random.nextInt(map.size() - 1);
    String lastEntry = null;
    for (final Map.Entry<String, Integer> entry : map.entrySet()) {
      lastEntry = entry.getKey();
      if (rand-- == 0) {
        return entry.getKey();
      }
    }
    return lastEntry;
  }

  private static String[] getRandomFeatures(final HashMap<String, HashMap<String, Integer>> stats) {
    final ArrayList<String> result = new ArrayList<>();
    for (final Map.Entry<String, HashMap<String, Integer>> entry : stats.entrySet()) {
      if (!entry.getKey().equals("RX")) {
        result.add(entry.getKey() + "=\"" + getRandomCode(entry.getValue()) + "\"");
      }
      else {
        result.add(entry.getKey() + "=" + getRandomCode(entry.getValue()) + "");
      }
    }
    return result.toArray(new String[0]);
  }

  public static void main(final String[] args) throws IOException {
    final HashMap<String, HashMap<String, Integer>> stats = readStats(new File("/home/podalv/stats.txt"));

    long totalCnt = 0;
    int cnt = 0;
    long minTime = Long.MAX_VALUE;
    long maxTime = 0;
    //100 UNION of 4 random fatures each a different type MIN=5ms, MAX=306MS, AVG=17.99MS
    //100 SEQUENCES of 2 random fatures each a different type MIN=5ms, MAX=214MS, AVG=15.7MS
    //100 INTERSECT of 2 random features each of different type MIN=5ms, MAX=314ms, AVG=24.42MS
    //100 OR of 4 random features each of different type MIN=5ms, MAX=211ms, AVG=14.5MS
    //100 AND of 2 random features each of different type MIN=5ms, MAX=681, AVG=25.9MS
    for (int x = 0; x < 1000; x++) {
      final PatientSearchRequest request = generateRequest("SEQUENCE", 2, getRandomFeatures(stats));
      final PatientSearchResponse response = new Gson().fromJson(QueryUtils.query("http://localhost:8080/query", new Gson().toJson(request), 100000), PatientSearchResponse.class);
      if (response.getCohortPatientCnt() != 0) {
        System.out.println(request.getQuery());
        System.out.println(response.containsErrors());
        totalCnt += response.getTimeTook();
        maxTime = Math.max(maxTime, response.getTimeTook());
        minTime = Math.min(minTime, response.getTimeTook());
        System.out.println(response.getTimeTook());
        cnt++;
      }
    }

    System.out.println("MIN/MAX/AVG");
    System.out.println(minTime + " / " + maxTime + " / " + (totalCnt / (double) cnt));

  }

}