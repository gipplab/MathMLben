MathML Pipeline
================

This project is a Java application (Spring Boot) with an embedded Apache Server.
It takes on the following tasks:

1. Convert LaTeX formula to MathML semantics (via LaTeXML).
2. Compare two MathML semantics and receive node similarities (via tree comparison on MathML).
3. Render a single MathML oder the comparison results of both MathML (via a JS widget).

Task 1 and 2 are executed via an embedded REST Service written in Java and can
be used as a standalone application.

Task 3 is executed via an external JS widget written by students at the HTW Berlin.

#### Installation / Build ####

For the conversion LaTeXML is required. (http://dlmf.nist.gov/LaTeXML/get.html)

Check out this project and compile via Maven. In the `/target` directory you will 
find the executable server instance `mathpipeline.jar`.

`mvn clean install`

Maven 3.2 or higher is required for the build plugin.
This plugin will create an executable jar.

#### Execution ###

Start the server by `java -jar mathpipeline.jar`.

Default port ist `8080`. Optionally you can start on a different port with
the option `--server.port=9000`. E.g.:

    java -jar mathpipeline.jar --server.port=9000
    
Now just call the server on `http://localhost:8080/index.html` and 
you can start whatever you what to do.

#### REST API ###

You can view the API on default on `http://localhost:8080/swagger-ui.html`.

