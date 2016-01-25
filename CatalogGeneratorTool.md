# Introduction #

Read a postgres catalog (xml or serialized file) and convert all column names, data types etc. to an oracle catalog.
Conversion rules for data types, column length, constraint names can be configured in the -conf file (db-conversion.xml). A default db-conversion.xml file is provided (checkout the  dbmigrate project from SVN or download dbmigrate-example-project.zip).

## Usage ##
```
java com.agimatec.dbtransform.CatalogGeneratorTool
  -conf db-conversion.xml
  -catalog catalog.xml 
  -ftldir templates 
  -dbms dbms 
  -ftl templateBaseName 
  -destdir targetDirectory
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
  * -outputCatalog File to write output catalog to.
  * -outputPrefix Prefix for the File(s) written by templates.
  * -outputSuffix Suffix for the File(s) written by templates.
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

## Usage ##
```
java com.agimatec.dbtransform.CatalogGeneratorTool 
  -conf db-conversion.xml
  -catalog target/my-catalog-postgres.xml
  -outputCatalog target/my-catalog-oracle.xml
  -dbms oracle -destdir oracle/setup
  -ftldir templates
  -ftl create-tables -outputPrefix my-
```

## db-conversion.xml ##

for details see class [CatalogConversion](http://agimatec-tools.googlecode.com/svn/trunk/javadoc/dbmigrate/com/agimatec/dbtransform/package-summary.html)

```
<conversion>
  <name>Postgres to Oracle</name>
  <maxLengthForConstraints>30</maxLengthForConstraints>
  <filterIndices>true</filterIndices>
  <dataTypes>
    <transformation>
      <source>
        <typeName>CHARACTER VARYING</typeName>
      </source>
      <target>
        <typeName>VARCHAR2</typeName>
      </target>
    </transformation>
    <transformation>
      <source>
        <typeName>TEXT</typeName>
      </source>
      <target>
        <typeName>CLOB</typeName>
      </target>
    </transformation>
    <transformation>
      <source>
        <typeName>SMALLINT</typeName>
      </source>
      <target>
        <typeName>NUMBER</typeName>
        <precision>5</precision>
        <precisionEnabled>true</precisionEnabled>
      </target>
    </transformation>
  </dataTypes>
</conversion>
```