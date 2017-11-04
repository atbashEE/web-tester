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
package be.atbash.ee.test.swarm_runner.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 */

public class SwarmConfiguration {

    private static final String PORT_OFFSET = "swarm.port.offset";
    private static final String CONTEXT_PATH = "swarm.context.path";

    private String contextRoot;
    private int portOffset;

    public SwarmConfiguration(Properties properties) {
        contextRoot = properties.getProperty(CONTEXT_PATH);
        portOffset = Integer.valueOf(properties.getProperty(PORT_OFFSET));
    }

    public String getContextRoot() {
        return contextRoot;
    }

    public int getPortOffset() {
        return portOffset;
    }

    public Map<String, String> getProperties() {
        Map<String, String> result = new HashMap<>();
        result.put(PORT_OFFSET, String.valueOf(portOffset));
        result.put(CONTEXT_PATH, String.valueOf(contextRoot));
        return result;
    }

    @Override
    public String toString() {
        return "SwarmConfiguration{" + "contextRoot='" + contextRoot + '\'' +
                ", portOffset=" + portOffset +
                '}';
    }
}
