---
layout: default
title: Layouts
parent: Examples
nav_order: 1
---

Layouts
=======

Create a template that will contain a layout that will be shared across
templates.  Often this will be a template that contains a page header,
navigation, a footer, and a spot where your content will go.

```html
Layout.html

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
Content1.html

<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
  layout:decorate="~{Layout}">
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
one of the reasons one uses Thymeleaf in the first place :)

> Fragment names must be unique within a template, otherwise fragment mismatches
> can occur and all sorts of hilarity will ensue.

Anyway, once you tell Thymeleaf to process `Content1.html`, the resulting page
will look like this:

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

The content template decorated `Layout.html`, the result being a combination of
the layout, plus the fragments of the content template (`<head>` elements from
both templates, with the `<title>` element from the content template taking
place of the layout's, all elements from the layout, but replaced by all content
template fragments where specified).

For more on how you can control the merging of `<head>` elements, see the
[`<head>` element merging](docs/configuration.md#head-element-merging) section.

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

<p layout:decorate="~{Layout}" layout:fragment="custom-footer">
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
