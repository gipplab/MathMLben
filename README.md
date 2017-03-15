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

### Build ###

Check out this project and compile via Maven. In the `/target` directory you will 
find the executable server instance `mathpipeline.jar`.

`mvn clean install`

Maven 3.2 or higher is required for the build plugin.
This plugin will create an executable jar.

### Installation ###

For the conversion LaTeXML is required (http://dlmf.nist.gov/LaTeXML/get.html).

### Usage ###

Start the server by `java -jar mathpipeline.jar`.
   
Now just call the server on `http://localhost:8080/index.html` and 
you can start whatever you what to do.

#### REST API ###

You can view the API on default on `http://localhost:8080/swagger-ui.html`.


### Configuration ###

If you want to use a custom configuration place a file named `application.yaml` 
in the execution folder. The content should be:

    server:
    #  servlet-path: /pipe   # custom servlet-path
      port: 8080            # default server port, if not set otherwise
    
    # the use of the latexml online service (via url) is optional
    # if service is not found or configured properly fallback is the local latexml installation
    latexml:
      url: http://gw125.iu.xsede.org:8888 # set a url if you want the option of the online service
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
        preload: "LaTeX.pool,article.cls,amsmath.sty,amsthm.sty,amstext.sty,amssymb.sty,eucal.sty,[dvipsnames]xcolor.sty,url.sty,hyperref.sty,[ids]latexml.sty,texvc"

This is the default configuration used by the application if no file is found.
