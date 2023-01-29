package no.nav.bidrag.aktoerregister.util;

public class StringUtil {

  public static boolean isEqual(String firstString, String secondString) {
    if (firstString == null) {
      return secondString == null;
    }
    return firstString.equals(secondString);
  }
}
