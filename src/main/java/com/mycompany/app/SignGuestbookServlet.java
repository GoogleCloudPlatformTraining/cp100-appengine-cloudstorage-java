/**
 * Copyright 2015 Google Inc. All Rights Reserved. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mycompany.app;

import java.io.IOException;

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
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

import java.io.OutputStream;
import java.nio.channels.Channels;

import java.util.Date;

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
