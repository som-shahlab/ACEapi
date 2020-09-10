package com.podalv.search.server.api;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.function.Predicate;

public class QueryUtils {

  public static String query(final String slaveUrl, final String query, final int timeout) throws IOException {
    return query(slaveUrl, query, timeout, null);
  }

  public static String query(final String slaveUrl, final String query, final int timeout, final Predicate<String> consumer) throws IOException {
    String result = null;
    if (slaveUrl != null) {
      final StringBuilder resultBuilder = new StringBuilder();
      final URL url = new URL(slaveUrl);
      final HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
      urlc.setConnectTimeout(timeout);
      urlc.setReadTimeout(timeout);
      urlc.setDoOutput(true);
      urlc.setRequestMethod("POST");
      urlc.setAllowUserInteraction(false);
      urlc.setReadTimeout(0);
      final PrintStream ps = new PrintStream(urlc.getOutputStream());
      ps.print(query);
      ps.close();

      final BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
      String line;
      while ((line = br.readLine()) != null) {
        if (consumer == null) {
          resultBuilder.append(line + "\n");
        }
        else {
          if (!consumer.test(line)) {
            break;
          }
        }
      }
      br.close();
      result = resultBuilder.toString();
    }
    return result;
  }

  public static void saveUrlToFile(final String url, final File outputFile) throws IOException {
    final URL website = new URL(url);
    final ReadableByteChannel rbc = Channels.newChannel(website.openStream());
    final FileOutputStream fos = new FileOutputStream(outputFile);
    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    fos.close();
  }
}
