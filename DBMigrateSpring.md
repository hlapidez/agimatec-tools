# Introduction #

You can invoke dbmigrate during startup of your application (e.g. a web-app) and configure it with the spring framework.


# Details #

The class **de.viaboxx.dbmigrate.spring.DBMigrateBean** offers spring integration.


After spring has configured the bean, it invokes the AutoMigrationTool that does the job. If it fails, the propagated exception causes spring to fail which stops your app service.

Add spring-dependencies to your project (e.g. maven pom.xml):

```
        <dependency>
            <groupId>commons-dbcp</groupId>
            <artifactId>commons-dbcp</artifactId>
            <version>1.4</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-beans</artifactId>
            <version>3.2.0.RELEASE</version>
        </dependency>

       <!-- if you want to use spring-data -->
        <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-jpa</artifactId>
            <version>1.3.0.RELEASE</version>
        </dependency>
```

  * dependency to commons-dbcp is optional. Use it if you want to configure the connection to the database for dbmigrate with a apache.commons.dbcp BasicDataSource

Example for a spring-configuration with dbmigrate:
```
    
    <bean id="dbmigrate" class="de.viaboxx.dbmigrate.spring.DBMigrateBean">
        <property name="configRootUrl" value="cp://dbmigrate/"/>
        <property name="configFile" value="db-upgrade.xml"/>
        <property name="dataSource" ref="dataSource"/>
        <property name="disabled" value="false"/>
        <property name="stopOnException" value="true"/> 
    </bean>     

    <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
        <property name="driverClassName" value="org.postgresql.Driver"/>
        <property name="url" value="${database-url}"/>
        <property name="username" value="${database-user}"/>
        <property name="password" value="${database-password}"/>
        <property name="defaultAutoCommit" value="false"/>
    </bean>

```

  * You can name the bean "dbmigrate" or however you like.
  * You can even configure multiple DBMigrateBeans to migrate different databases.
  * Example assumes that configFile is located in classpath resource folder "dbmigrate"

