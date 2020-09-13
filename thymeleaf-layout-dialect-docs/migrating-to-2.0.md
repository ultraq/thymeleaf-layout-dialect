---
layout: default
title: Migrating to 2.0
nav_order: 4
redirect_from:
  - /MigrationGuide.html
---

Migrating to 2.0
================
{: .no_toc }

The biggest change between 1.x and 2.x of the layout dialect is that 2.x is a
rewrite of the layout dialect to support Thymeleaf 3.  Thymeleaf 3 is largely
backwards compatible with Thymeleaf 2 templates, and so the layout dialect has
gone out of its way to be backwards compatible too.

As such, everything in this guide is *completely optional*.  However, if you
want to future-proof your code and keep your logs clean of deprecation warnings,
then follow the steps in this guide.


On this page
------------
{: .no_toc .text-delta }

1. TOC
{:toc}


`decorator` processor renamed to `decorate`
-------------------------------------------

[thymeleaf-layout-dialect/issues/95](https://github.com/ultraq/thymeleaf-layout-dialect/issues/95)

While the layout dialect does perform decoration as per the [decorator pattern](https://en.wikipedia.org/wiki/Decorator_pattern),
throughout 1.x it incorrectly identified the layout/parent template as the
decorator, when instead, according to the design pattern, the extension (content
template in this case) is the decorator.

This change is simply a rename of the `layout:decorator` processor to `layout:decorate`
so that the template being specified is the one being decorated, not the
decorator, plus an overhaul of the documentation to fix this inconsistency.


`$DECORATOR_TITLE` renamed to `$LAYOUT_TITLE`
---------------------------------------------

A consequence of the above is that the special tokens in the `title-pattern`
processor were also incorrectly named, and so a new token has been introduced to
fix this.


Deprecated `include`, introduced `insert`
-----------------------------------------

[thymeleaf-layout-dialect/issues/107](https://github.com/ultraq/thymeleaf-layout-dialect/issues/107)

Thymeleaf 3 deprecated the `th:include` processor and introduced the `th:insert`
processor as its replacement.  Because the layout dialect patterned the naming
of its template inclusion processors after Thymeleaf, it did the same -
deprecating `layout:include` and introducing `layout:insert`.


Thymeleaf 3 fragment processors
-------------------------------

[thymeleaf/issues/451](https://github.com/thymeleaf/thymeleaf/issues/451)

In 1.x of the layout dialect, it used the same template/fragment specifiers as
Thymeleaf 2, which was basically string paths with a special `::` separator.  In
Thymeleaf 3, these specifiers have been formalized into "fragment expressions",
and have a new syntax which surrounds the old one with `~{...}`.

The layout dialect now favours the use of these fragment expressions for all
processors that pick a template or fragment.
