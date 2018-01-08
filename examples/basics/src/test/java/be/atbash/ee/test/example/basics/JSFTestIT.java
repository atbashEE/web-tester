/*
 * Copyright 2017-2018 Rudy De Busscher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.atbash.ee.test.example.basics;

import be.atbash.ee.test.ui.AbstractWebTest;
import be.atbash.ee.test.ui.WebPage;
import be.atbash.ee.test.ui.dom.PageElement;
import be.atbash.ee.test.ui.runner.WebTestRunner;
import be.atbash.ee.test.ui.webarchive.WebArchiveBuilder;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
@RunWith(WebTestRunner.class)
public class JSFTestIT extends AbstractWebTest {


    /*
    @ServerConfigRule
    public static SwarmConfiguration getConfiguration() {
        SwarmConfiguration result = new SwarmConfiguration();
        result.setContextRoot("/test");
        return result;
    }
    */

    @BeforeClass
    public static void deploy() {
        WebArchive archive = WebArchiveBuilder.create("test.war")
                .addClass(HelloBean.class)
                .addWebPage("helloWorld.xhtml")
                .build();

        deployApplication(archive);

    }

    @Test
    public void checkHelloWorld() {

        WebPage webPage = openPage("http://localhost:8080/helloWorld.xhtml");

        webPage.checkPageTitle("Hello world web tester for JSF");

        PageElement inputElement = webPage.getElementById("#test:name");
        webPage.sendKeys(inputElement, "Atbash");

        PageElement buttonElement = webPage.getElementById("#test:greetBtn");
        webPage.guardClick(buttonElement).click();

        Optional<String> text = webPage.getElementById("#test:greetText").getText();
        assertThat(text.isPresent()).isTrue();
        assertThat(text.get()).isEqualTo("Hello Atbash");

    }

}
