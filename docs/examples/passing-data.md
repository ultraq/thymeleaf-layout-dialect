---
layout: default
title: Passing data to templates
parent: Examples
nav_order: 2
---

Passing data to templates
=========================

Since locating templates is done using standard Thymeleaf fragment expressions,
these can also be used to pass data from child templates up to their parent
layouts, eg:

Child/content template:

```html
<html layout:decorate="~{your-layout(greeting='Hello!')}">
```

Parent/layout template:

```html
<html>
  ...
  <p th:text="${greeting}"></p> <!-- You'll end up with "Hello!" in here -->
```

> Parameters passed this way *must* be named - an exception will be thrown if
> names are missing.

This feature is available from Thymeleaf Layout Dialect 2.4.0.  For older
versions, you can continue to use `th:with`/`data-th-with`:

```html
<html layout:decorate="~{your-layout}" th:with="greeting='Hello!'">
```
