package com.podalv.search.server.api;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.podalv.search.server.api.exceptions.QueryException;

/** Takes a query and comapres results between 2 search engines
 * 
 */
public class QueryComparator {

  private static void printPatientCnt(final AtlasConnection connection, final String query) {
    try {
      System.out.println(connection + " = " + connection.getPatientCnt(query));
    }
    catch (final Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(final String[] args) throws IOException, QueryException, InterruptedException {
    System.out.println("Usage: QUERY atlasUrl1 atlasUrl2");
    System.out.println();

    final AtlasConnection atlas1 = new AtlasConnection(args[1]);
    final AtlasConnection atlas2 = new AtlasConnection(args[2]);

    assert atlas1.test() && atlas2.test();

    final ExecutorService executor = Executors.newCachedThreadPool();
    executor.submit(() -> printPatientCnt(atlas1, args[0]));
    executor.submit(() -> printPatientCnt(atlas2, args[0]));

    executor.shutdown();

    executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.HOURS);
  }

}
