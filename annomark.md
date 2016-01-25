March 2013

"annomark" will NOT be developed any longer after release 2.5.11.

# Introduction #


"annomark" is a tool that combines ["annogen"](http://annogen.codehaus.org/) and ["freemarker"](http://freemarker.sourceforge.net/). This combination allows to process your classes with your annotations without the need for low-level java-programming with the annogen and freemarker APIs.
You just annotate your classes (with predefined or own annotations) and use one of the predefined freemarker templates (or create your own templates) to generate one or more output files.
With the templates already provided, you can:
  * generate POJOs as Transfer-Objects (template: java-pojo.ftl)
  * generate [Dozer](http://dozer.sourceforge.net/documentation/gettingstarted.html)-Mappings for the Transfer-Objects (template: dozer-mapping.ftl)
  * generate xml-beaninfos for the [agimatec-validation framework](http://code.google.com/p/agimatec-validation/) from javax.persistence annotations.

![http://agimatec-tools.googlecode.com/svn/wiki/agimatec-tools-c.jpg](http://agimatec-tools.googlecode.com/svn/wiki/agimatec-tools-c.jpg)

_**Hint:** the entity classes with javax.persistence-annotations can be generated
with the CatalogGeneratorTool from your sql schema (template ejb3-prototype.ftl)._

Create your own templates, e.g. for struts-validation from your Form-Classes, for WSDL from your classes, GUI layouts, documentation, Interfaces, business-rules and constraints . We appreciate your ideas and input to enhance "annomark".

## Reference annomark in maven pom.xml ##
```
  <dependency>
    <groupId>de.viaboxx</groupId>
    <artifactId>annomark</artifactId>
    <version>2.5.11</version>
  </dependency>
```

# Annomark #
  * [JavaDoc](http://agimatec-tools.googlecode.com/svn/trunk/javadoc/annomark/index.html)
  * [Getting started example](AnnomarkExample.md)

_**Hints:**_
  * _you need annogen-0.1.1. If you cannot find it on official annogen-sides, refer to the download-section of agimatec-tools._
  * _There might be logged errors and warnings when using annogen. It works nevertheless._
  * _annogen-0.1.1 cannot parse static imports. Avoid them when you want your classes processed._


In the annomark.jar are some ant-Tasks. Documentation here: [NLSTools](NLSTools.md)