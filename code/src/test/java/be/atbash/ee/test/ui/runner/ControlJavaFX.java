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

import be.atbash.ee.test.ui.exception.ExecutionTimeoutException;
import javafx.application.Platform;
import org.junit.runners.model.Statement;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 *
 */

public class ControlJavaFX {

    private static Statement nullStatement = new Statement() {
        @Override
        public void evaluate() throws Throwable {
        }
    };

    public static void start() throws Throwable {

        new JavaFXWrapperStatement(nullStatement).evaluate();
    }

    public static void shutdown() {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(new ExitRunner(latch));

        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new ExecutionTimeoutException(e, "Waiting for JavaFX platform to exit");
        }

    }

    private static class ExitRunner implements Runnable {

        private CountDownLatch latch;

        ExitRunner(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            if (Platform.isFxApplicationThread()) {
                Platform.exit();
            }
            // Signals that JavaFX command is finished.
            latch.countDown();
        }
    }
}
