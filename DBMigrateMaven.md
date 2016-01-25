# Introduction #

You can invoke dbmigrate with the maven-exec-plugin from within a pom.xml
This can be an alternative for the maven-sql-plugin to setup a new database (-Pdevelop-db) or to execute some scripts (-Preset-db) with maven.
The example shows the invocation bound to a maven lifecycle phase (process-test-resources).

# A real best practise example #
Refer to the spring integration to configure dbmigrate as part of the startup of your application.

If you additionally want to provide the option to run dbmigrate from maven without the need to start your app, you can follow the 'best practise example', too.

1. pom.xml
```
  ...
  <dependencies>
       <dependency>
            <groupId>de.viaboxx</groupId>
            <artifactId>dbmigrate</artifactId>
            <version>2.5.12</version>
        </dependency>
  ...
  </dependencies>

   <profiles>
        <profile>
            <!--
                The Profile "setup" is used when a developer wants to
                - drop/create the database/role/user/schema the first time or after a schema-change

                invoke: mvn -Psetup exec:java
            -->
            <id>setup</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.codehaus.mojo</groupId>
                        <artifactId>exec-maven-plugin</artifactId>
                        <version>1.2.1</version>
                        <executions>
                            <execution>
                                <goals>
                                    <goal>java</goal>
                                </goals>
                            </execution>
                        </executions>
                        <configuration>
                            <mainClass>com.agimatec.dbmigrate.AutoMigrationTool</mainClass>
                            <arguments>
                                <argument>-base</argument>
                                <argument>cp://database/</argument>
                                <argument>-conf</argument>
                                <argument>setup.xml</argument>
                                <argument>-exit</argument>
                                <argument>false</argument>
                            </arguments>
                            <systemProperties>
                                <systemProperty>
                                    <key>DB_URL</key>
                                    <value>jdbc:postgresql://localhost:5432/postgres</value>
                                </systemProperty>
                                <systemProperty>
                                    <key>DB_SCHEMA</key>
                                    <value>postgres</value>
                                </systemProperty>
                                <systemProperty>
                                    <key>DB_USER</key>
                                    <value>postgres</value>
                                </systemProperty>
                                <systemProperty>
                                    <key>DB_PASSWORD</key>
                                    <value>admin</value>
                                </systemProperty>
                                <systemProperty>
                                    <key>APP_DB</key>
                                    <value>appdb</value>
                                </systemProperty>
                                <systemProperty>
                                    <key>APP_USER</key>
                                    <value>appuser</value>
                                </systemProperty>
                                <systemProperty>
                                    <key>APP_PASSWORD</key>
                                    <value>secret</value>
                                </systemProperty>
                            </systemProperties>
                        </configuration>
                    </plugin>
                </plugins>
            </build>
        </profile>
        <profile>
            <!--
                The Profile "upgrade" is used when a developer wants to upgrade the database without starting the app
                invoke: mvn -Pupgrade exec:java
            -->
            <id>upgrade</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.codehaus.mojo</groupId>
                        <artifactId>exec-maven-plugin</artifactId>
                        <version>1.2.1</version>
                        <executions>
                            <execution>
                                <goals>
                                    <goal>java</goal>
                                </goals>
                            </execution>
                        </executions>
                        <configuration>
                            <mainClass>com.agimatec.dbmigrate.AutoMigrationTool</mainClass>
                            <systemProperties>
                                <systemProperty>
                                    <key>DB_URL</key>
                                    <value>jdbc:postgresql://localhost:5432/appdb</value>
                                </systemProperty>
                                <systemProperty>
                                    <key>DB_USER</key>
                                    <value>appuser</value>
                                </systemProperty>
                                <systemProperty>
                                    <key>DB_PASSWORD</key>
                                    <value>secret</value>
                                </systemProperty>
                            </systemProperties>
                            <arguments>
                                <argument>-base</argument>
                                <argument>cp://database/</argument>
                                <argument>-conf</argument>
                                <argument>upgrade.xml</argument>
                                <argument>-exit</argument>
                                <argument>false</argument>
                            </arguments>
                        </configuration>
                    </plugin>
                </plugins>
            </build>
        </profile>


        <!-- OPTIONAL
             this is a possibility to integrate hiberate4 SchemaExport with the hibernate4-maven-plugin to create
             the file schema.sql after you changed your entity classes -->
        <profile>
            <id>generate</id>
            <properties>
                <hibernate.connection.driver_class>org.postgresql.Driver</hibernate.connection.driver_class>
                <hibernate.dialect>org.hibernate.dialect.PostgreSQL82Dialect</hibernate.dialect>
            </properties>
            <build>
                <plugins>
                    <plugin>
                        <!-- mvn -Pgenerate clean compile

                            or - if no <phase> is configured:
                            mvn -Pgenerate clean compile hibernate4:export

                            documentation, see http://juplo.de/hibernate4-maven-plugin/
                            -->
                        <groupId>de.juplo</groupId>
                        <artifactId>hibernate4-maven-plugin</artifactId>
                        <version>1.0.1</version>
                        <executions>
                            <execution>
                                <id>generate-schema</id>
                                <phase>compile</phase>
                                <goals>
                                    <goal>export</goal>
                                </goals>
                                <configuration>
                                    <target>SCRIPT</target>
                                    <outputFile>src/main/resources/database/schema.sql</outputFile>
                                    <force>true</force>
                                    <format>true</format>
                                    <type>CREATE</type>
                                </configuration>
                            </execution>

                            <execution>
                                <id>generate-drop</id>
                                <phase>compile</phase>
                                <goals>
                                    <goal>export</goal>
                                </goals>
                                <configuration>
                                    <target>SCRIPT</target>
                                    <outputFile>src/main/resources/database/drop-schema.sql</outputFile>
                                    <force>true</force>
                                    <format>false</format>
                                    <type>DROP</type>
                                </configuration>
                            </execution>
                        </executions>

                    </plugin>
                </plugins>
            </build>
        </profile>

   </profiles>
 
  ...
</project>

```

