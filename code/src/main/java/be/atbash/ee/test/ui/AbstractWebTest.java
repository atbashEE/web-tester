/*
 * Copyright 2017 Rudy De Busscher
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
package be.atbash.ee.test.ui;

import be.atbash.ee.test.ui.browser.Browser;
import be.atbash.ee.test.ui.browser.Page;
import be.atbash.ee.test.ui.config.SwarmConfiguration;
import be.atbash.ee.test.ui.exception.UnexpectedException;
import be.atbash.ee.test.ui.external.ExternalResourceManager;
import be.atbash.ee.test.ui.javafx.ExecuteJavaFxStatement;
import be.atbash.ee.test.ui.javafx.TestApplication;
import be.atbash.ee.test.ui.plugin.WebPageFactory;
import be.atbash.ee.test.ui.runner.WebTestRunner;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.wildfly.swarm.Swarm;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Map;

/**
 *
 */
@PublicAPI
public abstract class AbstractWebTest {

    private static Swarm swarm;

    private static Browser browser;

    /**
     * Deploy the web application to a WildFly swarm instance.
     * Configuration can be provided by annotating a static method returning {@link SwarmConfiguration} @ServerConfigRule.
     *
     * @param archive
     */
    protected static void deployApplication(WebArchive archive) {

        try {

            if (WebTestRunner.swarmConfiguration != null) {
                for (Map.Entry<String, String> entry : WebTestRunner.swarmConfiguration.getConfigurationAsMap().entrySet()) {
                    swarm.withProperty(entry.getKey(), entry.getValue());
                }
            }

            swarm.start();
            swarm.deploy(archive);

        } catch (Exception e) {
            throw new UnexpectedException(String.format("There was an unexpected exception during startup of the application (with WildWly Swarm) : %s", e.getMessage()));
        }
    }

    /**
     * Experimental!
     *
     * @return
     */
    protected static ExternalResourceManager getExternalResourceManager() {
        return ExternalResourceManager.getInstance();
    }

    /**
     * Open the URL and retrieve the class capable of 'manipulating' the page.
     *
     * @param url
     * @return
     */
    protected <T extends WebPage> T openPage(String url) {
        return openPage(url, null);
    }

    /**
     * Open the URL and retrieve the class capable of 'manipulating' the page. Specifies
     * the userAgent string which needs to be used.
     *
     * @param url
     * @param userAgent
     * @return
     */
    protected <T extends WebPage> T openPage(String url, String userAgent) {
        checkBrowser();

        // navigate to page
        Page page = browser.navigate(url, userAgent);

        new ExecuteJavaFxStatement(page::show, "Show 'Browser page' with JavaFX").doExecute();

        T result;
        WebPageFactory webPageFactory = WebTestRunner.factories.get(WebTestRunner.technologyName);
        if (webPageFactory == null) {
            result = (T) new WebPage(browser, page);
        } else {
            result = webPageFactory.createWebPage(browser, page);
        }
        return result;
    }

    private void checkBrowser() {
        if (browser == null) {
            browser = TestApplication.getBrowserEngine();
            configureCookieManager();
        }
    }

    private void configureCookieManager() {
        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
    }

    @BeforeClass
    public static void startTestClass() {
        try {
            swarm = new Swarm();
        } catch (Exception e) {
            throw new UnexpectedException(String.format("There was an unexpected exception during the creation of the WildFly Swarm container : %s", e.getMessage()));
        }
    }

    @After
    public void endTest() {
        if (browser != null) {
            browser.clearCookies();
        }
    }

    @AfterClass
    public static void endTestClass() {
        if (browser != null) {
            browser.shutdown();
            browser = null;
        }

        if (swarm != null) {

            try {
                swarm.stop();
            } catch (Exception e) {
                throw new UnexpectedException(String.format("There was an unexpected exception during the shutdown of the WildFly Swarm container : %s", e.getMessage()));
            } finally {
                swarm = null;
            }
        }
    }

}
