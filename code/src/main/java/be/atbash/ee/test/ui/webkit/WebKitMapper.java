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
package be.atbash.ee.test.ui.webkit;

import be.atbash.ee.test.ui.spi.JavaScriptEngine;
import netscape.javascript.JSObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static java.util.Collections.emptyMap;

public class WebKitMapper {

    public WebKitMapper(JavaScriptEngine engine) {
        engine.executeScript(
                "Object.prototype.__ui4j_keys = function() { return Object.keys(this); };" +
                        "Object.prototype.__ui4j_isArray = function() { return Array.isArray(this); };");
    }

    public Map<String, Object> toJava(JSObject object) {
        if (object == null) {
            return emptyMap();
        }
        Map<String, Object> map = new LinkedHashMap<>();
        JSObject keys = (JSObject) object.call("__ui4j_keys");
        int length = parseInt(valueOf(keys.getMember("length")));
        for (int i = 0; i < length; i++) {
            String key = (String) keys.getSlot(i);
            Object value = object.getMember(key);
            value = toJava(value);
            if (!(value instanceof JSObject)) {
                map.put(key, value);
            } else {
                JSObject jsObject = (JSObject) value;
                boolean isArray = (Boolean) jsObject.call("__ui4j_isArray");
                if (isArray) {
                    List<Object> list = new ArrayList<>();
                    int arrayLength = parseInt(valueOf(jsObject.getMember("length")));
                    for (int j = 0; j < arrayLength; j++) {
                        Object slot = jsObject.getSlot(j);
                        slot = toJava(slot);
                        if (!(slot instanceof JSObject)) {
                            list.add(slot);
                        } else {
                            list.add(toJava((JSObject) slot));
                        }
                    }
                    map.put(key, list);
                } else {
                    map.put(key, toJava(jsObject));
                }
            }
        }
        return map;
    }

    protected Object toJava(Object value) {
        // FIXME What is the point of this?
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return value;
    }
}
