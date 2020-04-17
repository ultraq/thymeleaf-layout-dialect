---
layout: default
title: Home
nav_order: 1
---

Thymeleaf Layout Dialect
========================

A dialect for Thymeleaf that lets you build layouts and templates in a way to
encourage code reuse.
{: .fs-6 .fw-300 }


What does it do?
----------------

The Thymeleaf Layout Dialect is an extension for Thymeleaf, a server-side Java
templating engine.  The layout dialect's main goal is to allow for the
decoration of templates to let developers build layouts in a manner similar to
classical inheritence.

For example, given a template named `layout.html`:

```html
<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
  <title>Layout page</title>
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

And a content page being processed by Thymeleaf:

```html
<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
  layout:decorate="~{layout.html}">
<head>
  <title>Content page</title>
</head>
<body>
  <section layout:fragment="content">
    <p>This is a paragraph from the content page</p>
  </section>
</body>
</html>
```

The resulting HTML will be:

```html
<!DOCTYPE html>
<html>
<head>
  <title>Content page</title>
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


Getting started
---------------

Check out the [installation guide]({% link installation.md %}) to add the layout
dialect to your Thymeleaf project, or [the examples]({% link examples/index.md %})
for more ways of how the layout dialect can help you build your templates.
