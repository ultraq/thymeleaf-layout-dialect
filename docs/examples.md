---
layout: default
title: Examples
nav_order: 4
---

Examples
========

> The following examples:
>  - use the XML attribute version of the processors, but can just as easily be
>    swapped for their HTML data attribute equivalents if that's more your style
>  - assume that the Thymeleaf template engine is configured with `.html` as a
>    suffix.  This is the default in a lot of framework configurations, such as
>    Spring Boot.


Layouts
-------

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
[`<head>` element merging](configuration.md#head-element-merging) section.

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


Passing data to layouts
-----------------------

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

This feature is available from Thymeleaf Layout Dialect 2.4.0.  For older
versions, you can continue to use `th:with`/`data-th-with`:

```html
<html layout:decorate="~{your-layout}" th:with="greeting='Hello!'">
```


Configuring your title
----------------------

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


Reusable templates
------------------

Say you have some HTML or structure that you find repeating over and over and
want to make into its own template that you insert from several places to reduce
code duplication.  (Modular Thymeleaf anybody?)  An example of this might be a
modal panel consisting of several HTML elements and CSS classes to create the
illusion of a new window within your web application:

```html
Modal.html

<!DOCTYPE html>
<html>
<body>
  <div id="modal-container" class="modal-container" style="display:none;">
    <section id="modal" class="modal">
      <header>
        <h1>My Modal</h1>
        <div id="close-modal" class="modal-close">
          <a href="#close">Close</a>
        </div>
      </header>
      <div id="modal-content" class="modal-content">
        <p>My modal content</p>
      </div>
    </section>
  </div>
</body>
</html>
```

You find you can turn some things into variables like the header and IDs so that
pages including `Modal.html` can set their own name/IDs.  You continue making
your modal code as generic as possible, but then you get to the question of
filling-in the content of your modal panel, and that's where you start to reach
some limitations.

Some of the pages use a modal that has a simple message, others want to use the
modal to hold something more complex like a form to accept user input. The
possibilities for your modal become endless, but to support your imagination you
find yourself having to copy this modal structure into each template and vary
the contents for each use case, repeating the same HTML code to maintain the same
look-and-feel, breaking the [DRY principle](http://en.wikipedia.org/wiki/Don%27t_repeat_yourself)
in the process.

The main thing holding you back from proper reuse is the inability to pass HTML
elements to the inserted template.  That's where `layout:insert` comes in.  It
works exactly like `th:insert`, but by specifying and implementing fragments
much like with content/layout examples, you can create a common structure that
can respond to the use case of the template inserting it.

Here's an updated modal template, made more generic using Thymeleaf and the
`layout:fragment` attribute to define a replaceable modal content section:

```html
Modal2.html

<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
  xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<body layout:fragment="modal(modalId, modalHeader)">
  <div th:id="${modalId} + '-container'" class="modal-container" style="display:none;">
    <section th:id="${modalId}" class="modal">
      <header>
        <h1 th:text="${modalHeader}">My Modal</h1>
        <div th:id="'close-' + ${modalId}" class="modal-close">
          <a href="#close">Close</a>
        </div>
      </header>
      <div th:id="${modalId} + '-content'" class="modal-content">
        <div layout:fragment="modal-content">
          <p>My modal content</p>
        </div>
      </div>
    </section>
  </div>
</body>
</html>
```

Now you can insert this template using the `layout:insert` processor and
implement the `modal-content` fragment however you need by creating a fragment
of the same name _within the insert element_ of the calling template:

```html
Content.html

<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
  xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
  ...
  <div layout:insert="Modal2 :: modal(modalId='message', modalHeader='Message')" th:remove="tag">
    <p layout:fragment="modal-content">Message goes here!</p>
  </div>
  ...
</html>
```

Just like with the content/layout example, the `layout:fragment` of the template
you're inserted will be replaced by the element with the matching fragment name.
In this case, the entire `modal-content` of `Modal2.html` will be replaced by
the custom paragraph above.  Here's the result:

```html
<!DOCTYPE html>
<html>
  ...
  <div id="message-container" class="modal-container" style="display:none;">
    <section id="message" class="modal">
      <header>
        <h1>Message</h1>
        <div id="close-message" class="modal-close">
          <a href="#close">Close</a>
        </div>
      </header>
      <div id="message-content" class="modal-content">
        <p>Message goes here!</p>
      </div>
    </section>
  </div>
  ...
</html>
```

The custom message defined in the template including `Modal2.html` was made a
part of the contents of the modal.  Fragments in the context of an inserted
template work the same as they do when used in the context of the content/layout
process: if the fragment isn't defined in your template, then it won't override
whatever is in the inserted template, allowing you to create defaults in your
reusable templates.
