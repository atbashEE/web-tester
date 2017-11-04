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

import be.atbash.ee.test.ui.PublicAPI;
import be.atbash.ee.test.ui.dom.WebDocument;
import javafx.scene.web.WebEngine;

/**
 * Represents the JavaScript Window Object
 * <p>
 * Concept from UI4J.
 */
@PublicAPI
public class Window {

    private WebDocument webDocument;

    private WebEngine engine;

    public Window(WebDocument webDocument) {
        this.webDocument = webDocument;
        this.engine = webDocument.getEngine();
    }

    public WebDocument getWebDocument() {
        return webDocument;
    }

    /**
     * getLocation as in get URL of current page.
     *
     * @return
     */
    public String getLocation() {
        return engine.getLocation();
    }

    public void setLocation(String location) {
        engine.executeScript(String.format("window.location.href='%s'", location));
    }

    public void back() {
        engine.executeScript("window.history.back()");
    }

    public void forward() {
        engine.executeScript("window.history.forward()");
    }

    public void reload() {
        engine.executeScript("window.location.reload()");
    }
}
