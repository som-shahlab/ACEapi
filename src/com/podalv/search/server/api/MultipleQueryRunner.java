package com.podalv.search.server.api;

import java.io.*;
import java.util.Iterator;
import java.util.regex.Pattern;

import com.podalv.search.server.api.datastructures.PatientId;

/** Runs a list of queries and stores patient counts in a file
 * 
 */
public class MultipleQueryRunner {

  public static void main(final String[] args) throws IOException {
    System.out.println("Usage: URL variables_file query_file output_file");
    System.out.println();
    System.out.println("Format of the query file:");
    System.out.println("IDENTIFIER\tquery");
    String line;
    final AtlasConnection connection = new AtlasConnection(args[0]);
    if (!connection.test()) {
      System.out.println("Cannot connect to '" + args[0]);
      System.exit(1);
    }
    final StringBuilder variables = new StringBuilder();
    BufferedReader reader = new BufferedReader(new FileReader(args[1]));
    while ((line = reader.readLine()) != null) {
      variables.append(line + "\n");
    }
    reader.close();
    final BufferedWriter result = new BufferedWriter(new FileWriter(args[3]));
    reader = new BufferedReader(new FileReader(args[2]));
    while ((line = reader.readLine()) != null) {
      final String[] data = line.split(Pattern.quote("\t"));
      if (data.length != 2) {
        System.out.println("Ignoring line '" + line + "'");
      }
      else {
        System.out.print("Processing " + data[0]);
        long cnt = 0;
        try {
          final Iterator<PatientId> resp = connection.getPatientIds(variables.toString() + "\n" + data[1]);
          while (resp.hasNext()) {
            resp.next();
            cnt++;
          }
        }
        catch (final Exception e) {
          System.out.print(" ERROR " + e.getMessage());
          result.write(data[0] + "\t" + "error '" + e.getMessage() + "'\n");
        }
        System.out.print(" result = " + cnt);
        result.write(data[0] + "\t" + cnt + "\n");
        System.out.println();
      }
    }
    reader.close();
    result.close();
  }

}
