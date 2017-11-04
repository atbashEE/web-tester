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
package be.atbash.ee.test.ui.javafx;

import be.atbash.ee.test.ui.browser.Browser;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import static org.assertj.core.api.Fail.fail;

/**
 *
 */

public class TestApplication {

    private static TestApplication INSTANCE;

    private Stage stage;

    private Browser browserEngine;

    private TestApplication(Stage stage) {
        this.stage = stage;
        defineBrowserEngine();
    }

    private void defineBrowserEngine() {
        Scene scene = new Scene(new Group());

        StackPane root = new StackPane();

        scene.setRoot(root);
        stage.setScene(scene);

        browserEngine = new Browser(stage, scene);
    }

    public static void defineTestApplication(Stage stage) {
        INSTANCE = new TestApplication(stage);
    }

    public static Browser getBrowserEngine() {
        if (INSTANCE == null || INSTANCE.browserEngine == null) {
            fail("browserEngine is null. Did you annotate the test class correctly? @RunWith(WebTestRunner.class)");
        }
        return INSTANCE.browserEngine;
    }

}
