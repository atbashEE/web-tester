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
package be.atbash.ee.test.ui.dom;

import be.atbash.ee.test.ui.browser.PageContext;
import be.atbash.ee.test.ui.spi.JavaScriptEngine;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Selection of DOM elements.
 * <p>
 * Concept from UI4J.
 */
public class SelectorEngine {

    private PageContext pageContext;

    private WebDocument webDocument;

    private JavaScriptEngine engine;

    public SelectorEngine(PageContext pageContext, WebDocument webDocument, JavaScriptEngine engine) {
        this.pageContext = pageContext;
        this.webDocument = webDocument;
        this.engine = engine;
    }

    public Optional<PageElement> query(String selector) {
        Element htmlElement = findHtmlElement(webDocument);

        return query(htmlElement, selector);
    }

    private Optional<PageElement> query(Element htmlElement, String selector) {
        Element element = null;
        if (selector.startsWith("#")) {
            element = findElementById(htmlElement, selector.substring(1));
        }
        if (selector.startsWith(".")) {
            element = findElementByClass(htmlElement, selector.substring(1));
        }
        if (element == null) {
            element = findElementByTagName(htmlElement, selector);
        }
        if (element != null) {
            return Optional.of(pageContext.createElement(element, webDocument, engine));
        } else {
            return Optional.empty();
        }
    }

    private Element findHtmlElement(WebDocument webDocument) {
        Element result = null;
        NodeList childNodes = webDocument.getDomDocument().getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if ("HTML".equals(child.getNodeName())) {
                result = (Element) child;
            }
        }
        return result;
    }

    private Element findElementById(Element element, String idValue) {

        if (idValue.equals(getIdAttributeValue(element))) {
            return element;
        }

        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                //calls this method for all the children which is PageElement
                Element result = findElementById((Element) currentNode, idValue);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private Element findElementByClass(Element element, String classValue) {

        String classAttributeValue = getClassAttributeValue(element);
        if (classAttributeValue != null && classAttributeValue.contains(classValue)) {
            return element;
        }

        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                //calls this method for all the children which is PageElement
                Element result = findElementByClass((Element) currentNode, classValue);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private Element findElementByTagName(Element element, String tagName) {

        if (tagName.equalsIgnoreCase(element.getTagName())) {
            return element;
        }

        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                //calls this method for all the children which is PageElement
                Element result = findElementByTagName((Element) currentNode, tagName);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    // FIXME combine with getClassAttributeValue in 1 method
    private String getIdAttributeValue(Element element) {
        String result = null;
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            if ("id".equalsIgnoreCase(attributes.item(i).getNodeName())) {
                result = attributes.item(i).getNodeValue();
            }
        }
        return result;
    }

    private String getClassAttributeValue(Element element) {
        String result = null;
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            if ("class".equalsIgnoreCase(attributes.item(i).getNodeName())) {
                result = attributes.item(i).getNodeValue();
            }
        }
        return result;
    }

    public List<PageElement> queryAll(String selector) {
        // FIXME
        throw new UnsupportedOperationException("TODO");
    }

    public Optional<PageElement> query(PageElement element, String selector) {

        return query(element.getHtmlElement(), selector);

    }

    public List<PageElement> queryAll(PageElement element, String selector) {

        List<PageElement> result = new ArrayList<>();
        if (selector.startsWith("#")) {
            String idValue = selector.substring(1);
            if (idValue.equals(getIdAttributeValue(element.getHtmlElement()))) {
                result.add(element);
            }
        }

        if (selector.equalsIgnoreCase(element.getTagName())) {
            result.add(element);
        }

        for (PageElement child : element.getChildren()) {
            result.addAll(queryAll(child, selector));
        }

        return result;
    }
}
