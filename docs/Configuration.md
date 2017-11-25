
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
interface and the layout dialect provides 2 implementations to choose from:

 - `nz.net.ultraq.thymeleaf.decorators.strategies.AppendingStrategy`, the
   default, appends content `<head>` elements after layout ones
 - `nz.net.ultraq.thymeleaf.decorators.strategies.GroupingStrategy`, groups like
   elements together

To change to the grouping strategy, configure the Layout dialect using one of
the methods below.

For those who are configuring their own Thymeleaf template engine:

```java
TemplateEngine templateEngine = new TemplateEngine();  // Or SpringTemplateEngine for Spring
templateEngine.addDialect(new LayoutDialect(new GroupingStrategy()));
```

For those using XML config in Spring:

```xml
<bean id="groupingStrategy" class="nz.net.ultraq.thymeleaf.decorators.strategies.GroupingStrategy"/>

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
	return new LayoutDialect(new GroupingStrategy());
}
```

If neither strategy suits your needs, you can implement your own `SortingStrategy`
and pass it along to the layout dialect like above.
