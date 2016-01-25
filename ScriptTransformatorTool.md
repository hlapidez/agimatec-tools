# Introduction #

Tool can convert a migration sql script with ALTER-statements and other DDL-statements from one database dialect to another. All statements can cannot be transformed (because the parser cannot recognize them, remain unchanged).

This helps to create migration script for multiple database in a project.

## Usage ##

```
java com.agimatec.dbtransform.ScriptTransformatorTool
  -conf db-conversion.xml
  -catalog catalog.xml 
  -ftldir templates 
  -dbms dbms 
  -ftl templateBaseName 
  -destdir targetDirectory
  -fromDir fromDir
  -targetDir targetdir
  -fromDbms fromDbms
  -overwrite true|false
```

## Options ##
  * -help  (optional) print help
  * -conf  (optional) name of configuration file, default is db-conversion.xml
  * -nconf (optional) use NO configuration file
  * -nout  (optional) do NOT write a default output file (let templates handle output file names)
  * -catalog (required) path of catalog file to read (xml or dump), created by [DDLParserTool](DDLParserTool.md)
  * -ftldir  (optional) default: templates. Base directory with .ftl templates (in subdirs)
  * -dbms  (required) subdirs of ftldir with database-specific templates, e.g. oracle, postgres
  * -ftl (required) can appear multiple times. the template base name (without .ftl suffix)
  * -destdir (optional) default: target. Directory to write output files to.

  * -fromDir (mandatory) dir with scripts to transform, e.g. postgres/upgrade
  * -targetDir (mandatory) dir to write output scripts to, e.g. oracle/upgrade
  * -fromDbms  (optional) the source DBMS used to parse the scripts in 'fromDir', default: postgres
  * -overwrite (optional) overwrite target scripts (default: false), true/false
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
You could use this technique to access System.getProperty().

# Example #

The example syntax demonstrates the invocation of the ScriptTransformatorTool from the InvokerTool with ant (or the maven-antrun-plugin). You can imagine the native java call, can't you?
```
<java fork="true" failonerror="true"
   classname="com.agimatec.database.InvokerTool"
   classpathref="maven.compile.classpath">
  <jvmarg value="-Dfile.encoding=UTF-8"/>
               
  <!-- transform up-*.sql scripts from postgres to oracle -->
  <arg line="{ com.agimatec.dbtransform.ScriptTransformatorTool"/>
  <arg line="-fromDbms postgres"/>
  <arg line="-dbms oracle "/>
  <arg line="-fromDir postgres/upgrade"/>
  <arg line="-targetDir target/oracle/upgrade"/>
  <arg line="-overwrite false"/>
  <arg line="-ftldir templates }"/>
</java>
```