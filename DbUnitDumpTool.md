# Introduction #

Invoke [DBUnit](http://www.dbunit.org/) to create a database dump in xml or excel format.

## Usage ##
You can use the invocation "dbDump" from within the dbmigrate tool's xml configuration (see [xml script features](http://code.google.com/p/agimatec-tools/wiki/DBMigrateXmlScript)).

```
java com.agimatec.database.DbUnitDumpTool {driver} {url} {user} {password} [-f {outputDataFile}]
```


## Options ##
Parameters in required sequence:
  * driver  = Jdbc Driver class name
  * url     = Jdbc Connect url
  * user    = database user
  * password = database password
  * -f output filename (default = data.xml)

The suffix of the output filename determines the output format (refer to the classes of [DBUnit](http://www.dbunit.org/)):

| **suffix** | **format** |
|:-----------|:-----------|
| .xml       | FlatXmlDataSet |
| .xxml      | XmlDataSet |
| .xls       | XslDataSet |

# Example #

```
java com.agimatec.database.DbUnitDumpTool org.postgresql.Driver jdbc:postgresql://localhost:5432/testdb admin password -f testdb.xml
```