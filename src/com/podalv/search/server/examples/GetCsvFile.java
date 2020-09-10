package com.podalv.search.server.examples;

import java.io.File;
import java.io.IOException;

import com.google.gson.JsonSyntaxException;
import com.podalv.search.server.api.AtlasConnection;
import com.podalv.search.server.api.exceptions.QueryException;

/** Download a CSV file
 *
 * @author podalv
 *
 */
public class GetCsvFile {

  public static void main(final String[] args) throws IOException, JsonSyntaxException, QueryException {
    // URL example = http://localhost:8080
    // CSV_QUERY = 'CSV(ICD9=250.50, CPT, LABS, ICD9)'
    //Notice that the commas in the CSV_QUERY will cause a problem unless the query is within apostrophes
    System.out.println("Usage: URL CSV_QUERY OUTPUT_FILE");
    final AtlasConnection connection = new AtlasConnection(args[0]);

    // test whether connection is established
    if (!connection.test()) {
      System.out.println("Cannot establish a connection to ATLAS search engine");
    }
    else {
      connection.getFile(args[1], new File(args[2]));
    }
  }

}
