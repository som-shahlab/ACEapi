package com.podalv.search.server.api.datastructures;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.podalv.search.server.api.exceptions.QueryException;
import com.podalv.search.server.api.iterators.ImmutableIterator;
import com.podalv.search.server.api.responses.DumpResponse;
import com.podalv.search.server.api.timeintervals.*;

/** Contains all the data about the patient in the database. Can be generated from a DumpResponse
 *
 * @author podalv
 *
 */
public class PatientData {

  private final DumpResponse data;

  public static final double minutesToDays(final int age) {
    return (age == Integer.MAX_VALUE) ? Integer.MAX_VALUE : (age < 0) ? 0 : age / (double) (24 * 60);
  }

  /** From a dump file response creates PatientData object
  *
  * @return
  * @throws QueryException if the patient did not exist or the response did not return any data
   * @throws FileNotFoundException
   * @throws JsonIOException
   * @throws JsonSyntaxException
  */
  public static PatientData create(final File jsonDumpResponse) throws QueryException, JsonSyntaxException, JsonIOException, FileNotFoundException {
    final DumpResponse response = new Gson().fromJson(new FileReader(jsonDumpResponse), DumpResponse.class);
    if (response == null || response.getError() != null) {
      throw new QueryException(response == null ? "Patient dump request did not return any data" : "Patient dump request returned error '" + response.getError() + "'");
    }
    return new PatientData(response);
  }

  /** From a dump response creates PatientData object
   *
   * @param response
   * @return
   * @throws QueryException if the patient did not exist or the response did not return any data
   */
  public static PatientData create(final DumpResponse response) throws QueryException {
    if (response == null || response.getError() != null) {
      throw new QueryException(response == null ? "Patient dump request did not return any data" : "Patient dump request returned error '" + response.getError() + "'");
    }
    return new PatientData(response);
  }

  /** Returns time intervals with additional data about icd9 codes for the specified icd9 code
   *
   * @param icd9
   * @return
   */
  public ArrayList<TimeIntervalIcd9> getIcd9TimeIntervals(final String icd9) {
    final ArrayList<TimeIntervalIcd9> result = new ArrayList<>();
    final List<String> list = data.getIcd9().get(icd9);
    if (list != null) {
      for (int x = 0; x < list.size(); x += 3) {
        result.add(new TimeIntervalIcd9(minutesToDays(Integer.parseInt(list.get(x))), minutesToDays(Integer.parseInt(list.get(x + 1))), list.get(x + 2)));
      }
    }
    Collections.sort(result);
    return result;
  }

  /** Returns time intervals with additional data about icd10 codes for the specified icd10 code
   *
   * @param icd10
   * @return
   */
  public ArrayList<TimeIntervalIcd9> getIcd10TimeIntervals(final String icd10) {
    final ArrayList<TimeIntervalIcd9> result = new ArrayList<>();
    final List<String> list = data.getIcd10().get(icd10);
    if (list != null) {
      for (int x = 0; x < list.size(); x += 3) {
        result.add(new TimeIntervalIcd9(minutesToDays(Integer.parseInt(list.get(x))), minutesToDays(Integer.parseInt(list.get(x + 1))), list.get(x + 2)));
      }
    }
    Collections.sort(result);
    return result;
  }

  private HashSet<TimeInterval> addAll(final TimeInterval ... intervals) {
    final HashSet<TimeInterval> input = new HashSet<>();
    for (final TimeInterval t : intervals) {
      input.add(t);
    }
    return input;
  }

  /** Given a list of time intervals returns all ICD9 codes that are found in these intervals
   *
   * @param intervals
   * @return
   */
  public HashMap<String, ArrayList<TimeIntervalIcd9>> getIcd9InIntervals(final TimeInterval ... intervals) {
    final HashSet<TimeInterval> input = addAll(intervals);
    final HashMap<String, ArrayList<TimeIntervalIcd9>> result = new HashMap<>();
    final Iterator<String> i = getUniqueIcd9Codes().iterator();
    while (i.hasNext()) {
      final String icd9 = i.next();
      final Iterator<TimeIntervalIcd9> iterator = getIcd9TimeIntervals(icd9).iterator();
      while (iterator.hasNext()) {
        final TimeIntervalIcd9 it = iterator.next();
        if (input.contains(it)) {
          ArrayList<TimeIntervalIcd9> list = result.get(icd9);
          if (list == null) {
            list = new ArrayList<>();
            result.put(icd9, list);
          }
          list.add(it);
        }
      }
    }

    return result;
  }

