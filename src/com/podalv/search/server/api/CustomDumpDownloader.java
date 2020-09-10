package com.podalv.search.server.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.podalv.search.server.api.requests.CustomDumpRequest;
import com.podalv.search.server.api.responses.BooleanResponse;

/** Queries ATLAS /custom_dump endpoint and generates files based on specs
 * 
 */
public class CustomDumpDownloader {

  private static class Definition {

    private final ArrayList<String> columns  = new ArrayList<>();
    private final ArrayList<String> contents = new ArrayList<>();
    private final String            fileName;

    public Definition(final String fileName) {
      this.fileName = fileName;
    }

    public void add(final String column, final String content) {
      columns.add(column);
      contents.add(content);
    }

    public ArrayList<String> getColumns() {
      return columns;
    }

    public ArrayList<String> getContents() {
      return contents;
    }
  }

  public static void main(final String[] args) throws IOException {
    System.out.println("USAGE: url pids.tsv header_definition_file output_folder");
    System.out.println();
    System.out.println("url                    = ATLAS URL");
    System.out.println("pids.tsv               = file containing pids to download (one pid per line). Headers, etc. are skipped");
    System.out.println("header_definition_file = file format:");
    System.out.println("                         filename \t header_col1=content1 \t header_col2=content2 ...");
    System.out.println();
    System.out.println("example: icd9.txt \t pid=PID \t icd9=ICD9 \t time=START");
    final String url = args[0];
    final long[] pids = readPids(args[1]);
    final ArrayList<Definition> definitions = getDefinitions(args[2]);
    final File outputFolder = new File(args[3]);

    long queryTime = 0;
    long downloadTime = 0;
    for (int x = 0; x < definitions.size(); x++) {
      final CustomDumpRequest request = new CustomDumpRequest();
      request.setPatientIds(pids);
      request.setColumns(definitions.get(x).getContents().toArray(new String[0]));
      request.setHeader(definitions.get(x).getColumns().toArray(new String[0]));
      System.out.println("Querying " + pids.length + " patients");
      long time = System.currentTimeMillis();
      final BooleanResponse.CustomDumpResponse response = new Gson().fromJson(QueryUtils.query(url + "/custom_dump", new Gson().toJson(request), Integer.MAX_VALUE),
          BooleanResponse.CustomDumpResponse.class);
      queryTime += (System.currentTimeMillis() - time);
      System.out.println("Finished. Errors = " + ((response.getError() == null || response.getResult().equalsIgnoreCase("null")) ? "NO" : response.getError()));
      System.out.println("Downloading to file " + definitions.get(x).fileName);
      time = System.currentTimeMillis();
      QueryUtils.saveUrlToFile(url + "/" + response.getResult(), new File(outputFolder, definitions.get(x).fileName));
      downloadTime += (System.currentTimeMillis() - time);
      System.out.println("DONE\n");
    }
    System.out.println("Query took    " + (queryTime / 1000) + " sec");
    System.out.println("Download took " + (downloadTime / 1000) + " sec");

  }

  private static ArrayList<Definition> getDefinitions(final String file) throws IOException {
    final ArrayList<Definition> result = new ArrayList<>();
    final BufferedReader reader = new BufferedReader(new FileReader(file));
    String line;
    while ((line = reader.readLine()) != null) {
      final String[] data = line.split(Pattern.quote("\t"));
      if (data.length > 1) {
        final Definition def = new Definition(data[0]);
        for (int x = 1; x < data.length; x++) {
          if (!data[x].trim().isEmpty()) {
            final String[] val = data[x].split(Pattern.quote("="));
            if (val.length != 2) {
              System.out.println("Error parsing line '" + line + "'");
              System.exit(0);
            }
            def.add(val[0], val[1]);
          }
        }
        result.add(def);
      }
      else {
        System.out.println("Skipping line '" + line + "'");
      }
    }
    reader.close();
    return result;
  }

  private static long[] readPids(final String file) throws IOException {
    final BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
    String line;
    final ArrayList<Long> result = new ArrayList();
    while ((line = reader.readLine()) != null) {
      try {
        result.add(Long.parseLong(line));
      }
      catch (final Exception e) {
        System.out.println("Ignoring line '" + line + "'");
      }
    }
    reader.close();
    final long[] res = new long[result.size()];
    for (int x = 0; x < result.size(); x++) {
      res[x] = result.get(x);
    }
    return res;
  }
}
