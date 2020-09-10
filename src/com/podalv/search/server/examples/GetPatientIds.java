package com.podalv.search.server.examples;

import java.io.IOException;
import java.util.Iterator;

import com.google.gson.JsonSyntaxException;
import com.podalv.search.server.api.AtlasConnection;
import com.podalv.search.server.api.datastructures.PatientId;
import com.podalv.search.server.api.exceptions.QueryException;

/** Queries ATLAS and returns a list of PatientIds
 *
 * @author podalv
 *
 */
public class GetPatientIds {

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
      final Iterator<PatientId> patientIdIterator = connection.getPatientIds(args[1]);

      //iterate over results
      while (patientIdIterator.hasNext()) {
        System.out.println(patientIdIterator.next().getPatientId());
      }
    }
  }
}
