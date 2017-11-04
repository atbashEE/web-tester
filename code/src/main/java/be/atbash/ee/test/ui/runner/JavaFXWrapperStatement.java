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

import be.atbash.ee.test.ui.javafx.TestApplication;
import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.runners.model.Statement;

import java.util.concurrent.CountDownLatch;

/**
 *
 */

public class JavaFXWrapperStatement extends Statement {

    private static TestThreadData threadData;

    JavaFXWrapperStatement(Statement originalStatement) {
        threadData = new TestThreadData(originalStatement);
    }

    @Override
    public void evaluate() throws Throwable {
        new Thread(() -> new ApplicationImpl().initializeJavaFX()).start();

        threadData.getLatch().await();

        threadData.getOriginalStatement().evaluate();
    }

    public static class ApplicationImpl extends Application {

        void initializeJavaFX() {
            launch();
        }

        @Override
        public void start(Stage stage) {
            TestApplication.defineTestApplication(stage);

            threadData.getLatch().countDown();
        }
    }


    public static class TestThreadData {
        private CountDownLatch latch;
        private Statement originalStatement;

        public TestThreadData(Statement originalStatement) {
            this.originalStatement = originalStatement;
            latch = new CountDownLatch(1);
        }

        public CountDownLatch getLatch() {
            return latch;
        }

        public Statement getOriginalStatement() {
            return originalStatement;
        }
    }

}
