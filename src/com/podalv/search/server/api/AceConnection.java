package com.podalv.search.server.api;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Predicate;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.podalv.search.server.api.datastructures.PatientData;
import com.podalv.search.server.api.datastructures.PatientId;
import com.podalv.search.server.api.exceptions.QueryException;
import com.podalv.search.server.api.requests.ContainsPatientRequest;
import com.podalv.search.server.api.requests.DictionaryRequest;
import com.podalv.search.server.api.requests.DumpRequest;
import com.podalv.search.server.api.requests.PatientSearchRequest;
import com.podalv.search.server.api.responses.*;
import com.podalv.search.server.api.timeintervals.TimeInterval;

/** Stores connection details to ACE server
 *
 * @author podalv
 *
 */
public class AceConnection {

  public static final String STATUS_QUERY           = "status";
  public static final String PID_QUERY              = "pids";
  public static final String DUMP_QUERY             = "dump";
  public static final String PATIENT_LIST           = "patient_list";
  public static final String DICTIONARY_QUERY       = "dictionary";
  public static final String SEARCH_QUERY           = "query";
  public static final String CONTAINS_PATIENT_QUERY = "contains_patient";
  private final String       url;

  public AceConnection(final String url) {
    this.url = url;
  }

  /** Tests the connection to the server
   *
   * @return true if connection can be established
   * @throws IOException if connection is not successful
   */
  public boolean test() throws IOException {
    final String result = QueryUtils.query(url + "/" + STATUS_QUERY, "", 0);
    final ServerStatusResponse response = new Gson().fromJson(result, ServerStatusResponse.class);
    return response.isOk();
  }

  /** Returns an iterator of patient ids from a query
   *
   * @param query
   * @return Iterator<PatientId>
   * @throws IOException
   * @throws JsonSyntaxException
   */
  public Iterator<PatientId> getPatientIds(final String query) throws JsonSyntaxException, IOException, QueryException {
    final PatientSearchRequest request = new PatientSearchRequest();
    request.setQuery(query);
    request.setPidCntLimit(Integer.MAX_VALUE);
    request.setReturnPids(true);
    request.setReturnSurvivalData(false);
    request.setReturnTimeIntervals(false);
    request.setStatisticsLimit(0);
    final PatientSearchResponse response = new Gson().fromJson(QueryUtils.query(url + "/" + SEARCH_QUERY, new Gson().toJson(request), 0), PatientSearchResponse.class);
    if (response.containsErrors()) {
      throw new QueryException(response.getErrorMessage());
    }
    final Iterator<double[]> iterator = response.getPatientIds().iterator();
    final HashMap<Integer, PatientId> map = new HashMap<>();
    while (iterator.hasNext()) {
      final double[] value = iterator.next();
      if (value.length != 1 && value.length != 3) {
        throw new QueryException("Malformed query response. PatientId response has " + value.length + " columns. Only either 1 or 3 are allowed");
      }
      final int id = (int) value[0];
      PatientId patientId = map.get(id);
      if (patientId == null) {
        patientId = new PatientId(id);
        map.put(id, patientId);
      }
      if (value.length == 3) {
        patientId.addStartEndInterval(new TimeInterval(value[1], value[2]));
      }
    }

    return map.values().iterator();
  }

  /** Returns number of patients for the query
  *
  * @param query
  * @return Iterator<PatientId>
  * @throws IOException
  * @throws JsonSyntaxException
  */
  public int getPatientCnt(final String query) throws JsonSyntaxException, IOException, QueryException {
    final PatientSearchRequest request = new PatientSearchRequest();
    request.setQuery(query);
    request.setPidCntLimit(Integer.MAX_VALUE);
    request.setReturnPids(true);
    request.setReturnSurvivalData(false);
    request.setReturnTimeIntervals(false);
    request.setStatisticsLimit(0);
    final String list = QueryUtils.query(url + "/" + PID_QUERY, new Gson().toJson(request), 0);
    final String[] data = list.split("\n");
    int cnt = 0;
    for (int x = 0; x < data.length; x++) {
      try {
        Long.parseLong(data[x]);
        cnt++;
      }
      catch (final Exception e) {
        //
      }
    }

    return cnt;
  }

  /** Parses query and returns normalized parsed representation of the query
  *
  * @param query
  * @return Iterator<PatientId>
  * @throws IOException
  * @throws JsonSyntaxException
  */
  public String getParsedQuery(final String query) throws JsonSyntaxException, IOException, QueryException {
    final PatientSearchRequest request = new PatientSearchRequest();
    request.setQuery(query);
    request.setPidCntLimit(Integer.MAX_VALUE);
    request.setReturnPids(true);
    request.setReturnSurvivalData(false);
    request.setReturnTimeIntervals(false);
    request.setStatisticsLimit(0);
    final PatientSearchResponse response = new Gson().fromJson(QueryUtils.query(url + "/" + SEARCH_QUERY, new Gson().toJson(request), 0), PatientSearchResponse.class);
    if (response.containsErrors()) {
      throw new QueryException(response.getErrorMessage());
    }
    return response.getParsedQuery();
  }

