# As400Querier

Command line tool to query an AS/400 server and return results as JSON.
It uses the [JTOpen][jt400] Java library from IBM.

It was created to extract data from an AS/400 for consumption into a web application built on [CouchDB][couchdb].

## Development

Build is automated using [Gradle][gradle].

To configure the eclipse project, launch:

    gradle eclipse

and import the existing project in Eclipse.

Use `gradle distTar` to build a distributable package. Use `gradle installApp` to install into `build/install/`.

## Usage

### Configuration

Create a `local.properties` file in the working directory. Look a `Property.java` for an enumeration of all properties.

Put your queries in a `queries` folder in the working directory. Then, a query named `onequery` will go into `queries/onequery.sql`.

Each query is parsed with [MVEL][mvel] as an [MVEL template][mvelt]. The parameters are taken from the command line as `parameter=value` pairs.

### Running

To run the query `onequery` with parameters `a=b` and `c=d` use:

    java -jar as400-querier.jar onequery a=b c=d

[jt400]: http://jt400.sourceforge.net/
[couchdb]: http://couchdb.apache.org/
[mvel]: http://mvel.codehaus.org/
[mvelt]: http://mvel.codehaus.org/MVEL+2.0+Templating+Guide
[gradle]: http://www.gradle.org/
