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
