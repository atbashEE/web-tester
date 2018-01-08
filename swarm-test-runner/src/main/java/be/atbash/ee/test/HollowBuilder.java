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

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.wildfly.swarm.arquillian.resolver.ShrinkwrapArtifactResolvingHelper;
import org.wildfly.swarm.tools.ArtifactSpec;
import org.wildfly.swarm.tools.BuildTool;
import org.wildfly.swarm.tools.DeclaredDependencies;

import java.io.File;

/**
 * TODO This isn't working properly (yet?)
 */

public class HollowBuilder {


    public static void main(String[] args) {
        BuildTool buildTool = new BuildTool(ShrinkwrapArtifactResolvingHelper.defaultInstance());

        DeclaredDependencies dependencies = new DeclaredDependencies();

        // We need to add this because there is mismatch in versions WildFly 10.1.0 and WildFly Core 2.2.1.Final used in Swarm
        dependencies.add(ArtifactSpec.fromMscGav("io.undertow:undertow-servlet:1.4.0.Final"));


        // FIXME This needs to be in the local repository before it can be used by this class
        // FIXME so split it up even more.
        // FIXME And we need a plugin or investigate how to use the swarm-tool-plugin and creation of hollow jar
        // To Have the main class
        dependencies.add(ArtifactSpec.fromMscGav("be.atbash.ee.test:swarm-test-runner:0.1"));


        buildTool.declaredDependencies(dependencies);

        WebArchive webArchive = ShrinkWrap
                .create(WebArchive.class, "fake.war");
        // It just can't be empty, that's all :)
        // And brings already CDI fraction in as a bonus.
        webArchive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        buildTool.projectArchive(webArchive);
        buildTool.mainClass(SwarmTestRunner.class.getName());

        buildTool.hollow(true);

        buildTool.fraction(ArtifactSpec.fromMscGav("org.wildfly.swarm:jaxrs:2017.12.1"));
        buildTool.fraction(ArtifactSpec.fromMscGav("org.wildfly.swarm:jsf:2017.12.1"));

        try {
            Archive hollowJar = buildTool.build();

            File archiveFile = new File("./swarm-test-runner/target/swarm-test-runner-0.1-uberjar.jar");
            hollowJar.as(ZipExporter.class).exportTo(archiveFile.getCanonicalFile(), true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
