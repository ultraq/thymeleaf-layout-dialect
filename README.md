
Thymeleaf Layout Dialect
========================

A new dialect for Thymeleaf that allows you to use layout/decorator pages to
style your content, as well as pass entire fragment elements to included pages
to increase code reuse.  If you've ever used SiteMesh 2 or JSF with Facelets,
then the concepts of this library will be very familiar to you.


Requirements
------------

 - Java 6
 - Thymeleaf 2.0 (2.0.10 and its dependencies included)


Installation
------------

### Standalone distribution
1. Download a copy of of the pre-compiled JAR from [the Downloads section](thymeleaf-layout-dialect/downloads)
   or build the project from the source code here on GitHub.
2. Place the JAR in the `WEB-INF/lib` directory of your web application.

### For Maven and Maven-compatible dependency managers
Add a dependency to your project with the following co-ordinates:

 - GroupId: `nz.net.ultraq.web.thymeleaf`
 - ArtifactId: `thymeleaf-layout-dialect`
 - Version: `1.0.3`


Usage
-----

Add the Layout dialect to your existing Thymeleaf template engine, eg:

	ServletContextTemplateResolver templateresolver = new ServletContextTemplateResolver();
	templateresolver.setTemplateMode("HTML5");

	templateengine = new TemplateEngine();
	templateengine.setTemplateResolver(templateresolver);
	templateengine.addDialect(new LayoutDialect());		// This line here

This will introduce 3 new attributes that you can use in your pages:
`layout:decorator`, `layout:include`, and `layout:fragment`.

### layout:decorator
Used only in content pages and declared in the `<html>` tag, this attribute
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
to pass elements between your pages.

### layout:fragment
The glue that holds everything together: in the context of a content page, maps
content page elements to those in a decorator; in the context of an include,
maps content page elements to those in the included page.


Examples
--------

### Decorators and fragments

Create a page that will contain a layout that will be shared between pages.
Often this will be a template that contains a page header, navigation, a footer,
and a spot where your page content will go.

	Layout.html
	
	<html xmlns="http://www.w3.org/1999/xhtml"
	  xmlns:layout="http://www.ultraq.net.nz/web/thymeleaf/layout">
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

Notice how the `layout:fragment` attribute was applied to the `<section>` and
`<p>` element in the footer.  These are the points in the decorator page that
are candidates for replacement by matching fragments in the content pages.

Now, create some content pages.

	Content1.html
	
	<html xmlns="http://www.w3.org/1999/xhtml"
	  xmlns:layout="http://www.ultraq.net.nz/web/thymeleaf/layout"
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

The `layout:decorator` in the `<html>` tag of the content page says which
decorator page to apply this this content page.  Also, the content page defines
both the `content` and `custom-footer` fragments.  The `custom-footer` fragment
is within a `<footer>` element, which isn't really necessary, but might be handy
if you wish to do static templating of the content page which is one of the
reasons one uses Thymeleaf in the first place :)  Anyway, once you tell
Thymeleaf to process `Content1.html`, the resulting page will looks like this:

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

The content page was 'decorated' by the elements of `Layout.html`, the result
being a combination of the decorator page, plus the fragments of the content
page (`<head>` elements from both pages, the `<title>` element from the content
page, all elements from the decorator, but replaced by all content page
fragments where specified).

When you specify `layout:fragment` attributes in your decorator page, you don't
have to match all of them in your content pages, nor do you need to specify a
content-specific title if you really don't need to.  Here's an example that only
replaces the custom paragraph in the decorator's footer:

	Content2.html
	
	<html xmlns="http://www.w3.org/1999/xhtml"
	  xmlns:layout="http://www.ultraq.net.nz/web/thymeleaf/layout"
	  layout:decorator="Layout.html">
	  <body>
	    <p layout:fragment="custom-footer">This is some footer content from content page 2</p>
	  </body>
	</html>

This time the `layout:fragment` isn't in a `<footer>` element, just to show that
it's not needed.  The resulting page will look like this:

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
	     <p>This is some footer content from content page 2</p>
	   </footer>  
	  </body>
	</html>

So if your content pages don't specify any `<title>` element, or any fragment
replacements, the result will use whatever is in the decorator page.  This
allows you to create defaults in your decorator pages that can be replaced only
if you feel the need to replace them.


### Includes and fragments

Say you have some HTML or structure that you find repeating over and over and
want to make into its own page that you include from several places.  An example
of this might be a modal panel consisting of several HTML elements and CSS
classes to create the illusion of a new window within your web application:

	Modal.html
	
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
works exactly like `th:include` does, but by specifying and implementing
fragments much like with content/decorator page examples, you can create a
common structure that can respond to the use case of the page including it.

Here's an updated modal page, made more generic using Thymeleaf and the
`layout:fragment` attribute to define a replaceable modal content section:

	Modal2.html
	
	<html xmlns="http://www.w3.org/1999/xhtml"
	  xmlns:th="http://www.thymeleaf.org"
	  xmlns:layout="http://www.ultraq.net.nz/web/thymeleaf/layout">
	
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

Now you can include the page using the `layout:include` attribute and implement
the `modal-content` fragment however you need by creating a fragment of the same
name _within the include element_:

	Content.html
	
	<html xmlns="http://www.w3.org/1999/xhtml"
	  xmlns:th="http://www.thymeleaf.org"
	  xmlns:layout="http://www.ultraq.net.nz/web/thymeleaf/layout">
	
	  ...
	
	  <div layout:include="Modal2.html" th:with="modalId='message', modalHeader='Message'">
	    <p th:fragment="modal-content">Message goes here!</p>
	  </div>
	
	  ...
	
	</html>

Just like with the content/decorator example, the `layout:fragment` of the page
you're including will be replaced by the element with the matching fragment
name.  In this case, the entire `modal-content` of `Modal2.html` will include
the custom paragraph above.  Here's the result:

	<html xmlns="http://www.w3.org/1999/xhtml">
	
	  ...
	
	  <div>
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
	  </div>
	
	  ...
	
	</html>

The custom message defined in the page including `Modal2.html` was made a part
of the contents of the modal.  Fragments in the context of an included page work
the same as they do when used in the context of a decorator: if the fragment
isn't defined in your page, then it won't override whatever is in the included
page, allowing you to create defaults in your included page.

(And yeah, there's an extra `<div>` up there, the one that was used to make the
`layout:include` declaration.  You might want to add a `th:remove="tag"`
attribute to that so it gets stripped from the final result.  Look out for a
`layout:substituteby` attribute (equivalent of a `th:substituteby` that allows
for passing of fragment elements) in future versions.)


Changelog
---------

### 1.0.3
 - Added a `layout:include` attribute which works like `th:include` but allows
   for the passing of element fragments to the included page.
 - Updated Thymeleaf dependency from version 2.0.8 to 2.0.10.

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

