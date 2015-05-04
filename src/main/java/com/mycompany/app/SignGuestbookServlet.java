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

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

import java.io.OutputStream;
import java.nio.channels.Channels;

import java.util.Date;
import java.util.List;

public class SignGuestbookServlet extends HttpServlet {

  public static final String BUCKET_NAME = "cp100_bucket";
  
  private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
      .initialRetryDelayMillis(10)
      .retryMaxAttempts(10)
      .totalRetryPeriodMillis(15000)
      .build());

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {

  String entry = req.getParameter("entry");
  System.out.println(entry);
  
      try {
             String guestbookName = "default";
             Key guestbookKey = KeyFactory.createKey("Guestbook", guestbookName);
             Date date = new Date();
             Entity greeting = new Entity("Greeting", guestbookKey);
             greeting.setProperty("date", date);
             greeting.setProperty("content", entry);
             
             DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
             datastore.put(greeting);
             
        //Add the entry to GCS also
        String strFileName = System.currentTimeMillis() + ".txt";
        GcsOutputChannel outputChannel =
        gcsService.createOrReplace(new GcsFilename(BUCKET_NAME,strFileName), 
            new GcsFileOptions.Builder().mimeType("text/plain").build());
        OutputStream os = Channels.newOutputStream(outputChannel);
        try {
	        os.write(entry.getBytes(), 0, entry.length());
	        }
	        finally {
	           os.close();
	        }
             
         }
         catch (Exception e) { 
            System.out.println(e.getMessage());
         }
      resp.sendRedirect("index.jsp");
}
}
