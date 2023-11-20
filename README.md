# REST application (2021)
This is a Java exercise for a programming lecture from 2021.

The goal is to implement a simple REST server and sample application without using any frameworks.

I implemented @Route annotations that are used on controller methods to allow CRUD operations on the sample data. 
A Routing Handler parses the incoming requests and uses Java's reflection API to match paths to controller methods and parameters to arguments.

## This repository contains:
- the implementation of the rest server:
  - a wrapper class for java's HttpServer
  - a RoutingHandler implementing java's HttpHandler interface
  - the @Route annotation class
  - Request and Response classes
- a simple application with sample data:
  - 'House' and 'Person' classes whose objects get serialized as json and written to disk in the 'data' directory
  - a HouseController with methods that use the @Route annotation to allow operations on the house and person data
  - an 'rest.Application' class that loads the sample data and launches the server 
- tests
  - unit tests for the 'House' and 'Person' classes including tests for correct serialization and deserialization
  - a Postman collection with sample requests for manual testing

## How to run this application
1. install JKD 15 and JUnit 5
2. compile and run the 'rest.Application' class's main method
3. import the postman collection and run the provided requests or create your own