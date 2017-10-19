# checkstyle-edn-listener
A basic Checkstyle [AuditListener](http://checkstyle.sourceforge.net/apidocs/com/puppycrawl/tools/checkstyle/api/AuditListener.html) implementation 
to output checkstyle results in [EDN](https://github.com/edn-format/edn).  

EDN (Extensible Data Notation) is a superset of the format of the Clojure language.

This code was written to provide EDN formatted Checkstyle output to feed into another project
of mine: [aeolian](https://github.com/andeemarks/aeolian) which consumes Checkstyle data.

There are two main classes you can use:

1. [EdnListener](src/org/corvine/checkstyle/EdnListener.java) - produces a single EDN map for each checkstyle error observed.
1. [BatchingEdnListener](src/org/corvine/checkstyle/BatchingEdnListener.java) - produces a single EDN map for each set of errors for the same source file and line number.

```EdnListener``` was my first take on this functionality before I realised I needed to 
collapse all errors for the same file/line key into a single output EDN line - hence
the creation of ```BatchingEdnListener```.

```EdnListener``` also has the ability to limit the set of fields produced by
specifying a list of fields to retain in the output EDN.

## Configuration

When using custom audit listeners in Checkstyle, they need to be referenced as a module
in your Checkstyle configuration (usually stored in ```checkstyle.xml```) as a child
module of ```Checker``` (see below).

```$xslt
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
          "-//Puppy Crawl//DTD Check Configuration 1.3//EN"
          "http://www.puppycrawl.com/dtds/configuration_1_3.dtd">

    <module name="Checker">

        <module name="BatchingEdnListener">
            <property name="file" value="output.edn"/>
        </module>

        ...
	
        <module name="TreeWalker">

            ...
	
        </module>
    </module>
```

See the [Aeolian Checkstyle config file](https://github.com/andeemarks/aeolian/blob/master/resources/checkstyle-all.xml) for an example of how the ```BatchingEdnListener```
is used.

## Running

The following instructions assume you have [Apache Ant](http://ant.apache.org/) installed and configured.

```ant dist```

The ```dist``` target will compile the source and package it into a JAR file (```edn-listener.jar```)
in the ```dist``` directory.