  /** Given a list of time intervals returns all CPT codes that are found in these intervals
  *
  * @param intervals
  * @return
  */
  public HashMap<String, ArrayList<TimeInterval>> getCptInIntervals(final TimeInterval ... intervals) {
    final HashSet<TimeInterval> input = addAll(intervals);
    final HashMap<String, ArrayList<TimeInterval>> result = new HashMap<>();
    final Iterator<String> i = getUniqueCptCodes().iterator();
    while (i.hasNext()) {
      final String cpt = i.next();
      final Iterator<TimeInterval> iterator = getCptTimeIntervals(cpt).iterator();
      while (iterator.hasNext()) {
        final TimeInterval it = iterator.next();
        if (input.contains(it)) {
          ArrayList<TimeInterval> list = result.get(cpt);
          if (list == null) {
            list = new ArrayList<>();
            result.put(cpt, list);
          }
          list.add(it);
        }
      }
    }

    return result;
  }

  /** Given a list of time intervals returns all RxNorm codes that are found in these intervals
  *
  * @param intervals
  * @return
  */
  public HashMap<String, ArrayList<TimeInterval>> getRxInIntervals(final TimeInterval ... intervals) {
    final HashSet<TimeInterval> input = addAll(intervals);
    final HashMap<String, ArrayList<TimeInterval>> result = new HashMap<>();
    final Iterator<String> i = getUniqueRxNormCodes().iterator();
    while (i.hasNext()) {
      final String rx = i.next();
      final Iterator<TimeIntervalRxNorm> iterator = getRxNormTimeIntervals(rx).iterator();
      while (iterator.hasNext()) {
        final TimeInterval it = iterator.next();
        if (input.contains(it)) {
          ArrayList<TimeInterval> list = result.get(rx);
          if (list == null) {
            list = new ArrayList<>();
            result.put(rx, list);
          }
          list.add(it);
        }
      }
    }

    return result;
  }

  /** Given a list of time intervals returns all Vitals codes that are found in these intervals
  *
  * @param intervals
  * @return
  */
  public HashMap<String, ArrayList<TimeInterval>> getVitalsInIntervals(final TimeInterval ... intervals) {
    final HashSet<TimeInterval> input = addAll(intervals);
    final HashMap<String, ArrayList<TimeInterval>> result = new HashMap<>();
    final Iterator<String> i = getUniqueVitalsCodes().iterator();
    while (i.hasNext()) {
      final String vitals = i.next();
      final Iterator<TimeIntervalNumericValue> iterator = getVitalsTimeIntervals(vitals).iterator();
      while (iterator.hasNext()) {
        final TimeInterval it = iterator.next();
        if (input.contains(it)) {
          ArrayList<TimeInterval> list = result.get(vitals);
          if (list == null) {
            list = new ArrayList<>();
            result.put(vitals, list);
          }
          list.add(it);
        }
      }
    }

    return result;
  }

  /** Given a list of time intervals returns all Snomed codes that are found in these intervals
  *
  * @param intervals
  * @return
  */
  public HashMap<String, ArrayList<TimeInterval>> getSnomedInIntervals(final TimeInterval ... intervals) {
    final HashSet<TimeInterval> input = addAll(intervals);
    final HashMap<String, ArrayList<TimeInterval>> result = new HashMap<>();
    final Iterator<String> i = getUniqueSnomedCodes().iterator();
    while (i.hasNext()) {
      final String snomed = i.next();
      final Iterator<TimeInterval> iterator = getSnomedTimeIntervals(snomed).iterator();
      while (iterator.hasNext()) {
        final TimeInterval it = iterator.next();
        if (input.contains(it)) {
          ArrayList<TimeInterval> list = result.get(snomed);
          if (list == null) {
            list = new ArrayList<>();
            result.put(snomed, list);
          }
          list.add(it);
        }
      }
    }

    return result;
  }

