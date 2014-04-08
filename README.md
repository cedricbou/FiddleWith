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

And then run fiddle with the configuration you've created :

> java -jar app/target/app-0.1.0-SNAPSHOT.jar myapplication.conf


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


Quick user guide
----------------

### Fiddle and parameters ###

You can pass parameter in the query string :

> http://localhost:8080/fiddle/with/myfiddle?name=Foo&company=Bar

Or you can post a json object :

> curl -XPOST http://localhost:8080/fiddle/with/myfiddle -d '{"name": "Foo", "company": "Bar"}'

To read the parameter from the fiddle, you will use the **d** (like data) object :

```ruby
puts d.name
puts d.company
```

### Fiddle and response ###

Fiddle with automatically try to serialize anything you return from the fiddle script in JSON and emit an HTTP 200 response.

You can return most ruby types :

```ruby
# Ruby hashes
return { :a => 'Foo', :ttt => 'Bar' }

# Ruby array 
return [:one, :two, :three]

# Ruby primitives
return 2.65
return 100000
return "hello"
return :hello
```

Sometimes, you want to have control on the http response from your fiddle. It is possible with the **response** object :

```ruby
# Returning a 404 HTTP status
return response._404
return response._404 "not found customer in database"

# Forwarding a response from an inner http call
return response.auto http.url('http://www.google.fr/unknownpage').get
```

> **Improvement** : The response object needs support for more http response code and custom http code.
> This is in the roadmap.

### Using a database ###

Configuration is done in the workspace file `resources.conf`. Configuration is easy and uses YAML (no tabs in the file, only spaces!) :

```yaml
databases:
   foo:
      driverClass: org.h2.Driver
      url: jdbc:h2:mem:foo
      user: "sa"
      password: ""

   bar:
      driverClass: org.h2.Driver
      url: jdbc:h2:mem:foo
      user: "sa"
      password: ""

```

To use the database in the fiddle :

```ruby
# Return the first row as a hash
customer = dbi.foo.first("select * from customer limit 1")

# You can return this directly as the fiddle response
return customer 

# Get a number 
age = dbi.bar.number("select age from person where name = 'John Doe' limit 1")

# Get a text
age = dbi.foo.text("select title from person where name = 'John Doe' limit 1")

# Get an array
persons = dbi.foo.query("select name, age, title from person where age < 35")
```

You can obviously use query parameters :

```ruby
persons = dbi.foo.query("select name, age, title from person where age < ?", 35)
books = dbi.bar.query("select title, isbn from book where genre = ? and author = ?", "SciFi", "Bordage")
```

It is possible to insert or update :

```ruby
dbi.foo.update("insert into book values ('Harry Potter', 'ISBN-1...', 'Magical', 'Rowling')")
dbi.foo.update("update book set genre = 'Adventure' where title = ?", 'Harry Potter')
```

By default, fiddle will open and close a transaction for each query, you can control transactions as well :

```ruby
dbi.bar.in_transaction { |h|
  h.update("insert into person (name, age) values ('John Doe', 31)")
  h.update("insert into person (name, age) values ('Mickey Mouse', 70)")
}
```

### Calling HTTP services ###

You can call other http url. Fiddle provides default timeout for any of your http calls. It is set to 500ms.

```ruby
# Getting the content
content = http.url('http://www.google.fr').get.body

# Reading a json, assuming an URL returning a JSON like {"metrics" : { "temperature" : "5" }}.
temp = http.url('http://foobar.com/meteoAsAService/Paris').json.metrics.temperature.to_i

# You can do the same with XML, assuming an URL return a XML like
# <meteo><metrics><temperature>5</temperature>5 degrees</metrics></meteo>
temp = http.url('http://foobar.com/meteoAsAService/Paris?format=xml').xml.metrics.temperature.text
```

You can post data :

```ruby
r = http.url('http://foobar.com/aWebService').post('{"name":"John Doe", "age":31}')
return {
 :status => r.status,
 :reason => r.status_reason,
 :content => r.body
}
```

Fiddle supports templates for your post and get requests :

```ruby
# TODO : to document
```