  /** Downloads a file that is part of the search response for queries CSV and EVENTFLOW
  *
  * @param query
  * @param outputFile
  * @throws IOException
  * @throws JsonSyntaxException
  * @throws QueryException if query does not contain downloadable file
  */
  public void getFile(final String query, final File outputFile) throws JsonSyntaxException, IOException, QueryException {
    final PatientSearchRequest request = new PatientSearchRequest();
    request.setQuery(query);
    request.setPidCntLimit(Integer.MAX_VALUE);
    request.setReturnPids(true);
    request.setReturnSurvivalData(false);
    request.setReturnTimeIntervals(false);
    request.setStatisticsLimit(0);
    final PatientSearchResponse response = new Gson().fromJson(QueryUtils.query(url + "/" + SEARCH_QUERY, new Gson().toJson(request), 0), PatientSearchResponse.class);
    if (response.containsErrors()) {
      throw new QueryException(response.getErrorMessage());
    }
    if (response.getExportLocation() == null) {
      throw new QueryException("Query does not contain downloadable file");
    }
    final String exportLocation = url + "/" + response.getExportLocation();
    QueryUtils.saveUrlToFile(exportLocation, outputFile);
  }

  /** Returns a patient object containing all the information
   *
   * @param patientId
   * @return
   * @throws JsonSyntaxException
   * @throws IOException
   * @throws QueryException
   */
  public PatientData getPatient(final long patientId) throws JsonSyntaxException, IOException, QueryException {
    final DumpRequest request = DumpRequest.createFull(patientId);
    final DumpResponse response = new Gson().fromJson(QueryUtils.query(url + "/" + DUMP_QUERY, new Gson().toJson(request), 0), DumpResponse.class);
    return PatientData.create(response);
  }

  /** Returns a patient object containing all the information
  *
  * @param patientId
  * @return
  * @throws JsonSyntaxException
  * @throws IOException
  * @throws QueryException
  */
  public DumpResponse getPatientDumpResponse(final long patientId) throws JsonSyntaxException, IOException, QueryException {
    final DumpRequest request = DumpRequest.createFull(patientId);
    return new Gson().fromJson(QueryUtils.query(url + "/" + DUMP_QUERY, new Gson().toJson(request), 0), DumpResponse.class);
  }

  /** Stores DumpResponse to a Stream
  *
  * @param patientId
  * @return
  * @throws JsonSyntaxException
  * @throws IOException
  * @throws QueryException
  */
  public void getPatient(final long patientId, final BufferedWriter stream) throws JsonSyntaxException, IOException, QueryException {
    final DumpRequest request = DumpRequest.createFull(patientId);
    stream.write(QueryUtils.query(url + "/" + DUMP_QUERY, new Gson().toJson(request), 0));
  }

  /** Returns a patient object containing all the information
  *
  * @param patientId
  * @param selectionQuery query to select a portion of patient's timeline to output (example: YEAR(2012, 2015))
  * @param containsStart time intervals of the selectionQuery must contain the START of each event in order for it to be dumped
  * @param containsEnd time intervals of the selectionQuery must contain the END of each event in order for it to be dumped
  * @return
  * @throws JsonSyntaxException
  * @throws IOException
  * @throws QueryException
  */
  public PatientData getPatient(final long patientId, final String selectionQuery, final boolean containsStart, final boolean containsEnd) throws JsonSyntaxException, IOException,
      QueryException {
    final DumpRequest request = DumpRequest.createFull(patientId);
    request.setQuery(selectionQuery, containsStart, containsEnd);
    final DumpResponse response = new Gson().fromJson(QueryUtils.query(url + "/" + DUMP_QUERY, new Gson().toJson(request), 0), DumpResponse.class);
    return PatientData.create(response);
  }

  /** Returns names of RxNorm codes
   *
   * @param rxNormCodes
   * @return
   * @throws IOException
   * @throws JsonSyntaxException
   */
  public HashMap<String, String> getRxNormCodeNames(final String[] rxNormCodes) throws JsonSyntaxException, IOException {
    final DictionaryRequest request = new DictionaryRequest();
    request.setRxNorm(rxNormCodes);
    final DictionaryResponse response = new Gson().fromJson(QueryUtils.query(url + "/" + DICTIONARY_QUERY, new Gson().toJson(request), 0), DictionaryResponse.class);
    return response.getRxNorm();
  }

