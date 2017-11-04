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
package be.atbash.ee.test.ui.config.yaml;

/**
 * Part of the YAML configuration of Wildfly Swarm.
 */
public class SwarmConfigYAML {

    private SwarmConfigContextYAML context;
    private SwarmConfigPortYAML port;

    public SwarmConfigContextYAML getContext() {
        return context;
    }

    public void setContext(SwarmConfigContextYAML context) {
        this.context = context;
    }

    public SwarmConfigPortYAML getPort() {
        return port;
    }

    public void setPort(SwarmConfigPortYAML port) {
        this.port = port;
    }

    public static SwarmConfigYAML getEmptyInstance() {
        SwarmConfigContextYAML context = new SwarmConfigContextYAML();

        SwarmConfigPortYAML port = new SwarmConfigPortYAML();

        SwarmConfigYAML result = new SwarmConfigYAML();
        result.setContext(context);
        result.setPort(port);

        return result;
    }
}
