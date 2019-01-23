<%-- 
    Document   : ListEntities
    Created on : Nov 30, 2018, 11:17:02 PM
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
    String entityField_priceReq = (String) request.getParameter("price");
    String ts_Req = (String) request.getParameter("timeStart");
    String ds_Req = (String) request.getParameter("dayStart");
    String te_Req = (String) request.getParameter("timeEnd");
    String de_Req = (String) request.getParameter("dayEnd");

    String errorMessage = "";
    if ("deleteEntity".equals(action)) {
        try {
            Integer entityId = Integer.parseInt(entityIdReq);
            serviceFacade.deleteEntity(entityId);
        } catch (Exception e) {
            errorMessage = "problem deleting Entity " + e.getMessage();
        }
    } else if ("modifyEntity".equals(action)) {
        try {
            Integer entityId = Integer.parseInt(entityIdReq);
            Entity entityTemplate = new Entity();
            entityTemplate.setId(entityId);
            entityTemplate.setTimeStart(ts_Req+ " - " + ds_Req);
            entityTemplate.setTimeEnd(te_Req+" - "+de_Req);
            entityTemplate.setPrice(entityField_priceReq);
            Entity entity = serviceFacade.updateEntity(entityTemplate);
            if (entity == null) {
                errorMessage = "problem modifying Entity. could not find entityId " + entityId;
            }
        } catch (Exception e) {
            errorMessage = "problem modifying Entity " + e.getMessage();
        }
    } else if ("createEntity".equals(action)) {
        try {
            Entity entityTemplate = new Entity();
            entityTemplate.setTimeStart(ts_Req+ " - " + ds_Req);
            entityTemplate.setTimeEnd(te_Req+" - "+de_Req);
            entityTemplate.setPrice(entityField_priceReq);
            Entity entity = serviceFacade.createEntity(entityTemplate);
            if (entity == null) {
                errorMessage = "problem creating Entity. Service returned null ";
            }
        } catch (Exception e) {
            errorMessage = "problem creating  Entity " + e.getMessage();
        }
    } 

    List<Entity> entityList = serviceFacade.retrieveAllEntities();

%>



<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="css/style.css">
        <title>Schedule List</title>
    </head>
    <body>
        <!-- print error message if there is one -->
        <div style="color:red;"><%=errorMessage%></div>
        <h1>Schedule List</h1>
        <table>
            <tr>
                <th>Schedule ID</th>
                <th>Start Time</th>
                <th>End Time</th>
                <th>Price</th>
                <th></th>
            </tr>
            <%  for (Entity entity : entityList) {
            %>
            <tr>
                <td><%=entity.getId()%></td>
                <td><%=entity.getTimeStart()%></td>
                <td><%=entity.getTimeEnd()%></td>
                <td>£<%=entity.getPrice()%></td>
                <td>
                    <form action="AddOrModifyEntity.jsp">
                        <input type="hidden" name="action" value="modifyEntity">
                        <input type="hidden" name="entityId" value="<%=entity.getId()%>">
                        <input type="submit" value="Modify Schedule">
                    </form>
                    <form action="ListEntities.jsp">
                        <input type="hidden" name="action" value="deleteEntity">
                        <input type="hidden" name="entityId" value="<%=entity.getId()%>">
                        <input type="submit" value="Delete Schedule">
                    </form>
                </td>
            </tr>
            <% }%>

        </table> 
        <BR>
        <form action="AddOrModifyEntity.jsp">
            <input type="hidden" name="action" value="createEntity">
            <input type="submit" value="Create Schedule">
        </form>
    </body>
</html>
