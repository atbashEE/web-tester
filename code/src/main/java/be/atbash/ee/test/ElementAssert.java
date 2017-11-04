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
package be.atbash.ee.test;

import be.atbash.ee.test.ui.dom.PageElement;
import org.assertj.core.api.AbstractAssert;

import java.util.Optional;

/**
 *
 */

public class ElementAssert extends AbstractAssert<ElementAssert, Optional<PageElement>> {

    public ElementAssert(Optional<PageElement> actual) {
        super(actual, ElementAssert.class);
    }

    public static ElementAssert assertThat(Optional<PageElement> actual) {
        return new ElementAssert(actual);
    }

    public ElementAssert exists() {
        isNotNull();
        if (!actual.isPresent()) {
            failWithMessage("Page element is not found");
        }
        return this;
    }
}
