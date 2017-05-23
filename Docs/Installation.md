
Installation
============

Minimum of Java 7 and Thymeleaf 3.0 required.


### Standalone distribution

Copy the JAR from [one of the release bundles](https://github.com/ultraq/thymeleaf-layout-dialect/releases),
placing it in the classpath of your application, or build the project from the
source code on GitHub.


### For Maven and Maven-compatible dependency managers

Add a dependency to your project with the following co-ordinates:

 - GroupId: `nz.net.ultraq.thymeleaf`
 - ArtifactId: `thymeleaf-layout-dialect`
 - Version: (see the [project releases](https://github.com/ultraq/thymeleaf-layout-dialect/releases)
   for a list of available versions)


### Usage

Once downloaded/installed as part of your project, add the Layout dialect to
your existing Thymeleaf template engine.

For those who are configuring their own Thymeleaf template engine:

```java
TemplateEngine templateEngine = new TemplateEngine();  // Or SpringTemplateEngine for Spring
templateEngine.addDialect(new LayoutDialect());
```

For those using XML config in Spring:

```xml
<bean id="templateEngine" class="org.thymeleaf.spring4.SpringTemplateEngine">
  <property name="additionalDialects">
    <set>
      <bean class="nz.net.ultraq.thymeleaf.LayoutDialect"/>
    </set>
  </property>
</bean>
```

For those using Spring Boot 2 and Java configuration:

```java
@Bean
public LayoutDialect layoutDialect() {
	return new LayoutDialect();
}
```

> The layout dialect is already included as part of the Thymeleaf starter pack
> in Spring Boot 1.x, but has been removed in Spring Boot 2, hence the
> additional config step for Spring Boot 2 users above.

This will introduce the `layout` namespace, and 5 new attribute processors that
you can use in your templates: `decorate`, `title-pattern`, `insert`, `replace`,
and `fragment`.
