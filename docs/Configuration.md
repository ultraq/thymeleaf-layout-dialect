
Configuration
=============


`<head>` element merging
------------------------

When decorating the `<head>` sections of the content and layout templates, the
default behaviour is to place all content elements after the layout ones, eg:

```html
Content.html

<head>
  <title>Hello!</title>
  <link rel="stylesheet" href="content-stylesheet.css"/>
  <script src="content-script.js"></script>
</head>
```

```html
Layout.html

<head>
  <title>Goodbye!</title>
  <link rel="stylesheet" href="layout-stylesheet.css"/>
  <script src="layout-script.js"></script>
</head>
```

Result:

```html
<head>
  <title>Hello!</title>
  <link rel="stylesheet" href="layout-stylesheet.css"/>
  <script src="layout-script.js"></script>
  <link rel="stylesheet" href="content-stylesheet.css"/>
  <script src="content-script.js"></script>
</head>
```

However, sometimes we need a smarter merging of elements, such as grouping like
elements together (having scripts with scripts and stylesheets with stylesheets).

```html
Content.html

<head>
  <title>Hello!</title>
  <link rel="stylesheet" href="content-stylesheet.css"/>
  <script src="content-script.js"></script>
</head>
```

```html
Layout.html

<head>
  <title>Goodbye!</title>
  <link rel="stylesheet" href="layout-stylesheet.css"/>
  <script src="layout-script.js"></script>
</head>
```

Result:

```html
<head>
  <title>Hello!</title>
  <link rel="stylesheet" href="layout-stylesheet.css"/>
  <link rel="stylesheet" href="content-stylesheet.css"/>
  <script src="layout-script.js"></script>
  <script src="content-script.js"></script>
</head>
```

The Layout dialect can be configured to support either use case, with the
ability for developers to define their own sorting.

This sorting is exposed by the `nz.net.ultraq.thymeleaf.decorators.SortingStrategy`
interface and the layout dialect provides 4 implementations to choose from:

 - `nz.net.ultraq.thymeleaf.decorators.strategies.AppendingStrategy`, the
   default, appends content `<head>` elements after layout ones.  Deprecated.
 - `nz.net.ultraq.thymeleaf.decorators.strategies.GroupingStrategy`, groups like
   elements together.  Deprecated.
 - `nz.net.ultraq.thymeleaf.decorators.strategies.AppendingRespectLayoutTitleStrategy`,
   a new variant of the `AppendingStrategy` which respects the position of the
   `<title>` element based on its position in the layout template.
 - `nz.net.ultraq.thymeleaf.decorators.strategies.GroupingRespectLayoutTitleStrategy`,
   a new variant of the `GroupingStrategy` which respects the position of the
   `<title>` element based on its position in the layout template.

> The default behaviour of the layout dialect has historically been to place the
> `<title>` element at the beginning of the `<head>` element during the
> decoration process; an arbitrary design decision which made development of
> this library easier.  However, this runs against the expectations of
> developers who wished to control the order of elements, most notably the
> practice of putting `<meta charset...>` as the first element in the `<head>`.
> The new strategies (ones with `RespectLayoutTitle` in their name) instead keep
> `<title>`s wherever they exist within the target/layout template being
> decorated, and then work on everything else as normal.
> 
> The `RespectLayoutTitle` strategies will become the default strategies from
> version 3.x.  The existing strategies are deprecated but have been updated to
> retain the historic behaviour, maintaining backwards compatibility with the
> 2.x versions.

To change to the grouping strategy, configure the Layout dialect using one of
the methods below.

For those who are configuring their own Thymeleaf template engine:

```java
TemplateEngine templateEngine = new TemplateEngine();  // Or SpringTemplateEngine for Spring
templateEngine.addDialect(new LayoutDialect(new GroupingRespectLayoutTitleStrategy()));
```

For those using XML config in Spring:

```xml
<bean id="groupingStrategy" class="nz.net.ultraq.thymeleaf.decorators.strategies.GroupingRespectLayoutTitleStrategy"/>

<bean id="templateEngine" class="org.thymeleaf.spring4.SpringTemplateEngine">
  <property name="additionalDialects">
    <set>
      <bean class="nz.net.ultraq.thymeleaf.LayoutDialect">
        <constructor-arg ref="groupingStrategy"/>
      </bean>
    </set>
  </property>
</bean>
```

For those using Spring Boot and Java configuration:

```java
@Bean
public LayoutDialect layoutDialect() {
	return new LayoutDialect(new GroupingRespectLayoutTitleStrategy());
}
```

If neither strategy suits your needs, you can implement your own `SortingStrategy`
and pass it along to the layout dialect like above.
