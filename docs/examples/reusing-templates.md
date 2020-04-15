---
layout: default
title: Reusable templates
parent: Examples
nav_order: 4
---

Reusable templates
==================

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
