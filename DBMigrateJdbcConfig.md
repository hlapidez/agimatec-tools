# Examples #
## postgres.properties ##

```
jdbcUrl=jdbc:postgresql://localhost:5432/test
jdbcDriver=org.postgresql.Driver
```

## oracle.properties ##

```
jdbcUrl=jdbc:oracle:thin:@localhost:1521:XE
jdbcDriver=oracle.jdbc.driver.OracleDriver
user=system
password=manager
```


The settings in this file are overwritten by the environment variables DB\_USER (overwrite user), DB\_PASSWORD (overwrite password)

Driver class must exist in the classpath.