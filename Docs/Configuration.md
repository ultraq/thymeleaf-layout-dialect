
Configuration
-------------

### `<head>` element merging

By default, when decorating the `<head>` sections of the content and decorator
templates, the result is that the content elements will come after the decorator
ones.  Some use cases need a smarter merging of elements, such as grouping like
elements together (having scripts with scripts and stylesheets with stylesheets).
The Layout dialect supports both of these use cases, with the ability for
developers to define their own sorting.

This sorting is exposed by the `nz.net.ultraq.thymeleaf.decorators.SortingStrategy`
interface and the layout dialect provides 2 implementations to choose from:

 - `AppendingStrategy`, the default, appends content `<head>` elements after
   decorator ones
 - `GroupingStrategy`, groups like elements together

To change to the grouping strategy, configure the Layout dialect like so:

```java
TemplateEngine templateEngine = new TemplateEngine();
templateEngine.addDialect(new LayoutDialect(new GroupingStrategy()));
```

Spring XML:

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

If neither strategy suits your needs, you can implement your own `SortingStrategy`
and pass it along to the Layout dialect like above.
