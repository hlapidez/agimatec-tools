# annomark #

## March 2013 ##

"annomark" will NOT be developed any longer.

## 2.5.0 (July 2011) ##
  * Changed artifactId from com.agimatec to de.viaboxx
  * Published artifacts to maven central.


## 2.4.0 (2912.2010) ##
  * NLSTools moved to a separate subproject with own jar nlsTools.jar

## 2.3.3 ##
  * NLSTools support for Adobe Flex (generate Flex class, support Flex directory format)
  * MessageBundleTask: new option writeInterface "Flex" and "smallFlex"
  * OptimizeBundlesTask: new option "deleteEmptyEntries", "commonEntryKeyPrefix" may be null

## 2.2.7 (16.12.2009) ##
doctype for dozer5.0 fixed
3rd party libs upgraded

  * minor bug fixes
  * fixed method getDtoFieldType() for nested path with generic type
  * OptmizeBundesTask
  * NLS-Entry useDefault
  * enum-support improved

## 2.2.5 (24.03.2009) ##
  * bug fixed: DTO-annotated boolean getter methods starting with "is"
  * new NLS-tasks: CopyBundlesTask, UpdateBundlesTask, MBBundlesZipper,
> > simplified bundles-xml format (providing backward compatibility)

## 2.2.2 ##
  * upgrade from groovy-1.0-jsr-06 to groovy-1.5.6
  * TestDocumentation Annotation
  * Added profile to adjust the jdk tools path used on mac os x

## 2.2.1-SNAPSHOT (checkout sources) ##
  * @ToString annotation to generate toString() methods (+examples)

## 2.1.3 (june 2008) ##
  * Generate files based on annotated java classes
  * Generate java, json, properties, sql for Resourcebundles in XML-format
  * Generate Resourcebundle in XML-format from properties files