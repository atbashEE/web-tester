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

import be.atbash.ee.test.ui.PublicAPI;
import be.atbash.ee.test.ui.browser.PageContext;
import be.atbash.ee.test.ui.spi.JavaScriptEngine;
import be.atbash.ee.test.ui.util.Point;
import be.atbash.ee.test.ui.webkit.WebKitMapper;
import com.sun.webkit.dom.DocumentImpl;
import com.sun.webkit.dom.HTMLElementImpl;
import com.sun.webkit.dom.NodeImpl;
import netscape.javascript.JSObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLInputElement;
import org.w3c.dom.html.HTMLOptionElement;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represent a dom element on the page.
 * <p>
 * Concept from UI4J.
 */
@PublicAPI
public class PageElement {

    private Node element;

    private WebDocument webDocument;

    private PageContext pageContext;

    private JavaScriptEngine engine;

    public PageElement(Node element, WebDocument webDocument, PageContext pageContext, JavaScriptEngine engine) {
        this.element = element;
        this.webDocument = webDocument;
        this.pageContext = pageContext;
        this.engine = engine;
    }

    public Optional<String> getAttribute(String name) {
        String val = getHtmlElement().getAttribute(name);
        if (val == null || val.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(val);
        }
    }

    public boolean hasAttribute(String name) {
        return getHtmlElement().hasAttribute(name);
    }

    public boolean hasClass(String name) {
        boolean result = false;
        Optional<String> attribute = getAttribute("class");

        if (attribute.isPresent()) {
            result = attribute.get().contains(name);
        }
        return result;
    }

    public List<String> getClasses() {

        Optional<String> attribute = getAttribute("class");

        String[] classes = attribute.orElse("").split(" ");

        return Arrays.stream(classes)
                .filter(s -> !s.trim().isEmpty())
                .collect(Collectors.toList());
    }

