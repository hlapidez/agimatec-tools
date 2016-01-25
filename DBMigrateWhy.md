# The New-software-and-old-data-problem #

Applications or products are developed sometimes for years, while various versions on different test or production systems are already in use. As long as no release is used in a production environment, changes are still easily possible. But as soon as live data is gathered in the databases, it has to be maintained as well. And that is today's topic: installing and upgrading database schemas.

## These problems are common among almost all projects involving databases ##

1. Installation of the software including creating a database with initial data varying depending on installation parameters

2. Upgrade of the software, with the database structure (new tables, columns, refactoring of the scheme) changing while keeping the existing data

3. Different test / Acceptance / production systems of various customers on different version stands have to be automatically upgraded to a newer version. Whose keeping track of that?

4. The anxious question after every deployment: Did we forget something about the database? (Is index XY available? Can we remove the column YZ now? Are there really all tables in the database and are there any invalid triggers or views?) Quite a lot of little thing to keep an eye on!

If you have to have a multi vendor database support these problems increase exponentially. Also requirements like that certain views or tables have to be generated automatically on the basis of other tables (eg history tables), are not uncommon.

To solve these problems we've developed "agimatec dbmigrate". This project is now available as open source.

## What is "dbmigrate"? ##

  * An open source framework for the execution of SQL and Groovy scripts. (Includes a simple SQL parser.)
  * A tool that uses an XML configuration for setting up and migrating database schemas.

## What features are included? ##

  * Necessary SQL scripts are run according to naming conventions automatically.
  * Scripts can be configured to run before or after the migration run.
  * Execution of Groovy scripts or calls to any Java classes is possible.
  * Conditional execution of scripts or conditions within scripts allow for the targeting of different environments.
  * Data imports based on text files or DBUnit are supported
  * Tests for completeness and consistency of the schema (triggers, views, Functions)
  * Tested with Oracle and PostgresSQL - but should be usable with all databases supporting JDBC.

## How to use "agimatec dbmigrate"? ##

  1. For "dbmigrate" Java knowledge is not necessary, but, ok, quite helpful.

> 2. It is therefore usable outside of pure Java projects. "Dbmigrate" can be used for any databases.

> 3. "dbmigrate" just needs a Java VM to run.

> 4. The SQL scripts (CREATE TABLE, ALTER TABLE) can be written by hand or via a different tool (such as a modeler or ER-UML tool).

## Short how to: ##

  1. Download an example project here.

> 2. To create the database call: "dbtool.bat setup.xml"

> 3. To upgrade the database: "dbtool.bat upgrade.xml"

> 4. Have a look at the documentation

We look forward to your feedback, questions and are happy to help you with your first "agimatec dbmigrate" project.