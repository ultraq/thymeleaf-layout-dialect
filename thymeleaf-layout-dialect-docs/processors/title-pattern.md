---
layout: default
title: title-pattern
parent: Processors
nav_order: 2
---

title-pattern
=============
{: .no_toc }

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


On this page
------------
{: .no_toc .text-delta }

1. TOC
{:toc}


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


Using standard Thymeleaf expression variables instead of special tokens
-----------------------------------------------------------------------

An experimental option was added in 3.4.0 to use standard Thymeleaf expression
syntax for title patterns and to have access to the title parts in templates as
the variables `layoutDialectLayoutTitle` and `layoutDialectContentTitle` (the
names are pretty verbose but I want to avoid any potential clashes while I
either come up with new ones or some way to configure the names).

So instead of the example above for setting what the final title will look like,
you can do this instead:

```html
<title layout:title="|${layoutDialectLayoutTitle} - ${layoutDialectContentTitle}|">...</title>
```

The title parts will also be made available anywhere in the template as the
variables above, so you can reference them as necessary:

```html
<body>
  <p>The title of my page is
    <span th:text="${layoutDialectContentTitle}">(title here)</span>
  <p>
</body>
```

To configure this option, set `experimentalTitleTokens` to `true`:

```java
new LayoutDialect()
  .withExperimentalTitleTokens(true);
```

Any feedback for this experimental option can be made in
[thymeleaf-layout-dialect/issues/172](https://github.com/ultraq/thymeleaf-layout-dialect/issues/172)
