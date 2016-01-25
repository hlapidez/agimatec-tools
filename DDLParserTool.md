# Introduction #

### Motivation for the DDLParserTool ###
Most ER-Modelling-Tools or UML-Tools support to generate an SQL file with table definitions. The XMI/XML format supported by these tools - if the tool you are using is supporting it - is rather complex. The proprietary format is often undocumented.
_We are using [DeZign](http://www.datanamic.com/dezign/index.html) to model our databases._

Even if you have a stylesheet to process the XMI created by the ER-tool of your choice, there are sometimes some scripts to define tables, constraints, indices, that you maintain manually. So: you always end up with one or more sql scripts that define your database.

The DDLParserTool can parse SQL-scripts and create an object model (CatalogDescription) that can be stored as XML or serialized object file.
This allows to process the catalog automatically, e.g.
  * generate a history schema, triggers (see HistoryGeneratorTool)
  * generate prototypes of POJOs/Entity-Beans (see CatalogGeneratorTool)
  * validate whether your physical database fits completely to the sql scripts (see dbmigrate#checkObjectsValid or class DatabaseSchemaChecker)
  * _... your use cases here ..._

## Usage ##
```
java com.agimatec.sql.meta.script.DDLParserTool 
  -catalog catalog.xml -dbms dbms -parse file:script-to-parse.sql
```

## Options ##
  * -help    (optional) print help
  * -catalog (required) path of catalog file (xml or dmp) to write
  * -dbms    (required) e.g. oracle, postgres. Determines the database syntax for the script parser.
  * -parse   (required) can appear multiple times. a URL of a script to parse into the catalog, e.g. file:create-tables.sql
All scripts must belong to the same database schema catalog.


# Example #

Parse DDL-Statements in create-tables-1.sql and create-tables-2.sql and create a file my-catalog.xml with the database catalog.

```
java com.agimatec.sql.meta.script.DDLParserTool 
  -catalog my-catalog.xml
  -dbms oracle
  -parse file:create-tables-1.sql
  -parse file:create-tables-2.sql
```