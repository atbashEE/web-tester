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
import be.atbash.ee.test.ui.exception.UnexpectedException;
import be.atbash.ee.test.ui.spi.JavaScriptEngine;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;

import static javafx.embed.swing.SwingFXUtils.fromFXImage;

/**
 * Represents a Browser page and is represented by a JavaFX stage.
 * <p>
 * Concept from UI4J.
 */
public class Page {

    private WebView webView;

    private Window window;

    private WebDocument webDocument;

    private Stage stage;

    private JavaScriptEngine engine;
    private SynchronousQueue<Boolean> documentLoadedQueue;

    public Page(WebView webView, JavaScriptEngine engine, Window window, Stage stage, WebDocument webDocument, SynchronousQueue<Boolean> documentLoadedQueue) {
        this.webView = webView;
        this.window = window;
        this.webDocument = webDocument;
        this.engine = engine;
        this.stage = stage;
        this.documentLoadedQueue = documentLoadedQueue;
    }

    /**
     *
     */
    public void show() {
        stage.setMaximized(true);
        stage.toFront();
        stage.show();
    }

    public WebView getWebView() {
        return webView;
    }

    public WebDocument getWebDocument() {
        return webDocument;
    }

    public Window getWindow() {
        return window;
    }

    public Stage getStage() {
        return stage;
    }

    public void hide() {
        if (stage != null) {
            stage.hide();
        }
    }

    public Object executeScript(String script) {
        CountDownLatch latch = new CountDownLatch(1);
        Data result = new Data();
        Platform.runLater(() -> {

            result.setResult(engine.executeScript(script));
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result.getResult();
    }

    public WebEngine getEngine() {
        return engine.getEngine();
    }

    public SynchronousQueue<Boolean> getDocumentLoadedQueue() {
        return documentLoadedQueue;
    }

    private static class Data {
        Object result;

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }
    }

    public WebView getView() {
        return webView;
    }

    public String getDocumentState() {
        return String.valueOf(webView.getEngine().executeScript("webDocument.readyState"));
    }

    public void captureScreen(OutputStream os) {
        final AnimationTimer timer = new AnimationTimer() {

            private int pulseCounter;

            @Override
            public void handle(long now) {
                pulseCounter += 1;
                if (pulseCounter > 2) {
                    stop();
                    WebView view = getView();
                    WritableImage snapshot = view.snapshot(new SnapshotParameters(), null);
                    BufferedImage image = fromFXImage(snapshot, null);
                    try (OutputStream stream = os) {
                        ImageIO.write(image, "png", stream);
                    } catch (IOException e) {
                        throw new UnexpectedException(e);
                    }
                }
            }
        };

        timer.start();
    }
}
