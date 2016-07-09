
Thymeleaf Layout Dialect
========================

[![Build Status](https://travis-ci.org/ultraq/thymeleaf-layout-dialect.svg?branch=thymeleaf-3.0)](https://travis-ci.org/ultraq/thymeleaf-layout-dialect)
[![GitHub Release](https://img.shields.io/github/release/ultraq/thymeleaf-layout-dialect.svg?maxAge=3600)](https://github.com/ultraq/thymeleaf-layout-dialect/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/nz.net.ultraq.thymeleaf/thymeleaf-layout-dialect.svg?maxAge=3600)](http://search.maven.org/#search|ga|1|g%3A%22nz.net.ultraq.thymeleaf%22%20AND%20a%3A%22thymeleaf-layout-dialect%22)
[![License](https://img.shields.io/github/license/ultraq/thymeleaf-layout-dialect.svg?maxAge=2592000)](https://github.com/ultraq/thymeleaf-layout-dialect/blob/master/LICENSE.txt)

A dialect for Thymeleaf that lets you build layouts and reusable templates in
order to improve code reuse.

If you've ever used SiteMesh for your JSPs, or Facelets with JSFs, then the
concepts of this library will be very familiar to you.


Docs
----

For installation instructions, examples, and configuration options, check out
the docs pages over on https://ultraq.github.io/thymeleaf-layout-dialect/

Upgrading an existing project to Thymeleaf 3 and layout dialect 2?  Then give
the [migration guide](https://ultraq.github.io/thymeleaf-layout-dialect/MigrationGuide.html)
a read.


TL;DR
-----

Create a common layout to be used for several pages, defining extension points
in the body with the `layout:fragment`/`data-layout-fragment` processors:

```html
<!DOCTYPE html>
<html>
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
  <footer>
    <p>My footer</p>
    <p layout:fragment="custom-footer">Custom footer here</p>
  </footer>  
</body>
</html>
```

Create a content page that will use the layout, defining any HTML to use for the
extension points in the layout, and specified by a `layout:decorate`/`data-layout-decorate`
processor at the root element of your page:

```html
<html layout:decorate="~{layout.html}">
<head>
  <title>Content page</title>
  <script src="content-script.js"></script>
</head>
<body>
  <section layout:fragment="content">
    <p>This is a paragraph from the content page</p>
  </section>
  <footer>
    <p layout:fragment="custom-footer">This is some footer content from the content page</p>
  </footer>
</body>
</html>
```

Get Thymeleaf to process your content page.  The result will be the layout
template decorated by your content page, meaning that the content page will fill
out the layout's extension points, replace titles, and merge `<head>` items:

```html
<!DOCTYPE html>
<html>
<head>
  <title>Content page 1</title>
  <script src="common-script.js"></script>
  <script src="content-script.js"></script>
</head>
<body>
  <header>
    <h1>My website</h1>
  </header>
  <section>
    <p>This is a paragraph from content page 1</p>
  </section>
  <footer>
    <p>My footer</p>
    <p>This is some footer content from content page 1</p>
  </footer>  
</body>
</html>
```

Intrigued?  Check out the documentation links near the top of this readme to
learn more.
