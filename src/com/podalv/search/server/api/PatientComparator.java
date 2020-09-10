package com.podalv.search.server.api;

import java.io.IOException;
import java.util.LinkedList;

import com.podalv.search.server.api.exceptions.QueryException;
import com.podalv.search.server.api.responses.DumpResponse;

/** Utility that allows comparison of individual patients between two search engines
 *  Useful to validate that the extraction generated same patients from the same immutable dataset
 * 
 */
public class PatientComparator {

  public static void main(final String[] args) throws IOException, QueryException {
    System.out.println("Usage: PATIENT_CNT atlasUrl1 atlasUrl2");
    System.out.println();

    final AtlasConnection atlas1 = new AtlasConnection(args[1]);
    final AtlasConnection atlas2 = new AtlasConnection(args[2]);

    assert atlas1.test() && atlas2.test();

    final int maxPatientCnt = Integer.parseInt(args[0]);

    final LinkedList<Long> pids = atlas1.getPatientIds(maxPatientCnt);

    for (final Long pid : pids) {
      if (atlas1.containsPatient(pid) && atlas2.containsPatient(pid)) {
        final DumpResponse p1 = atlas1.getPatientDumpResponse(pid);
        final DumpResponse p2 = atlas2.getPatientDumpResponse(pid);
        final String comp = CompareUtils.explain(p1, p2);
        if (!comp.isEmpty()) {
          System.out.println(pid + " = " + comp);
        }
      }
    }

  }
}
