# migration.xml #
```
 The example project contains two configurations:
 setup.xml to show how to setup a new database
 upgrade.xml to demonstrate a configuration for database migration (upgrade)
```

This configuration is responsible for:

  * Optional: Environment variables, usable in sql scripts.
```
      <map name="env">
        <String name="key" value="value"/>
        ...
      </map>
```
> > Predefined are: DB\_USER, DB\_PASSWORD, DB\_SCHEMA, DB\_URL, DB\_DRIVER, FAIL\_ON\_ERROR. (described below)
  * Optional: System-properties usable in SQL-Scripts
> > All JVM-Systemproperties are accessible as environment variables. System properties overrule the values in "env" map! (This behavior has changed since version 2.3.3!)
  * Mandatory: Directory, in which the upgrade scripts are:
```
      <file name="Scripts" dir="test-scripts" file="" relative="false"/>

      dir = Subdirectory
      file = "" this is the way a directory is referenced (otherwise a file is assumed)
      relative = false. true: file is relative to config-root directory (default- config-root is the current directory anyway.)
```


**Important:** Scripts that should be executed automatically must start with prefix "up-". You can configure another prefix as well. (described below)

Next part of the file name is a version identifier, which determines the sequence of execution. The version consists of up to 3 numbers, separated by . _or -._

File may end with .sql, .xml or .groovy.

.xml files can be used to invoke java classes or to describe conditional migration steps. ([XML-scripts](DBMigrateXmlScript.md))

Script name examples: up-1.0.0\_hello.sql, up\_2\_1.sql, up-1-0-1.xml

  * Optional: file with database configuration.
```
<file name="JdbcConfig" file="oracle.properties"/>
```
> > You can provide a properties file (see [JdbcConfig](DBMigrateJdbcConfig.md))

> Or you can use the information from a Datasource (see [DBMigrateSpring](DBMigrateSpring.md))
> Or you configure the database in the "env" section (see "Environment variables")

  * Optional: start-version
```
      <text name="from-version" value="2.0.12"/>
```
> > if not given, start version will be read from the database table DB\_VERSION
  * Optional: target-version
```
      <text name="to-version" value="2.0.15"/>
```
> > if not given, the migration will execute the scripts up to the latest version. (this is what you normally want to do.)

# Example migration.xml / upgrade.xml #
```
<?xml version="1.0" encoding="UTF-8"?>

<config name="migration">

  <!-- Optional: environment variables, can be used inside scripts as ${DB_USER}.
   Predefined keys:
    FAIL_ON_ERROR : if false, allow initial database connection 
       to fail without exiting with an exception. default is: true
    DB_USER       : if exists, overrule user from JdbcConfig
    DB_PASSWORD   : if exists, overrule password from JdbcConfig
    DB_SCHEMA     : if exists, overrule schema name of jdbcUrl from JdbcConfig
    DB_URL        : if exists, overrule jdbcUrl from JdbcConfig
    DB_DRIVER        : if exists, overrule jdbcDriver from JdbcConfig
Note: These 4 variables overrule the configuration in the JdbcConfig file!
  -->
  <map name="env">
    <Boolean name="FAIL_ON_ERROR" value="true"/>
    <String name="DB_USER" value="postgres"/>
    <String name="DB_PASSWORD" value="anfang"/>
    <String name="DB_SCHEMA" value="test"/>
    <!--<String name="DB_URL" value="jdbc:postgresql://localhost:5432/test"/>-->
  </map>


  <!-- Optional. File prefix of script files to execute automatically.
       Filename: prefix version _ rest . ending -->
  <String name="Scripts-Prefix" value="up-"/>

 <!-- Optional. Scripts-Before-All = the source path for scripts to execute
      (before the sequence of migration scripts is executed) -->
  <file name="Scripts-Before-All" dir="upgrade/before-all" file=""/>

  <!-- Scripts = the source path for scripts to scan for -->
  <file name="Scripts" dir="test-scripts" file="" relative="false"/>

  <!-- Optional = the *additional* URLs/Paths where to search for groovy scripts (to be called explicitly with doGroovyScript). 
      can be file or String or text nodes -->
    <list name="GroovyScripts">
        <String dir="cp://scripts1/"/>
        <file dir="cp://scripts2/" file=""/>        
    </list>

  <!-- Optional. Scripts-After-All = the source path for scripts to execute
      (after the sequence of migration scripts has been executed) -->
  <file name="Scripts-After-All" dir="upgrade/after-all" file=""/>

 <!-- Optional -->
  <file name="JdbcConfig" file="postgres-test.properties"/>

  <!-- optional:
    CREATE TABLE DB_VERSION (SINCE timestamp, VERSION varchar(100));

    when from-version is not set, determine automatically from database (table: DB_VERSION).
    when from-version is set, versions higher than this (exclusive!) will be executed. -->
  <!--<text name="from-version" value="2.0.12"/>-->

  <!-- optional:
    when to-version is NOT set, execute configs/scripts for ALL LATER versions.
    when to-version is set, it is the last version (inclusive) to be executed. -->
  <!--<text name="to-version" value="2.0.15"/>-->

<!-- optionale map to configure the version table. these are the default settings -->
<!--  <map name="version-meta">
	<String name="table" value="DB_VERSION"/>
        <String name="version" value="version" />
        <String name="since" value="since"/>
        <Boolean name="insert-only" value="false"/>
        <Boolean name="auto-create" value="true"/>
        <Boolean name="auto-version" value="false"/>
  </map>-->

</config>

```

#### version-meta ####
configure where/how to store the database version in the database.
  * table = table name, default = db\_version
  * version = column with version number (string), default = version
  * since = optional column. set to "" if you do not need it. stores timestamp of last update/insert of the version (timestamp of migration), default = since
  * insert-only = boolean, default = false. if true, tool inserts new version into db\_version (so that you get a version journal)
  * auto-create = boolean, default = true. if true, create db\_version table if it does not exist
  * auto-version = boolean, default = false. insert/update the value in db\_version after execution of an upgrade script automatically. false: you need to set the version in the script explicitly


#### lock-busy: prevent parallel execution ####
The configuration "lock-busy" in the "version-meta" section is a new feature, since dbmigrate 2.5.19.
It is useful when dbmigrate is installed in a cluster to prevent parallel execution on the same database at the same time.
Parallel execution will most likely corrupt the database or fail.
The lock-busy behavior writes a row "busy" into the db\_version table and deletes when dbmigrate is finished.

Known issue:
It requires a unique index (or primary key) on the "version" column of "db\_version"!! It does not work with "insert-only" = "true"!!

```
    <map name="version-meta">
        <Boolean name="auto-version" value="true"/>
        <String name="lock-busy" value="Wait"/>
    </map>
```

lock-busy can be one of: No, Fail, Wait. The default behavior is "No".

  * No: do not care about parallel runs. The user must himself avoid to run multiple dbmigrate processes at the same database at the same time.
  * Wait or Fail:
  * Fail: fail when busy
  * Wait: wait until not busy anymore, then start
> > Defaults for Wait: unlimited attempts, 10 seconds between delay between attempts.



For more information see class com.agimatec.dbmigrate.util.BusyLocker