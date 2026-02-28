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
new LayoutDialect()
  .withSortingStrategy(new AppendingStrategy());
```

Sets how `<head>` elements will be sorted when combined from the layout and
content templates.  See [`<head>` element merging]({% link processors/decorate.md %}#head-element-merging)
for more details and examples.


`autoHeadMerging`
-----------------

Default: `true`

```java
new LayoutDialect()
  .withAutoHeadMerging(true);
```

Bypass the layout dialect prforming any `<head>` element merging altogether.
See [Bypassing <head> element merging altogether]({% link processors/decorate.md %}#bypassing-head-element-merging-altogether)
for more details.


`experimentalTitleTokens`
-------------------------

Default: `false`

```java
new LayoutDialect()
  .withExperimentalTitleTokens(false);
```

An experimental option added in 3.4.0 to use standard Thymeleaf expression
syntax for title patterns instead of special tokens.  See
[Using standard Thymeleaf expression variables instead of special tokens]({% link processors/title-pattern.md %}#using-standard-thymeleaf-expression-variables-instead-of-special-tokens)
for more details.
