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
package be.atbash.ee.test.ui.browser;

import be.atbash.ee.test.ui.dom.PageElement;
import be.atbash.ee.test.ui.dom.SelectorEngine;
import be.atbash.ee.test.ui.dom.WebDocument;
import be.atbash.ee.test.ui.spi.JavaScriptEngine;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.concurrent.SynchronousQueue;

/**
 * Context class for creation of WebTester concept instances.
 * <p>
 * Concept from UI4J.
 */
public class PageContext {

    private Logger log = LoggerFactory.getLogger(getClass());

    private SelectorEngine selector;
    private String userAgent;

    public PageContext(String userAgent) {
        this.userAgent = userAgent;
    }

    public PageElement createElement(Node node, WebDocument webDocument, JavaScriptEngine engine) {
        return new PageElement(node, webDocument, this, engine);
    }

    public WebDocument createDocument(JavaScriptEngine engine) {
        Document document = engine.getEngine().getDocument();

        WebEngine webEngine = engine.getEngine();
        if (userAgent != null) {
            webEngine.setUserAgent(userAgent);
        }
        webEngine.getLoadWorker().exceptionProperty().addListener(new ExceptionListener(log));
        webEngine.setOnError(new DefaultErrorEventHandler());
        WebDocument webDocument = new WebDocument(this, document, engine);
        selector = initializeSelectorEngine(webDocument, engine);
        return webDocument;
    }

    public Window createWindow(WebDocument webDocument) {
        return new Window(webDocument);
    }

    public Page newPage(Object view, JavaScriptEngine engine, Window window, Stage stage, WebDocument webDocument, SynchronousQueue<Boolean> documentLoadedQueue) {
        WebView webView = (WebView) view;
        return new Page(webView, engine, window, stage, webDocument, documentLoadedQueue);
    }

    public SelectorEngine getSelectorEngine() {
        return selector;
    }


    protected SelectorEngine initializeSelectorEngine(WebDocument webDocument, JavaScriptEngine engine) {
        return new SelectorEngine(this, webDocument, engine);
    }

    public static class DefaultErrorEventHandler implements EventHandler<WebErrorEvent> {

        private Logger log = LoggerFactory.getLogger(getClass());

        @Override
        public void handle(WebErrorEvent event) {
            log.error("Javascript error: " + event.getMessage());
        }
    }

    public static class ExceptionListener implements ChangeListener<Throwable> {

        private Logger log;

        public ExceptionListener(Logger log) {
            this.log = log;
        }

        @Override
        public void changed(ObservableValue<? extends Throwable> observable,
                            Throwable oldValue, Throwable newValue) {
            log.error(newValue.getMessage());
        }
    }
}