2. other files
the other files needed to run this pom.xml are contained in [spring integration - best practise example](DBMigrateSpring.md).

you need
  * setup.xml
  * upgrade.xml

in directory resources/database. But spring is not required for a pure maven integration, so you do NOT need the configuration files and properties unless you also want to have spring integration (I prefer to have both).

## Usage ##

1. Create the database (drop/create)
```
mvn -Psetup exec:java
```

2. Migrate the database
```
    mvn -Pupgrade exec:java
```

3. Let hibernate generate the schema.sql file
```
    mvn -Pgenerate clean compile
```

---


# Example to just check the database schema #
Check the database for compliance with sql scripts.


Example profiles-section of a maven pom.xml
```
...
        <profile>
            <id>checkdb</id>
            <activation><activeByDefault>false</activeByDefault></activation>
            <properties>

            </properties>
            <dependencies>
                <dependency>
                    <groupId>de.viaboxx</groupId>
                    <artifactId>dbmigrate</artifactId>
                    <version>2.5.12</version>
                </dependency>
            </dependencies>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.codehaus.mojo</groupId>
                        <artifactId>exec-maven-plugin</artifactId>
                        <version>1.2.1</version>
                        <executions>
                            <execution>
                                <phase>process-test-resources</phase>
                                <goals>
                                    <goal>exec</goal>
                                </goals>
                                <configuration>
                                    <executable>java</executable>
                                    <arguments>
                                        <argument>-classpath</argument>
                                        <classpath/>
                                        <argument>com.agimatec.dbmigrate.AutoMigrationTool</argument>
                                        <argument>-base</argument>
                                        <argument>cp://db/</argument>
                                        <argument>-conf</argument>
                                        <argument>dbmigrate.xml</argument>
                                    </arguments>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
...
```

In the src/main/resources/db folder add:

file dbmigrate.xml
```
<?xml version="1.0" encoding="UTF-8"?>

<config name="migration">
    <map name="env">
        <String name="DB_USER" value="root"/>
        <String name="DB_PASSWORD" value=""/>
        <String name="DB_DRIVER" value="com.mysql.jdbc.Driver"/>
        <String name="DB_URL" value="jdbc:mysql://localhost:3306/mysqlexample_db"/>
    </map>
    <file name="Scripts-After-All" dir="check" file=""/>
</config>
```

in src/main/resources/db/check folder add:

file 1.0\_check.xml
```
<?xml version="1.0" encoding="UTF-8"?>

<config>
    <map name="env">
        <ArrayList name="scripts">
            <String value="cp://db/setup/structure.sql"/>
            <!-- add more script urls here to create the database scheme -->
        </ArrayList>
    </map>

    <list name="Operations">
        <text name="checkSchemaComplete" value="mysql,scripts"/>
    </list>
</config>
```


The sql script to create the database is stored in
src/main/resources/db/setup/structure.sql:
```
  
CREATE TABLE mytable (
  `id` varchar(100) not null,
  dtcreated datetime,
  dtupdated datetime,
  updated_by varchar(32),
  created_by varchar(32),
  PRIMARY KEY (`id`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

...
```


invoke with:

mvn process-test-resources -Pcheckdb

That's it. You do not need a db\_version table or anything, because dbmigrate is just used to validate the schema against the scripts.
No runtime dependencies are need in your project, other can the jar containing the jdbc driver.

### How to check multiple databases? ###
There is only 1 connection at a time. You can reconnect to a different database or change the user in .sql scripts:

src/main/resources/db/check/2.0\_reconnect.sql
```
CONNECT ${DB_USER}/${DB_PASSWORD}@otherdb_db;
```

DB\_USER and DB\_PASSWORD: are properties defined in dbmigrate.xml or given as JVM system properties


# More examples #

