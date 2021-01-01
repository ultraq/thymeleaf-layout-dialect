
Changelog
=========

As of 1.3.0, this project follows [Semantic Versioning](http://semver.org/).


### 2.5.2

 - Fix from [@silkentrance](https://github.com/silkentrance) for re-declared
   layout fragments in deep hierarchies, where the layout dialect would return
   the wrong fragment from the template hierarchy
   ([#200])(https://github.com/ultraq/thymeleaf-layout-dialect/issues/200)


### 2.5.1

 - Fix POM generation issue that declared dependencies as `runtime` instead of
   `compile`
   ([#208](https://github.com/ultraq/thymeleaf-layout-dialect/issues/208))


### 2.5.0

 - Upgrade Groovy dependency to 2.5.x, fixing bytecode compatibility issues with
   JDK11+ in certain situations
   ([#205](https://github.com/ultraq/thymeleaf-layout-dialect/issues/205))
 - Groovy metaprogramming enhancements to Thymeleaf objects is now done via
   [extension modules](http://groovy-lang.org/metaprogramming.html#_extension_modules).
   This has led to a slight improvement in performance: ~10% faster to complete
   the included benchmark project on JDK8, memory footprint remains unchanged.
 - Fixed the fragment cache not being cleared when dispatching/forwarding to the
   error page
   ([#189](https://github.com/ultraq/thymeleaf-layout-dialect/issues/189))
 - Revamped the documentation website on https://ultraq.github.io/thymeleaf-layout-dialect/
   ([#204](https://github.com/ultraq/thymeleaf-layout-dialect/issues/204))
 - Rolled the benchmark project into this repo so that performance testing can
   be done right next to the code
   ([#192](https://github.com/ultraq/thymeleaf-layout-dialect/issues/192))
 - Migrated unit tests from JUnit to Spock
   ([#193](https://github.com/ultraq/thymeleaf-layout-dialect/issues/193))


### 2.4.1

 - Fix missing `positions` value from `GroupingStrategy` sorting method
   ([#185](https://github.com/ultraq/thymeleaf-layout-dialect/issues/185))


### 2.4.0
 - Allow passing of values up to layout templates using fragment parameters
   ([#157](https://github.com/ultraq/thymeleaf-layout-dialect/issues/157))
 - Updated how `<head>` element sorting was done so that the `<title>` can now
   optionally be put as the first element, instead of always making it first.
   Also created 2 new sorting strategies that reflect this respecting of the
   `<title>` and other element positions.
   ([#176](https://github.com/ultraq/thymeleaf-layout-dialect/issues/176))
 - Fixed a bug around nested elements which arose in 2.3.0
   ([#178](https://github.com/ultraq/thymeleaf-layout-dialect/issues/178))
 - Fixed a bug when self-closing `<html>` tags are used as a root element
   ([#173](https://github.com/ultraq/thymeleaf-layout-dialect/issues/173))
 - Added an experimental option to allow developers to opt-out of the automatic
   `<head>` merging that normally occurs, useful if wanted to manage that
   section using other Thymeleaf processors
   ([#165](https://github.com/ultraq/thymeleaf-layout-dialect/issues/165))


### 2.3.0

 - Verification that the layout dialect plays well with Java 9 and Spring 5,
   involved a patch upgrade of Groovy to 2.4.13
   ([#161](https://github.com/ultraq/thymeleaf-layout-dialect/issues/161))
 - Contribution from [@Vineg](https://github.com/Vineg) to add a
   `layout:collect`/`data-layout-collect` processor that accumulates encountered
   fragments of the same name.  Documentation is still pending, but eager devs
   can take a look at the PR for more details
   ([#166](https://github.com/ultraq/thymeleaf-layout-dialect/pull/166))
 - A few tool updates for linting, automated testing, and code coverage w/
   Travis CI (included dropping the JDK7 build as I could no longer get a
   supported configuration working on Travis, but the layout dialect still aims
   to support Java 7 for the remainder of the 2.x releases).


### 2.2.2

 - Another decorate processor root element check fix for deep hierarchies that
   include a `th:with` attribute processor (which gets mutated by the various)
   decoration processes)
   ([#127](https://github.com/ultraq/thymeleaf-layout-dialect/issues/127))
 - Updated [thymeleaf-expression-processor](https://github.com/ultraq/thymeleaf-expression-processor),
   which includes a `null` check for parsing fragment expressions, a potential
   fix for ([#151](https://github.com/ultraq/thymeleaf-layout-dialect/issues/151))


### 2.2.1

 - Fix decorate processor root element check when interacting with high-priority
   custom dialects
   ([#127](https://github.com/ultraq/thymeleaf-layout-dialect/issues/127))


### 2.2.0

 - Rework how titles are handled to support inline expressions in Thymeleaf 3
   ([#145](https://github.com/ultraq/thymeleaf-layout-dialect/issues/145))
 - Now that the complete title cannot be known during execution of the title
   pattern processor, remove the exposed "layout context" object which contained
   the title values.
   ([#147](https://github.com/ultraq/thymeleaf-layout-dialect/issues/147))
 - Implement a more accurate way to check if the decorate processor is in the
   root element of a template
   ([#127](https://github.com/ultraq/thymeleaf-layout-dialect/issues/127))


### 2.1.2

 - Fix insertion of elements into a `<head>` section that is empty, ie: `<head></head>`
   ([#144](https://github.com/ultraq/thymeleaf-layout-dialect/issues/144))


### 2.1.1

 - Simplify and fix the "model level" counting algorithm after a better
   understanding of attoparser and how it works
   ([#142](https://github.com/ultraq/thymeleaf-layout-dialect/issues/142),
   [#143](https://github.com/ultraq/thymeleaf-layout-dialect/issues/143))


### 2.1.0

 - Be less strict with HTML templates that are auto-balanced by Attoparser
   (usually a result of not knowing which HTML elements cause auto-closing
   behaviours), instead only using tags that are in the original templates to
   influence the "model level".  While this was a great tool for learning more
   about the HTML spec when it errors, it is more in line with how Thymeleaf
   behaves
   ([#138](https://github.com/ultraq/thymeleaf-layout-dialect/issues/138))
 - Reveal the processed content and layout title values on the `layout` object
   ([#137](https://github.com/ultraq/thymeleaf-layout-dialect/issues/137))
 - **Huge** improvements to the memory profile of the layout dialect
   ([#102](https://github.com/ultraq/thymeleaf-layout-dialect/issues/102),
   [#139](https://github.com/ultraq/thymeleaf-layout-dialect/issues/139))

What follows is a summary of the performance imrovements in 2.1.0.  For details
such as the test methodology and changes made, see the full release notes at:
https://github.com/ultraq/thymeleaf-layout-dialect/releases/tag/2.1.0

#### Thymeleaf Layout Dialect 2.0.4

![memory usage 2 0 4](https://cloud.githubusercontent.com/assets/1686920/20034461/c17b6eb8-a423-11e6-8fe0-d2a5572f3b8c.png)

Main takeaways:
 - The JMeter test took about 3 minutes to complete (started around the 30
   second mark), with requests taking an average of 1.674 seconds each
 - Old generation space at 99MB
 - 35 garbage collections
 - 27 million object allocations
 - 4 seconds spent in GC
 - Several items taking over 10MB of retained memory (none of them appearing as
   dominators however, so are potentially GC'able, but don't seem to have been collected)

   ![screen shot 2016-11-06 at 1 33 32 pm](https://cloud.githubusercontent.com/assets/1686920/20034519/aaf7123a-a425-11e6-9a87-857bd3960dc1.png)

 - Majority of the object allocations taking place in the `IModelExtensions.findModel`
   closure, which uses a Groovy feature of dynamic metaclass creation

   ![screen shot 2016-11-06 at 1 36 02 pm](https://cloud.githubusercontent.com/assets/1686920/20034527/152d07b8-a426-11e6-9a1c-78143ed44895.png)

#### Thymeleaf Layout Dialect 2.1.0

![memory usage 2 1 0-snapshot](https://cloud.githubusercontent.com/assets/1686920/20034539/9449f510-a426-11e6-86cc-0fa2ab52e8fe.png)

Differences:
 - The JMeter test took about 1 minute to complete (also started around the 30
   second mark), with requests taking an average of 452ms to complete **(at
   least 3x faster)**
 - Old generation space at 22MB **(memory footprint 1/5th the size)**
 - 21 garbage collections **(40% less GCs)**
 - 7.1 million object allocations **(74% less objects created)**
 - 1 second spent in GC **(75% less time spent in GC)**
 - Only 1 item taking over 10MB of retained memory (dominator profile looking
   mostly the same however)

   ![screen shot 2016-11-06 at 1 51 37 pm](https://cloud.githubusercontent.com/assets/1686920/20034609/6496e1c8-a428-11e6-9678-4544303b5d97.png)

 - Majority of the object allocations no longer in a Groovy dynamic meta class
   method, but in one of Thymeleaf's utility projects, [unbescape](http://www.unbescape.org/)

   ![screen shot 2016-11-06 at 1 54 42 pm](https://cloud.githubusercontent.com/assets/1686920/20034622/cf95185a-a428-11e6-8999-894cabde6cde.png)


### 2.0.5

 - Upgrade thymeleaf-expression-processor to 1.1.2, which includes a fix for
   multiline fragment expressions
   ([#140](https://github.com/ultraq/thymeleaf-layout-dialect/issues/140))
 - Use Thymeleaf's `AssignationUtils` class for parsing variable declarations so
   that variable declarations behave the same way they do in Thymeleaf
   ([#126](https://github.com/ultraq/thymeleaf-layout-dialect/issues/126))


### 2.0.4

 - Have the `layout:title-pattern` processor work when using `th:utext`/`data-th-utext`
   on the `<title>` tag as well
   ([#136](https://github.com/ultraq/thymeleaf-layout-dialect/issues/136))


### 2.0.3

 - Kill the rise in memory usage found by
   ([#134](https://github.com/ultraq/thymeleaf-layout-dialect/issues/134))


### 2.0.2

 - Convert `FragmentProcessor` from a model processor to a tag processor to get
   around model level problems when inserting fragments that may alter the model
   level
   ([#129](https://github.com/ultraq/thymeleaf-layout-dialect/issues/129))
 - Fix the merging of the `<head>` section in multi-level layouts
   ([#130](https://github.com/ultraq/thymeleaf-layout-dialect/issues/130))
 - Respect the runtime-configured prefix
   ([#103](https://github.com/ultraq/thymeleaf-layout-dialect/issues/103))


### 2.0.1

 - Fix `NullPointerException` when using any of the `include`/`insert`/`replace`
   processors with a full template fragment expression
   ([#120](https://github.com/ultraq/thymeleaf-layout-dialect/issues/120))
 - Restored support for multi-level layouts
   ([#121](https://github.com/ultraq/thymeleaf-layout-dialect/issues/121))
 - Reduced logging of backwards compatibility warnings to log just the first
   time the compatibility was encountered
   ([#124](https://github.com/ultraq/thymeleaf-layout-dialect/issues/124))
 - Improved memory usage by not being so wasteful of objects in a few parts of
   the codebase, which in turn fixed a potential memory leak
   ([#122](https://github.com/ultraq/thymeleaf-layout-dialect/issues/122))
 - Fixed a backwards compatibility problem when using `layout:decorator`
   ([#125](https://github.com/ultraq/thymeleaf-layout-dialect/issues/125))


### 2.0.0

 - Layout dialect rewritten to support Thymeleaf 3
   ([#68](https://github.com/ultraq/thymeleaf-layout-dialect/issues/68),
   [Layout Dialect 2.0 milestone](https://github.com/ultraq/thymeleaf-layout-dialect/milestone/6?closed=1))
 - `layout:decorator` processor renamed to `layout:decorate`, `$DECORATOR_TITLE`
   renamed to `$LAYOUT_TITLE`
   ([#95](https://github.com/ultraq/thymeleaf-layout-dialect/issues/95))
 - Deprecated `layout:include` processor for the new `layout:insert` processor
   ([#107](https://github.com/ultraq/thymeleaf-layout-dialect/issues/107))
 - New documentation site created to hold what was turning into a gigantic
   readme!
   ([#115](https://github.com/ultraq/thymeleaf-layout-dialect/issues/115))

Upgrading to 2.0?  I've written a migration guide to help make the transition
easier.  Check it out on the new documentation pages site, here:
https://ultraq.github.io/thymeleaf-layout-dialect/MigrationGuide.html

As of release there are still some tests that have been disabled and may
cause regressions for these use cases.  They are listed here:
https://github.com/ultraq/thymeleaf-layout-dialect/blob/d4f57d08cbf5c70a33cfa45283015811c90a3765/Tests/nz/net/ultraq/thymeleaf/tests/LayoutDialectTestExecutor.groovy#L55-L61
One is a very specific and rare case, another is an undocumented use of the
layout dialect, and the commented one I don't feel I should fix as it feels like
the incorrect use of an element.  For those first 2 cases, I'll work on them in
upcoming patches so they don't hold up the release.


### 1.4.0

 - Change the behaviour of merging `th:with` attributes so that child pages can
   override layout values
   ([#99](https://github.com/ultraq/thymeleaf-layout-dialect/issues/99))
 - Fixed lack of a 'groovydoc' archive being included with the download bundle
   and Maven artifacts


### 1.3.3

 - Don't copy collapsible whitespace between `<head>` elements during decoration
   ([#92](https://github.com/ultraq/thymeleaf-layout-dialect/issues/92))


### 1.3.2

 - Prevent `IllegalArgumentException` when using a template with a condensed
   `<head>` section
   ([#90](https://github.com/ultraq/thymeleaf-layout-dialect/issues/90))
 - Fix fragment scoping so that fragments from one decorator/include/replace
   operation aren't shared with other ones
   ([#87](https://github.com/ultraq/thymeleaf-layout-dialect/issues/87))


### 1.3.1

 - Restore Java 6 support
   ([#85](https://github.com/ultraq/thymeleaf-layout-dialect/issues/85))


### 1.3.0

 - Dialect rewritten in Groovy
 - Dropped the deprecated `substituteby` processor
 - Allow the title to be completely processed
   ([#72](https://github.com/ultraq/thymeleaf-layout-dialect/issues/72)) before
   expanding the value of `title-pattern`, fixing cases like that described in
   ([#64](https://github.com/ultraq/thymeleaf-layout-dialect/issues/64))
 - Split out the `<head>` element sorting into a customizable sorting strategy,
   and provided strategies for each of the behaviours that the layout dialect
   has implemented in the past.  See the
   [<head> element merging](https://github.com/ultraq/thymeleaf-layout-dialect#head-element-merging)
   section in the README for more details.


### 1.2.9

 - Have all non-element nodes (text, comments) included in the merging of the
   `<head>` section
   ([#70](https://github.com/ultraq/thymeleaf-layout-dialect/issues/70))


### 1.2.8

 - Contribution from [Oliver Niekrenz](https://github.com/oniekrenz) to fix a
   recursion issue in the way fragments are picked out of templates
   ([#60](https://github.com/ultraq/thymeleaf-layout-dialect/pull/60))


### 1.2.7

 - Fix for `ArrayIndexOutOfBoundsException`s coming out of the grouping feature
   introduced in 1.2.6
   ([#54](https://github.com/ultraq/thymeleaf-layout-dialect/issues/54))


### 1.2.6

 - Reduce the amount of whitespace copied surrounding the `<html>` tag
   ([#50](https://github.com/ultraq/thymeleaf-layout-dialect/issues/50))
 - Start logic to group like elements in the merged `<head>` section together
   ([#52](https://github.com/ultraq/thymeleaf-layout-dialect/issues/52))


### 1.2.5

 - Restore title fallback behaviour when either content or decorator title
   elements are empty
   ([#45](https://github.com/ultraq/thymeleaf-layout-dialect/issues/45))
 - Fix title creation when using `th:text` without a `layout:title-pattern`
   processor
   ([#46](https://github.com/ultraq/thymeleaf-layout-dialect/pull/46))


### 1.2.4

 - Contribution from [Justin Munn](https://github.com/munnja001) to fix bugs in
   the `layout:title-pattern` processor, making it behave more naturally in the
   process
   ([#42](https://github.com/ultraq/thymeleaf-layout-dialect/pull/42))


### 1.2.3

 - Fix for nested layout fragments using the replace/substituteby processors
   ([#37](https://github.com/ultraq/thymeleaf-layout-dialect/issues/37))
 - Don't pass arbitrary attributes (ie: anything that isn't an attribute
   processor) up to decorator pages if the content template is a fragment
   ([#39](https://github.com/ultraq/thymeleaf-layout-dialect/issues/39))


### 1.2.2

 - Allow passing variables for all document types, not just full HTML templates
   ([#32](https://github.com/ultraq/thymeleaf-layout-dialect/issues/32))
 - Remove the reliance on Thymeleaf's [literal substitutions](http://www.thymeleaf.org/whatsnew21.html#lsub)
   feature for the `layout:title-pattern` processor, allowing devs to use the
   pipe `|` character in their title patterns
   ([#33]((https://github.com/ultraq/thymeleaf-layout-dialect/issues/33)))
 - Fix the passing of decorator/content title values up deep layout hierarchies
   ([#34](https://github.com/ultraq/thymeleaf-layout-dialect/issues/34))


### 1.2.1

 - Restore `layout:decorator` behaviour when using it for deep page hierarchies
   in Thymeleaf 2.1.2
   ([#30](https://github.com/ultraq/thymeleaf-layout-dialect/issues/30))


### 1.2

 - Works with Thymeleaf 2.1
   ([#26](https://github.com/ultraq/thymeleaf-layout-dialect/issues/26))
 - `layout:title-pattern` now works on title text created using the `th:text`
   attribute
   ([#28](https://github.com/ultraq/thymeleaf-layout-dialect/issues/28))


### 1.1.4

 - Minor bugfixes, small refactoring, and a large cleanup as a result of the
   work done towards being compatible with Thymeleaf 2.1.


### 1.1.3

 - Refactored handling of `<title>` elements for when they're lacking in either
   content or decorator templates and the `layout:title-pattern` processor is
   being used
   ([#25](https://github.com/ultraq/thymeleaf-layout-dialect/issues/25))
 - Added a `layout:replace` attribute processor, an alias of `layout:substituteby`
   (Just like Thymeleaf, the `replace` processor will eventually replace `substituteby`.
   They both perform the same function, but `substituteby` is effectively
   deprecated.)
 - Updated the Eclipse plugin help file to point to the updated 1.1.x processor
   locations.


### 1.1.2

 - Relaxed the root element restriction when using the `LEGACYHTML5` template
   mode due to the way the NekoHTML parser works on HTML fragments
   ([#23](https://github.com/ultraq/thymeleaf-layout-dialect/issues/23))


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
 - Added a `layout:substituteby` processor which works much like `layout:include`,
   but with the behaviour of Thymeleaf's `th:substituteby`
   ([#21](https://github.com/ultraq/thymeleaf-layout-dialect/issues/21))


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
 - Made it so that content outside the decorator page's `<html>` element (like
   IE conditional comments) are included in the resulting page
   ([#10](https://github.com/ultraq/thymeleaf-layout-dialect/issues/10))


### 1.0.4

 - Fixed enforcing of `layout:decorator` element being in the root element,
   leading to a better error message if this attribute is found elsewhere.
 - Fixed the resulting JAR which didn't recreate the proper Maven metadata in
   the manifest section and might have caused it to not be picked up by tools
   like m2eclipse.
 - Fix a `ClassCastException` for cases when `layout:include` was used to
   include entire pages
   ([#7](https://github.com/ultraq/thymeleaf-layout-dialect/issues/7))


### 1.0.3

 - Added a `layout:include` attribute which works like `th:include` but allows
   for the passing of element fragments to the included page.
 - Allow `th:with` local variable declarations made in the decorator page to be
   visible in content pages during processing
   ([#3](https://github.com/ultraq/thymeleaf-layout-dialect/issues/3))
 - Removed the restriction that the `layout:decorator` tag appear in an HTML
   element since Thymeleaf 2.0.10 relaxed that restriction too (tag must still
   appear in the root element of your page however)
   ([#4](https://github.com/ultraq/thymeleaf-layout-dialect/issues/4))
 - Updated Thymeleaf dependency to 2.0.11 for a required API change.


### 1.0.2

 - Allow a decorator and content pages to contain just a `<head>` section, or
   just a `<body>` section, or neither section, or some other combination
   between pages
   ([#2](https://github.com/ultraq/thymeleaf-layout-dialect/issues/2))


### 1.0.1

 - Switched from Ant to Gradle as a build tool and to generate Maven-compatible
   artifacts.
 - To appease the Mavenites amongst you, the project is now being served from
   Maven Central, co-ordinates added to [installation](#installation)
   instructions
   ([#1](https://github.com/ultraq/thymeleaf-layout-dialect/issues/1))


### 1.0

 - Initial release.
