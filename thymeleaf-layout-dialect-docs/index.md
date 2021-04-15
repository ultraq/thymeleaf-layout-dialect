---
layout: default
title: Home
nav_order: 1
---

Thymeleaf Layout Dialect
========================
{: .no_toc }

A dialect for Thymeleaf that lets you build layouts and reusable templates in
order to improve code reuse.
{: .fs-6 .fw-300 }


On this page
------------
{: .no_toc .text-delta }

1. TOC
{:toc}


What does it do?
----------------

The Thymeleaf Layout Dialect adds the ability to decorate templates -
automatically for the `<head>` section of an HTML template, and explicitly
through extension points that developers can add to their templates.  This all
adds up to create layouts that can be extended in a manner similar to classical
inheritence.

For example, given a common `layout.html` file, set some shared static assets in
the `<head>` and define extension points in the body with the `layout:fragment`
processor:

```html
<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
  <title>Layout page</title>
  <script src="common-script.js"></script>
</head>
<body>
  <header>
    <h1>My website</h1>
  </header>
  <section layout:fragment="content">
    <p>Page content goes here</p>
  </section>
</body>
</html>
```

Create a content template that will define its own title, static resources, and
replacements for those extension points.  Link the page to the layout by using
the `layout:decorate` processor at the root element of the page which will
instruct Thymeleaf to decorate the layout with this template:

```html
<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
  layout:decorate="~{layout.html}">
<head>
  <title>Content page</title>
  <script src="content-script.js"></script>
</head>
<body>
  <section layout:fragment="content">
    <p>This is a paragraph from the content page</p>
  </section>
</body>
</html>
```

When Thymeleaf processes your content template, the resulting HTML will be:

```html
<!DOCTYPE html>
<html>
<head>
  <title>Content page</title>
  <script src="common-script.js"></script>
  <script src="content-script.js"></script>
</head>
<body>
  <header>
    <h1>My website</h1>
  </header>
  <section>
    <p>This is a paragraph from the content page</p>
  </section>
</body>
</html>
```


Learn more
----------

Check out the [getting started]({{ site.baseurl }}{% link getting-started.md %})
guide to learn how to add the layout dialect to your Thymeleaf project, or the
[processors]({{ site.baseurl}}{% link processors/index.md %}) pages for in-depth
examples of each of the layout dialect features and how they can help you build
your templates.
