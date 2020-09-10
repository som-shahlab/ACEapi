package com.podalv.search.server.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

/** Request to dump selected patients
 *
 * @author podalv
 *
 */
public class DumpRequest {

  @JsonProperty("patientId") private final Long  patientId;
  @JsonProperty("icd9") private boolean          icd9;
  @JsonProperty("icd10") private boolean         icd10;
  @JsonProperty("departments") private boolean   departments;
  @JsonProperty("cpt") private boolean           cpt;
  @JsonProperty("rx") private boolean            rx;
  @JsonProperty("snomed") private boolean        snomed;
  @JsonProperty("notes") private boolean         notes;
  @JsonProperty("visitTypes") private boolean    visitTypes;
  @JsonProperty("noteTypes") private boolean     noteTypes;
  @JsonProperty("encounterDays") private boolean encounterDays;
  @JsonProperty("ageRanges") private boolean     ageRanges;
  @JsonProperty("labs") private boolean          labs;
  @JsonProperty("vitals") private boolean        vitals;
  @JsonProperty("atc") private boolean           atc;
  @JsonProperty("selectionQuery") private String selectionQuery;
  @JsonProperty("containsStart") private boolean containsStart;
  @JsonProperty("containsEnd") private boolean   containsEnd;
  @JsonProperty("compress") private boolean      compress;

  public static DumpRequest createWorkshopRequest(final long patientId) {
    final DumpRequest r = new DumpRequest(patientId);
    r.setIcd9(true);
    r.setIcd10(true);
    r.setIcd10(true);
    r.setDepartments(true);
    r.setCpt(true);
    r.setRx(true);
    r.setLabs(true);
    r.setVitals(true);
    r.setNotes(false);
    r.setNoteTypes(true);
    r.setAtc(true);
    return r;
  }

  public static DumpRequest createFull(final long patientId) {
    final DumpRequest req = new DumpRequest(patientId);
    req.setAgeRanges(true);
    req.setAtc(true);
    req.setCpt(true);
    req.setIcd10(true);
    req.setDepartments(true);
    req.setEncounterDays(true);
    req.setIcd9(true);
    req.setLabs(true);
    req.setNotes(true);
    req.setRx(true);
    req.setSnomed(true);
    req.setVisitTypes(true);
    req.setVitals(true);
    req.setNoteTypes(true);
    return req;
  }

  public DumpRequest setQuery(final String query, final boolean start, final boolean end) {
    selectionQuery = query;
    containsStart = start;
    containsEnd = end;
    return this;
  }

  public DumpRequest(final long patientId) {
    this.patientId = patientId;
  }

  public void setAtc(final boolean atc) {
    this.atc = atc;
  }

  public void setVitals(final boolean vitals) {
    this.vitals = vitals;
  }

  public void setLabs(final boolean labs) {
    this.labs = labs;
  }

  public void setNoteTypes(final boolean noteTypes) {
    this.noteTypes = noteTypes;
  }

  public void setAgeRanges(final boolean ageRanges) {
    this.ageRanges = ageRanges;
  }

  public void setSnomed(final boolean snomed) {
    this.snomed = snomed;
  }

  public void setIcd10(final boolean icd10) {
    this.icd10 = icd10;
  }

  public void setDepartments(final boolean departments) {
    this.departments = departments;
  }

  public boolean isIcd10() {
    return icd10;
  }

  public boolean isDepartments() {
    return departments;
  }

  public void setEncounterDays(final boolean encounterDays) {
    this.encounterDays = encounterDays;
  }

  public void setVisitTypes(final boolean visitTypes) {
    this.visitTypes = visitTypes;
  }

  public void setNotes(final boolean notes) {
    this.notes = notes;
  }

  public void setRx(final boolean rx) {
    this.rx = rx;
  }

  public void setCpt(final boolean cpt) {
    this.cpt = cpt;
  }

  public void setIcd9(final boolean icd9) {
    this.icd9 = icd9;
  }

  public Long getPatientId() {
    return patientId;
  }

  public boolean isIcd9() {
    return icd9;
  }

  public boolean isRx() {
    return rx;
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

  public boolean isCompress() {
    return compress;
  }

  public void setCompress(boolean value) {
    this.compress = value;
  }

  public boolean isNotes() {
    return notes;
  }

  public boolean isSnomed() {
    return snomed;
  }

  public boolean isCpt() {
    return cpt;
  }

  public boolean isVisitTypes() {
    return visitTypes;
  }

  public boolean isEncounterDays() {
    return encounterDays;
  }

  public boolean isAgeRanges() {
    return ageRanges;
  }

  public boolean isLabs() {
    return labs;
  }

  public boolean isVitals() {
    return vitals;
  }

  public boolean isNoteTypes() {
    return noteTypes;
  }

  public boolean isAtc() {
    return atc;
  }

  public DumpRequest clone(final long patientId) {
    final DumpRequest req = new DumpRequest(patientId);
    req.setAgeRanges(ageRanges);
    req.setAtc(atc);
    req.setCpt(cpt);
    req.setDepartments(departments);
    req.setEncounterDays(encounterDays);
    req.setIcd10(icd10);
    req.setIcd9(icd9);
    req.setLabs(labs);
    req.setNotes(notes);
    req.setNoteTypes(noteTypes);
    req.setQuery(selectionQuery, containsStart, containsEnd);
    req.setRx(rx);
    req.setSnomed(snomed);
    req.setVisitTypes(visitTypes);
    req.setVitals(vitals);
    return req;
  }

  public static void main(final String[] args) {
    final DumpRequest req = new DumpRequest(5538);
    req.setAgeRanges(true);
    req.setAtc(true);
    req.setIcd9(true);
    req.setRx(true);
    req.setCpt(true);
    req.setEncounterDays(true);
    req.setLabs(true);
    req.setNotes(true);
    req.setNoteTypes(true);
    req.setSnomed(true);
    req.setVisitTypes(true);
    req.setVitals(true);
    System.out.println(new Gson().toJson(req));
  }

}