# Ordo Project
Ordo is a parking meter tool used to organise the utilization of physical parking meters and automate the upkeep of said parking meters. All using Representational State Transfer technologies.


## Instructions

### Getting Started
In order to use, you first are going to want to set up an online web server which can host the project. But first you must use 
```
mvn clean install
```
in the main multi-module project folder first to download all dependancies.
once the build has finished, you can navigate to the web module and utilize the created snapshot to host the server. For this instance, I'm going to be refering to the useage of jetty.

In the web module of the project you can use 
```
mvn jetty:run
```
to start running a local server to which you can do some testing.

Once the server is running locally, you can navigate to 
```
http://localhost:8680/
```
which will display an interface where you can configure your scheduling times and prices. It would be best to insert some schedules into the project first before moving on.

### Requests
making requests to view the schedules inserted is easy. All you have to do is remove the directorys from the address bar and append:
```
/rest/example/retrieve?id=x
```
to retrieve a schedule ID that you insered earlier. Where id=x is the ID of the schedule. You also have the option to retreive all schedules that you've inserted by appending:
```
/rest/example/retrieveAll?id=x
```
into the address bar. Where id=x being the scheduling group to pull from. which leads us onto the Parking Meter client.

### Parking Meter Client
the parking meter client comes with its own interface using javafx. running the ReST client will automatically open the interface where you will be able to setup the meter before being used by customers.
