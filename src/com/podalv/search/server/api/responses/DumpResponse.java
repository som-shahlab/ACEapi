package com.podalv.search.server.api.responses;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Complete patient's record
 *
 * @author podalv
 *
 */
public class DumpResponse {

  @JsonProperty("patientId") private final long                    patientId;
  @JsonProperty("recordStart") private int                         recordStart;
  @JsonProperty("recordEnd") private int                           recordEnd;
  @JsonProperty("death") private int                               death;
  @JsonProperty("gender") private String                           gender;
  @JsonProperty("race") private String                             race;
  @JsonProperty("ethnicity") private String                        ethnicity;
  @JsonProperty("icd9") private Map<String, List<String>>          icd9           = null;
  @JsonProperty("icd10") private Map<String, List<String>>         icd10          = null;
  @JsonProperty("cpt") private Map<String, List<Integer>>          cpt            = null;
  @JsonProperty("rx") private Map<String, List<String>>            rx             = null;
  @JsonProperty("snomed") private Map<String, List<Integer>>       snomed         = null;
  @JsonProperty("negatedTerms") private Map<String, List<String>>  negatedTerms   = null;
  @JsonProperty("fhTerms") private Map<String, List<String>>       fhTerms        = null;
  @JsonProperty("positiveTerms") private Map<String, List<String>> positiveTerms  = null;
  @JsonProperty("visitTypes") private Map<String, List<Integer>>   visitTypes     = null;
  @JsonProperty("departments") private Map<String, List<Integer>>  departments    = null;
  @JsonProperty("noteTypes") private Map<String, List<Integer>>    noteTypes      = null;
  @JsonProperty("atc") private Map<String, List<Integer>>          atc            = null;
  @JsonProperty("labs") private Map<String, List<String>>          labs           = null;
  @JsonProperty("labsRaw") private Map<String, List<String>>       labsRaw        = null;
  @JsonProperty("vitals") private Map<String, List<String>>        vitals         = null;
  @JsonProperty("encounterDays") private List<Integer>             encounterDays  = null;
  @JsonProperty("ageRanges") private List<Integer>                 ageRanges      = null;
  @JsonProperty("yearRanges") private Map<Integer, List<Integer>>  yearRanges     = null;
  @JsonProperty("error") private String                            error          = null;
  @JsonProperty("selectionQuery") private String                   selectionQuery = null;
  @JsonProperty("containsStart") private boolean                   containsStart  = false;
  @JsonProperty("containsEnd") private boolean                     containsEnd    = false;

  public static DumpResponse createError(final String error) {
    final DumpResponse result = new DumpResponse(-1);
    result.setError(error);
    return result;
  }

  public DumpResponse(final long patientId) {
    this.patientId = patientId;
  }

  public void setLabsRaw(final Map<String, List<String>> labs) {
    labsRaw = labs;
  }

  public String getSelectionQuery() {
    return selectionQuery;
  }

  public boolean isContainsEnd() {
    return containsEnd;
  }

  public boolean isContainsStart() {
    return containsStart;
  }

  public void setSelectionQuery(final String selectionQuery) {
    this.selectionQuery = selectionQuery;
  }

  public void setContainsEnd(final boolean containsEnd) {
    this.containsEnd = containsEnd;
  }

  public void setContainsStart(final boolean containsStart) {
    this.containsStart = containsStart;
  }

  public Map<String, List<String>> getLabsRaw() {
    return labsRaw;
  }

  public void setDepartments(final Map<String, List<Integer>> departments) {
    this.departments = departments;
  }

  public void setIcd10(final Map<String, List<String>> icd10) {
    this.icd10 = icd10;
  }

  public void setYearRanges(final Map<Integer, List<Integer>> yearRanges) {
    this.yearRanges = yearRanges;
  }

  public Map<Integer, List<Integer>> getYearRanges() {
    return yearRanges;
  }

  public List<Integer> getAgeRanges() {
    return ageRanges;
  }

  public void setDeath(final int death) {
    this.death = death;
  }

  public int getDeath() {
    return death;
  }

  public void setEthnicity(final String ethnicity) {
    this.ethnicity = ethnicity;
  }

  public void setNoteTypes(final Map<String, List<Integer>> noteTypes) {
    this.noteTypes = noteTypes;
  }

  public Map<String, List<Integer>> getNoteTypes() {
    return noteTypes;
  }

  public void setGender(final String gender) {
    this.gender = gender;
  }

  public void setRace(final String race) {
    this.race = race;
  }

  public String getEthnicity() {
    return ethnicity;
  }

  public String getGender() {
    return gender;
  }

  public String getRace() {
    return race;
  }

  public void setLabs(final Map<String, List<String>> labs) {
    this.labs = labs;
  }

  public void setAtc(final Map<String, List<Integer>> atc) {
    this.atc = atc;
  }

  public Map<String, List<Integer>> getAtc() {
    return atc;
  }

  public Map<String, List<String>> getLabs() {
    return labs;
  }

  public void setAgeRanges(final List<Integer> ageRanges) {
    this.ageRanges = ageRanges;
  }

  public void setRecordEnd(final int recordEnd) {
    this.recordEnd = recordEnd;
  }

  public void setVitals(final Map<String, List<String>> vitals) {
    this.vitals = vitals;
  }

  public Map<String, List<String>> getVitals() {
    return vitals;
  }

  public void setEncounterDays(final List<Integer> encounterDays) {
    this.encounterDays = encounterDays;
  }

  public List<Integer> getEncounterDays() {
    return encounterDays;
  }

  public void setFhTerms(final Map<String, List<String>> fhTerms) {
    this.fhTerms = fhTerms;
  }

  public Map<String, List<Integer>> getVisitTypes() {
    return visitTypes;
  }

  public void setVisitTypes(final Map<String, List<Integer>> visitTypes) {
    this.visitTypes = visitTypes;
  }

  public void setNegatedTerms(final Map<String, List<String>> negatedTerms) {
    this.negatedTerms = negatedTerms;
  }

  public void setPositiveTerms(final Map<String, List<String>> positiveTerms) {
    this.positiveTerms = positiveTerms;
  }

  public Map<String, List<String>> getFhTerms() {
    return fhTerms;
  }

  public Map<String, List<Integer>> getDepartments() {
    return departments;
  }

  public Map<String, List<String>> getIcd10() {
    return icd10;
  }

  public Map<String, List<String>> getNegatedTerms() {
    return negatedTerms;
  }

  public Map<String, List<String>> getPositiveTerms() {
    return positiveTerms;
  }

  public void setRecordStart(final int recordStart) {
    this.recordStart = recordStart;
  }

  public int getRecordEnd() {
    return recordEnd;
  }

  public int getRecordStart() {
    return recordStart;
  }

  public void setError(final String error) {
    this.error = error;
  }

  public void setIcd9(final Map<String, List<String>> icd9) {
    this.icd9 = icd9;
  }

  public void setSnomed(final Map<String, List<Integer>> snomed) {
    this.snomed = snomed;
  }

  public Map<String, List<Integer>> getSnomed() {
    return snomed;
  }

  public void setRx(final Map<String, List<String>> rx) {
    this.rx = rx;
  }

  public Map<String, List<String>> getRx() {
    return rx;
  }

  public void setCpt(final Map<String, List<Integer>> cpt) {
    this.cpt = cpt;
  }

  public Map<String, List<String>> getIcd9() {
    return icd9;
  }

  public Map<String, List<Integer>> getCpt() {
    return cpt;
  }

  public long getPatientId() {
    return patientId;
  }

  public String getError() {
    return error;
  }

}
