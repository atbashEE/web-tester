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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 */

public class SwarmTestRunner {


    public static void main(String[] args) {
        System.out.println("Starting external resource");

        /*
        args = new String[]{"/var/folders/4t/xy045xdd6ng11wkmk0gl61sm0000gn/T/test.properties"};

        checkArguments(args);
        Properties config = readConfiguration(args[0]);
        SwarmConfiguration swarmConfiguration = new SwarmConfiguration(config);

        File archiveFile = new File(config.getProperty("archive.path"));

        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            System.out.println(entry.getKey() + "->" + entry.getValue());
        }

        try {
            Swarm swarm = new Swarm();

            WebArchive archive = ShrinkWrap.create(WebArchive.class, archiveFile.getName());
            archive.as(ZipImporter.class).importFrom(archiveFile);

            swarm.fraction(new CDIFraction())
                    .fraction(new JSFFraction())
                    .fraction(new JAXRSFraction());


            for (Map.Entry<String, String> entry : swarmConfiguration.getProperties().entrySet()) {
                swarm.withProperty(entry.getKey(), entry.getValue());
            }

            swarm.start(archive);

        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

    private static Properties readConfiguration(String configFile) {
        Properties result = new Properties();
        try {
            InputStream file = new FileInputStream(configFile);
            result.load(file);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
            // FIXME
        }
        return result;

    }

    private static void checkArguments(String[] args) {
        // FIXME
    }

}
