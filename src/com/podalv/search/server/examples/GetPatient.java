package com.podalv.search.server.examples;

import java.io.IOException;
import java.util.Iterator;

import com.google.gson.JsonSyntaxException;
import com.podalv.search.server.api.AtlasConnection;
import com.podalv.search.server.api.datastructures.PatientData;
import com.podalv.search.server.api.exceptions.QueryException;
import com.podalv.search.server.api.timeintervals.TimeIntervalIcd9;

/** Downloads a patient denoted by the patient Id and gives access to the PatientData object that contains all the
 *  information about the patient
 *
 * @author podalv
 *
 */
public class GetPatient {

  public static void main(final String[] args) throws IOException, JsonSyntaxException, QueryException {
    // URL example = http://localhost:8080
    // PATIENT_ID = 1
    System.out.println("Usage: URL PATIENT_ID");
    final AtlasConnection connection = new AtlasConnection(args[0]);

    // test whether connection is established
    if (!connection.test()) {
      System.out.println("Cannot establish a connection to ATLAS search engine");
    }
    else {
      // fetch the patient from the database
      final PatientData patient = connection.getPatient(Integer.parseInt(args[1]), "YEAR(2015, 2015)", true, false);

      //display available ICD9 codes
      final Iterator<String> icd9Codes = patient.getUniqueIcd9Codes().iterator();
      while (icd9Codes.hasNext()) {
        final String icd9Code = icd9Codes.next();

        // for each ICD9 code get the list of all its time intervals
        final Iterator<TimeIntervalIcd9> icd9TimeIntervals = patient.getIcd9TimeIntervals(icd9Code).iterator();
        while (icd9TimeIntervals.hasNext()) {
          final TimeIntervalIcd9 ti = icd9TimeIntervals.next();

          //output time intervals and whether the ICD9 was a primary diagnosis
          System.out.println("ICD9 = " + icd9Code + " START = " + ti.getStart() + " END = " + ti.getEnd() + " PRIMARY = " + ti.isPrimary());
        }
      }
    }
  }

}