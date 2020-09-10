package com.podalv.search.server.api;

import java.io.File;
import java.io.IOException;

import com.google.gson.JsonSyntaxException;
import com.podalv.search.server.api.exceptions.QueryException;

public class Test {

  public static void main(final String[] args) throws IOException, JsonSyntaxException, QueryException {
    final AtlasConnection connection = new AtlasConnection("http://localhost:8080");
    System.out.println(connection.test());
    connection.getFile("ICD9=250.50", new File("/home/podalv/test.txt"));
    connection.getFile("ICD9=250", new File("/home/podalv/test2.txt"));
    connection.getFile("ICD9=249", new File("/home/podalv/test3.txt"));
  }
}