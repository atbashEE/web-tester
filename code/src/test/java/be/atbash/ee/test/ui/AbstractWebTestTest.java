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
import be.atbash.ee.test.ui.plugin.WebPageFactory;
import be.atbash.ee.test.ui.runner.ControlJavaFX;
import be.atbash.ee.test.ui.runner.WebTestRunner;
import be.atbash.ee.test.util.ReflectionUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractWebTestTest {

    private String url = "someURL";
    private String userAgent = "theUserAgent";

    @Mock
    private Browser browserMock;

    @Mock
    private Page pageMock;

    @Mock
    private WebPageFactory webPageFactoryMock;

    @BeforeClass
    public static void setup() throws Throwable {
        ControlJavaFX.start();
    }

    @AfterClass
    public static void teardown() {
        ControlJavaFX.shutdown();
    }

    private AbstractWebTest test = new AbstractWebTest() {
    };

    @Test
    public void openPage() throws Throwable {
        WebTestRunner.factories.clear();

        ReflectionUtil.setFieldValue(test, "browser", browserMock);
        when(browserMock.navigate(url, userAgent)).thenReturn(pageMock);

        WebTestRunner.technologyName = "";
        WebPage webPage = test.openPage(url, userAgent);

        assertThat(webPage).isExactlyInstanceOf(WebPage.class);
    }

    @Test
    public void openPage_fromFactory() throws Throwable {
        WebTestRunner.factories.clear();

        ReflectionUtil.setFieldValue(test, "browser", browserMock);
        when(browserMock.navigate(url, userAgent)).thenReturn(pageMock);

        WebTestRunner.factories.put("key", webPageFactoryMock);
        WebTestRunner.technologyName = "key";
        when(webPageFactoryMock.createWebPage(any(Browser.class), any(Page.class))).thenReturn(new TestWebPage(null, null));

        WebPage webPage = test.openPage(url, userAgent);

        assertThat(webPage).isExactlyInstanceOf(TestWebPage.class);
    }

    private class TestWebPage extends WebPage {

        protected TestWebPage(Browser browser, Page page) {
            super(browser, page);
        }
    }
}