    public Optional<String> getText() {
        String textContent = element.getTextContent();
        if (textContent == null || textContent.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(textContent);
    }

    public PageElement setText(String text) {
        HTMLElementImpl elementImpl = getHtmlElement();
        elementImpl.setTextContent(text);
        return this;
    }

    public String getTagName() {
        return getNode().getNodeName().toLowerCase(Locale.ENGLISH);
    }

    public Optional<String> getValue() {
        String value = null;
        if (element instanceof HTMLInputElement) {
            value = ((HTMLInputElement) element).getValue();
        } else if (element instanceof HTMLOptionElement) {
            value = ((HTMLOptionElement) element).getValue();
        }
        return value == null || value.isEmpty() ? Optional.empty() : Optional.of(value);
    }

    public PageElement setValue(String value) {
        if (element instanceof HTMLInputElement) {
            ((HTMLInputElement) element).setValue(value);
        } else if (element instanceof HTMLOptionElement) {
            ((HTMLOptionElement) element).setValue(value);
        }
        return this;
    }

    public List<PageElement> getChildren() {
        NodeList nodes = element.getChildNodes();
        List<PageElement> elements = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                PageElement element = pageContext.createElement(node, webDocument, engine);
                elements.add(element);
            }
        }
        return elements;
    }

    public List<PageElement> find(String selector) {
        return pageContext.getSelectorEngine().queryAll(this, selector);

    }

    private void find(Node node, List<PageElement> elements, String selector) {
        if (Node.ELEMENT_NODE != node.getNodeType()) {
            return;
        }
        NodeList nodes = node.getChildNodes();
        int length = nodes.getLength();
        if (length <= 0) {
            return;
        }
        for (int i = 0; i < length; i++) {
            Node item = nodes.item(i);
            if (Node.ELEMENT_NODE == item.getNodeType()) {
                PageElement element = pageContext.createElement(item, webDocument, engine);
                if (element.is(selector)) {
                    elements.add(element);
                }
                find(item, elements, selector);
            }
        }
    }


    public PageElement click() {
        HTMLElementImpl element = getHtmlElement();
        element.click();
        return this;
    }

    public Optional<PageElement> getParent() {
        Node parentNode = element.getParentNode();
        if (parentNode == null) {
            return Optional.empty();
        }
        return Optional.of(pageContext.createElement(element.getParentNode(), webDocument, engine));
    }

    public Optional<String> getId() {
        String id = getHtmlElement().getId();
        if (id == null || id.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(id);
    }


    public Object getProperty(String key) {
        JSObject obj = getHtmlElement();
        Object member = obj.getMember(key);
        if (member instanceof String && "undefined".equals(member)) {
            return null;
        }
        return member;
    }

    public boolean isHtmlElement() {
        return element instanceof HTMLElement;
    }

    public String getInnerHTML() {
        return getHtmlElement().getInnerHTML();
    }

    public PageElement focus() {
        getHtmlElement().focus();
        return this;
    }

    public Optional<PageElement> query(String selector) {
        return pageContext.getSelectorEngine().query(this, selector);
    }

    public List<PageElement> queryAll(String selector) {
        return pageContext.getSelectorEngine().queryAll(this, selector);
    }


    public Point getOffset() {
        DocumentImpl document = (DocumentImpl) engine.getEngine().getDocument();
        com.sun.webkit.dom.ElementImpl elementImpl = (com.sun.webkit.dom.ElementImpl) document.getBody();
        int scrollTop = elementImpl.getScrollTop();
        int scrollLeft = elementImpl.getScrollLeft();
        HTMLElementImpl htmlElementImpl = getHtmlElement();
        JSObject clientRect = (JSObject) htmlElementImpl.call("getBoundingClientRect");
        int top = (int) (Float.parseFloat(String.valueOf(clientRect.getMember("top"))) + scrollTop);
        int left = (int) (Float.parseFloat(String.valueOf(clientRect.getMember("left"))) + scrollLeft);
        return new Point(top, left);
    }

    public PageElement scrollIntoView(boolean alignWithTop) {
        getHtmlElement().scrollIntoView(alignWithTop);
        return this;
    }

    public Optional<PageElement> getPrev() {
        Element prev = getHtmlElement().getPreviousElementSibling();
        if (prev != null) {
            return Optional.of(pageContext.createElement(prev, webDocument, engine));
        } else {
            return Optional.empty();
        }
    }

    public Optional<PageElement> getNext() {
        Element next = getHtmlElement().getNextElementSibling();
        if (next != null) {
            return Optional.of(pageContext.createElement(next, webDocument, engine));
        } else {
            return Optional.empty();
        }
    }

    public Optional<String> getTitle() {
        String title = getHtmlElement().getTitle();
        if (title == null || title.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(title);
    }

    public int getTabIndex() {
        return getHtmlElement().getTabIndex();
    }

    public boolean hasChildNodes() {
        return getNode().hasChildNodes();
    }


    /**
     * FIXME This exposes underlying implementation but Oracle JDK only (since com.sun)!!
     */
    public HTMLElementImpl getHtmlElement() {
        if (element instanceof HTMLElementImpl) {
            return (HTMLElementImpl) element;
        } else {
            return null;
        }
    }

    /**
     * FIXME This exposes underlying implementation but Oracle JDK only (since com.sun)!!
     */
    public NodeImpl getNode() {
        return (NodeImpl) element;
    }

    public PageContext getConfiguration() {
        return pageContext;
    }

    public boolean isEqualNode(PageElement pageElement) {
        return getNode().isEqualNode(pageElement.getNode());
    }

    public boolean isSameNode(PageElement pageElement) {
        return getNode().isSameNode(pageElement.getNode());
    }

    public float getOuterHeight() {
        int height = getHtmlElement().getOffsetHeight();
        float marginTop =
                Float.parseFloat(getHtmlElement().eval("parseFloat(window.getComputedStyle(this, null).marginTop, 10)").toString());
        float marginBottom =
                Float.parseFloat(getHtmlElement().eval("parseFloat(window.getComputedStyle(this, null).marginBottom, 10)").toString());
        return height + marginTop + marginBottom;
    }

    public float getClientHeight() {
        return (float) getHtmlElement().getClientHeight();
    }

    public float getClientWidth() {
        return (float) getHtmlElement().getClientWidth();
    }

    public float getOuterWidth() {
        int width = getHtmlElement().getOffsetWidth();
        float marginLeft =
                Float.parseFloat(getHtmlElement().eval("parseFloat(window.getComputedStyle(this, null).marginLeft, 10)").toString());
        float marginRight =
                Float.parseFloat(getHtmlElement().eval("parseFloat(window.getComputedStyle(this, null).marginRight, 10)").toString());
        return width + marginLeft + marginRight;
    }

    public boolean is(String selector) {
        return getHtmlElement().webkitMatchesSelector(selector);
    }

    public boolean contains(PageElement element) {
        return !element.getHtmlElement().isSameNode(getHtmlElement()) &&
                getHtmlElement().contains(element.getHtmlElement());
    }

    public String getOuterHTML() {
        HTMLElementImpl htmlElementImpl = getHtmlElement();
        return htmlElementImpl.getOuterHTML();
    }

    public Optional<PageElement> getOffsetParent() {
        HTMLElementImpl htmlElementImpl = getHtmlElement();
        Element offsetParent = htmlElementImpl.getOffsetParent();
        return Optional.of(pageContext.createElement(offsetParent, webDocument, engine));
    }

    public Optional<Point> getPosition() {
        HTMLElementImpl htmlElementImpl = getHtmlElement();
        if (htmlElementImpl != null) {
            Point point = new Point(new Double(htmlElementImpl.getOffsetLeft()).intValue(), new Double(htmlElementImpl.getOffsetTop()).intValue());
            return Optional.of(point);
        } else {
            return Optional.empty();
        }
    }

    public List<PageElement> getSiblings(String selector) {
        Optional<PageElement> parent = getParent();
        if (!parent.isPresent()) {
            return Collections.emptyList();
        }
        List<PageElement> children = parent.get().getChildren();
        List<PageElement> siblings = new ArrayList<>();
        for (PageElement next : children) {
            if (next.is(selector) && !next.isSameNode(this)) {
                siblings.add(next);
            }
        }
        if (siblings.isEmpty()) {
            return Collections.emptyList();
        }
        return siblings;
    }

    public List<PageElement> getSiblings() {
        Optional<PageElement> parent = getParent();
        if (!parent.isPresent()) {
            return Collections.emptyList();
        }
        List<PageElement> children = parent.get().getChildren();
        List<PageElement> siblings = new ArrayList<>();
        for (PageElement next : children) {
            if (next.isSameNode(this)) {
                continue;
            }
            siblings.add(next);
        }
        if (siblings.isEmpty()) {
            return Collections.emptyList();
        }
        return siblings;
    }

    public WebDocument getWebDocument() {
        return webDocument;
    }

    public JavaScriptEngine getEngine() {
        return engine;
    }

    public Optional<PageElement> closest(String selector) {
        HTMLElementImpl el = getHtmlElement();
        HTMLElementImpl parent;
        while (el != null) {
            parent = (HTMLElementImpl) el.getParentElement();
            if (parent != null) {
                PageElement pElement = pageContext.createElement(parent, webDocument, engine);
                return pageContext.getSelectorEngine().query(pElement, selector);
            }
            el = parent;
        }
        return Optional.empty();
    }

    public Optional<PageElement> getNextSibling() {
        HTMLElementImpl el = getHtmlElement();
        Node sibling = el.getNextElementSibling();
        if (sibling == null) {
            return Optional.empty();
        } else {
            PageElement element = pageContext.createElement(sibling, webDocument, engine);
            return Optional.of(element);
        }
    }

    @Override
    public String toString() {
        return "PageElement [element=" + this.getInnerHTML() + "]";
    }

    public Object eval(String expression) {
        Object result = getHtmlElement().eval(expression);
        if (result instanceof JSObject) {
            return new WebKitMapper(engine).toJava((JSObject) result);
        } else {
            return result;
        }
    }
}
