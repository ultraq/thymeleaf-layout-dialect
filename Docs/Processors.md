
Processors
==========

### decorator

 - XML attribute: `layout:decorator`
 - Data attribute: `data-layout-decorator`

Used in your content templates and declared in the root tag (usually `<html>`),
this processor specifies the location of the decorator template to apply.  The
mechanism for resolving decorator pages is the same as that used by Thymeleaf to
resolve `th:fragment` and `th:replace` templates.

Check out the [Decorators and fragments](#decorators-and-fragments) example for
how to apply a decorator to your content templates.

### include

 - XML attribute: `layout:include`
 - Data attribute: `data-layout-include`

Similar to Thymeleaf's `th:include`, but allows the passing of entire element
fragments to the included template.  Useful if you have some HTML that you want
to reuse, but whose contents are too complex to determine or construct with
context variables alone.

Check out the [Includes and fragments](#includes-and-fragments) example for how
to pass HTML code to the templates you want to include.

### replace

 - XML attribute: `layout:replace`
 - Data attribute: `data-layout-replace`

Similar to `layout:include` in that you can pass HTML content to the
template/fragments you're replacing, but with the behaviour of Thymeleaf's `th:replace`.

### fragment

 - XML attribute: `layout:fragment`
 - Data attribute: `data-layout-fragment`

The glue that holds everything together; it marks sections in the decorator
template that can be replaced by sections in the content template, which share
the same name.

### title-pattern

 - XML attribute: `layout:title-pattern`
 - Data attribute: `data-layout-title-pattern`

Allows for greater control of the resulting `<title>` element by specifying a
pattern with some special tokens.  This can be used to extend the decorator's
title with the content's one, instead of simply overriding it.

Check out the [Configuring your Title](#configuring-your-title) example for how
to control the final title of your page.