### Properties of DBMigrateBean ###
for more details, see source code
  * disabled = disable running the migration tool (false by default)
  * stopOnException = throw exception to let spring fail when an exception occurs during dbmigrate (true by default
  * configFile = the xml configration for migration (default migration.xml)
  * simulation = true/false to switch simulation mode (see -sim switch), default is false
  * configRootUrl = set the URL as root for all paths of dbmigrate (default is file: e.g. the current directory)
  * toVersion = if you want to set the migration to-version (optional)
  * fromVersion = if you wan to set the migration from-version (optional)
  * environment = a map of key-values for the "env" section of the migration configuration
  * migrateConfig = a map of key-values for the migration configration (if you do not want a xml configuration at all)
  * dataSource = a DataSource to configure the db-connection, alternatively you can configure it with env entries DB\_USER, DB\_PASSWORD, DB\_URL and DB\_DRIVER (see config documentation). Currently only org.apache.commons.dbcp.BasicDataSource or subclasses are supported.


# A real best-practise example #
After having done several projects with dbmigrate and the springframework, this is the way, I like the integration & configuration best:

  * configure a datasource
  * configure a dbmigrate bean
  * use properties with spring and configure in spring as much as possible
  * the spring-configuration provides the setup-option to recreate your databases/users etc from scratch and the upgrade-option to migrate to the lastest schema version
  * example for postgres. similar configuration for mysql possible
  * I assume, your application is using the default directory layout of a maven java project

## Configuration ##

1. file spring-context.xml in resources/META-INF/spring
```
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:p="http://www.springframework.org/schema/p"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:util="http://www.springframework.org/schema/util"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd">

  <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close"
          p:driverClassName="org.postgresql.Driver"
          p:url="${app-jdbc-url}"
          p:username="${app-jdbc-user}" p:password="${app-jdbc-password}"
          p:defaultAutoCommit="false"/>

  <!-- dbmigrate integration -->
    <bean id="dbmigrate" class="de.viaboxx.dbmigrate.spring.DBMigrateBean"
          p:dataSource-ref="dataSource" p:configRootUrl="cp://database/"
          p:disabled="${dbmigrate-disabled:false}" p:stopOnException="${dbmigrate-stopOnException:true}"
          p:configFile="${dbmigrate-env:upgrade}.xml"
          p:environment-ref="dbmigrate_${dbmigrate-env:upgrade}">
    </bean>

  <!-- properties for setup of database. this shows how to connect with an admin-user and create the user and database first --> 
   <util:map id="dbmigrate_setup">
        <entry key="DB_URL" value="${app-jdbc-url}"/>
        <entry key="DB_SCHEMA" value="${dbmigrate-schema:postgres}"/>
        <entry key="DB_USER" value="${dbmigrate-jdbc-user:postgres}"/>
        <entry key="DB_PASSWORD" value="${dbmigrate-jdbc-password:admin}"/>

        <entry key="APP_DB" value="${app-db:appdb}"/>
        <entry key="APP_USER" value="${app-jdbc-user}"/>
        <entry key="APP_PASSWORD" value="${app-jdbc-password}"/>
    </util:map>

   <!-- properties for upgrade of database -->
    <util:map id="dbmigrate_upgrade"/>


   <!-- this is standard hibernate/jpa configuration and shows how to ensure that the entityManagerFactory is created AFTER migration -->
   <bean id="entityManagerFactory" depends-on="dbmigrate"
          class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean"
          p:dataSource-ref="dataSource"
          p:jpaVendorAdapter-ref="jpaVendorAdapter"
          p:jpaPropertyMap-ref="jpaPropertyMap"
          p:packagesToScan="yourpackage.database.domain"/>

    <util:map id="jpaPropertyMap">
        <entry key="hibernate.dialect" value="org.hibernate.dialect.PostgreSQL82Dialect"/>
    </util:map>

    <bean id="jpaVendorAdapter" class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter"
          p:showSql="true"/>

</beans>
```

2. file upgrade.xml in resources/database
```
<?xml version="1.0" encoding="UTF-8"?>

<config name="migration">
    <map name="env"/>

    <file name="Scripts" dir="upgrade" file=""/>
    <file name="Scripts-After-All" dir="verify" file=""/>
    <map name="version-meta">
        <Boolean name="auto-version" value="true"/>
    </map>
</config>
```


3. file setup.xml in resources/database
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
        <String name="DB_DRIVER" value="org.postgresql.Driver"/>
        <String name="DB_VERSION" value="1.0"/>
    </map>

    <!-- Scripts = the source path for scripts to scan for -->
    <file name="Scripts-Before-All" dir="setup" file=""/>
    <file name="Scripts-After-All" dir="verify" file=""/>

    <map name="version-meta">
        <Boolean name="auto-version" value="false"/>
    </map>
</config>
```

When you make changes on your database, create upgrade scripts in folder resources/database/upgrade and
```
##################################################################
### Do not forget to set/update the DB_VERSION in 'setup.xml'! ###
##################################################################
```
4. script to create user and database in resources/database/setup

file 0.0.1\_create\_database.sql
```

SET FAIL_ON_ERROR=false;

CREATE ROLE ${APP_USER} LOGIN PASSWORD '${APP_PASSWORD}' NOSUPERUSER INHERIT CREATEDB NOCREATEROLE;

SET FAIL_ON_ERROR=true;

CONNECT ${APP_USER}/${APP_PASSWORD};

SET FAIL_ON_ERROR=false;
-- now creating database ${APP_DB}
CREATE DATABASE ${APP_DB} encoding 'UTF-8' template template0;

SET FAIL_ON_ERROR=true;

-- now connect to the new database
CONNECT ${APP_USER}/${APP_PASSWORD}@${APP_DB};

SET FAIL_ON_ERROR=false;
DROP TABLE IF EXISTS db_version;
@../drop-schema.sql;

SET FAIL_ON_ERROR=true;
@../schema.sql;

@../create_table_db_version.sql;


-- execute more scripts to insert intial data here
-- @../insert-initial-data.sql;

-- #version(${DB_VERSION});
```


and the script create\_table\_db\_version.sql in resources/database
```
CREATE TABLE db_version (
  version varchar(100) not null,
  since timestamp not null,
  PRIMARY KEY (version));
```

5. OPTIONAL: verify-configuration to check the database schema after setup and upgrade

file resources/database/verify/1.0.0\_check.xml

```
<?xml version="1.0" encoding="UTF-8"?>

<config>
    <map name="env">
        <ArrayList name="schema-files">
            <String value="cp://database/schema.sql"/>
            <String value="cp://database/dbversion.sql"/>
        </ArrayList>
    </map>

    <list name="Operations">
        <text name="checkSchemaComplete" value="postgres,schema-files"/>
    </list>
</config>
```

6. properties

you can configure the properties used in the spring-context.xml with a PropertyPlaceholderConfigurer (springframework)

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="placeholderProperties" class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
        <property name="locations">
            <list>
                <value>classpath:app.properties</value>
                <value>file:${app-settings:app-local.properties}</value>
            </list>
        </property>
        <property name="systemPropertiesModeName" value="SYSTEM_PROPERTIES_MODE_OVERRIDE"/>
        <property name="ignoreResourceNotFound" value="true"/>
        <property name="ignoreUnresolvablePlaceholders" value="false"/>
        <property name="order" value="1"/>
    </bean>

</beans>
```

the file 'app.properties' contains the default values, if you do not overrule them by JVM properties when starting your Application

file resources/app.properties
```
app-jdbc-url=jdbc:postgresql://localhost:5432/appdb
app-jdbc-user=appuser
app-jdbc-password=secret
```

Other properties used in spring that do not appear here (like dbmigrate-jdbc-password) are optional, because their default value is provided after the ':' => ${dbmigrate-jdbc-password:admin} default value is 'admin' for the property 'dbmigrate-jdbc-password'


## Usage ##

1. first start: setup database
Before starting the app the first time, the database must be created:
> Run your app with -Ddbmigrate-env=setup

2. normal start: migrate database
When the app starts normally, it migrates the database to the latest version:
> Run your app with -Ddbmigrate-env=upgrade
```
#########################################
### 'upgrade' is the default behavior ###
#########################################
```