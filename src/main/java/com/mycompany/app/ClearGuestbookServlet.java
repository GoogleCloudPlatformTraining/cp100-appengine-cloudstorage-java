package com.mycompany.app;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.memcache.*;

public class ClearGuestbookServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {


      try {
             MemcacheService _service = MemcacheServiceFactory.getMemcacheService();
             _service.delete("entries");
         }
         catch (Exception e) { 
           System.out.println(e.getMessage());
         }
  resp.sendRedirect("index.jsp");
}
}
