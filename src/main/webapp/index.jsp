<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="java.util.List" %>

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
              String guestbookName = request.getParameter("guestbookName");
              guestbookName = "default";
              pageContext.setAttribute("guestbookName", guestbookName);
              DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
              Key guestbookKey = KeyFactory.createKey("Guestbook", guestbookName);
              // Run an ancestor query to ensure we see the most up-to-date
              // view of the Greetings belonging to the selected Guestbook.
              Query query = new Query("Greeting", guestbookKey).addSort("date", Query.SortDirection.DESCENDING);
              List<Entity> greetings = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(5));
              if (greetings != null)  {
                  for (Entity greeting : greetings) {
                      String greeting_content = (String) greeting.getProperty("content");
          %>
          <blockquote><%=greeting_content%></blockquote>
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
