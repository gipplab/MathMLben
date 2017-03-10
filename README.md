MathML Pipeline
================

This project is a Java application with an embedded Apache Server.
It consists of th following tasks:

1. Convert LaTeX formula to MathML semantics.
2. Compare two MathML semantics and receive node similarities.
3. Render a single MathML oder the comparison results of both MathML via a JS widget.

Task 1 and 2 are executed via an embedded REST Service written in Java.
Task 3 is executed via an external JS widget.

#### Execution ###

Start the server by `java -jar mathpipeline.jar`.

Default port ist `8080`. Optionally you can start on a different port with
the option `--server.port=9000`. E.g.:

    java -jar mathpipeline.jar --server.port=9000

#### Build ####

`mvn clean install`

Maven 3.2 or higher is required for the build plugin.
This plugin will create an executable jar.

#### REST API ###

You can view the API on default on `http://localhost:8080/swagger-ui.html`.

