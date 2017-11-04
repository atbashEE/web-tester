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

import be.atbash.ee.test.ui.exception.UnexpectedException;
import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;

/**
 *
 */

public class ExecuteJavaFxStatement {


    private JavaFxStatement statement;
    private String statementInfo;

    public ExecuteJavaFxStatement(JavaFxStatement statement, String statementInfo) {
        this.statement = statement;
        this.statementInfo = statementInfo;
    }

    public void doExecute() {
        CountDownLatch clickLatch = new CountDownLatch(1);

        Platform.runLater(() -> {

            statement.execute();
            try {
                // Give a bit time to execute/update the screen
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                throw new UnexpectedException(String.format("There was an InterruptedException when executing %s", e.getStackTrace()[2].toString()));
            }
            clickLatch.countDown();
        });

        try {
            clickLatch.await();
        } catch (InterruptedException e) {
            throw new UnexpectedException(String.format("There was an InterruptedException waiting for the completion of %s", statementInfo));
        }

    }
}
