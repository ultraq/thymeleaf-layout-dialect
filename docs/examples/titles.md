---
layout: default
title: Controlling the document title
parent: Examples
nav_order: 3
---

Controlling the document title
==============================

Given that the Layout dialect automatically overrides the layout's `<title>`
with that found in the content template, you might find yourself repeating parts
of the title found in the layout, especially if you like to create breadcrumbs
or retain the name of the website in the page title.  The `layout:title-pattern`
processor can save you the trouble of repeating the layout title by using some
special tokens in a pattern of how you want your title to appear.

Here's an example:

```html
Layout.html

<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
  <title layout:title-pattern="$LAYOUT_TITLE - $CONTENT_TITLE">My website</title>
</head>
...
</html>
```

The `layout:title-pattern` processor takes a simple string that recognizes 2
special tokens: `$LAYOUT_TITLE` and `$CONTENT_TITLE`.  Each token will be
replaced by their respective titles in the resulting page.  So, if you had the
following content template:

```html
Content.html

<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
  layout:decorator="Layout">
<head>
  <title>My blog</title>
</head>
...
</html>
```

The resulting page would be:

```html
<!DOCTYPE html>
<html>
<head>
  <title>My website - My blog</title>
</head>
...
</html>
```

This works for both static/inlined text inside the `<title>` elements, or
dynamic text using `th:text` found on the `<title>` element.

The pattern in the example above was specified in the layout, so applies to all
content templates that make use of the layout.  If you specify another title
pattern in the content template, then it will override the one found in the
layout, allowing for fine-grained control of the appearance of your title.