  /** Given a list of time intervals returns all ATC codes that are found in these intervals
  *
  * @param intervals
  * @return
  */
  public HashMap<String, ArrayList<TimeInterval>> getAtcInIntervals(final TimeInterval ... intervals) {
    final HashSet<TimeInterval> input = addAll(intervals);
    final HashMap<String, ArrayList<TimeInterval>> result = new HashMap<>();
    final Iterator<String> i = getUniqueAtcCodes().iterator();
    while (i.hasNext()) {
      final String atc = i.next();
      final Iterator<Integer> rxNorms = getAtcRxNorms(atc).iterator();
      while (rxNorms.hasNext()) {
        final Integer rxnorm = rxNorms.next();
        final Iterator<TimeIntervalRxNorm> iterator = getRxNormTimeIntervals(String.valueOf(rxnorm)).iterator();
        while (iterator.hasNext()) {
          final TimeInterval it = iterator.next();
          if (input.contains(it)) {
            ArrayList<TimeInterval> list = result.get(atc);
            if (list == null) {
              list = new ArrayList<>();
              result.put(atc, list);
            }
            list.add(it);
          }
        }
      }
    }

    return result;
  }

  /** Given a list of time intervals returns all Labs codes that are found in these intervals
  *
  * @param intervals
  * @return
  */
  public HashMap<String, ArrayList<TimeInterval>> getLabsInIntervals(final TimeInterval ... intervals) {
    final HashSet<TimeInterval> input = addAll(intervals);
    final HashMap<String, ArrayList<TimeInterval>> result = new HashMap<>();
    final Iterator<String> i = getUniqueLabCodes().iterator();
    while (i.hasNext()) {
      final String lab = i.next();
      final Iterator<TimeIntervalLabs> iterator = getLabsComputedTimeIntervals(lab).iterator();
      while (iterator.hasNext()) {
        final TimeInterval it = iterator.next();
        if (input.contains(it)) {
          ArrayList<TimeInterval> list = result.get(lab);
          if (list == null) {
            list = new ArrayList<>();
            result.put(lab, list);
          }
          list.add(it);
        }
      }
      final Iterator<TimeIntervalNumericValue> iterator2 = getLabsNumericTimeIntervals(lab).iterator();
      while (iterator2.hasNext()) {
        final TimeInterval it = iterator2.next();
        if (input.contains(it)) {
          ArrayList<TimeInterval> list = result.get(lab);
          if (list == null) {
            list = new ArrayList<>();
            result.put(lab, list);
          }
          list.add(it);
        }
      }
    }

    return result;
  }

  /** Returns time intervals for all labs that had a computed value ("HIGH", "NORMAL", etc.)
   *  If the lab did not have a computed value, but a numeric value, use the getLabsNumericTimeIntervals method
   *
   * @param lab
   * @return
   */
  public ArrayList<TimeIntervalLabs> getLabsComputedTimeIntervals(final String lab) {
    final ArrayList<TimeIntervalLabs> result = new ArrayList<>();
    final List<String> list = data.getLabs().get(lab);
    if (list != null) {
      for (int x = 0; x < list.size(); x += 2) {
        result.add(new TimeIntervalLabs(minutesToDays(Integer.parseInt(list.get(x))), minutesToDays(Integer.parseInt(list.get(x))), list.get(x + 1)));
      }
    }
    Collections.sort(result);
    return result;
  }

  /** Returns time intervals for all labs that had a numeric value
   *  The same lab could have had also a computed value. Use the getLabsComputedTimeIntervals method in that case
   *
   * @param lab
   * @return
   */
  public ArrayList<TimeIntervalNumericValue> getLabsNumericTimeIntervals(final String lab) {
    final ArrayList<TimeIntervalNumericValue> result = new ArrayList<>();
    final List<String> list = data.getLabsRaw().get(lab);
    if (list != null) {
      for (int x = 0; x < list.size(); x += 2) {
        result.add(new TimeIntervalNumericValue(minutesToDays(Integer.parseInt(list.get(x))), minutesToDays(Integer.parseInt(list.get(x))), Double.parseDouble(list.get(x + 1))));
      }
    }
    Collections.sort(result);
    return result;
  }

  /** Returns all time intervals for which a year was indicated
   *  The time intervals are not contiguous. Returns all time intervals for which the year was known, but does not merge them into continuous intervals
   *
   * @param year
   * @return
   */
  public ArrayList<TimeInterval> getYearTimeIntervals(final int year) {
    final ArrayList<TimeInterval> result = new ArrayList<>();
    final List<Integer> list = data.getYearRanges().get(year);
    if (list != null) {
      for (int x = 0; x < list.size(); x += 2) {
        result.add(new TimeInterval(list.get(x), list.get(x + 1)));
      }
    }
    Collections.sort(result);
    return result;
  }

