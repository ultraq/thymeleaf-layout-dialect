---
layout: default
title: Migrating to 4.0
nav_order: 6
---

Migrating to 4.0
================
{: .no_toc }

Version 4.0 of the layout dialect is made to align with Spring Boot 4's build
and dependency targets, and so is built for Java 17, Groovy 5, and Gradle 9 (for
building the project).


On this page
------------
{: .no_toc .text-delta }

1. TOC
{:toc}


Adopt Spring Boot 4 targets
---------------------------

This has led to a slimming-down of the codebase and an upgrade of the build
tools as the new Java 17 baseline allowed for the removal of old Java 8
workarounds.  Performance is also slightly faster on the internal benchmark,
most likely attributed to Java 17.

This also means targeting Groovy 5, which recently had a regression that caused
projects compiled against Groovy 4 (like 3.x of the layout dialect) to error
([thymeleaf-layout-dialect/issues/251](https://github.com/ultraq/thymeleaf-layout-dialect/issues/251)).
This upgrade should mitigate that kind of regression in future.


Deprecated `layout:collect` processor has been deleted
------------------------------------------------------

This was deprecated back in 3.0 as it was too special-purpose and generally
harder to maintain, particularly with deeper layout hierarchies.  It is now
deleted.


The configuration constructors have been deprecated
---------------------------------------------------

As the number of options grows, the old way of configuring the layout dialect
via one of several constructors became more confusing.  When the most recent
option was added, a fluent configuration API was also added, and with 4.0 these
fluent configuration methods are now the preferred way to configure the layout
dialect. eg:

```java
// Old way
var layoutDialect = new LayoutDialect(new GroupingStrategy(), false);

// New way
var layoutDialect = new LayoutDialect()
  .withSortingStrategy(new GroupingStrategy())
  .withAutoHeadMerging(false);
```

Documentation of the configuration constructors has been replaced with the
fluent API, and the constructors themselves will be removed in the next major
version of the layout dialect.
