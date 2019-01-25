<%-- 
    Document   : AddOrModifyEntity
    Created on : Nov 30, 2018, 11:17:38 PM
    Author     : cgallen
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="org.ordo.project.web.WebObjectFactory"%>
<%@page import="org.ordo.project.model.ServiceFactory"%>
<%@page import="org.ordo.project.model.ServiceFacade"%>
<%@page import="org.ordo.project.model.Entity"%>


<%

    ServiceFacade serviceFacade = (ServiceFacade) session.getAttribute("serviceFacade");

    // If the user session has no bankApi, create a new one
    if (serviceFacade == null) {
        ServiceFactory serviceFactory = WebObjectFactory.getServiceFactory();
        serviceFacade = serviceFactory.getServiceFacade();
        session.setAttribute("ServiceFacade", serviceFacade);
    }

    // get request values
    String action = (String) request.getParameter("action");
    String entityIdReq = (String) request.getParameter("entityId");

    String errorMessage = "";

    Entity entity = null;
    Integer entityId = null;
    
    String timeStart = null;
    String dayStart = null;
    String timeEnd = null;
    String dayEnd = null;

    if ("modifyEntity".equals(action)) {
        try {
            entityId = Integer.parseInt(entityIdReq);
            entity = serviceFacade.retrieveEntity(entityId);
        } catch (Exception e) {
            errorMessage = "problem finding schedule " + e.getMessage();
        }
    } else if ("createEntity".equals(action)) {
        try {
            entity = new Entity();
        } catch (Exception e) {
            errorMessage = "problem finding schedule " + e.getMessage();
        }
    } else {
        errorMessage = "cannot recognise action: " + action;
    }

%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="css/style.css">
        <title>Edit Schedule</title>
    </head>
    <body>
        <% if ("createEntity".equals(action)) {
        %>
        <h1>Add New Schedule</h1>
        <% } else {%>
        <h1>Modify Schedule <%=entityId%></h1>
        <% }%>
        <form action="ListEntities.jsp">
            <table>
                <tr>
                    <th>Field</th>
                    <th>Current Value</th>
                    <th>New Value</th>
                </tr>
                <tr>
                    <td>Schedule ID</td>
                    <td><%=entity.getId()%></td>
                    <td></td>
                </tr>
                <tr>
                    <td>Start Time</td>
                    <td><%=entity.getTimeStart()%></td>
                    <td><input type="time" name="timeStart" required="true">
                        <select name="dayStart" required="true">
                            <option value="Mon">Monday</option>
                            <option value="Tue">Tuesday</option>
                            <option value="Wed">Wednesday</option>
                            <option value="Thu">Thursday</option>
                            <option value="Fri">Friday</option>
                            <option value="Sat">Saturday</option>
                            <option value="Sun">Sunday</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>End Time</td>
                    <td><%=entity.getTimeEnd()%></td>
                    <td><input type="time" name="timeEnd" required="true">
                        <select name="dayEnd" required="true">
                            <option value="Mon">Monday</option>
                            <option value="Tue">Tuesday</option>
                            <option value="Wed">Wednesday</option>
                            <option value="Thu">Thursday</option>
                            <option value="Fri">Friday</option>
                            <option value="Sat">Saturday</option>
                            <option value="Sun">Sunday</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Price</td>
                    <td>£<%=entity.getPrice()%></td>
                    <td>£ <input type="number" name="price" min="0.00" step="0.01" value ="0.00" required="true"></td>
                </tr>
            </table> 
            <BR>
            <% if ("createEntity".equals(action)) {
            %>
            <input type="hidden" name="action" value="createEntity">
            <input type="hidden" name="entityId" value="<%=entityId%>">
            <input type="submit" value="Create New Schedule">
            <% } else if ("modifyEntity".equals(action)) {
            %>
            <input type="hidden" name="action" value="modifyEntity">
            <input type="hidden" name="entityId" value="<%=entityId%>">
            <input type="submit" value="Modify Schedule">
            <% }%>
        </form>
        <form action="ListEntities.jsp">
            <input type="submit" value="Cancel and Return">
        </form>
    </body>
</html>
