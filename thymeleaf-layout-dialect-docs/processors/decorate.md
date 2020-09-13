---
layout: default
title: decorate
parent: Processors
nav_order: 1
redirect_from:
  - /Configuration.html
---

decorate
========
{: .no_toc }

 - XML attribute: `layout:decorate`
 - Data attribute: `data-layout-decorate`

Used in your content templates and declared in the root tag (usually `<html>`),
this processor takes a [fragment expression](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#fragments)
that specifies the layout template to decorate with the content template.

```html
<html layout:decorate="~{layout}>"
```


On this page
------------
{: .no_toc .text-delta }

1. TOC
{:toc}


Example
-------

Create a template that will contain a layout that will be shared across pages.
Often this will be a template that contains a page header, navigation, a footer,
and a spot where your page-specific content will go.

```html
layout.html

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
  <footer>
    <p>My footer</p>
    <p layout:fragment="custom-footer">Custom footer here</p>
  </footer>  
</body>
</html>
```

Notice how the `layout:fragment` attribute was applied to the `<section>` and
`<p>` element in the footer.  These are the points in the layout that are
candidates for replacement by matching fragments in your content templates.

Now, create some content templates.

```html
content1.html

<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
  layout:decorate="~{layout}">
<head>
  <title>Content page 1</title>
  <script src="content-script.js"></script>
</head>
<body>
  <section layout:fragment="content">
    <p>This is a paragraph from content page 1</p>
  </section>
  <footer>
    <p layout:fragment="custom-footer">This is some footer content from content page 1</p>
  </footer>
</body>
</html>
```

The `layout:decorate` in the `<html>` tag says which layout template to decorate
using this content template.  The content template defines its own title and
script, as well as both the `content` and `custom-footer` fragments.  The `custom-footer`
fragment is within a `<footer>` element, which isn't really necessary, but might
be handy if you wish to do static templating of the content template which is
one of the reasons one uses Thymeleaf in the first place 😊

> Fragment names must be unique within a template, otherwise fragment mismatches
> can occur and all sorts of hilarity will ensue.

Once you tell Thymeleaf to process `content1.html`, the resulting page will
look like this:

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

The content template decorated `layout.html`, the result being a combination of
the layout, plus the fragments of the content template.  `<head>` elements from
both templates, with the `<title>` element from the content template taking
place of the layout's, all elements from the layout, but replaced by all content
template fragments where specified.

By default, `<head>` elements from the content template are added to the end,
after all of the layout's `<head>` elements.  This can be configured.  See the
[`<head>` element merging](#head-element-merging) section below.

> The decoration process redirects processing from your content template to the
> layout, picking `layout:fragment` parts out of your content template as the
> layout demands them.  Because of this, anything _outside_ of a `layout:fragment`
> within the `<body>` in your content template never actually gets executed,
> meaning you can't do this in your content template:
> 
> ```html
> <div th:if="${user.admin}">
>   <div layout:fragment="content">
>     ...
>   </div>
> </div>
> ```
> 
> If the layout template wants the 'content' fragment, then it'll get that
> fragment, regardless of any conditions around it because those conditions
> aren't executed.

Say you just want to replace _only_ the footer of your decorator with the
absolute minimum of HTML code:

```html
Content2.html

<p layout:decorate="~{layout}" layout:fragment="custom-footer">
  This is some footer text from content page 2.
</p>
```

And that's all you need!  The `<p>` tag acts as both root element and fragment
definition, resulting in a page that will look like this:

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
  <section>
    <p>Page content goes here</p>
  </section>
  <footer>
    <p>My footer</p>
    <p>
      This is some footer text from content page 2.
    </p>
  </footer>  
</body>
</html>
```

You can think of the layout as your parent template that will get filled-up or
overwritten by your content (child templates), but only if your content chooses
to fill-up/overwrite the parent.  This way the layout acts as a sort of
'default', with your content acting as implementations on top of this default.


`<head>` element merging
------------------------

When decorating the `<head>` sections of the content and layout templates, the
default behaviour is to place all content elements after the layout ones, eg:

```html
Content.html

<head>
  <title>Hello!</title>
  <link rel="stylesheet" href="content-stylesheet.css"/>
  <script src="content-script.js"></script>
</head>
```

```html
Layout.html

<head>
  <title>Goodbye!</title>
  <link rel="stylesheet" href="layout-stylesheet.css"/>
  <script src="layout-script.js"></script>
</head>
```

Result:

```html
<head>
  <title>Hello!</title>
  <link rel="stylesheet" href="layout-stylesheet.css"/>
  <script src="layout-script.js"></script>
  <link rel="stylesheet" href="content-stylesheet.css"/>
  <script src="content-script.js"></script>
</head>
```

However, sometimes we need a smarter merging of elements, such as grouping like
elements together (having scripts with scripts and stylesheets with stylesheets).

```html
Content.html

<head>
  <title>Hello!</title>
  <link rel="stylesheet" href="content-stylesheet.css"/>
  <script src="content-script.js"></script>
</head>
```

```html
Layout.html

<head>
  <title>Goodbye!</title>
  <link rel="stylesheet" href="layout-stylesheet.css"/>
  <script src="layout-script.js"></script>
</head>
```

Result:

```html
<head>
  <title>Hello!</title>
  <link rel="stylesheet" href="layout-stylesheet.css"/>
  <link rel="stylesheet" href="content-stylesheet.css"/>
  <script src="layout-script.js"></script>
  <script src="content-script.js"></script>
</head>
```

The Layout dialect can be configured to support either use case, with the
ability for developers to define their own sorting.

This sorting is exposed by the [`SortingStrategy`](/thymeleaf-layout-dialect/groovydoc/nz/net/ultraq/thymeleaf/decorators/SortingStrategy.html)
interface and the layout dialect provides 4 implementations to choose from:

 - `nz.net.ultraq.thymeleaf.decorators.strategies.AppendingStrategy`, the
   default, appends content `<head>` elements after layout ones.  Deprecated.
 - `nz.net.ultraq.thymeleaf.decorators.strategies.GroupingStrategy`, groups like
   elements together.  Deprecated.
 - `nz.net.ultraq.thymeleaf.decorators.strategies.AppendingRespectLayoutTitleStrategy`,
   a new variant of the `AppendingStrategy` which respects the position of the
   `<title>` element based on its position in the layout template.
 - `nz.net.ultraq.thymeleaf.decorators.strategies.GroupingRespectLayoutTitleStrategy`,
   a new variant of the `GroupingStrategy` which respects the position of the
   `<title>` element based on its position in the layout template.

> The default behaviour of the layout dialect has historically been to place the
> `<title>` element at the beginning of the `<head>` element during the
> decoration process; an arbitrary design decision which made development of
> this library easier.  However, this runs against the expectations of
> developers who wished to control the order of elements, most notably the
> practice of putting `<meta charset...>` as the first element in the `<head>`.
> The new strategies (ones with `RespectLayoutTitle` in their name) instead keep
> `<title>`s wherever they exist within the target/layout template being
> decorated, and then work on everything else as normal.
> 
> The `RespectLayoutTitle` strategies were introduced in version 2.4.0 and will
> become the default strategies from version 3.x.  The existing strategies are
> now deprecated but retain the historic behaviour, maintaining backwards
> compatibility with the 2.x versions.

To change to the grouping strategy, configure the Layout dialect using one of
the methods below:

 - Spring or Spring Boot 2 w/ Java/annotation config:
```java
@Bean
public LayoutDialect layoutDialect() {
  return new LayoutDialect(new GroupingRespectLayoutTitleStrategy());
}
```

 - DIY management of the Thymeleaf template engine:
```java
TemplateEngine templateEngine = new TemplateEngine();
templateEngine.addDialect(new LayoutDialect(new GroupingRespectLayoutTitleStrategy()));
```

If neither strategy suits your needs, you can implement your own [`SortingStrategy`](/thymeleaf-layout-dialect/groovydoc/nz/net/ultraq/thymeleaf/decorators/SortingStrategy.html)
and pass it along to the layout dialect like above.


Bypassing `<head>` element merging altogether
---------------------------------------------

From version 2.4.0, an experimental option was added to skip the special `<head>`
merging entirely.  Reasons for this might be that you wish to manage that
section yourself so are using things like Thymeleaf's `th:replace` to fill that
special part of your HTML document in.

To bypass the layout dialect's `<head>` element merging, the second optional
parameter to the `LayoutDialect` constructor should be set to `false`.  (The
first parameter can be set to `null` as a merging strategy isn't really relevant
when `<head>` element merging is disabled.)

 - Spring or Spring Boot 2 w/ Java/annotation config:
```java
@Bean
public LayoutDialect layoutDialect() {
  return new LayoutDialect(null, false);
}
```

 - DIY management of the Thymeleaf template engine:
```java
TemplateEngine templateEngine = new TemplateEngine();
templateEngine.addDialect(new LayoutDialect(null, false));
```

When disabled, the `<head>` element will be whatever it was in the content
template, so the `<head>` from the layout is not applied.


Passing data to the decorated template
--------------------------------------

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

### Workaround for Thymeleaf Layout Dialect 2.3.0 and older

Parameterized fragments was made available from Thymeleaf Layout Dialect 2.4.0.
For older versions, you can continue to use `th:with`:

```html
<html layout:decorate="~{your-layout}" th:with="greeting='Hello!'">
```

### Attributes in the element with `layout:decorate` will also get copied over

Say you have a content template like so:

```html
<div class="container" layout:decorate="~{layout}">
```

Notice the `class` attribute alongside `layout:decorate`.  That attribute will
be copied over to the root element of the template you are decorating.

This behaviour has been around since the beginning when Thymeleaf was a bit more
limited in supported template types (ie: when it *had* to have a root element
like `<html>`) and didn't yet support passing data between fragments (so was a
handy way to be able to set IDs or classes from content to layout).  However,
it has tripped some people up before, so this section is only here to document
that behaviour.

If you don't want to have those attributes present in the output, then a
workaround is to split the element that has `layout:decorate` from your
attributes:

```html
<div layout:decorate="~{layout}">
  <div layout:fragment="content" class="container">
```