  /** Returns names of ICD9 codes
  *
  * @param icd9Codes
  * @return
  * @throws IOException
  * @throws JsonSyntaxException
  */
  public HashMap<String, String> getIcd9CodeNames(final String[] icd9Codes) throws JsonSyntaxException, IOException {
    final DictionaryRequest request = new DictionaryRequest();
    request.setIcd9(icd9Codes);
    final DictionaryResponse response = new Gson().fromJson(QueryUtils.query(url + "/" + DICTIONARY_QUERY, new Gson().toJson(request), 0), DictionaryResponse.class);
    return response.getIcd9();
  }

  /** Returns names of CPT codes
  *
  * @param cptCodes
  * @return
  * @throws IOException
  * @throws JsonSyntaxException
  */
  public HashMap<String, String> getCptCodeNames(final String[] cptCodes) throws JsonSyntaxException, IOException {
    final DictionaryRequest request = new DictionaryRequest();
    request.setCpt(cptCodes);
    final DictionaryResponse response = new Gson().fromJson(QueryUtils.query(url + "/" + DICTIONARY_QUERY, new Gson().toJson(request), 0), DictionaryResponse.class);
    return response.getCpt();
  }

  /** Returns names of ATC codes
  *
  * @param atcCodes
  * @return
  * @throws IOException
  * @throws JsonSyntaxException
  */
  public HashMap<String, String> getAtcCodeNames(final String[] atcCodes) throws JsonSyntaxException, IOException {
    final DictionaryRequest request = new DictionaryRequest();
    request.setAtc(atcCodes);
    final DictionaryResponse response = new Gson().fromJson(QueryUtils.query(url + "/" + DICTIONARY_QUERY, new Gson().toJson(request), 0), DictionaryResponse.class);
    return response.getAtc();
  }

  /** Returns names of labs codes
  *
  * @param labsCodes
  * @return
  * @throws IOException
  * @throws JsonSyntaxException
  */
  public HashMap<String, String> getLabsCodeNames(final String[] labsCodes) throws JsonSyntaxException, IOException {
    final DictionaryRequest request = new DictionaryRequest();
    request.setLabs(labsCodes);
    final DictionaryResponse response = new Gson().fromJson(QueryUtils.query(url + "/" + DICTIONARY_QUERY, new Gson().toJson(request), 0), DictionaryResponse.class);
    return response.getLabs();
  }

  /** Returns names of vitals codes
  *
  * @param vitalsCodes
  * @return
  * @throws IOException
  * @throws JsonSyntaxException
  */
  public HashMap<String, String> getVitalsCodeNames(final String[] vitalsCodes) throws JsonSyntaxException, IOException {
    final DictionaryRequest request = new DictionaryRequest();
    request.setLabs(vitalsCodes);
    final DictionaryResponse response = new Gson().fromJson(QueryUtils.query(url + "/" + DICTIONARY_QUERY, new Gson().toJson(request), 0), DictionaryResponse.class);
    return response.getVitals();
  }

  /** Returns a patient object containing all the information
  *
  * @return
  * @throws JsonSyntaxException
  * @throws IOException
  * @throws QueryException
  */
  public PatientData getPatient(final DumpRequest request) throws JsonSyntaxException, IOException, QueryException {
    final DumpResponse response = new Gson().fromJson(QueryUtils.query(url + "/" + DUMP_QUERY, new Gson().toJson(request), 0), DumpResponse.class);
    return PatientData.create(response);
  }

  /** 
   * 
   * @return list of all patients in the dataset
   */
  public LinkedList<Long> getPatientIds(final int maxPatientCnt) throws IOException {
    final LinkedList<Long> result = new LinkedList<>();

    QueryUtils.query(url + "/" + PATIENT_LIST, "", 0, new Predicate<String>() {

      @Override
      public boolean test(final String s) {
        result.add(Long.parseLong(s));
        return result.size() < maxPatientCnt;
      }

    });
    return result;
  }

  /** Returns true if patient with this patient_id exists
   *
   * @param patientId
   * @return
   * @throws JsonSyntaxException
   * @throws IOException
   */
  public boolean containsPatient(final long patientId) throws JsonSyntaxException, IOException {
    final BooleanResponse response = new Gson().fromJson(QueryUtils.query(url + "/" + CONTAINS_PATIENT_QUERY, new Gson().toJson(new ContainsPatientRequest(patientId)), 0),
        BooleanResponse.class);
    return response.getResponse();
  }

  @Override
  public String toString() {
    return url;
  }
}
