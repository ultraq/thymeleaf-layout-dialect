---
layout: default
title: fragment
parent: Processors
nav_order: 5
---

fragment
========

 - XML attribute: `layout:fragment`
 - Data attribute: `data-layout-fragment`

The glue that holds everything together; it marks sections in your layout or
reusable templates that can be replaced by sections which share the same name.

```html
<div layout:fragment="fragment-name">
```

Fragment names must be unique within a template, otherwise fragment mismatches
can occur and all sorts of hilarity (ie: errors) can ensue.
