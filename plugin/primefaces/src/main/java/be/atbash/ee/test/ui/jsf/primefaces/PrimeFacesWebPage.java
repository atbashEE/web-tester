/*
 * Copyright 2017-2018 Rudy De Busscher
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
package be.atbash.ee.test.ui.jsf.primefaces;

import be.atbash.ee.test.ui.PublicAPI;
import be.atbash.ee.test.ui.browser.Browser;
import be.atbash.ee.test.ui.browser.Page;
import be.atbash.ee.test.ui.dom.PageElement;
import be.atbash.ee.test.ui.jsf.JSFWebPage;
import be.atbash.ee.test.ui.jsf.message.MessageInfo;
import be.atbash.ee.test.ui.jsf.message.Severity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 *
 */
@PublicAPI
public class PrimeFacesWebPage extends JSFWebPage {

    protected PrimeFacesWebPage(Browser browser, Page page) {
        super(browser, page);
    }

    public void defineSeverityStyleClass(Severity severity, String styleClass) {
        throw new UnsupportedOperationException("PrimeFaces has some fix defined StyleClasses for messages");
    }

    /**
     * @return
     */
    public List<MessageInfo> getMessages(String selector) {
        List<MessageInfo> result = new ArrayList<>();

        Optional<PageElement> messagesNode = page.getWebDocument().query(".ui-messages");

        messagesNode.ifPresent(element -> result.addAll(extractMessages(element)));

        return result;
    }

    private Collection<? extends MessageInfo> extractMessages(PageElement element) {
        List<PageElement> elements = element.find("li");

        List<MessageInfo> result = new ArrayList<>();
        for (PageElement messageElement : elements) {
            String text = messageElement.getText().get();
            // FIXME, the attributes aren't available why??
            result.add(new MessageInfo(determineSeverity(messageElement.getOuterHTML()), text));
        }
        return result;
    }

    private Severity determineSeverity(String classAttributeValue) {
        Severity result = null;
        if (classAttributeValue.contains("ui-messages-fatal-")) {
            result = Severity.FATAL;
        }
        if (classAttributeValue.contains("ui-messages-error-")) {
            result = Severity.ERROR;
        }
        if (classAttributeValue.contains("ui-messages-warn-")) {
            result = Severity.WARNING;
        }
        if (classAttributeValue.contains("ui-messages-info-")) {
            result = Severity.INFO;
        }
        return result;
    }

}
