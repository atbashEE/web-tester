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
package be.atbash.ee.test.ui.dom;

import be.atbash.ee.test.ui.browser.PageContext;
import be.atbash.ee.test.ui.spi.JavaScriptEngine;
import javafx.scene.web.WebEngine;
import org.w3c.dom.Document;

import java.util.List;
import java.util.Optional;

/**
 * Representation of the HTML document.
 * <p>
 * Concept from UI4J.
 */
public class WebDocument {

    private JavaScriptEngine engine;

    private PageContext pageContext;

    private Document document;

    public WebDocument(PageContext pageContext, Document document, JavaScriptEngine engine) {
        this.pageContext = pageContext;
        this.document = document;
        this.engine = engine;
    }

    public Optional<PageElement> query(String selector) {
        return pageContext.getSelectorEngine().query(selector);
    }

    public List<PageElement> queryAll(String selector) {
        return pageContext.getSelectorEngine().queryAll(selector);
    }

    public WebEngine getEngine() {
        return engine.getEngine();
    }

    public Optional<String> getTitle() {
        Optional<PageElement> title = query("title");
        return title.map(PageElement::getInnerHTML);

    }

    public Document getDomDocument() {
        return document;
    }

}
