---
layout: default
title: Getting started
permalink: /getting-started
nav_order: 2
---

Getting started
===============
{: .no_toc }


On this page
------------
{: .no_toc .text-delta }

1. TOC
{:toc}


Adding to your project
----------------------

A minimum of Java 7 and Thymeleaf 3.0 is required.  Add the layout dialect by
configuring Maven or your Maven-compatible dependency manager to the following
co-ordinates:

 - GroupId: `nz.net.ultraq.thymeleaf`
 - ArtifactId: `thymeleaf-layout-dialect`
 - Version: `2.5.0`

Check the [project releases](https://github.com/ultraq/thymeleaf-layout-dialect/releases)
for a list of available versions.  Each release page also includes a
downloadable JAR if you want to manually add it to your project classpath.


Using in your application
-------------------------

Once downloaded/installed as part of your project, add the layout dialect to
your existing Thymeleaf template engine using one of the methods below:

 - Spring or Spring Boot 2 w/ Java/annotation config:
   ```java
   @Bean
   public LayoutDialect layoutDialect() {
     return new LayoutDialect();
   }
   ```

 - For those who are configuring their own Thymeleaf template engine:
   ```java
   TemplateEngine templateEngine = new TemplateEngine();
   templateEngine.addDialect(new LayoutDialect());
   ```

This will introduce the `layout` namespace, and 5 new attribute processors that
you can use in your templates: `decorate`, `title-pattern`, `insert`, `replace`,
and `fragment`.

Continue on to the [processors]({% link processors/index.md %}) section to
learn how to use these in your templates.
