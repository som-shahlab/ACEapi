package com.podalv.search.server.api;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.podalv.search.server.api.datastructures.PatientData;
import com.podalv.search.server.api.datastructures.PatientId;
import com.podalv.search.server.api.exceptions.QueryException;
import com.podalv.search.server.api.timeintervals.TimeInterval;
import com.podalv.search.server.api.timeintervals.TimeIntervalIcd9;

public class CovidFeatures {

  public static void main(final String[] args) throws IOException, QueryException {
    final AtlasConnection atlas = new AtlasConnection("http://shahlab-dev11.stanford.edu:8080");
    System.out.println(atlas.test());
    final BufferedWriter out = new BufferedWriter(new FileWriter("/home/podalv/negated.txt"));
    final HashSet<Long> pat = new HashSet<>();
    final HashMap<String, HashSet<Long>> matrix = new HashMap<>();
    /*final Iterator<PatientId> patients = atlas.getPatientIds(
        "OUTPUT(FIRST MENTION(UNION(labs(\"94309-2 []\", \"DETECTED\"), LABS(\"94309-2 []\", \"POS\"), labs(\"94500-6 []\", \"DETECTED\"), labs(\"94534-5 []\", \"DETECTED\"))))");*/
    final String negated = "OUTPUT(UNION(labs(\"94309-2 []\", \"NOT DETECTED\"), labs(\"94309-2 []\", \"NEGATIVE\"), labs(\"94309-2 []\", \"NEG\"), labs(\"94500-6 []\", \"NOT DETECTED\"), labs(\"94500-6 []\", \"(NORMAL REFERENCE: NOT DETECTED)\"), labs(\"94500-6 []\", \"SARS-COV-2: NEGATIVE, 2019 NOVEL CORONAVIRUS NOT DETECTED.\"), labs(\"94500-6 []\", \"NEGATIVE\"), labs(\"94500-6 []\", \"NOTDETECTED\"), labs(\"94534-5 []\", \"NOT DETECTED\"), labs(\"94306-8 []\", \"NONE DETECTED.\"), labs(\"94316-7 []\", \"NOT-DETECTED\")))";
    final Iterator<PatientId> patients = atlas.getPatientIds(negated);
    System.out.println("Query done");
    while (patients.hasNext()) {
      if (pat.size() > 10000) {
        break;
      }
      final PatientId id = patients.next();
      System.out.println(id.getPatientId());
      final PatientData data = atlas.getPatient(id.getPatientId());
      final Set<String> codes = data.getUniqueIcd10Codes();
      if (codes != null) {
        for (final String code : codes) {
          final ArrayList<TimeIntervalIcd9> ti = data.getIcd10TimeIntervals(code);
          for (final TimeIntervalIcd9 interval : ti) {
            if (Math.abs(interval.getStart() - id.getStartEndIntervals().getFirst().getStart()) <= 3) {
              matrix.putIfAbsent("I" + code, new HashSet<>());
              matrix.get("I" + code).add(id.getPatientId());
              pat.add(id.getPatientId());
            }
          }
        }
      }
      final Set<String> cpt = data.getUniqueCptCodes();
      if (codes != null) {
        for (final String code : cpt) {
          final ArrayList<TimeInterval> ti = data.getCptTimeIntervals(code);
          for (final TimeInterval interval : ti) {
            if (Math.abs(interval.getStart() - id.getStartEndIntervals().getFirst().getStart()) <= 3) {
              matrix.putIfAbsent("C" + code, new HashSet<>());
              matrix.get("C" + code).add(id.getPatientId());
              pat.add(id.getPatientId());
            }
          }
        }
      }
      final Set<String> visit = data.getUniqueVisitTypes();
      if (codes != null) {
        for (final String code : visit) {
          final ArrayList<TimeInterval> ti = data.getVisitTypeTimeIntervals(code);
          for (final TimeInterval interval : ti) {
            if (Math.abs(interval.getStart() - id.getStartEndIntervals().getFirst().getStart()) <= 3) {
              matrix.putIfAbsent("V" + code, new HashSet<>());
              matrix.get("V" + code).add(id.getPatientId());
              pat.add(id.getPatientId());
            }
          }
        }
      }
      final Set<String> dept = data.getUniqueDepartmentCodes();
      if (codes != null) {
        for (final String code : dept) {
          final ArrayList<TimeInterval> ti = data.getDepartmentTimeIntervals(code);
          for (final TimeInterval interval : ti) {
            if (Math.abs(interval.getStart() - id.getStartEndIntervals().getFirst().getStart()) <= 3) {
              matrix.putIfAbsent("D" + code, new HashSet<>());
              matrix.get("D" + code).add(id.getPatientId());
              pat.add(id.getPatientId());
            }
          }
        }
      }
    }
    final ArrayList<String> uniqueCodes = new ArrayList<>();
    out.write("ID");
    for (final String code : matrix.keySet()) {
      uniqueCodes.add(code);
      out.write("\t" + code);
    }
    out.write("\n");
    for (final Long pid : pat) {
      out.write(pid.toString());
      for (final String code : uniqueCodes) {
        out.write("\t" + (matrix.get(code).contains(pid) ? "1" : "0"));
      }
      out.write("\n");
    }

    for (final Map.Entry<String, HashSet<Long>> entry : matrix.entrySet()) {
      System.out.println(entry.getKey() + "\t" + entry.getValue().size());
    }
    out.close();
  }

}
