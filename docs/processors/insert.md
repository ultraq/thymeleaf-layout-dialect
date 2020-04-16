---
layout: default
title: insert
parent: Processors
nav_order: 3
---

insert
======

 - XML attribute: `layout:insert`
 - Data attribute: `data-layout-insert`

Similar to Thymeleaf's `th:insert`, but allows the passing of entire element
fragments to the inserted template.  Useful if you have some HTML that you want
to reuse, but whose contents are too complex to determine or construct with
context variables alone.

Check out the [Reusable templates](docs/examples/examples.md#reusable-templates) example for
how to pass HTML code to the templates you want to insert.
