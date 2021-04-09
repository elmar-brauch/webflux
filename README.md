# Webflux demo project

## How to get it running
* Clone this GIT project.
* Make sure it is a Gradle project and Gradle is executed to load dependencies.
* This project demonstrates several aspects of Spring Webflux:
    * for WebClient usage the ReactiveVsClassicClientTest and its classes in package de.bsi.webflux.backend can just be used.
    * for Mono and Flux demonstration the ReactiveServiceTest and ReactiveEmployeeController can also just be used.
    * for reactive MongoDB interaction a running MongoDB is required. Database connection is configured in application.properties. If you have Docker installed a MongoDB container can be started like this: 
    `docker run -d -p 27017:27017 --name mongodb mongo`
    &nbsp;Then classes in package de.bsi.webflux.database and the JUnit test ReactiveEmployeePersistenceControllerTest will run properly.

## More information
Visit Elmar Brauch's German blog to read the articles behind this project:
https://agile-coding.blogspot.com
