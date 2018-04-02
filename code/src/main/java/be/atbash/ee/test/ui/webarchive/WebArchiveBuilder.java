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
import be.atbash.ee.test.ui.exception.WrongArchiveNameException;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;

/**
 *
 */
@PublicAPI
public class WebArchiveBuilder {

    private WebArchive webArchive;
    private PomEquippedResolveStage pom;
    private boolean customCDIConfig;
    private boolean customWebConfig;

    private WebArchiveBuilder(String name) {
        webArchive = ShrinkWrap
                .create(WebArchive.class, fixExtension(name));

        pom = Resolvers.use(ConfigurableMavenResolverSystem.class).withMavenCentralRepo(false).loadPomFromFile("pom.xml");
    }

    private String fixExtension(String name) {
        String[] parts = name.split("\\.", 2);
        if (parts.length == 1) {
            return name + ".war";
        }
        if (!"war".equalsIgnoreCase(parts[1])) {
            throw new WrongArchiveNameException(parts[1]);
        }
        return name;
    }

    public WebArchiveBuilder addDependencyNoTransitivity(Library library) {
        webArchive.addAsLibraries(pom.resolve(library.getCanonicalForms()).withoutTransitivity().asFile());
        return this;
    }

    public WebArchiveBuilder addDependencyNoTransitivity(String canonicalForm) {
        webArchive.addAsLibraries(pom.resolve(canonicalForm).withoutTransitivity().asFile());
        return this;
    }

    public WebArchiveBuilder addDependency(Library library) {
        webArchive.addAsLibraries(pom.resolve(library.getCanonicalForms()).withTransitivity().asFile());
        return this;
    }

    public WebArchiveBuilder addDependency(String canonicalForm) {
        webArchive.addAsLibraries(pom.resolve(canonicalForm).withTransitivity().asFile());
        return this;
    }

    public WebArchiveBuilder addClass(Class<?> aClass) {
        webArchive.addClass(aClass);
        return this;
    }

    public WebArchiveBuilder addResource(String localFileName, String archiveName) {
        webArchive.addAsResource(localFileName, archiveName);
        return this;
    }

    public WebArchiveBuilder addWebPage(String localFileName, String archiveName) {

        webArchive.addAsWebResource(localFileName, archiveName);
        return this;
    }

    public WebArchiveBuilder addAsWebInfResource(String localFileName, String archiveName) {
        webArchive.addAsWebInfResource(localFileName, archiveName);
        return this;
    }

    public WebArchiveBuilder addCustomCDIBean(String localFileName) {
        customCDIConfig = true;
        webArchive.addAsWebInfResource(localFileName, "beans.xml");
        return this;
    }

    public WebArchiveBuilder addCustomWebXml(String localFileName) {
        customWebConfig = true;
        webArchive.addAsWebInfResource(localFileName, "web.xml");
        return this;
    }

    public WebArchiveBuilder addWebPage(String fileName) {
        // Use the method with 2 parameters, otherwise a file within a subdirectory like pages/main.xhtml, is imported
        // at /main.xhtml (not within pages directory)
        webArchive.addAsWebResource(fileName, fileName);
        return this;
    }

    public WebArchive build() {
        if (!customCDIConfig) {
            webArchive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        }

        if (!customWebConfig) {
            webArchive.addAsWebInfResource("default/WEB-INF/web.xml", "web.xml");

        }
        return webArchive;
    }

    public static WebArchiveBuilder create(String name) {
        return new WebArchiveBuilder(name);
    }

}
