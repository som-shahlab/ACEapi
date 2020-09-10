package com.podalv.search.server.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.gson.JsonSyntaxException;
import com.podalv.search.server.api.AtlasConnection;
import com.podalv.search.server.api.datastructures.PatientData;
import com.podalv.search.server.api.exceptions.QueryException;
import com.podalv.search.server.api.timeintervals.TimeInterval;
import com.podalv.search.server.api.timeintervals.TimeIntervalIcd9;

public class GetPatientDataBeforeAYear {

  public static void main(final String[] args) throws JsonSyntaxException, IOException, QueryException {
    // URL example = http://localhost:8080
    System.out.println("Usage: URL PATIENT_ID");
    final AtlasConnection connection = new AtlasConnection(args[0]);

    final int[] patientIds = new int[] {1, 2, 3, 4};
    final int[] years = new int[] {2005, 2006, 2007, 2008};

    // test whether connection is established
    if (!connection.test()) {
      System.out.println("Cannot establish a connection to ATLAS search engine");
    }
    else {
      // fetch the patient from the database

      for (int x = 0; x < patientIds.length; x++) {
        final PatientData patient = connection.getPatient(patientIds[x]);

        final Iterator<Integer> yearsWithData = patient.getYearsWithData();
        int minYear = -1;
        while (yearsWithData.hasNext()) {
          final int yearWithData = yearsWithData.next();
          if (yearWithData < years[x]) {
            minYear = Math.max(yearWithData, minYear);
          }
        }

        if (minYear != -1) {
          final ArrayList<TimeInterval> timeIntervalsForSelectedMaxYear = patient.getYearTimeIntervals(minYear);
          double minYearOffset = Double.MAX_VALUE;
          for (final TimeInterval t : timeIntervalsForSelectedMaxYear) {
            minYearOffset = Math.min(minYearOffset, t.getStart());
          }

          //display available ICD9 codes
          final Iterator<String> icd9Codes = patient.getUniqueIcd9Codes().iterator();
          while (icd9Codes.hasNext()) {
            final String icd9Code = icd9Codes.next();

            // for each ICD9 code get the list of all its time intervals
            final Iterator<TimeIntervalIcd9> icd9TimeIntervals = patient.getIcd9TimeIntervals(icd9Code).iterator();
            while (icd9TimeIntervals.hasNext()) {
              final TimeIntervalIcd9 ti = icd9TimeIntervals.next();
              if (ti.getStart() < minYearOffset) {

                //output time intervals and whether the ICD9 was a primary diagnosis
                System.out.println("ICD9 = " + icd9Code + " START = " + ti.getStart() + " END = " + ti.getEnd() + " PRIMARY = " + ti.isPrimary());
              }
            }
          }
        }
      }
    }

  }
}
