# Introduction #

You can find the latest version on maven-central:
<a href='http://search.maven.org/#search|ga|1|dbmigrate'>here</a>

**Why database migration?**

The flyway framework is a good alternative for dbmigrate, too.
It also explains the motivation for such a framework:
  * http://flywaydb.org/getstarted/whyDatabaseMigrations.html



# Integrating dbmigrate into your project #

  * [maven integration](DBMigrateMaven.md)
> If you want to invoke dbmigrate from maven or avoid runtime dependencies with your code.
  * [spring integration](DBMigrateSpring.md)
> If you want you app to automatically migrate the database if required during startup using dbmigrate.

## Tipp ##
You probably want to have both maven + spring integration!