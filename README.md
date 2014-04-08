FiddleWith.It!
======

[![Build Status](https://travis-ci.org/cedricbou/FiddleWith.png?branch=master)](https://travis-ci.org/cedricbou/FiddleWith)

Build prototypes, experiment quick features or create micro projects in short time.

This project is under development, only basic features are available for the moments.

FiddleWith.it is built over DropWizard. It allows very
Rapid Feature Development using Ruby scripts (the Fiddles!).

You write ruby scripts, execute them from a browser or an http client.
Parameters are passed in as JSon post data or in the URL query string.

The result is transformed to JSON.

FiddleWith.It provides tools and quick access to many common development features,
such a SQL queries and transactions, HTTP requests with the ability to use
templates for POST data, and many more in the futures.

Because it is built over DropWizard, it benefits from the best features
of this framework, such as metrics, health check, easy configuration
as a yaml file. FiddleWith.it is ready for production and ops!


Inspiration
-----------

Quite often, I am in need of small features or projects, for which
using the classical project stacks (symfony, spring framework, hibernate,
...) looks like overkill. I wanted something that would help me to go
straight to the point, very quickly or instantly, but with nice production
support.

So I wanted :
	- A nice language with first order functions, and nice collections
	  manipulation tools. I first thought of Scala, but Ruby integrates so well
	  with Java, and is actually quite easy to learn.
	- Web Services producing JSON response.
	- Easy configuration.
	- Easy calls to web services.
	- Out of the box Timeouts on external http calls.
	- Out of the box Automatic DB connection pooling.
	- Edit code online.
	- Nice metrics and app monitoring.

During the last months, I had been learning Ruby and dicovered Dropwizard. It
immediately comes to my mind, that I could be even more productive if I could
use scripting language in Dropwizard. So I dig into integrating JRuby in the
framework, and FiddleWith was born!
 

Key Concepts
--------

* *Fiddles*
  You write fiddles, to fiddle with features and business logic! Fiddles are
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

 * ...