  /** For vitals name returns all time intervals and the numeric value of the vitals reading
   *
   * @param vitalsName
   * @return
   */
  public ArrayList<TimeIntervalNumericValue> getVitalsTimeIntervals(final String vitalsName) {
    final ArrayList<TimeIntervalNumericValue> result = new ArrayList<>();
    final List<String> list = data.getVitals().get(vitalsName);
    if (list != null) {
      for (int x = 0; x < list.size(); x += 2) {
        result.add(new TimeIntervalNumericValue(minutesToDays(Integer.parseInt(list.get(x))), minutesToDays(Integer.parseInt(list.get(x))), Double.parseDouble(list.get(x + 1))));
      }
    }
    Collections.sort(result);
    return result;
  }

  /** For the specified term returns time point, note id and note type where there was a positive mention of the term
   *
   * @param term
   * @return
   */
  public ArrayList<TimeIntervalTerm> getPositiveTermTimeIntervals(final String term) {
    final ArrayList<TimeIntervalTerm> result = new ArrayList<>();
    final List<String> list = data.getPositiveTerms().get(term);
    if (list != null) {
      for (int x = 0; x < list.size(); x += 3) {
        result.add(new TimeIntervalTerm(minutesToDays(Integer.parseInt(list.get(x))), minutesToDays(Integer.parseInt(list.get(x))), Integer.parseInt(list.get(x + 1)), list.get(x
            + 2)));
      }
    }
    Collections.sort(result);
    return result;
  }

  /** For the specified term returns time point, note id and note type where there was a negated mention of the term
  *
  * @param term
  * @return
  */
  public ArrayList<TimeIntervalTerm> getNegatedTermTimeIntervals(final String term) {
    final ArrayList<TimeIntervalTerm> result = new ArrayList<>();
    final List<String> list = data.getNegatedTerms().get(term);
    if (list != null) {
      for (int x = 0; x < list.size(); x += 3) {
        result.add(new TimeIntervalTerm(minutesToDays(Integer.parseInt(list.get(x))), minutesToDays(Integer.parseInt(list.get(x))), Integer.parseInt(list.get(x + 1)), list.get(x
            + 2)));
      }
    }
    Collections.sort(result);
    return result;
  }

  /** For the specified term returns time point, note id and note type where there was a family history mention of the term
  *
  * @param term
  * @return
  */
  public ArrayList<TimeIntervalTerm> getFamilyHistoryTermTimeIntervals(final String term) {
    final ArrayList<TimeIntervalTerm> result = new ArrayList<>();
    final List<String> list = data.getFhTerms().get(term);
    if (list != null) {
      for (int x = 0; x < list.size(); x += 3) {
        result.add(new TimeIntervalTerm(minutesToDays(Integer.parseInt(list.get(x))), minutesToDays(Integer.parseInt(list.get(x))), Integer.parseInt(list.get(x + 1)), list.get(x
            + 2)));
      }
    }
    Collections.sort(result);
    return result;
  }

  /** For the specified RxNorm returns time interval, drug status and drug route
   *
   * @param rx
   * @return
   */
  public ArrayList<TimeIntervalRxNorm> getRxNormTimeIntervals(final String rx) {
    final ArrayList<TimeIntervalRxNorm> result = new ArrayList<>();
    final List<String> list = data.getRx().get(rx);
    if (list != null) {
      for (int x = 0; x < list.size(); x += 4) {
        result.add(new TimeIntervalRxNorm(minutesToDays(Integer.parseInt(list.get(x))), minutesToDays(Integer.parseInt(list.get(x + 1))), list.get(x + 2), list.get(x + 3)));
      }
    }
    Collections.sort(result);
    return result;
  }

  /** For CPT code returns all time intervals when the CPT was indicated
   *
   * @param cpt
   * @return
   */
  public ArrayList<TimeInterval> getCptTimeIntervals(final String cpt) {
    final ArrayList<TimeInterval> result = new ArrayList<>();
    final List<Integer> list = data.getCpt().get(cpt);
    if (list != null) {
      for (int x = 0; x < list.size(); x += 2) {
        result.add(new TimeInterval(minutesToDays(list.get(x)), minutesToDays(list.get(x + 1))));
      }
    }
    Collections.sort(result);
    return result;
  }

  /** For DEPARTMENT code returns all time intervals when the DEPARTMENT was indicated
   *
   * @param department
   * @return
   */
  public ArrayList<TimeInterval> getDepartmentTimeIntervals(final String department) {
    final ArrayList<TimeInterval> result = new ArrayList<>();
    final List<Integer> list = data.getDepartments().get(department);
    if (list != null) {
      for (int x = 0; x < list.size(); x += 2) {
        result.add(new TimeInterval(minutesToDays(list.get(x)), minutesToDays(list.get(x + 1))));
      }
    }
    Collections.sort(result);
    return result;
  }

