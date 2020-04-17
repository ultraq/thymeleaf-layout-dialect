
Thymeleaf Layout Dialect
========================

[![Build Status](https://travis-ci.org/ultraq/thymeleaf-layout-dialect.svg)](https://travis-ci.org/ultraq/thymeleaf-layout-dialect)
[![Coverage Status](https://coveralls.io/repos/github/ultraq/thymeleaf-layout-dialect/badge.svg?branch=master)](https://coveralls.io/github/ultraq/thymeleaf-layout-dialect?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/nz.net.ultraq.thymeleaf/thymeleaf-layout-dialect.svg?maxAge=3600)](http://search.maven.org/#search|ga|1|g%3A%22nz.net.ultraq.thymeleaf%22%20AND%20a%3A%22thymeleaf-layout-dialect%22)

A dialect for Thymeleaf that lets you build layouts and reusable templates in
order to improve code reuse.


Documentation
-------------

All of the latest documentation on the layout dialect can be found on the
dedicated docs website at https://ultraq.github.io/thymeleaf-layout-dialect/

For version 1 docs which supported Thymeleaf 2, the classic readme still exists
over on https://github.com/ultraq/thymeleaf-layout-dialect/tree/1.4.0


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

Check out the [getting started](https://ultraq.github.io/thymeleaf-layout-dialect/getting-started)
guide to learn how to add the layout dialect to your Thymeleaf project, or the
[examples](https://ultraq.github.io/thymeleaf-layout-dialect/examples/) for more
ways of how the layout dialect can help you build your templates.
