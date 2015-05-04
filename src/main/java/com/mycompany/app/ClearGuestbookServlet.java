package com.mycompany.app;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

import java.util.List;
import java.util.ArrayList;

public class ClearGuestbookServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {

      try {
              String guestbookName = "default";
              DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
              Key guestbookKey = KeyFactory.createKey("Guestbook", guestbookName);
              Query query = new Query("Greeting", guestbookKey);
              ArrayList<Key> keys = new ArrayList<Key>();
              for (Entity greetings: datastore.prepare(query).asIterable()) {
                  keys.add(greetings.getKey());
              }
              datastore.delete(keys);
         }
         catch (Exception e) { 
           System.out.println(e.getMessage());
         }
  resp.sendRedirect("index.jsp");
}
}
