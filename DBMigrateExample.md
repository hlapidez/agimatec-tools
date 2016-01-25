# Using the dbmigrate-example project #
This is a project that demonstrates some of the basic features.
You can use it as a template to build your own database migration/setup project.
It is configured to create or migrate an Oracle10 database.

Some of the features demonstrated by the example project:
  * setup of database (configurable for different environments)
  * creation of users, tables, functions
  * import of data with dbunit
  * import of data from xml file
  * import of data from csv file
  * sanity check of schema, views, triggers
  * using own java classes (e.g. DeleteHistoryTriggers.java) during setup/migration

## For SVN and maven2 users ##
1. Checkout the dbmigrate-example from Source with SVN.

2. Build the project
> mvn package

3. Look into the target/dist directory

## For maven2 users that cannot checkout from SVN ##
1. Download dbmigrate-example-project.zip and extract

2. Build the project
> mvn package

3. Look into the target/dist directory

## For others ##
1. Download dbmigrate-example-dist.zip


The dist-directory contains the distribution of a runnable dbmigrate project.

# Run dbmigrate example #
1. change to directory with dbtool.sh or dbtool.bat

2. unix users:
to setup a new database
> ./dbtool.sh setup.xml
to migrate a database
> ./dbtool.sh upgrade.xml

3. windows users:
to setup a new database
> dbtool.bat setup.xml
to migrate a database
> dbtool.bat upgrade.xml