  /** Returns a list of RxNorms assigned to the specified ATC code
   *
   * @param atc
   * @return
   */
  public HashSet<Integer> getAtcRxNorms(final String atc) {
    final HashSet<Integer> result = new HashSet<>();
    final List<Integer> list = data.getAtc().get(atc);
    if (list != null) {
      for (int x = 0; x < list.size(); x++) {
        result.add(list.get(x));
      }
    }
    return result;
  }

  /** For the specified snomed code (replacement of ICD9/ICD10 code) returns all time intervals when it was indicated
   *  Snomed codes unlike ICD9 codes do not have PRIMARY diagnosis information (the snomed to icd9 mapping is N->N which makes primary diagnosis flag impossible to
   *  propagate)
   *
   * @param snomed
   * @return
   */
  public ArrayList<TimeInterval> getSnomedTimeIntervals(final String snomed) {
    final ArrayList<TimeInterval> result = new ArrayList<>();
    final List<Integer> list = data.getSnomed().get(snomed);
    if (list != null) {
      for (int x = 0; x < list.size(); x += 2) {
        result.add(new TimeInterval(minutesToDays(list.get(x)), minutesToDays(list.get(x + 1))));
      }
    }
    Collections.sort(result);
    return result;
  }

  /** Returns time intervals when a visit with the specified type occurred
   *
   * @param visitType
   * @return
   */
  public ArrayList<TimeInterval> getVisitTypeTimeIntervals(final String visitType) {
    final ArrayList<TimeInterval> result = new ArrayList<>();
    final List<Integer> list = data.getVisitTypes().get(visitType);
    if (list != null) {
      for (int x = 0; x < list.size(); x += 2) {
        result.add(new TimeInterval(minutesToDays(list.get(x)), minutesToDays(list.get(x + 1))));
      }
    }
    Collections.sort(result);
    return result;
  }

  /** Returns time points when patient had notes with the specified type
   *
   * @param noteType
   * @return
   */
  public ArrayList<TimeInterval> getNoteTypeTimeIntervals(final String noteType) {
    final ArrayList<TimeInterval> result = new ArrayList<>();
    final List<Integer> list = data.getNoteTypes().get(noteType);
    if (list != null) {
      for (int x = 0; x < list.size(); x++) {
        result.add(new TimeInterval(minutesToDays(list.get(x)), minutesToDays(list.get(x))));
      }
    }
    Collections.sort(result);
    return result;
  }

  private PatientData(final DumpResponse response) {
    data = response;
  }

  private Set<String> getUniqueCodes(final Map<String, ?> map) {
    return map != null ? map.keySet() : new HashSet<>();
  }

  public long getPatientId() {
    return data.getPatientId();
  }

  /** Returns offset of the first data point for the patient from the date of birth
   *
   * @return
   */
  public double getRecordStart() {
    return minutesToDays(data.getRecordStart());
  }

  /** Returns offset of the last data point for the patient from the date of birth
   *
   * @return
   */
  public double getRecordEnd() {
    return data.getRecordEnd();
  }

  /**
   *
   * @return true if there is death record for the patient
   */
  public boolean hasDied() {
    return data.getDeath() > 0;
  }

  /** Returns gender string
   *
   * @return
   */
  public String getGender() {
    return data.getGender();
  }

  /** Returns ethnicity string
   *
   * @return
   */
  public String getEthnicity() {
    return data.getEthnicity();
  }

  private ArrayList<TimeInterval> generateTimeIntervals(final List<Integer> array) {
    final ArrayList<TimeInterval> result = new ArrayList<>();
    if (array != null) {
      for (int x = 0; x < array.size(); x += 2) {
        result.add(new TimeInterval(minutesToDays(array.get(x)), minutesToDays(array.get(x + 1))));
      }
    }
    Collections.sort(result);
    return result;
  }

  /** Returns time intervals of 24 hour length during which there was at least one datapoint for the patient
   *
   * @return
   */
  public ArrayList<TimeInterval> getEncounterDays() {
    return generateTimeIntervals(data.getEncounterDays());
  }

