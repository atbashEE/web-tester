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

import be.atbash.ee.test.ui.browser.Page;
import be.atbash.ee.test.ui.dom.PageElement;
import be.atbash.ee.test.ui.dom.WebDocument;
import be.atbash.ee.test.ui.jsf.message.MessageInfo;
import be.atbash.ee.test.ui.jsf.message.Severity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class JSFWebPageTest {

    private static final String ERROR_MESSAGE_1 = "Error message 1";
    private static final String ERROR_MESSAGE_2 = "Error message 2";
    private static final String INFO_MESSAGE_1 = "Info message";
    private static final String INFO_CLASS = "infoClass";

    @Mock
    private Page pageMock;

    @Mock
    private WebDocument webDocumentMock;

    @Mock
    private PageElement pageElementMock;

    //@Mock
    //private PageElement listItemsMock;

    @Mock
    private PageElement item1Mock;

    @Mock
    private PageElement item2Mock;

    @Test
    public void getMessages_NoStyleClassDefined() {
        JSFWebPage webPage = new JSFWebPage(null, pageMock);

        when(pageMock.getWebDocument()).thenReturn(webDocumentMock);
        when(webDocumentMock.query(anyString())).thenReturn(Optional.of(pageElementMock));
        List<PageElement> messageElements = new ArrayList<>();
        messageElements.add(item1Mock);
        when(item1Mock.getText()).thenReturn(Optional.of(ERROR_MESSAGE_1));
        when(item1Mock.getAttribute("class")).thenReturn(Optional.empty());

        when(pageElementMock.find("li")).thenReturn(messageElements);

        List<MessageInfo> messages = webPage.getMessages();
        assertThat(messages).hasSize(1);
        assertThat(messages).extracting("severity").containsExactly(Severity.ERROR);
        assertThat(messages).extracting("text").containsExactly(ERROR_MESSAGE_1);

    }

    @Test
    public void getMessages_styleClassDefined() {
        JSFWebPage webPage = new JSFWebPage(null, pageMock);

        webPage.defineSeverityStyleClass(Severity.INFO, INFO_CLASS);

        when(pageMock.getWebDocument()).thenReturn(webDocumentMock);
        when(webDocumentMock.query(anyString())).thenReturn(Optional.of(pageElementMock));
        List<PageElement> messageElements = new ArrayList<>();
        messageElements.add(item1Mock);
        when(item1Mock.getText()).thenReturn(Optional.of(INFO_MESSAGE_1));
        when(item1Mock.getAttribute("class")).thenReturn(Optional.of(INFO_CLASS));

        when(pageElementMock.find("li")).thenReturn(messageElements);

        List<MessageInfo> messages = webPage.getMessages();
        assertThat(messages).hasSize(1);
        assertThat(messages).extracting("severity").containsExactly(Severity.INFO);
        assertThat(messages).extracting("text").containsExactly(INFO_MESSAGE_1);

    }

    @Test
    public void getMessages_MultipleErrors() {
        JSFWebPage webPage = new JSFWebPage(null, pageMock);

        when(pageMock.getWebDocument()).thenReturn(webDocumentMock);
        when(webDocumentMock.query(anyString())).thenReturn(Optional.of(pageElementMock));
        List<PageElement> messageElements = new ArrayList<>();
        messageElements.add(item1Mock);
        when(item1Mock.getText()).thenReturn(Optional.of(ERROR_MESSAGE_1));
        when(item1Mock.getAttribute("class")).thenReturn(Optional.empty());

        messageElements.add(item2Mock);
        when(item2Mock.getText()).thenReturn(Optional.of(ERROR_MESSAGE_2));
        when(item2Mock.getAttribute("class")).thenReturn(Optional.empty());

        when(pageElementMock.find("li")).thenReturn(messageElements);

        List<MessageInfo> messages = webPage.getMessages();
        assertThat(messages).hasSize(2);
        assertThat(messages).extracting("severity").containsOnly(Severity.ERROR);
        assertThat(messages).extracting("text").containsOnly(ERROR_MESSAGE_1, ERROR_MESSAGE_2);
    }

    @Test
    public void getMessages_NoItems() {
        JSFWebPage webPage = new JSFWebPage(null, pageMock);

        when(pageMock.getWebDocument()).thenReturn(webDocumentMock);
        when(webDocumentMock.query(anyString())).thenReturn(Optional.of(pageElementMock));

        List<PageElement> messageElements = new ArrayList<>();
        when(pageElementMock.find("li")).thenReturn(messageElements);

        List<MessageInfo> messages = webPage.getMessages();
        assertThat(messages).isEmpty();
    }

    @Test
    public void getMessages_NoElement() {
        JSFWebPage webPage = new JSFWebPage(null, pageMock);

        when(pageMock.getWebDocument()).thenReturn(webDocumentMock);
        when(webDocumentMock.query(anyString())).thenReturn(Optional.empty());

        List<MessageInfo> messages = webPage.getMessages();
        assertThat(messages).isEmpty();
    }
}