# Introduction #

If you need to understand the configuration details of a database migration project, this side is for you. Mostly you just add new up-.sql files or up-.groovy scripts in the scripts directory without the need to edit the configurations. You can download **dbmigrate-example-dist.zip** which is a completly preconfigured example project.

If you check out the sources from SVN, you can refer to the **test cases** for the classes you need to give you some some hints about features and usage. The **JavaDoc** of dbmigrate might be useful as well.

Some of the classes in dbmigrate have a startable **main()** method. They should print a usage description to the console when invoked with inadequate parameters.

# Usage AutoMigrationTool #
AutoMigrationTool is a class with a main()-method. You need the JDBC driver and some jars (refer to lib directory in dbmigrate-example-dist.zip) in the classpath.

```
Don't forget to create a backup of your database before using the migration tool!
```

_usage: java com.agimatec.dbmigrate.AutoMigrationTool -help -sim true|yes|false -conf conf/migration.xml_ -script aScript -op operationName operationParameter

| Parameter  | Description |
|:-----------|:------------|
| -sim       | -sim true: simulation mode ON. no scripts will be executed. the logger only prints the script names that the programm would  execute. This is for testing what will happen. -sim false: simulation mode OFF (default). Caution: this is the real migration mode that could modify the database. |
| -conf      | default = -conf migration.xml. Name of the main configuration file to use. |
| -help      | show help only |
| -script    | optional, multiple occurrence supported. name of a script-file (sql, groovy, xml) with operations. tool will execute the given file(s) only!|
| -op        | optional, multiple occurrence supported. the operation in the same syntax as in a xml-script. tool will execute the given operation(s) only! |
| -exit      | -exit false: do not exit JVM with System.exit() after migration. The default is "true", but this option improves the integration into a maven pom and other Java processes, that want to call dbmigrate via its main() method |
| -base      | (optional) set the config base URL of the resource or directory, defaults to the path given in resource 'configmanager.ini' or file: if none available. cp:// is the classpath-protocol. |

## Examples ##
```
java com.agimatec.dbmigrate.AutoMigrationTool -conf upgrade.xml

java com.agimatec.dbmigrate.AutoMigrationTool -conf setup.xml

java com.agimatec.dbmigrate.AutoMigrationTool -conf upgrade.xml -script up-2.1.26.sql -script up-2.1.35_functions.xml

java com.agimatec.dbmigrate.AutoMigrationTool -conf setup.xml -op execSQLScript plsql-history-session-spec.sql -op execSQLScript plsql-history-session-body.sql

```

# Main configuration file #
  * [Main configuration file](DBMigrateConfigFile.md)
  * [JdbcConfig](DBMigrateJdbcConfig.md)
  * [SQL scripts](DBMigrateSqlScript.md)
  * [XML scripts](DBMigrateXmlScript.md)
  * [groovy scripts](DBMigrateGroovyScript.md)
  * [maven integration](DBMigrateMaven.md)
  * [spring integration](DBMigrateSpring.md)