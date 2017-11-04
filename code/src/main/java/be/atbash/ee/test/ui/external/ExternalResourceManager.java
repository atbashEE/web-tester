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
package be.atbash.ee.test.ui.external;

import be.atbash.ee.test.ui.config.SwarmConfiguration;
import be.atbash.ee.test.ui.config.yaml.SwarmConfigYAML;
import org.assertj.core.api.Fail;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

/**
 * Experimental !!
 */
public class ExternalResourceManager {

    private static final ExternalResourceManager INSTANCE = new ExternalResourceManager();
    private static final String HOLLOWSWARM_FILE_MARKER = "-hollowswarm.jar";
    private static final String JAR_FILE_PROTOCOL = "jar:file:";


    private ExternalResourceManager() {
    }

    public void runExternal(WebArchive webArchive, SwarmConfiguration configuration) {

        String archiveFile = saveArchive(webArchive);

        String yamlConfigFileName = defineYAMLConfigFileName(webArchive.getName());

        String propertiesFile = saveConfigYAML(yamlConfigFileName, configuration);
        System.out.println(propertiesFile);

        String path = defineSwarmJar();
        System.out.println(path);

        String[] command = defineCommand(path, archiveFile, propertiesFile);
        startExternalResource(command);

    }

    private String defineSwarmJar() {
        String result = null;
        Enumeration<URL> resources = null;
        try {
            resources = ExternalResourceManager.class.getClassLoader().getResources("META-INF/wildfly-swarm-manifest.yaml");
        } catch (IOException e) {
            e.printStackTrace();
            // FIXME
        }
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            String x = url.toExternalForm();

            int i = x.indexOf(HOLLOWSWARM_FILE_MARKER + "!");

            result = x.substring(JAR_FILE_PROTOCOL.length(), i + HOLLOWSWARM_FILE_MARKER.length());
        }

        return result;
    }

    private String defineYAMLConfigFileName(String name) {
        String[] parts = name.split("\\.", 2);
        return parts[0] + ".yml";
    }

    private String saveArchive(WebArchive archive) {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File archiveFile = new File(tempDir, archive.getName());
        archive.as(ZipExporter.class).exportTo(archiveFile, true);
        return archiveFile.getAbsolutePath();
    }

    private String saveConfigYAML(String yamlConfigFileName, SwarmConfiguration configuration) {

        Yaml yaml = getYAML();


        Map<String, SwarmConfigYAML> root = new HashMap<>();
        root.put("swarm", configuration.getConfigurationAsYAML());
        System.out.println(yaml.dump(root));

        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File file = new File(tempDir, yamlConfigFileName);
        try {
            Writer configFile = new FileWriter(file);
            configFile.write(yaml.dump(root));
            configFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

    private Yaml getYAML() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Representer representer = new Representer();
        representer.addClassTag(SwarmConfigYAML.class, Tag.MAP);
        return new Yaml(representer, dumperOptions);

    }

    private String[] defineCommand(String path, String archiveFile, String propertiesFile) {
        // java -jar myapp-swarm -s/home/app/openshift.yml /tmp/test.war
        String[] result = new String[5];
        result[0] = "java";
        result[1] = "-jar";
        result[2] = path;
        result[3] = "-s" + propertiesFile;
        result[4] = archiveFile;


        System.out.println("Executing following command");
        for (String s : result) {
            System.out.print(s);
            System.out.print(" ");
        }
        System.out.println();


        return result;
    }

    private void startExternalResource(String[] command) {
        ProcessBuilder pb = new ProcessBuilder(command);
        try {
            pb.redirectErrorStream(true);

            Process process = pb.start();

            LogStreamReader logReader = new LogStreamReader(process.getInputStream());
            new Thread(logReader).start();

            try {
                // Wait until Wildfly Swarm is up and running.
                while (!logReader.upAndRunning) {
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    public static ExternalResourceManager getInstance() {
        return INSTANCE;
    }

    static class LogStreamReader implements Runnable {

        private BufferedReader reader;
        boolean upAndRunning = false;
        boolean shutdownInitiated = false;
        boolean minimalLog = false;
        // TODO Support for showing error and setting upAndRunning = True or some other meaning of getting out of if the infinite loop.

        public LogStreamReader(InputStream is) {
            this.reader = new BufferedReader(new InputStreamReader(is));
        }

        public void run() {
            try {
                String line = reader.readLine();
                while (line != null) {
                    System.out.println("Remote resource " + line);

                    if (line.contains("WFSWARM99999")) {
                        upAndRunning = true;
                    }

                    if (line.contains("WFLYSRV0028: Stopped deployment")) {
                        upAndRunning = true;
                        Fail.fail(line);
                    }

                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {

                if (!shutdownInitiated) {
                    fail(e.getMessage());
                }
            }
        }
    }

}
