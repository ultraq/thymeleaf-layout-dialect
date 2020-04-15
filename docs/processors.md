---
layout: default
title: Processors
nav_order: 3
---

Processors
==========
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}


decorate
--------

 - XML attribute: `layout:decorate`
 - Data attribute: `data-layout-decorate`

Used in your content templates and declared in the root tag (usually `<html>`),
this processor takes a [fragment expression](https://github.com/thymeleaf/thymeleaf/issues/451)
that specifies the layout template to decorate with the current, content
template.

Check out the [Layouts](examples.md#layouts) example for how to decorate a
layout template with your content templates.


title-pattern
-------------

 - XML attribute: `layout:title-pattern`
 - Data attribute: `data-layout-title-pattern`

Allows for greater control of the resulting `<title>` element by specifying a
pattern with some special tokens.  This can be used to extend the layout's title
with the content's one, instead of simply overriding it.

Check out the [Configuring your Title](examples.md#configuring-your-title)
example for how to control the final title of your page.


insert
------

 - XML attribute: `layout:insert`
 - Data attribute: `data-layout-insert`

Similar to Thymeleaf's `th:insert`, but allows the passing of entire element
fragments to the inserted template.  Useful if you have some HTML that you want
to reuse, but whose contents are too complex to determine or construct with
context variables alone.

Check out the [Reusable templates](examples.md#reusable-templates) example for
how to pass HTML code to the templates you want to insert.


replace
-------

 - XML attribute: `layout:replace`
 - Data attribute: `data-layout-replace`

Similar to `layout:insert` in that you can pass HTML content to the
template/fragments you're replacing, but with the behaviour of Thymeleaf's `th:replace`.


fragment
--------

 - XML attribute: `layout:fragment`
 - Data attribute: `data-layout-fragment`

The glue that holds everything together; it marks sections in your layout or
reusable templates that can be replaced by sections which share the same name.
