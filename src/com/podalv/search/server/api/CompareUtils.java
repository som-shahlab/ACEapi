package com.podalv.search.server.api;

import java.util.*;

import com.podalv.search.server.api.responses.DumpResponse;

/** Allows comparing 2 patients and any parts of their data
 * 
 */
public interface CompareUtils {

  static Map<Object, List<Object>> getMissingInLeft(final Map<Object, List<Object>> left, final Map<Object, List<Object>> right) {
    final Map<Object, List<Object>> result = new HashMap<>();
    if (left == null && right != null) {
      right.forEach((o, objects) -> result.put(o, objects));
    }
    else if (left != null) {
      left.forEach((key, value) -> {
        if (right == null) {
          result.put(key, value);
        }
        else {
          final List<Object> rightValue = right.get(key);
          if (rightValue == null) {
            result.put(key, value);
          }
        }
      });
    }
    return result;
  }

  static Map<Object, List<Object>> getDifferentInLeft(final Map<Object, List<Object>> left, final Map<Object, List<Object>> right, final int numberOfValueColumns) {
    final Map<Object, List<Object>> result = new HashMap<>();
    if (left == null && right != null) {
      right.forEach((o, objects) -> result.put(o, objects));
    }
    else if (left != null && right != null) {
      left.forEach((key, value) -> {
        final List<Object> rightValue = right.get(key);
        if (rightValue != null && !right.get(key).equals(value)) {
          final Set<List<Object>> rightSingle = extractSingleValue(numberOfValueColumns, rightValue);
          final Set<List<Object>> leftSingle = extractSingleValue(numberOfValueColumns, value);
          leftSingle.removeAll(rightSingle);
          final Iterator<List<Object>> i = leftSingle.iterator();
          final ArrayList<Object> diff = new ArrayList<>();
          while (i.hasNext()) {
            final List<Object> val = i.next();
            for (final Object obj : val) {
              diff.add(obj);
            }
          }
          if (diff.size() != 0) {
            result.put(key, diff);
          }
        }
      });
    }
    return result;
  }

  static Set<List<Object>> extractSingleValue(final int numberOfValueColumns, final List<Object> rightValue) {
    final Set<List<Object>> values = new HashSet<>();
    for (int x = 0; x < rightValue.size(); x += numberOfValueColumns) {
      final ArrayList<Object> singleValue = new ArrayList<>();
      for (int y = x; y < x + numberOfValueColumns; y++) {
        singleValue.add(rightValue.get(y));
      }
      values.add(singleValue);
    }
    return values;
  }

  static ComparisonResult compare(final Map<Object, List<Object>> left, final Map<Object, List<Object>> right, final int numberOfValueColumns) {
    final ComparisonResult<Map<Object, List<Object>>> result = new ComparisonResult<>();
    result.setMissingLeft(getMissingInLeft(left, right));
    result.setMissingRight(getMissingInLeft(right, left));
    result.setDifferentLeft(getDifferentInLeft(left, right, numberOfValueColumns));
    result.setDifferentRight(getDifferentInLeft(right, left, numberOfValueColumns));

    return result.missingLeft().size() == 0 && result.missingRight().size() == 0 && result.differentLeft().size() == 0 && result.differentRight().size() == 0
        ? new IdenticalComparisonResult()
        : result;
  }

  static String explainDiff(final String prefix, final Object o1, final Object o2) {
    return !Objects.equals(o1, o2) ? prefix : "";
  }

  static String explainYears(final String prefix, final Map<Integer, List<Integer>> o1, final Map<Integer, List<Integer>> o2) {
    if (o1.size() != o2.size()) {
      return prefix;
    }
    final Iterator<Map.Entry<Integer, List<Integer>>> iterator = o1.entrySet().iterator();
    while (iterator.hasNext()) {
      final Map.Entry<Integer, List<Integer>> entry = iterator.next();
      final List<Integer> list2 = o2.get(entry.getKey());
      if (entry.getValue().size() != list2.size()) {
        return prefix;
      }
      for (int x = 0; x < list2.size(); x++) {
        if (Math.abs(list2.get(x) - entry.getValue().get(x)) > 1) {
          return prefix;
        }
      }
    }
    return "";
  }

  static String explainDiff(final String prefix, final Map o1, final Map o2, final int numberOfValueColumns) {
    final ComparisonResult<Map<Object, List<Object>>> result = compare(o1, o2, numberOfValueColumns);
    return result instanceof IdenticalComparisonResult ? "" : prefix;
  }

  static String explain(final DumpResponse patient1, final DumpResponse patient2) {
    return explainDiff("PID ", patient1.getPatientId(), patient2.getPatientId()) + //
        explainDiff("RECS ", patient1.getRecordStart(), patient2.getRecordStart()) + //
        explainDiff("RECE ", patient1.getRecordEnd(), patient2.getRecordEnd()) + //
        explainDiff("DEATH ", patient1.getDeath(), patient2.getDeath()) + //
        explainDiff("CONTS ", patient1.isContainsStart(), patient2.isContainsStart()) + //
        explainDiff("CONTE ", patient1.isContainsEnd(), patient2.isContainsEnd()) + //
        explainDiff("GEND ", patient1.getGender(), patient2.getGender()) + //
        explainDiff("RAC ", patient1.getRace(), patient2.getRace()) + //
        explainDiff("ETH ", patient1.getEthnicity(), patient2.getEthnicity()) + //
        explainDiff("SELQ ", patient1.getSelectionQuery(), patient2.getSelectionQuery()) + //
        explainDiff("ERR ", patient1.getError(), patient2.getError()) + //
        explainDiff("ICD9 ", patient1.getIcd9(), patient2.getIcd9(), 3) + //
        explainDiff("CPT ", patient1.getCpt(), patient2.getCpt(), 2) + //
        explainDiff("RX ", patient1.getRx(), patient2.getRx(), 2) + //
        explainDiff("SNOMED ", patient1.getSnomed(), patient2.getSnomed(), 2) + //
        explainDiff("NEGT ", patient1.getNegatedTerms(), patient2.getNegatedTerms(), 2) + //
        explainDiff("FAMT ", patient1.getFhTerms(), patient2.getFhTerms(), 2) + //
        explainDiff("POST ", patient1.getPositiveTerms(), patient2.getPositiveTerms(), 2) + //
        explainDiff("VIS ", patient1.getVisitTypes(), patient2.getVisitTypes(), 2) + //      
        explainDiff("NOT ", patient1.getNoteTypes(), patient2.getNoteTypes(), 2) + //
        explainDiff("ATC ", patient1.getAtc(), patient2.getAtc()) + //
        explainDiff("LABS ", patient1.getLabs(), patient2.getLabs(), 2) + //
        explainDiff("LABSR ", patient1.getLabsRaw(), patient2.getLabsRaw(), 2) + //
        explainDiff("VIT ", patient1.getVitals(), patient2.getVitals(), 2) + //          
        explainDiff("ENC ", patient1.getEncounterDays(), patient2.getEncounterDays()) + //
        explainDiff("AGE ", patient1.getAgeRanges(), patient2.getAgeRanges()) + //
        explainYears("YR ", patient1.getYearRanges(), patient2.getYearRanges());
  }
}
