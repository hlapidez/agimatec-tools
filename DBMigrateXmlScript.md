If you need to invoke java-beans, you cannot do that in sql scripts. You can use a xml script instead which gives you more control about the migration process.

# Example #
```
<config>
  <list name="Operations">
    <text name="doSQLScript" value="subscript.sql"/>
    <!--<text name="halt" value="manually perform something"/>-->
    <text name="doSQLScriptIgnoreErrors" value="subscript.sql"/>
    <text name="version" value="2.0.16"/>
  </list>
</config>
```

# Features #

list "Operations" is mandatory and contains the migration steps to execute.
text name="" is a public method in class [BaseMigrationTool](http://www.agimatec.de/agimatec-tools/site/dbmigrate/apidocs/com/agimatec/dbmigrate/BaseMigrationTool.html) (or a subclass)
The methods get a single string parameter.

```
<text name="doSQLScript" value="subscript.sql"/> 
```
invokes
```
public void doSQLScript(String parameter); // parameter="subscript.sql"
```

## Predefined Operations ##

  * doSQLScript
Execute a SQL-Script (parse statements). Fail on SQLExceptions.
  * doSQLScriptIgnoreError
Execute a SQL-Script (parse statements). Log SQLExceptions and continue.

  * doSQLLines or doSQLLinesIgnoreErrors.
Execute a SQL-Script that does not have ; to separate statements, but uses line-feeds (eclipselink).

  * execSQLScript
Execute a SQL-Script as a single jdbc statement. Can be used to install a PL/SQL package.
Use a separate file for each PL/SQL package-spec, package-body. The script will not be parsed.
  * invokeBean
invoke a n-arg-method on a new instance of a class.
All parameters of the target method must be of type String!
```
 <text name="invokeBean" value="com.agimatec.database.InsertJasperReport#save(2,user-chart.jrxml)"/>
```
  * invokeStatic
invoke a static n-arg-method on a class.
All parameters of the target method must be of type String!
  * dbDump
invoke dbunit, parameter is the output file name, see DbUnitDumpTool

  * dbSetup
invoke dbunit, parameters are delete-data-set (optional) and import-data-set(mandatory).

Examples:
```
<text name="dbSetup" value="delete_data.xml,data.xml"/>
<text name="dbSetup" value="data.xml"/>
```
see DbUnitSetupTool

  * halt
Explicit program stop. Some migration steps might require manual activities by the administrator. The migration program can execute until this step and invoke a halt. The given parameter will be printed and logged.
  * version
same as -- @version() in sql scripts. set the DB\_VERSION value
  * **doGroovyScript**
invoke a groovy script.
Name of scripts in value="" can contain optional parameters, use brackets ().
The script gets some default Bindings (variables):
| **binding** | **description** |
|:------------|:----------------|
| params      | all parameters as a list of Strings |
| tool        | the current instance of MigrationTool (allows access to environment and jdbcConnection) |

  * copyFiles
Copy one or multiple files (URLs) from a source to a target location.
The parameter is the name of the list in the environment with source-target file names.

Example: copy file:sourceDir/fileA.txt to targetDir/fileA.txt
```
  <map name="env">

    <ArrayList name="files-to-copy">

      <String value="file:sourceDir/fileA.txt"/>

      <String value="file:targetDir/fileA.txt/>

    </ArrayList>

  </map>



  <list name="Operations">

    <text name="copyFiles" value="files-to-copy"/>
  </list>
```

You can use ${property} in file names. You can rename a file copied.

### Examples ###
a) no parameter
```
<text name="doGroovyScript" value="importProductsCsv.groovy"/>
```
b) 2 parameters
```
<text name="doGroovyScript" value="importProductsCsv.groovy(products.txt,myapp)"/>
```

  * checkObjectsValid
Check if database contains invalid Trigger, Indices, Views.
Parameter: databaseType ("oracle", "postgres")

## conditional execution with 

&lt;list&gt;

 ##

Operations, that you need to execute under specific conditions (depending on environment variables from 

&lt;map name="env"&gt;

), are nested elements under a 

&lt;list&gt;

 tag.
The name-attribute of the 

&lt;list&gt;

 contains the condition expression
(same syntax and features as in SQL-Scripts, see -- #if).

Example:
  * Execute "doSQLScript" and "invokeBean" when "testdata\_enabled=true"
```
<config>
  <list name="Operations">
    <list name="testdata_enabled=true">
        <text name="doSQLScript" value="insert-some-data.sql"/>
        <text name="invokeBean" value="com.agimatec.database.InsertJasperReport#save(2,user-chart.jrxml)"/>
     </list>
     <text name="version" value="2.0.24"/>
  </list>
</config>
```

# Database Schema Checker #

  * checkSchemaComplete
Compare schema of database with SQL-Scripts.
1. Parameter: databaseType ("oracle", "postgres")
2. (Comma-separated) Name of env-entries, that contains a list of Strings, that are File-URLs. These files are .sql-Files, which will be parsed und compared with the physical database schema (read from the database).
This is a mightly operation that allows to check whether all schema elements (columns, keys, tables) are correctly migrated.

### Example 1 ###
```
<config>
  <map name="env">
    <ArrayList name="sqlfiles">
      <String value="file:setup/application-tables.sql"/>
      <String value="file:setup/history-tables.sql"/>
    </ArrayList>
  </map>

  <list name="Operations">
    <text name="checkObjectsValid" value="oracle"/>
    <text name="checkSchemaComplete" value="oracle,sqlfiles"/>
  </list>
</config>
```

### Example 2 (eclipselink) ###
```
  <config>
    <map name="env">
        <ArrayList name="schema-files">
            <String value="cp://database/schema.sql"/>
            <String value="cp://database/create_custom_tables.sql"/>
        </ArrayList>
        <ArrayList name="options">
            <map>
                <String name="format" value="JDBC"/>
            </map>
            <map/>
        </ArrayList>
    </map>

    <list name="Operations">
        <text name="checkSchemaComplete" value="postgres,schema-files,options"/>
    </list>
</config>
```

You can tell the tool, that some scripts use a different file format. Provide a list of "options" that are maps will the following entries supported:
  * format = one of JDBC, SQL, STMT
> > SQL (default) = a sql script with ; separator between statements
> > JDBC (eclipselink) = a sql script where each line is a statement, no ;
> > STMT = a sql script that contains a single sql statement

The 'options' list contains a 'map' per file-url. 'options' is optional.