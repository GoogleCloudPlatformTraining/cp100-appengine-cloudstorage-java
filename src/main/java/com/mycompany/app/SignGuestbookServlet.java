package com.mycompany.app;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

import java.io.OutputStream;
import java.nio.channels.Channels;

import com.google.appengine.api.memcache.*;

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
  
      try {
             MemcacheService _service = MemcacheServiceFactory.getMemcacheService();
             List entries = (List)_service.get("entries");
             if (entries == null) {
	             entries = new java.util.ArrayList<String>();
			 }
	         entries.add(entry);
             _service.put("entries",entries);
             
             //Add the entry to GCS also
             String strFileName = System.currentTimeMillis() + ".txt";
             GcsOutputChannel outputChannel =
        gcsService.createOrReplace(new GcsFilename(BUCKET_NAME,strFileName), GcsFileOptions.getDefaultInstance());
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
