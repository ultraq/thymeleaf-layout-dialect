---
layout: default
title: Configuration options
nav_order: 4
---

Configuration options
=====================
{: .no_toc }


On this page
------------
{: .no_toc .text-delta }

1. TOC
{:toc}


`sortingStrategy`
-----------------

Default: `ApendingStrategy`

```java
new LayoutDialect(new AppendingStrategy());
```

Sets how `<head>` elements will be sorted when combined from the layout and
content templates.  See [the decorate processor page]({{ site.baseurl }}{% link processors/decorate.md %})
for more details and examples.


`autoHeadMerging`
-----------------

Default: `true`

```java
new LayoutDialect(null, true);
```

Bypass the layout dialect prforming any `<head>` element merging altogether.
See [Bypassing <head> element merging altogether]({{ site.baseurl }}{% link processors/decorate.md %}#bypassing-head-element-merging-altogether)
for more details.


`experimentalTitleTokens`
-------------------------

Default: `false`

```java
var layoutDialect = new LayoutDialect();
layoutDialect.setExperimentalTitleTokens(false);
```

An experimental option added in 3.4.0 to use standard Thymeleaf expression
syntax for title patterns and to have access to the title parts in templates as
the variables `layoutDialectLayoutTitle` and `layoutDialectContentTitle`.

So instead of the example in [Processors > title-pattern]({{ site.baseurl }}{% link processors/title-pattern.md %})
for setting what the final title will look like:

```html
<title layout:title-pattern="$LAYOUT_TITLE - $CONTENT_TITLE">...</title>
```

You can do this instead:

```html
<title layout:title="|${`layoutDialectLayoutTitle} - ${layoutDialectContentTitle}|">...</title>
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

Any feedback for this experimental option can be made in
[thymeleaf-layout-dialect/issues/172](https://github.com/ultraq/thymeleaf-layout-dialect/issues/172)
