# Introduction #

## Usage ##
```
java com.agimatec.dbhistory.HistoryGeneratorTool
  -conf historyConfig.xml
  -catalog catalog.xml 
  -ftldir templates 
  -dbms dbms 
  -ftl templateBaseName 
  -destdir targetDirectory
```

## Options ##
  * -help  (optional) print help
  * -conf  (optional) name of configuration file, default is historyConfig.xml
  * -nconf (optional) use NO configuration file
  * -nout  (optional) do NOT write a default output file (let templates handle output file names)
  * -catalog (required) path of catalog file to read (xml or dump), created by [DDLParserTool](DDLParserTool.md)
  * -ftldir  (optional) default: templates. Base directory with .ftl templates (in subdirs)
  * -dbms  (required) subdirs of ftldir with database-specific templates, e.g. oracle, postgres
  * -ftl (required) can appear multiple times. the template base name (without .ftl suffix)
  * -destdir (optional) default: target. Directory to write output files to.
  * +key=value	A property key and value that the template can access with ${key}
> > Example: +tablespace=TEMP      in template: ${tablespace}
> > You can provide multiple properties to the template this way.

### Accessing static methods inside the freemarker template ###
The template can access a predefined property "statics" to invoke static methods.
See freemarker documentation for further details.

Example: prints milliseconds into template by invoking System.currentTimeMillis();
```
${statics["java.lang.System"].currentTimeMillis()}
```

Property statics is BeansWrapper.getDefaultInstance().getStaticModels().

# Example #

The example syntax demonstrates the invocation of the HistoryGeneratorTool from the InvokerTool with ant (or the maven-antrun-plugin). You can imagine the native java call, can't you?

```
<java fork="true" failonerror="true"
   classname="com.agimatec.database.InvokerTool"
   classpathref="maven.compile.classpath">
 <jvmarg value="-Dfile.encoding=UTF-8"/>
               
 <arg line="{ com.agimatec.dbhistory.HistoryGeneratorTool "/>
 <arg line="-conf historyconfig.xml"/>
 <arg line="-catalog target/my-catalog-postgres.xml"/>
 <arg line="-dbms postgres -destdir postgres/setup "/>
 <arg line="-ftldir templates"/>
 <arg line="-ftl history-schema -ftl history-triggers }"/>
</java>
```

This generates the history-schema.sql and the history-triggers.sql file for the catalog "my-catalog-postgres.xml".

To do the same for oracle, just use `-dbms oracle` instead and provide the catalog of your oracle database.