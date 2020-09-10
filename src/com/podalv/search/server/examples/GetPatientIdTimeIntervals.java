package com.podalv.search.server.examples;

import java.io.IOException;
import java.util.Iterator;

import com.google.gson.JsonSyntaxException;
import com.podalv.search.server.api.AtlasConnection;
import com.podalv.search.server.api.datastructures.PatientId;
import com.podalv.search.server.api.exceptions.QueryException;
import com.podalv.search.server.api.timeintervals.TimeInterval;

/** Using the OUTPUT query get list of patient ids and time intervals when the query was true
 *
 * @author podalv
 *
 */
public class GetPatientIdTimeIntervals {

  public static void main(final String[] args) throws IOException, JsonSyntaxException, QueryException {
    // URL example = http://localhost:8080
    // QUERY example = ICD9=250.50
    System.out.println("Usage: URL QUERY");
    final AtlasConnection connection = new AtlasConnection(args[0]);

    // test whether connection is established
    if (!connection.test()) {
      System.out.println("Cannot establish a connection to ATLAS search engine");
    }
    else {
      // fetch patient ids from server
      final Iterator<PatientId> patientIdIterator = connection.getPatientIds("OUTPUT(" + args[1] + ")");

      //iterate over results
      while (patientIdIterator.hasNext()) {
        final PatientId patient = patientIdIterator.next();
        System.out.println("Patient ID = " + patient.getPatientId());

        //each patientId object contains multiple time intervals for which the query was true
        final Iterator<TimeInterval> timeIntervals = patient.getStartEndIntervals().iterator();
        while (timeIntervals.hasNext()) {
          final TimeInterval ti = timeIntervals.next();
          System.out.println("Start = " + ti.getStart() + " End = " + ti.getEnd());
        }
      }
    }
  }
}
