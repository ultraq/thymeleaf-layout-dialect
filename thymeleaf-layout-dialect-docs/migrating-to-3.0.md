---
layout: default
title: Migrating to 3.0
nav_order: 5
---

Migrating to 3.0
================
{: .no_toc }

Version 3.0 of the layout dialect is largely an upgrade to use Groovy 3.0 which
removes the 'reflective access warning' in Java (which is set to become an error
in Java 17) and the deletion of code that has been deprecated in version 2.x.
It's also a step towards becoming a full Java module, applying an automatic
module name and doing some reorganizing of packages in the interim.

All the changes are listed below to help you make an assessment of what an
upgrade would entail.

On this page
------------
{: .no_toc .text-delta }

1. TOC
{:toc}


Deprecated `decorator` processor has been deleted
-------------------------------------------------

This was deprecated back in 2.0 ([thymeleaf-layout-dialect/issues/95](https://github.com/ultraq/thymeleaf-layout-dialect/issues/95))
in favour of `decorate` as the naming was misleading, and has now been deleted.


Deprecated `$DECORATOR_TITLE` constant has been deleted
-------------------------------------------------------

Similar to the change above, another poorly-named constant that was deprecated
is now deleted.


`<head>` merging strategies now respect the `<title>` position by default
-------------------------------------------------------------------------

The default behaviour of the layout dialect has historically been to place the
`<title>` element at the beginning of the `<head>` element during the decoration
process; an arbitrary design decision which made development of this library
easier.  However, this runs against the expectations of developers who wished to
control the order of elements, most notably the practice of putting
`<meta charset...>` as the first element in the `<head>`.

In 2.4.0, new `<head>` merging strategies were introduced ([thymeleaf-layout-dialect/issues/177](https://github.com/ultraq/thymeleaf-layout-dialect/issues/177),
`AppendingRespectLayoutTitleStrategy` and `GroupingRespectLayoutTitleStrategy`)
to keep `<title>`s wherever they exist within the target/layout template being
decorated, and then work on everything else as normal.

These strategies have been renamed to replace the old strategies (`AppendingRespectLayoutTitleStrategy
-> AppendingStrategy` and `GroupingRespectLayoutTitleStrategy -> GroupingStrategy`)
and the old strategies have been removed.


Deprecated `layout:collect` with no replacement
-----------------------------------------------

This was a feature added in 2.3.0 ([#166](https://github.com/ultraq/thymeleaf-layout-dialect/pull/166)).
However, it has complicated the maintenance of the layout dialect, and, after
some time with it, feels like a very special purpose tool that this library
doesn't need to cover.  As such, I am deprecating this feature.


Automatic module name and package restructure
---------------------------------------------

As part of the work to convert the layout dialect into a full Java module ([#171](https://github.com/ultraq/thymeleaf-layout-dialect/issues/171)),
I've taken the steps of setting an `Automatic-Module-Name` in the manifest file,
and reorganizing packages to meet the standards set out by Java modules.

This module now has the name of `nz.net.ultraq.thymeleaf.layoutdialect`, and the
base package (previously `nz.net.ultraq.thymeleaf`) has been changed to match.

As a consequence of this move, if you use Spring Boot to automatically configure
the layout dialect, you will now need to specify it yourself until Spring Boot
is updated to support version 3 of the layout dialect.  To do that, add the
following to any of your Spring Boot configuration classes:

```java
@Bean
public LayoutDialect layoutDialect() {
  return new LayoutDialect();
}
```
