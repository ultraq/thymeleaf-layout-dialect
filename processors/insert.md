---
layout: default
title: insert
parent: Processors
nav_order: 3
---

insert
======

 - XML attribute: `layout:insert`
 - Data attribute: `data-layout-insert`

Similar to Thymeleaf's `th:insert`, but allows the passing of entire template
fragments to the target template.  Useful if you have some HTML that you want to
reuse but whose contents are best constructed in the context of the calling
template.

```html
<div layout:insert="~{modal :: modal(title='Greetings')}">
  <p layout:fragment="modal-content">Hi there! 👋</p>
</div>
```


Example
-------

Say you want to build a modal template that can be reused from several places.
You make it generic with placeholders for the header and body content, allowing
the body to be defined by the calling template:

```html
modal.html

<section class="modal" layout:fragment="modal(title)">
  <header th:text="${title}">Title goes here</header>
  <div class="modal-body">
    <div layout:fragment="modal-content">
      Content goes here
    </div>
  </div>
</section>
```

If we took the HTML code at the top of this page to call this modal template,
the result would be this:

```html
<div>
  <section class="modal">
    <header>Greetings</header>
    <div class="modal-body">
      <p>Hi there! 👋</p>
    </div>
  </section>
</div>
```

Note the surrounding `<div>` from the calling template remains.  This is in line
with how Thymeleaf's `th:insert` behaves.  One way to remove the `<div>` would
be to use [`layout:replace`]({{ site.baseurl }}{% link processors/replace.md %})
instead, whose behaviour aligns with Thymeleaf's `th:replace`.
