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
package be.atbash.ee.test.ui.config;

import be.atbash.ee.test.ui.PublicAPI;
import be.atbash.ee.test.ui.config.yaml.SwarmConfigYAML;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@PublicAPI
public class SwarmConfiguration {

    private static final String PORT_OFFSET = "swarm.port.offset";
    private static final String CONTEXT_PATH = "swarm.context.path";

    private SwarmConfigYAML yamlConfig = SwarmConfigYAML.getEmptyInstance();

    public String getContextRoot() {
        return yamlConfig.getContext().getPath();
    }

    public void setContextRoot(String contextRoot) {
        yamlConfig.getContext().setPath(contextRoot);
    }

    public int getPortOffset() {
        return yamlConfig.getPort().getOffset();
    }

    public void setPortOffset(int portOffset) {
        yamlConfig.getPort().setOffset(portOffset);
    }

    /**
     * Configuration options as Map where keys are WildFly Swarm config keys.
     *
     * @return configuration as map.
     */
    public Map<String, String> getConfigurationAsMap() {
        Map<String, String> result = new HashMap<>();
        result.put(PORT_OFFSET, String.valueOf(getPortOffset()));
        result.put(CONTEXT_PATH, getContextRoot());
        return result;
    }

    /**
     * Configuration as on Object graph compatible with WildFLy Swarm YAML config.
     */
    public SwarmConfigYAML getConfigurationAsYAML() {
        return yamlConfig;
    }

    @Override
    public String toString() {
        return "SwarmConfiguration{" + "contextRoot='" + getContextRoot() + '\'' +
                ", portOffset=" + getPortOffset() +
                '}';
    }
}
