# web-tester
JavaFX Web tester

## License

Project is available under "Apache License Version 2"

Some concepts and code snippets come from the UI4J, https://github.com/webfolderio/ui4j.

## Limitations

It only runs on Oracle JDK 8u40 and later.
It will not run on Java 9 (as it uses internal classes)
It will only run on Oracle JDK (restriction will be removed as soon as possible)

## Example usage.

* Create a Test case which extends from **AbstractWebTest** and which will be executed by the custom **WebTestRunner**

```java
@RunWith(WebTestRunner.class)
public class JSFTestIT extends AbstractWebTest {
    
}
``` 

* Create the WAR file with the code required for testing.  It uses ShrinkWrap under the hood, but a custom builder, **WebArchiveBuilder** can be used.

```java
    WebArchive archive = WebArchiveBuilder.create("test.war")
            .addClass(HelloBean.class)
            .addWebPage("helloWorld.xhtml")
            .build();
``` 

* Deploy the Archive with WildFly Swarm within a **@BeforeClass** static method.

```java
    @BeforeClass
    public static void deploy() {
        WebArchive archive = WebArchiveBuilder.create.... ;

        deployApplication(archive);
    }
``` 

* Open a web page within the _'browser'_.

```java
    @Test
    public void checkHelloWorld() {

        WebPage webPage = openPage("http://localhost:8080/helloWorld.xhtml");
    }

```
