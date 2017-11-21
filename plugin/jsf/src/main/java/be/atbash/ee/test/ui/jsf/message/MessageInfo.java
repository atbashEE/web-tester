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
package be.atbash.ee.test.ui.jsf.message;

import be.atbash.ee.test.ui.PublicAPI;

/**
 * D
 */
@PublicAPI
public class MessageInfo {

    private Severity severity;
    private String text;

    public MessageInfo(Severity severity, String text) {
        this.severity = severity;
        this.text = text;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getText() {
        return text;
    }
}
