
Thymeleaf Layout Dialect
========================

A new dialect for Thymeleaf that allows you to use layout/decorator pages to
style your content.  If you've ever used SiteMesh 2, then the concepts of this
library will be very familiar to you.


Requirements
------------

 - Java 6
 - Thymeleaf 2.0 (2.0.8 and its dependencies included)


Installation
------------

### Standalone distribution
1. Download a copy of of the pre-compiled JAR from [the Downloads section](thymeleaf-layout-dialect/downloads)
   or build the project from the source code here on GitHub.
2. Place the JAR in the `WEB-INF/lib` directory of your web application.

### For Maven and Maven-compatible dependency managers
Add a dependency to your project with the following Maven co-ordinates:

 - GroupId: `nz.net.ultraq.web.thymeleaf`
 - ArtifactId: `thymeleaf-layout-dialect`
 - Version: `1.0.1`


Usage
-----

Add the Layout dialect to your existing Thymeleaf template engine, eg:

	ServletContextTemplateResolver templateresolver = new ServletContextTemplateResolver();
	templateresolver.setTemplateMode("HTML5");
	templateresolver.setPrefix("/");

	templateengine = new TemplateEngine();
	templateengine.setTemplateResolver(templateresolver);
	templateengine.addDialect(new LayoutDialect());		// This line here

This will introduce 2 new attributes that you can use in your pages:
`layout:decorator` and `layout:fragment`.

### layout:decorator
Used only in content pages and declared in the `<html>` tag, specifies the
location of the decorator page to apply to the content page.  The mechanism for
resolving decorator pages is the same as that used by Thymeleaf to resolve
`th:fragment` and `th:substituteby` pages.

### layout:fragment
Used in both content and decorator pages.  When in a content page, specifies
sections of the page that will replace those found in the decorator page with a
matching name.  When used in a decorator page, specifies sections of the page
that will be replaced by those found in the content page with a matching name.


Example
-------

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
	  <head></head>
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


Changelog
---------

### 1.0.1
 - Switched from Ant to Gradle as a build tool and to generate Maven-compatible
   artifacts.
 - Resolved [Issue #1](thymeleaf-layout-dialect/issues/1) to appease the
   Mavenites amongst you :)  Project is now being served from Maven Central,
   co-ordinates added to [installation](#installation) instructions.

### 1.0
 - Initial release.

