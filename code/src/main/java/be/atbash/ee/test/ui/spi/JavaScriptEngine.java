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
package be.atbash.ee.test.ui.spi;

import javafx.scene.web.WebEngine;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class JavaScriptEngine {

    private WebEngine engine;

    public JavaScriptEngine(WebEngine engine) {
        this.engine = engine;
    }

    public WebEngine getEngine() {
        return engine;
    }

    public Object executeScript(String script) {
        Object result = engine.executeScript(script);

        String resultStr = String.valueOf(result);

        try {
            NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);
            ParsePosition pos = new ParsePosition(0);
            Number number = formatter.parse(resultStr, pos);
            if (number != null) {
                if (resultStr.length() == pos.getIndex()) {
                    return number;
                }
            }
        } catch (Throwable t) {
            // ignore issue #55
        }

        return result;
    }
}
