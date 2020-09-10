package com.podalv.search.server.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DictionaryRequest {

  @JsonProperty("cpt") private String[]     cpt;
  @JsonProperty("icd9") private String[]    icd9;
  @JsonProperty("icd10") private String[]   icd10;
  @JsonProperty("atc") private String[]     atc;
  @JsonProperty("labs") private String[]    labs;
  @JsonProperty("rxNorm") private String[]  rxNorm;
  @JsonProperty("compress") private boolean compress;

  public String[] getAtc() {
    return atc;
  }

  public String[] getCpt() {
    return cpt;
  }

  public String[] getIcd9() {
    return icd9;
  }

  public String[] getIcd10() {
    return icd10;
  }

  public String[] getLabs() {
    return labs;
  }

  public String[] getRxNorm() {
    return rxNorm;
  }

  public boolean isCompress() {
    return compress;
  }

  public void setAtc(final String[] atc) {
    this.atc = atc;
  }

  public void setCpt(final String[] cpt) {
    this.cpt = cpt;
  }

  public void setIcd10(final String[] icd10) {
    this.icd10 = icd10;
  }

  public void setIcd9(final String[] icd9) {
    this.icd9 = icd9;
  }

  public void setLabs(final String[] labs) {
    this.labs = labs;
  }

  public void setRxNorm(final String[] rxNorm) {
    this.rxNorm = rxNorm;
  }

  public void setCompress(boolean compress) {
    this.compress = compress;
  }
}
