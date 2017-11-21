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
package be.atbash.ee.test.ui.jsf;

import be.atbash.ee.test.ui.PublicAPI;
import be.atbash.ee.test.ui.WebPage;
import be.atbash.ee.test.ui.browser.Browser;
import be.atbash.ee.test.ui.browser.Page;
import be.atbash.ee.test.ui.dom.PageElement;
import be.atbash.ee.test.ui.jsf.message.MessageInfo;
import be.atbash.ee.test.ui.jsf.message.Severity;

import java.util.*;

/**
 *
 */
@PublicAPI
public class JSFWebPage extends WebPage {

    private Map<String, Severity> messagesSeverityStyleClasses = new HashMap<>();

    protected JSFWebPage(Browser browser, Page page) {
        super(browser, page);
    }

    public void defineSeverityStyleClass(Severity severity, String styleClass) {
        messagesSeverityStyleClasses.put(styleClass, severity);
    }

    /**
     * @return
     */
    public List<MessageInfo> getMessages() {
        List<MessageInfo> result = new ArrayList<>();

        Optional<PageElement> messagesNode = page.getWebDocument().query("#errors");

        messagesNode.ifPresent(element -> result.addAll(extractMessages(element)));

        return result;
    }

    private Collection<? extends MessageInfo> extractMessages(PageElement element) {
        List<PageElement> elements = element.find("li");

        List<MessageInfo> result = new ArrayList<>();
        for (PageElement messageElement : elements) {
            String text = messageElement.getText().orElse("");
            result.add(new MessageInfo(defineSeverity(messageElement), text));
        }
        return result;
    }

    private Severity defineSeverity(PageElement messageElement) {

        Optional<String> classAttribute = messageElement.getAttribute("class");
        return classAttribute.map(this::defineSeverity).orElse(Severity.ERROR);
    }

    private Severity defineSeverity(String classAttribute) {
        Severity result = null;
        String[] classes = classAttribute.split(" ");
        for (String styleClass : classes) {
            if (messagesSeverityStyleClasses.containsKey(styleClass)) {
                result = messagesSeverityStyleClasses.get(styleClass);
            }
        }
        if (result == null) {
            result = Severity.ERROR;
        }
        return result;
    }
}
