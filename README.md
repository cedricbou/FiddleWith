FiddleWith.It!
======

[![Build Status](https://travis-ci.org/cedricbou/FiddleWith.png?branch=master)](https://travis-ci.org/cedricbou/FiddleWith)

Quite often, I am in need of small features or projects, for which
using the classical project stacks (symfony, spring framework, hibernate,
...) looks like overkill. I wanted something that would help me to go
straight to the point, very quickly or instantly, but with nice production
support.

So I wanted :
* A nice language with first order functions, and nice collections manipulation tools. I first thought of Scala, but Ruby integrates so well with Java, and is actually quite easy to learn.
* Web Services producing JSON response.
* Easy configuration.
* Easy calls to web services.
* Out of the box Timeouts on external http calls.
* Out of the box Automatic DB connection pooling.
* Edit code online.
* Nice metrics and app monitoring.

During the last months, I had been learning Ruby and dicovered Dropwizard. It
immediately comes to my mind, that I could be even more productive if I could
use scripting language in Dropwizard. So I dig into integrating JRuby in the
framework, and FiddleWith was born!
 

Key Concepts
--------

* *Fiddles*
  You write fiddles to fiddle with features and business logic! Fiddles are
  ruby scripts. Input parameters can be read from the "d" object. They output
  an HTTP response containing the result of the script, as a JSon object.

* *Workspaces*
  Your fiddles are created in a workspace. It is simply a directory to contain
  a set of Fiddles.
  
* *Repository*
  This is the directory containing your Workspaces. It is configured at server
  startup.
  

Installation and Configuration
------------------------------

To compile the project, you'll a JDK 7 and maven 3, the following maven command should produce the required binary :

> mvn clean install

The binary will be in ''app/target/app-0.1.0-SNAPSHOT.jar''.

To execute it :

> java -jar app/target/app-0.1.0-SNAPSHOT.jar

It will use the user directory as the default repository.

To use another directory as repository, create a configuration file :

> echo "repository: /any/dir/here" > myapplication.conf
> java -jar app/target/app-0.1.0-SNAPSHOT.jar


My first fiddle
---------------

Go into your repository directory, and then create a workspace :

> mkdir calculator && cd calculator

We'll create a simple fiddle doing just a simple addition.

> echo "d.a + d.b" > add.rb

Then go where the jar and configuration files are located and run FiddleWith.it :

> java -jar app-0.1.0-SNAPSHOT.jar myapplication.conf

Try your calculator :

> curl http://localhost:8080/fiddle/with/calculator/add?a=3&b=10

Will output :

> 13

Ok, you're done.








