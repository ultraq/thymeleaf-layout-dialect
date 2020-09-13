---
layout: default
title: replace
parent: Processors
nav_order: 4
---

replace
=======

 - XML attribute: `layout:replace`
 - Data attribute: `data-layout-replace`

Similar to `layout:insert` in that you can pass HTML content to the
template/fragments you're replacing, but with the behaviour of Thymeleaf's `th:replace`.

```html
<div layout:replace="~{modal :: modal(title='Greetings')}">
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
<section class="modal">
  <header>Greetings</header>
  <div class="modal-body">
    <p>Hi there! 👋</p>
  </div>
</section>
```

Note the `<div>` that included the `layout:replace` processor is gone.  This is
in line with how Thymeleaf's `th:replace` behaves.  If you need to keep the
original element, try [`layout:insert`]({{ site.baseurl }}{% link processors/insert.md %})
instead, whose behaviour aligns with Thymeleaf's `th:insert`.
