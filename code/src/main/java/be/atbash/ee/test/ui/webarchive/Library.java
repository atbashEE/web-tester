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
package be.atbash.ee.test.ui.webarchive;

import be.atbash.ee.test.ui.PublicAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
@PublicAPI
public enum Library {

    JERRY("be.atbash.ee.jsf:jerry"),
    VALERIE("be.atbash.ee.jsf:valerie"),
    PRIMEFACES("org.primefaces:primefaces"),
    VALERIE_PRIMEFACES("be.atbash.ee.jsf.valerie:valerie-primefaces"),
    OCTOPUS_JSF_EE7("be.c4j.ee.security.octopus:octopus-javaee7-jsf"),
    DELTASPIKE_CORE("org.apache.deltaspike.core:deltaspike-core-api", "org.apache.deltaspike.core:deltaspike-core-impl"),
    DELTASPIKE_SECURITY("org.apache.deltaspike.modules:deltaspike-security-module-api", "org.apache.deltaspike.modules:deltaspike-security-module-impl"),
    ATBASH_CONFIG("be.atbash.config:atbash-config", "be.atbash.config:geronimo-config"),
    ATBASH_OCTOPUS_JSF_EE7("be.atbash.ee.security:octopus-jsf7"),
    ATBASH_OCTOPUS_KEYCLOAK("be.atbash.ee.security:octopus-keycloak"),
    ;

    private List<String> canonicalForms;

    Library(String canonicalForm) {
        canonicalForms = new ArrayList<>();
        canonicalForms.add(canonicalForm);
    }

    Library(String... canonicalForms) {
        this.canonicalForms = Arrays.asList(canonicalForms);
    }

    public List<String> getCanonicalForms() {
        return canonicalForms;
    }
}
