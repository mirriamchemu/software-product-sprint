// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    response.getWriter().println("<h1>Hello Mirriam!</h1>");

     // Only logged-in users can see the form
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      Query query = new Query("Task");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<String> tasks = new ArrayList<>();
    String string = getParameter(request, "text-input", "");
    String upper = string.toUpperCase();

    for (Entity entity : results.asIterable()) {
      if (upper.equals("BIRTHDAY")) {
          String birthday = (String) entity.getProperty("birthday");
          tasks.add(birthday);
          break;
    } else if (upper.equals("BEST FRIEND")) {
        String bff = (String) entity.getProperty("best friend");
        tasks.add(bff);
        break;
    } else if (upper.equals("COUNTRY")) {
        String country = (String) entity.getProperty("country");
        tasks.add(country);
        break;
    } else if (upper.equals("FAVORITE MOVIE")) {
        String movie = (String) entity.getProperty("favorite movie");
        tasks.add(movie);
        break;
    } else {
        String no = (String) entity.getProperty("no");
        tasks.add(no);
        break;
      }
    }
    Gson gson = new Gson();
    response.getWriter().println(gson.toJson(tasks));
    } else {
      String urlToRedirectToAfterUserLogsIn = "/data";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
      response.getWriter().println("<p>Hello stranger.</p>");
      response.getWriter().println("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    // Only logged-in users can post messages
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/data");
      return;
    }
    
    Entity taskEntity = new Entity("Task");
    String string = getParameter(request, "text-input", "");
    String upper = string.toUpperCase();

    if (upper.equals("BIRTHDAY")) {
        String birthday = "July 13th";
        taskEntity.setProperty("birthday", birthday);
    } else if (upper.equals("BEST FRIEND")) {
        String bff = "I have a couple!";
        taskEntity.setProperty("best friend", bff);
    } else if (upper.equals("COUNTRY")) {
        String country = "Kenya";
        taskEntity.setProperty("country", country);
    } else if (upper.equals("FAVORITE MOVIE")) {
        String movie = "Saving Private Ryan";
        taskEntity.setProperty("favorite movie", movie);
    } else {
        String no = "You have to choose from the 4 options!";
        taskEntity.setProperty("no", no);
    }

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(taskEntity);
    doGet(request, response);
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
