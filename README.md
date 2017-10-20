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

## Building

The following instructions assume you have [Apache Ant](http://ant.apache.org/) installed and configured.

```ant dist```

The ```dist``` target will compile the source and package it into a JAR file (```edn-listener.jar```)
in the ```dist``` directory.

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

## Sample Output

## ```EdnListener```

The ```EdnListener``` output looks like:

```$xslt
{:source-file "file" :line 23 :column 45 :severity "error" :message "Hello" :source "org.corvine.checkstyle.EdnListener"}
```

where the keys are derived as follows:

* ```source-file``` - the source file containing the Error
* ```line``` - the line number containing the Error
* ```column``` - the column containing the Error
* ```severity``` - the string "error" as warnings are not captured
* ```message``` - the message describing the error 
* ```source``` - the class name of the Checkstyle checker producing the error 

### ```BatchingEdnListener```

From the above configuration, the ```BatchingEdnListener``` output would be stored in ```output.edn```, and would
contain a number of lines looking like:

```$xslt
{ :key "file1#24" :source-file "file1" :line 24 :org.corvine.checkstyle.EdnListener "Hello" }
```

where the keys are derived as follows:

* ```key``` - the source file and line number concatenated
* ```source-file``` - the source file containing the Error
* ```line``` - the line number containing the Error
* ```[source-name]``` - 1-n uniquely named entries with the class name of the 
Checkstyle checker producing the error and the error message itself as the value associated with the key