  /** Returns all available time points for which the patient had at least one event
   *
   * @return
   */
  public ArrayList<TimeInterval> getAgeRanges() {
    return generateTimeIntervals(data.getAgeRanges());
  }

  /** Returns race string
   *
   * @return
   */
  public String getRace() {
    return data.getRace();
  }

  /** Returns date of death. If the death date is not available, returns -1
   *
   * @return
   */
  public int getDeath() {
    return hasDied() ? data.getDeath() : -1;
  }

  /** Returns a list of unique ATC codes for the patient
   *
   * @return
   */
  public Set<String> getUniqueAtcCodes() {
    return getUniqueCodes(data.getAtc());
  }

  /** Returns years for the specified start/end time
   *
   * @param start
   * @param end
   * @return
   */
  public HashSet<Integer> getYears(final double start, final double end) {
    final HashSet<Integer> result = new HashSet<>();
    final Iterator<Integer> years = getYearsWithData();
    while (years.hasNext()) {
      final int year = years.next();
      final ArrayList<TimeInterval> ti = getYearTimeIntervals(year);
      for (final TimeInterval t : ti) {
        if ((start >= t.getStart() && start <= t.getEnd()) || (start <= t.getStart() && end >= t.getStart())) {
          result.add(year);
        }
      }
    }
    return result;
  }

  /** Returns a list of years for which there is at least 1 data point
   *
   * @return
   */
  public Iterator<Integer> getYearsWithData() {
    return data.getYearRanges() != null ? new ImmutableIterator<>(data.getYearRanges().keySet().iterator()) : new ImmutableIterator<>(null);
  }

  /** Returns a list of unique ICD9 codes for the patient
   *
   * @return
   */
  public Set<String> getUniqueIcd9Codes() {
    return getUniqueCodes(data.getIcd9());
  }

  /** Returns a list of unique ICD9 codes for the patient
   *
   * @return
   */
  public Set<String> getUniqueIcd10Codes() {
    return getUniqueCodes(data.getIcd10());
  }

  /** Returns a list of unique CPT codes for the patient
   *
   * @return
   */
  public Set<String> getUniqueCptCodes() {
    return getUniqueCodes(data.getCpt());
  }

  /** Returns a list of unique RxNorm codes for the patient
   *
   * @return
   */
  public Set<String> getUniqueRxNormCodes() {
    return getUniqueCodes(data.getRx());
  }

  /** Returns a list of unique department codes for the patient
   *
   * @return
   */
  public Set<String> getUniqueDepartmentCodes() {
    return getUniqueCodes(data.getDepartments());
  }

  /** Returns a list of unique SNOMED codes for the patient
   *
   * @return
   */
  public Set<String> getUniqueSnomedCodes() {
    return getUniqueCodes(data.getSnomed());
  }

  /** Returns a list of unique terms for which the patient had at least 1 positive mention
   *
   * @return
   */
  public Set<String> getUniquePositiveTerms() {
    return getUniqueCodes(data.getPositiveTerms());
  }

  /** Returns a list of unique terms for which the patient had at least 1 negated mention
   *
   * @return
   */
  public Set<String> getUniqueNegatedTerms() {
    return getUniqueCodes(data.getNegatedTerms());
  }

  /** Returns a list of unique terms for which the patient had at least 1 family history mention
   *
   * @return
   */
  public Set<String> getUniqueFamilyHistoryTerms() {
    return getUniqueCodes(data.getFhTerms());
  }

  /** Returns a list of unique note types for the patient
   *
   * @return
   */
  public Set<String> getUniqueNoteTypes() {
    return getUniqueCodes(data.getNoteTypes());
  }

  /** Returns a list of unique visit types for the patient
   *
   * @return
   */
  public Set<String> getUniqueVisitTypes() {
    return getUniqueCodes(data.getVisitTypes());
  }

  private Set<String> mergeLabs() {
    final HashSet<String> result = new HashSet<>();
    if (data.getLabs() != null) {
      result.addAll(data.getLabs().keySet());
    }
    if (data.getLabsRaw() != null) {
      result.addAll(data.getLabsRaw().keySet());
    }
    return result;
  }

  /** Returns a list of unique lab codes for which the patient had either a computed value or a numeric value
   *
   * @return
   */
  public Set<String> getUniqueLabCodes() {
    return mergeLabs();
  }

  /** Returns a list of unique vitals codes for the patient
   *
   * @return
   */
  public Set<String> getUniqueVitalsCodes() {
    return getUniqueCodes(data.getVitals());
  }

}
