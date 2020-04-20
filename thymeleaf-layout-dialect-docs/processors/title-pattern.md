---
layout: default
title: title-pattern
parent: Processors
nav_order: 2
---

title-pattern
=============

 - XML attribute: `layout:title-pattern`
 - Data attribute: `data-layout-title-pattern`

Allows for greater control of the resulting `<title>` element by specifying a
pattern with some special tokens.

```html
<title layout:title-pattern="$LAYOUT_TITLE - $CONTENT_TITLE">My content page</title>
```

 - `$LAYOUT_TITLE` represents the title value found in the decorated template
 - `$CONTENT_TITLE` represents the title value found in the content template

Each token will be replaced by their respective titles in the resulting page,
leaving the rest of the string alone.


Example
-------

By default, the layout dialect overrides the decorated template's `<title>` with
the one found in the content template.  To overcome this, you might find
yourself repeating parts of the title found in the layout, especially if you
like to create breadcrumbs or retain the name of the website in the page title.
The `layout:title-pattern` processor can save you the trouble of repeating the
layout title by using some special tokens in a pattern of how you want your
title to appear.

So given a layout with this title:

```html
<title layout:title-pattern="$LAYOUT_TITLE - $CONTENT_TITLE">My website</title>
```

And this content template:

```html
<title>My blog</title>
```

The resulting title would have:

```html
<title>My website - My blog</title>
```

This works for both static/inlined text inside the `<title>` elements, or
dynamic text using `th:text`/`th:utext` found on the `<title>` element.

The pattern in the example above was specified in the layout, so applies to all
content templates that make use of the layout.  If you specify another title
pattern in the content template, then it will override the one found in the
layout, allowing for fine-grained control of the appearance of your title.
