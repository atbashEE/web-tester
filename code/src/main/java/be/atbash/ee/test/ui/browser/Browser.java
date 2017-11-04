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

import be.atbash.ee.test.ui.dom.WebDocument;
import be.atbash.ee.test.ui.event.DocumentListener;
import be.atbash.ee.test.ui.event.DocumentLoadEvent;
import be.atbash.ee.test.ui.exception.ExecutionTimeoutException;
import be.atbash.ee.test.ui.exception.UnexpectedException;
import be.atbash.ee.test.ui.exception.WebTesterException;
import be.atbash.ee.test.ui.spi.JavaScriptEngine;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A web-browser engine capable of navigating to URLs and retrieving their content as {@link Page}
 * instances. It also allows to interact with the JavaFX WebView
 * <p>
 * <p>When an instance is no longer required, its {@link #shutdown()} method should be called so
 * that the JavaFX system can be properly closed.
 * <p>
 * Concept from UI4J.
 */
public class Browser {

    private final Stage stage;
    private final Scene scene;

    private WebView webView;

    private SynchronousQueue<Boolean> documentLoadedQueue = new SynchronousQueue<>();
    private ProgressListener progressListener;
    private WorkerLoadListener loadListener;

    public Browser(Stage stage, Scene scene) {
        this.stage = Objects.requireNonNull(stage);
        this.scene = Objects.requireNonNull(scene);
    }

    private static class SyncDocumentListener implements DocumentListener {

        private CountDownLatch latch;

        private Window window;

        private WebDocument webDocument;

        SyncDocumentListener(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onLoad(DocumentLoadEvent event) {
            this.window = event.getWindow();
            this.webDocument = event.getDocument();
            latch.countDown();
        }

        WebDocument getWebDocument() {
            return webDocument;
        }

        Window getWindow() {
            return window;
        }
    }

    private static class ExitRunner implements Runnable {

        private CountDownLatch latch;

        ExitRunner(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            if (Platform.isFxApplicationThread()) {
                Platform.exit();
            }
            // Signals that JavaFX command is finished.
            latch.countDown();
        }
    }

    private class WorkerLoadListener implements ChangeListener<Worker.State> {

        private PageContext pageContext;

        private DocumentListener documentListener;

        private JavaScriptEngine engine;

        WorkerLoadListener(JavaScriptEngine engine, PageContext context, DocumentListener documentListener) {
            this.engine = engine;
            this.pageContext = context;
            this.documentListener = documentListener;
        }

        @Override
        public void changed(ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) {
            if (newState == Worker.State.SUCCEEDED) {
                WebDocument webDocument = pageContext.createDocument(engine);
                Window window = pageContext.createWindow(webDocument);
                DocumentLoadEvent event = new DocumentLoadEvent(window);
                documentListener.onLoad(event);

            }
            if (newState == Worker.State.FAILED) {
                Browser.this.shutdown();
                throw new WebTesterException("Loading webDocument failed");
            }
        }
    }

    public static class ProgressListener implements ChangeListener<Number> {

        private WebEngine engine;
        private SynchronousQueue<Boolean> queue;

        public ProgressListener(WebEngine engine, SynchronousQueue<Boolean> queue) {
            this.engine = engine;
            this.queue = queue;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            double progress = Math.floor((double) newValue * 100);

            if (progress == 100.0) {
                try {
                    queue.offer(Boolean.TRUE, 1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Constructs and returns a {@link Page} representing the web-page content obtained by this
     * {@link Browser} from a given URL, and optionally a userAgent.
     *
     * @param url
     * @param userAgent The HTTP "User-Agent" string to be presented by the browser when
     *                  requesting processing a web page using this instance. May be null to indicate that no
     *                  specific user-agent string is required and that its own default user-agent string
     *                  (which itself may vary depending on the underlying JVM, JavaFX implementation, operating system,
     *                  or other factors).
     */

    @SuppressWarnings("unchecked")
    public Page navigate(String url, String userAgent) {
        // Prepare the WebView as child of the root of the Scene.
        prepareWebView();

        PageContext context = new PageContext(userAgent);

        // A latch to verify when document content is not loaded.
        CountDownLatch documentReadyLatch = new CountDownLatch(1);

        SyncDocumentListener adapter = new SyncDocumentListener(documentReadyLatch);

        JavaScriptEngine engine = new JavaScriptEngine(webView.getEngine());

        loadListener = new WorkerLoadListener(engine, context, adapter);
        progressListener = new ProgressListener(webView.getEngine(), documentLoadedQueue);

        Platform.runLater(() -> {
            webView.getEngine().getLoadWorker().progressProperty().addListener(progressListener);

            webView.getEngine().getLoadWorker().stateProperty().addListener(loadListener);

            webView.getEngine().load(url);
        });

        try {
            // documentReadyLatch is adjusted by the SyncDocumentListener when the URL contents is fully loaded.
            documentReadyLatch.await(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new ExecutionTimeoutException(e, "Waiting for URL content to be loaded");
        }

        Page page = context.newPage(webView, engine, adapter.getWindow(),
                stage, adapter.getWebDocument(), documentLoadedQueue);

        // Check for the JSF Page not found message.
        Object html = page.executeScript("document.body.innerHTML");
        assertThat(html.toString()).doesNotContain("Not Found in ExternalContext as a Resource");

        return page;
    }

    /**
     * Terminates the 'Browser' and also the JavaFX System.
     */
    public void shutdown() {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(new ExitRunner(latch));

        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new ExecutionTimeoutException(e, "Waiting for JavaFX platform to exit");
        }

    }

    /**
     * Removes all cookies held by the engine.
     */
    @SuppressWarnings("rawtypes")
    public void clearCookies() {
        CookieHandler cookieHandler = CookieHandler.getDefault();
        if (cookieHandler == null) {
            return;
        }
        if (cookieHandler instanceof CookieManager) {
            CookieManager manager = (CookieManager) cookieHandler;
            manager.getCookieStore().removeAll();
        }
    }

    /**
     * Retrieves the list of all Cookies known by the engine.
     *
     * @return
     */

    public List<HttpCookie> getCookies() {
        List<HttpCookie> result = new ArrayList<>();
        CookieHandler cookieHandler = CookieHandler.getDefault();
        if (cookieHandler instanceof CookieManager) {
            CookieManager manager = (CookieManager) cookieHandler;
            result.addAll(manager.getCookieStore().getCookies());
        }
        return result;
    }

    private void prepareWebView() {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            StackPane root = (StackPane) scene.getRoot();
            root.getChildren().remove(webView);

            webView = new WebView();
            root.getChildren().add(webView);

            if (progressListener != null) {
                webView.getEngine().getLoadWorker().progressProperty().removeListener(progressListener);
            }

            if (loadListener != null) {
                webView.getEngine().getLoadWorker().stateProperty().removeListener(loadListener);
            }


            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send the text by using Key events to the WebView.
     *
     * @param data
     */
    public void sendKeys(String data) {
        for (char c : data.toCharArray()) {

            Event event = new KeyEvent(
                    KeyEvent.KEY_TYPED, String.valueOf(c),
                    "", null, false, false, false, false);
            webView.fireEvent(event);
            try {
                // Wait a bit so that event can be handled
                Thread.sleep(75L);
            } catch (InterruptedException e) {
                throw new UnexpectedException("There was an InterruptedException during sending key events");
            }
        }
    }
}
