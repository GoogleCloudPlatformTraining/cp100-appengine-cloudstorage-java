<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.memcache.MemcacheServiceFactory" %>
<%@ page import="com.google.appengine.api.memcache.MemcacheService" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">
  </head>
  <body>
    <div class=container>
      <h1>Simple Guestbook</h1>
      <div class="panel panel-default">
        <div class="panel-heading">
          <h3 class="panel-title">New Entry</h3>
        </div>
        <div class="panel-body">
          <form role="form" action="add" method=post>
              <div class="form-group">
                <textarea class="form-control" name="entry" id="entry" rows="3"></textarea>
              </div>
              <button type="submit" class="btn btn-primary">Submit</button>
          </form>
        </div>
      </div>
      <ul class="list-group">
      <%
      try {
             MemcacheService _service = MemcacheServiceFactory.getMemcacheService();
             List<String> entries = (List<String>)_service.get("entries");
             if (entries != null) {
                  for (String entry : entries) {
      %>
        <li class="list-group-item" class=entry><%=entry%></li>
        <% 
        }
        }
         }
         catch (Exception e) { 
            out.println(e.getMessage());
         }
        %>
      </ul>
      <form role=form action="clear" method=post>
        <button type="submit" class="btn btn-danger">Clear Entries</button>
      </form>
    </div>
  </body>
</html>
