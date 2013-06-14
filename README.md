
Thymeleaf Layout Dialect
========================

A dialect for Thymeleaf that allows you to use layout/decorator templates to
style your content, as well as pass entire fragment elements to included pages,
all to help improve code reuse.  If you've ever used SiteMesh 2 or JSF with
Facelets, then the concepts of this library will be very familiar to you.

 - Current version: 1.1.1
 - Released: 14 Jun 2013


Requirements
------------

 - Java 6
 - Thymeleaf 2.0.12+ (2.0.17 and its dependencies included)


Installation
------------

### Standalone distribution
Copy the JAR from [the latest download bundle](http://www.ultraq.net.nz/downloads/programming/Thymeleaf Layout Dialect 1.1.1.zip),
placing it in the `WEB-INF/lib` directory of your web application, or build the
project from the source code here on GitHub.

### For Maven and Maven-compatible dependency managers
Add a dependency to your project with the following co-ordinates:

 - GroupId: `nz.net.ultraq.thymeleaf`
 - ArtifactId: `thymeleaf-layout-dialect`
 - Version: `1.1.1`


Usage
-----

Add the Layout dialect to your existing Thymeleaf template engine, eg:

```java
ServletContextTemplateResolver templateresolver = new ServletContextTemplateResolver();
templateresolver.setTemplateMode("HTML5");

templateengine = new TemplateEngine();
templateengine.setTemplateResolver(templateresolver);
templateengine.addDialect(new LayoutDialect());		// This line adds the dialect to Thymeleaf
```

Or, for those using Spring configuration files:

```xml
<bean id="templateResolver" class="org.thymeleaf.templateresolver.ServletContextTemplateResolver">
  <property name="templateMode" value="HTML5"/>
</bean>

<bean id="templateEngine" class="org.thymeleaf.spring3.SpringTemplateEngine">
  <property name="templateResolver" ref="templateResolver"/>

  <!-- These lines add the dialect to Thymeleaf -->
  <property name="additionalDialects">
    <set>
      <bean class="nz.net.ultraq.thymeleaf.LayoutDialect"/>
    </set>
  </property>

</bean>
```

This will introduce 5 new attributes that you can use in your pages:
`layout:decorator`, `layout:include`, `layout:substituteby`, `layout:fragment`,
and `layout:title-pattern`.

### layout:decorator
Used in your content pages and declared in the root tag (usually `<html>`, but
as of Thymeleaf 2.0.10 this is no longer a restriction), this attribute
specifies the location of the decorator page to apply to the content page.  The
mechanism for resolving decorator pages is the same as that used by Thymeleaf to
resolve `th:fragment` and `th:substituteby` pages.
Check out the [Decorators and fragments](#decorators-and-fragments) example for
how to apply a decorator to your content pages.

### layout:include
Similar to Thymeleaf's `th:include`, but allows the passing of entire element
fragments to the included page.  Useful if you have some HTML that you want to
reuse, but whose contents are too complex to determine or construct with context
variables alone.
Check out the [Includes and fragments](#includes-and-fragments) example for how
to pass HTML code to the pages you want to include.

### layout:substituteby
Similar to `layout:include` in that you can pass HTML content to the
page/fragments you're substituting in, but with the behaviour of Thymeleaf's
`th:substituteby`.

### layout:fragment
The glue that holds everything together; it marks sections in the decorator page
that can be replaced by sections in the content page, which share the same name.

### layout:title-pattern
Allows for greater control of the resulting `<title>` element by specifying a
pattern with some special tokens.  This can be used to extend the decorator's
title with the content's one, instead of simply overriding it.
Check out the [Title pattern](#title-pattern) example for how to create a
configurable title pattern.


Examples
--------

### Decorators and fragments

Create a page that will contain a layout that will be shared between pages.
Often this will be a template that contains a page header, navigation, a footer,
and a spot where your page content will go.

```html
Layout.html

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
  xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
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
`<p>` element in the footer.  These are the points in the decorator page that
are candidates for replacement by matching fragments in the content pages.

Now, create some content pages.

```html
Content1.html

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
  xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
  layout:decorator="Layout.html">
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

The `layout:decorator` in the `<html>` tag says which decorator page to apply to
this content page.  Also, the content page defines its own title and a script,
as well as both the `content` and `custom-footer` fragments.  The `custom-footer`
fragment is within a `<footer>` element, which isn't really necessary, but might
be handy if you wish to do static templating of the content page which is one of
the reasons one uses Thymeleaf in the first place :)

> Fragment names must be unique within a page (ie: you cannot have 2 'content'
> fragments within a decorator or content page), otherwise the matching of
> fragments might not work as you expect.

Anyway, once you tell Thymeleaf to process `Content1.html`, the resulting page
will look like this:

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
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

The content page was 'decorated' by the elements of `Layout.html`, the result
being a combination of the decorator page, plus the fragments of the content
page (`<head>` elements from both pages with the `<title>` element from the
content page taking place of the decorator's, all elements from the decorator,
but replaced by all content page fragments where specified).

> The decoration process redirects processing from your content page to the
> decorator page, picking `layout:fragment` parts out of your content page as
> the decorator page demands them.  Because of this, anything _outside_ of a
> `layout:fragment` within the `<body>` in your content page never actually gets
> executed, meaning you can't do this in your content page:
> 
> ```html
> <div th:if="${user.admin}">
>   <div layout:fragment="content">
>     ...
>   </div>
> </div>
> ```
> 
> If the decorator wants the 'content' fragment, then it'll get that fragment,
> regardless of any conditions around it because those conditions aren't
> executed.

Say you just want to replace _only_ the footer of your decorator page with the
absolute minimum of HTML code:

```html
Content2.html

<p layout:decorator="Layout.html" layout:fragment="custom-footer">
  This is some footer text from content page 2.
</p>
```

And that's all you need!  The full-HTML-page restriction of Thymeleaf was lifted
from version 2.0.10, so now you can do things like the above.  The `<p>` tag
acts as both root element and fragment definition, resulting in a page that will
look like this:

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
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

You can think of the decorator page as your parent template that will get
filled-up or overwritten by your content pages (child templates), but only if
your content pages choose to fill-up/overwrite the parent.  This way the
decorator acts as a sort of 'default', with your content pages acting as
implementations on top of your default.


### Includes and fragments

Say you have some HTML or structure that you find repeating over and over and
want to make into its own page that you include from several places to reduce
code duplication.  An example of this might be a modal panel consisting of
several HTML elements and CSS classes to create the illusion of a new window
within your web application:

```html
Modal.html

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
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

Some of the pages use a modal that includes a simple message, others want to use
the modal to hold something more complex like a form to accept user input. The
possibilities for your modal become endless, but to support your imagination you
find yourself having to copy this modal structure into each page and vary the
contents for each use case, repeating the same HTML code to maintain the same
look-and-feel, breaking the [DRY principle](http://en.wikipedia.org/wiki/Don%27t_repeat_yourself)
in the process.

The main thing holding you back from proper reuse is the inability to pass HTML
elements to your included page.  That's where `layout:include` comes in.  It
works exactly like `th:include`, but by specifying and implementing fragments
much like with content/decorator page examples, you can create a common
structure that can respond to the use case of the page including it.

Here's an updated modal page, made more generic using Thymeleaf and the
`layout:fragment` attribute to define a replaceable modal content section:

```html
Modal2.html

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
  xmlns:th="http://www.thymeleaf.org"
  xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
  <body layout:fragment="modal">
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

Now you can include the page using the `layout:include` attribute and implement
the `modal-content` fragment however you need by creating a fragment of the same
name _within the include element_ of the calling page:

```html
Content.html

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
  xmlns:th="http://www.thymeleaf.org"
  xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">

  ...

  <div layout:include="Modal2.html :: modal" th:with="modalId='message', modalHeader='Message'" th:remove="tag">
    <p th:fragment="modal-content">Message goes here!</p>
  </div>

  ...

</html>
```

Just like with the content/decorator example, the `layout:fragment` of the page
you're including will be replaced by the element with the matching fragment
name.  In this case, the entire `modal-content` of `Modal2.html` will be
replaced by the custom paragraph above.  Here's the result:

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">

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

The custom message defined in the page including `Modal2.html` was made a part
of the contents of the modal.  Fragments in the context of an included page work
the same as they do when used in the context of a decorator: if the fragment
isn't defined in your page, then it won't override whatever is in the included
page, allowing you to create defaults in your included page.


### Title pattern

Given that the layout dialect automatically overrides the decorator page's `title`
element with that found in the content page, you might find yourself repeating
parts of the title found in the decorator page, especially if you like to create
breadcrumbs or retain the name of the website in the page title.  The `layout:title-pattern`
attribute can save you the trouble of repeating the decorator title by using
some special tokens in a pattern of how you want your title to appear.

Here's an example:

Layout.html

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
  xmlns:th="http://www.thymeleaf.org"
  xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
  <head>
    <title layout:title-pattern="$DECORATOR_TITLE - $CONTENT_TITLE">My website</title>
  </head>

  ...

</html>
```

The `layout:title-pattern` attribute is a simple string that recognizes 2
special tokens: `$DECORATOR_TITLE` and `$CONTENT_TITLE`.  Each token will be
replaced by their respective titles in the resulting page.  So, if you had the
following content page:

```html
Content.html

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
  xmlns:th="http://www.thymeleaf.org"
  xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
  layout:decorator="Layout.html">

  <head>
    <title>My blog</title>
  </head>

  ...

</html>
```

The resulting page would be:

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">

  <head>
    <title>My website - My blog</title>
  </head>

  ...

</html>
```

The pattern was specified in the decorator, so applies to all content pages that
make use of the decorator.  If you specify another title pattern in the content
page, then it will override the one found in the decorator, allowing for
fine-grained control of the appearance of your title.


Changelog
---------

### 1.1.1
 - Restore the help/documentation file so that this dialect will appear in
   content assist in the Thymeleaf Eclipse plugin. (I used a new build process
   for the 1.1 branch, and it missed this resource file!  Apologies for that
   folks.)

### 1.1
 - Change package to `nz.net.ultraq.thymeleaf` (dropped the 'web' part).
 - Change XML namespace to `http://www.ultraq.net.nz/thymeleaf/layout` (dropped
   the 'web' part).
 - Implemented unit tests, using the [Thymeleaf Testing](https://github.com/thymeleaf/thymeleaf-testing)
   library.
 - Resolved [Issue #21](thymeleaf-layout-dialect/issues/21), adding a `layout:substituteby`
   processor which works much like `layout:include`, but with the behaviour of
   Thymeleaf's `th:substituteby`.
 - Updated Thymeleaf dependency from version 2.0.15 to 2.0.17.

### 1.0.6
 - Added a help/documentation file so that this dialect will appear in content
   assist in the Thymeleaf Eclipse plugin.

### 1.0.5
 - Added the ability to specify the final `<title>` through a configurable
   pattern, specified in a new `layout:title-pattern` attribute.
 - Made code emit a warning to the logs if the `layout:fragment` element was in
   the `<title>` element (I keep seeing people doing this, even though it's not
   required since the dialect automatically takes the content `<title>` over the
   decorator `<title>`).
 - Resolved [Issue #10](thymeleaf-layout-dialect/issues/10), so that content
   outside the decorator page's `<html>` element (like IE conditional comments)
   are included in the resulting page.
 - Updated Thymeleaf dependency from version 2.0.13 to 2.0.15.

### 1.0.4
 - Fixed enforcing of `layout:decorator` element being in the root element,
   leading to a better error message if this attribute is found elsewhere.
 - Fixed the resulting JAR which didn't recreate the proper Maven metadata in
   the manifest section and might have caused it to not be picked up by tools
   like m2eclipse.
 - Resolved [Issue #7](thymeleaf-layout-dialect/issues/7), which caused a `ClassCastException`
   for cases when `th:include` was used to include entire pages.
 - Updated Thymeleaf dependency from version 2.0.11 to 2.0.13.

### 1.0.3
 - Added a `layout:include` attribute which works like `th:include` but allows
   for the passing of element fragments to the included page.
 - Resolved [Issue #3](thymeleaf-layout-dialect/issues/3), allowing `th:with`
   local variable declarations made in the decorator page to be visible in
   content pages during processing.
 - Resolved [Issue #4](thymeleaf-layout-dialect/issues/4), removing the
   restriction that the `layout:decorator` tag appear in an HTML element since
   Thymeleaf 2.0.10 relaxed that restriction too (tag must still appear in the
   root element of your page however).
 - Updated Thymeleaf dependency from version 2.0.8 to 2.0.11 for the above issue
   due to a required API change in 2.0.11.

### 1.0.2
 - Resolved [Issue #2](thymeleaf-layout-dialect/issues/2), allowing decorator
   and content pages to contain just a `<head>` section, or just a `<body>`
   section, or neither section, or some other combination between pages.

### 1.0.1
 - Switched from Ant to Gradle as a build tool and to generate Maven-compatible
   artifacts.
 - Resolved [Issue #1](thymeleaf-layout-dialect/issues/1) to appease the
   Mavenites amongst you :)  Project is now being served from Maven Central,
   co-ordinates added to [installation](#installation) instructions.

### 1.0
 - Initial release.

