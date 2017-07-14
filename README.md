MathML Pipeline
================

[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

This project is a Java application (Spring Boot) with an embedded Apache Server.
It takes on the following tasks:

1. Convert LaTeX formula to MathML semantics (via MathMLConverters).
2. Compare two MathML semantics and receive node similarities (via tree comparison on MathML).
3. Render a single MathML oder the comparison results of both MathML (via a JS widget).

Task 1 and 2 are executed via an embedded REST Service in this project and is written in Java.
Task 3 is executed via an external JS widget written by students at the HTW Berlin.

## Build ##

Check-out this project and build it via Maven. In the `/target` directory you will 
find the executable server instance `mathpipeline.jar`.

`mvn clean install`

Maven 3.2 or higher is required for the build plugin, it will create an executable jar.

## Requirements ##

For the conversion from LaTeX to MathML, `LaTeXML` is required. Via configuration you can choose
to use an external service for the LaTeX &gt; MathML conversion or use a
local installation of LaTeXML.

You can find LaTeXML here: http://dlmf.nist.gov/LaTeXML/get.html.

## Installation (Standalone) ##

Copy the Jar from the `target` folder to wherever you want. 

Start the server by `java -jar mathpipeline.jar`.
   
Now just call the server on `http://localhost:8080/index.html` and 
you can start whatever you what to do.

## Installation (Service) ##

Since this is a Spring Boot application it can easily be used as a 
Service in a Linux environment, see: 
https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html

Copy the Jar from the `target` folder to `/var/mathpipeline/`

1. Simply create a symlink (_change the path towards your installation_)

    `$ sudo ln -s /var/mathpipeline/mathpipeline.jar /etc/init.d/mathpipeline`
    
2. Once installed, you can start and stop the service in the usual way,
 e.g. `service mathpipeline [start|stop|status|restart]`

You can find an automatic log in `/var/log/mathpipeline.log`.

### JVM Options ###

It is recommended to limit your JVM and set VM options. Just place a file
called `mathpipeline.conf` besides the `mathpipeline.jar` and add the following content:

    export JAVA_OPTS="-Xmx512m -Xms256m"

It should then look like this:

    $ <your_installation_path>    
    $ ls
    mathpipeline.jar
    mathpipeline.conf
    $ cat mathpipeline.conf
    export JAVA_OPTS="-Xmx512m -Xms256m"

## REST API ##

We use Swagger for the API documentation. You can always view the API per 
default on `http://localhost:8080/swagger-ui.html`.

## Configuration ##

If you want to use a custom configuration place a file named `application.yaml` 
in the execution / installation folder. The content should be (_this is the default configuration used by the application_):

    server:
    #  servlet-path: /pipe   # custom servlet-path
      port: 8080            # default server port, if not set otherwise
    
    # Math AST Renderer - Main URL
    mast.url: http://math.citeplag.org
    
    # the use of the latexml online service (via url)
    # if no url is given a local latexml installation will be called
    latexml:
      active: true
      url: http://gw125.iu.xsede.org:8888 # url for online service or emtpy ""
      params:                             # parameters for the online service
        whatsin: math
        whatsout: math
        includestyles:
        format: xhtml
        pmml:
        cmml:
        nodefaultresources:
        linelength: 90
        quiet:
        preload:
          - "LaTeX.pool"
          - "article.cls"
          - "amsmath.sty"
          - "amsthm.sty"
          - "amstext.sty"
          - "amssymb.sty"
          - "eucal.sty"
          - "DLMFmath.sty"
          - "[dvipsnames]xcolor.sty"
          - "url.sty"
          - "hyperref.sty"
          - "[ids]latexml.sty"
          - "texvc"

    # Mathoid - alternative Latex to MathML converter
    mathoid:
      active: true
      url: http://localhost:10044/mml
      