Example pom.xml:

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>de.viaboxx.dbmigrate</groupId>
    <artifactId>example</artifactId>
    <packaging>jar</packaging>
    <name>Integrate dbmigrate into a maven pom</name>

    <!-- 
         you can pass the property to dbmigrate to 
         determine the db_version after a new setup of the database 
     -->
    <properties>
        <database-version>1.0.1</database-version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>8.4-702.jdbc4</version>
        </dependency>

	<dependency>
            <groupId>de.viaboxx</groupId>
            <artifactId>dbmigrate</artifactId>
            <version>2.5.12</version>
        </dependency>
    </dependencies>


    <profiles>
        <profile>
            <!--
                The Profile "reset-db" is used on continues-integration builds
                to:
                - drop the schema and create all tables to have a clean and up-to-date
                  database for integration tests.
                  
                It demonstrates one possible integration of dbmigrate with the "java" goal
            -->
            <id>reset-db</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.codehaus.mojo</groupId>
                        <artifactId>exec-maven-plugin</artifactId>
                        <version>1.2.1</version>
                        <executions>

                            <execution>
                                <phase>process-test-resources</phase>
                                <goals>
                                    <goal>java</goal>
                                </goals>
                                <configuration>
                                   <systemProperties>
                                        <systemProperty>
                                            <key>to-version</key>
                                            <value>${database-version}</value>
                                        </systemProperty>
                                    </systemProperties> <mainClass>com.agimatec.dbmigrate.AutoMigrationTool</mainClass>
                                    <arguments>
                                        <argument>-conf</argument>
                                        <argument>etc/dbmigrate/db-reset.xml</argument>
                                        <argument>-exit</argument>
                                        <argument>false</argument>
                                    </arguments>
                                </configuration>
                            </execution>
                       </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
        <profile>
            <!--
                The Profile "develop-db" is used when a developer wants to
                - create the database/role/user/schema the first time or after a schema-change
                
                It demonstrates one possible integration of dbmigrate with the "exec" goal and another configuration.
            -->
            <id>develop-db</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.codehaus.mojo</groupId>
                        <artifactId>exec-maven-plugin</artifactId>
                        <version>1.2.1</version>
                        <executions>
                            <execution>
                                <phase>process-test-resources</phase>
                                <goals>
                                    <goal>exec</goal>
                                </goals>
                                <configuration>
                                    <systemProperties>
                                        <systemProperty>
                                            <key>to-version</key>
                                            <value>${database-version}</value>
                                        </systemProperty>
                                    </systemProperties>
                                    <executable>java</executable>
                                    <arguments>
                                        <argument>-classpath</argument>
                                        <classpath/>
                                        <argument>com.agimatec.dbmigrate.AutoMigrationTool</argument>
                                        <argument>-conf</argument>
                                        <argument>etc/dbmigrate/db-setup.xml</argument>
                                    </arguments>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>

</project>

```


  * the to-version system property will be used by dbmigrate to set the version in the database after the scripts have been executed

Example for db-setup.xml configuration file
```
<?xml version="1.0" encoding="UTF-8"?>

<config name="migration">

    <!-- Optional: environment variables, can be used inside scripts as ${DB_USER}.
     Predefined keys:
      DB_USER       : if exists, overrule user from JdbcConfig
      DB_PASSWORD   : if exists, overrule password from JdbcConfig
      DB_SCHEMA     : if exists, overrule schema name of jdbcUrl from JdbcConfig
      DB_URL        : if exists, overrule jdbcUrl from JdbcConfig
      DB_DRIVER        : if exists, overrule jdbcDriver from JdbcConfig
    -->
    <map name="env">
        <String name="DB_USER" value="postgres"/>
        <String name="DB_PASSWORD" value="syspassword"/>
        <String name="DB_NAME" value="mydatabase"/>

        <String name="DB_USER_MYDB" value="mydb"/>
        <String name="DB_PASSWORD_MYDB" value="mypassword"/>
        <String name="DB_DRIVER" value="org.postgresql.Driver"/>
        <String name="DB_URL" value="jdbc:postgresql://localhost:5432/postgres"/>
    </map>

    <!-- Scripts = the source path for scripts to scan for -->
    <file name="Scripts-Before-All" dir="cp://database/setup" file=""/>
    <file name="Scripts-After-All" dir="cp://dbmigrate/verify" file=""/>

    <map name="version-meta">
        <Boolean name="auto-version" value="false"/>
    </map>
</config>

```

example for the setup script 0.0.1\_setup.sql in resource directory "database/setup" to create a new database and setup its schema:
```
-- SQL SCRIPT FOR dbmigrate TO CREATE DATABASE AND SETUP ROLES, USERS, SCHEMA 

SET FAIL_ON_ERROR=false;

@../create-role.sql;

CONNECT ${DB_USER_MYDB}/${DB_PASSWORD_MYDB};

@../create-database.sql;

CONNECT ${DB_USER_MYDB}/${DB_PASSWORD_MYDB}@${DB_NAME};

@../drop-schema.sql;

SET FAIL_ON_ERROR=true;
@../schema.sql;

-- #version(${to-version});

```

  * create-role.sql, create-database.sql, drop-schema.sql and schema.sql
> are standard sql scripts (manually written or created by hibernate/any persistence framework) to create the user, database and tables. In this example the scripts are located in the parent directory "database"