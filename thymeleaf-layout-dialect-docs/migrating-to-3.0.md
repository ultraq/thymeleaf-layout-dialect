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
in Java 17) and the deletion of code that has been deprecated in version 2.0.
As such, if you never encountered logs about deprecations using version 2.0,
then there's likely nothing you need to do to upgrade to version 3.0!

All the changes are listed below to help make the assessment on what an upgrade
would entail for you.

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
