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

import be.atbash.ee.test.ElementAssert;
import be.atbash.ee.test.ui.browser.Browser;
import be.atbash.ee.test.ui.browser.Page;
import be.atbash.ee.test.ui.dom.PageElement;
import be.atbash.ee.test.ui.javafx.ExecuteJavaFxStatement;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
@PublicAPI
public class WebPage {

    protected Browser browser;

    protected Page page;

    protected WebPage(Browser browser, Page page) {
        this.browser = browser;
        this.page = page;
    }

    public void checkPageTitle(String pageTitle) {
        String title = page.getWebDocument().getTitle().get();
        assertThat(title).isEqualTo(pageTitle);
    }

    public List<PageElement> getElementsByTag(String tagName) {
        return page.getWebDocument().queryAll(tagName);
    }

    public List<String[]> getTableContents(String tableId, int columns) {
        Optional<PageElement> element = page.getWebDocument().query(tableId);
        ElementAssert.assertThat(element).exists();

        List<PageElement> tableCells = element.get().find("td");
        List<String[]> result = new ArrayList<>();
        int idx = 0;
        String[] item = new String[columns];
        for (PageElement tableCell : tableCells) {
            item[idx++] = getContent(tableCell);
            if (idx == columns) {
                result.add(item);
                item = new String[columns];
                idx = 0;
            }
        }
        return result;
    }

    private String getContent(PageElement element) {
        String result = null;
        if (element.getText().isPresent()) {
            result = element.getText().get().replaceAll("\n", "").trim();
        }
        return result;
    }

    public PageElement getElementById(String id) {
        Optional<PageElement> element = page.getWebDocument().query(id);

        ElementAssert.assertThat(element).exists();
        // The previous assertThat verifies if the Optional contains if value.
        // So calling get() here is safe.
        return element.get();

    }

    public PageElement getOptionalElementById(String id) {

        Optional<PageElement> element = page.getWebDocument().query(id);
        return element.orElse(null);
    }

    public void checkPageContains(String content) {
        Object html = page.executeScript("document.body.innerHTML");

        assertThat(html.toString()).contains(content);
    }

    public String getPageContents() {
        return page.executeScript("document.body.innerHTML").toString();
    }

    public void checkPageDoesNotContains(String content) {
        Object html = page.executeScript("document.body.innerHTML");

        assertThat(html.toString()).doesNotContain(content);
    }

    public GuardClick guardClick(PageElement element) {
        return new WebPage.DefaultGuardClick(element, page);
    }

    public List<HttpCookie> getCookies() {
        return browser.getCookies();
    }

    public void sendKeys(PageElement element, String text) {

        new ExecuteJavaFxStatement(element::focus, "Web element focus").doExecute();

        browser.sendKeys(text);
    }

    private class DefaultGuardClick implements GuardClick {
        private PageElement element;
        private Page page;

        DefaultGuardClick(PageElement element, Page page) {
            this.element = element;
            this.page = page;
        }

        @Override
        public void click() {

            clickElement();
            try {
                page.getDocumentLoadedQueue().poll(1, TimeUnit.SECONDS);
                Thread.sleep(500);  // So that the WebView can render the webDocument
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        private void clickElement() {
            new ExecuteJavaFxStatement(() -> element.click(), "Web element click").doExecute();

        }

    }
}
