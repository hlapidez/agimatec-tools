# Using the annomark-example project #
This is a project that demonstrates the features of "annomark".
It creates some classes and xml files based on various annotations.

Some of the features demonstrated by the example project:
  * launch annomark with groovy (!JAMLauncher.groovy)
  * process own annotations (DTO, DTOs)
  * use all templates provided with annomark (POJO, Dozer-mapping, xml-metadata)
  * generate different objects for same class with relationships between them

## For SVN and maven2 users ##
1. Checkout the annomark-example from Source with SVN.

2. Build the project
> mvn clean package

3. Look into the target/generated/src/main directory

## For maven2 users that cannot checkout from SVN ##
1. Download annomark-example-project.zip and extract

2. Build the project
> mvn clean package

3. Look into the target/generated/src/main directory

## Annomark explained ##

1. The pom launches a groovy script that contains all instructions and configurations

_pom.xml:_
```
 <java classname="org.codehaus.gram.Gram" fork="true"
       failonerror="true">
   <classpath refid="maven.test.classpath"/>
   <arg value="${basedir}/src/main/java/com/agimatec"/>
   <arg value="src/main/groovy/JAMLauncher.groovy"/>
 </java>
```
  * _the first parameter is the source directory to parse_
  * _the second parameter is the groovy script to launch_

2. The groovy script configures annomark:

_JAMLancher.groovy_
```
import java.util.*;
import com.agimatec.annotations.jam.*;
import com.agimatec.annotations.*;

def dtoClasses = new HashSet();
dtoClasses.addAll(classes.findAll { it.getAnnotation(DTO.class) != null });
dtoClasses.addAll(classes.findAll { it.getAnnotation(DTOs.class) != null });

generator = new JAMDtoGenerator();
generator.setTemplateDir("../annomark/templates");

generator.addInstruction("java-pojo", "target/generated/src/main/java", null)
    .setUsageQualifier("Edit")
    .setPrefix("Transfer")
    .setSuffix(".java")
    .setDefaultPackage("com.agimatec.annomark.example.transfer");

// ... more instructions here ...    

generator.generate(dtoClasses);
```
  * _getAnnotation filters classes by annotations (here: DTO and DTOs). You can use own annotations as well._
  * _[JAMDtoGenerator](http://www.agimatec.de/documentation/public/agimatec-tools/site/annomark/apidocs/com/agimatec/annotations/jam/JAMDtoGenerator.html) assists in invoking freemarker to generate DataTransferObjects_
  * _refer to documentation of_ [JAMGenInstruction](http://www.agimatec.de/documentation/public/agimatec-tools/site/annomark/apidocs/com/agimatec/annotations/jam/JAMGenInstruction.html)

3. Study the example
  * _Take a look at the the annotated model classes and experiment with the @DTO, @DTOAttribute annotations if you need TransferObjects_
  * _Create own annotations or own templates to generate the artifacts you need_