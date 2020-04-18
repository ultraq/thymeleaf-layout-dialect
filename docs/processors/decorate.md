---
layout: default
title: decorate
permalink: /processors/decorate
parent: Processors
nav_order: 1
---

decorate
========

 - XML attribute: `layout:decorate`
 - Data attribute: `data-layout-decorate`

Used in your content templates and declared in the root tag (usually `<html>`),
this processor takes a [fragment expression](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#fragments)
that specifies the layout template to decorate with the content template.

```html
<html layout:decorate="~{layout.html}>"
```

Check out the [Layouts](docs/examples/index.md#layouts) example for how to decorate a
layout template with your content templates.
