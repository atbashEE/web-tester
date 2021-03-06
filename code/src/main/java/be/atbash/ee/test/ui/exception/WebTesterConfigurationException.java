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
package be.atbash.ee.test.ui.exception;

/**
 *
 */

public class WebTesterConfigurationException extends RuntimeException {

    public WebTesterConfigurationException(Class returnClass) {
        super(String.format("static method annotated with @ServerConfigRule must return SwarmConfiguration instance (found %s)", returnClass.getName()));
    }

    public WebTesterConfigurationException(Class testerClass, String message) {
        super(String.format("Configuration exception for %s : %s", testerClass.getName(), message));
    }
}
