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
package be.atbash.ee.test.ui.runner;

import be.atbash.ee.test.ui.PublicAPI;
import be.atbash.ee.test.ui.config.ServerConfigRule;
import be.atbash.ee.test.ui.config.SwarmConfiguration;
import be.atbash.ee.test.ui.exception.WebTesterConfigurationException;
import be.atbash.ee.test.ui.plugin.Technology;
import be.atbash.ee.test.ui.plugin.WebPageFactory;
import org.apache.commons.lang.StringUtils;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;

/**
 *
 */
@PublicAPI
public class WebTestRunner extends BlockJUnit4ClassRunner {

    // We can't run in parallel due to JavaFX, so this is not an issue
    public static SwarmConfiguration swarmConfiguration;

    // We can't run in parallel due to JavaFX, so this is not an issue
    public static String technologyName;

    // TODO We need to hide this so developer can't manipulate it.
    // But it will complicate testing :)
    public static Map<String, WebPageFactory> factories;

    static {
        factories = new HashMap<>();
        for (WebPageFactory webPageFactory : ServiceLoader.load(WebPageFactory.class)) {
            factories.put(webPageFactory.getTechnologyName(), webPageFactory);
        }
    }

    /**
     * Creates a BlockJUnit4ClassRunner to run {@code klass}
     *
     * @param klass
     * @throws InitializationError if the test class is malformed.
     */
    public WebTestRunner(Class<?> klass) throws InitializationError {
        super(klass);

        defineSwarmConfiguration(klass);

        defineTechnology(klass);
        checkTechnology(klass);
    }

    private void checkTechnology(Class<?> klass) {
        String trimmedName = StringUtils.trimToEmpty(technologyName).toLowerCase(Locale.ENGLISH);
        if (StringUtils.isNotEmpty(trimmedName)) {
            if (!factories.containsKey(trimmedName)) {
                throw new WebTesterConfigurationException(klass, String.format("Unknown technology name %s", trimmedName));
            }
        }
        technologyName = trimmedName;
    }

    private void defineTechnology(Class<?> klass) {
        Technology technology = klass.getAnnotation(Technology.class);
        if (technology != null) {
            technologyName = technology.value();
        } else {
            technologyName = null;
        }
    }

    private void defineSwarmConfiguration(Class<?> klass) {
        Method[] methods = klass.getDeclaredMethods();
        for (Method method : methods) {
            int isStatic = method.getModifiers() & Modifier.STATIC;
            if (isStatic != 0 && method.getAnnotation(ServerConfigRule.class) != null) {
                Class<?> returnType = method.getReturnType();
                if (!SwarmConfiguration.class.isAssignableFrom(returnType)) {
                    throw new WebTesterConfigurationException(returnType);
                }

                try {
                    swarmConfiguration = (SwarmConfiguration) method.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    // FIXME
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        return new JavaFXWrapperStatement(super.classBlock(notifier));
    }
}
