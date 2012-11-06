/**
 * Copyright JRebirth.org © 2011-2012 
 * Contact : sebastien.bordes@jrebirth.org
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
package org.jrebirth.analyzer;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.jrebirth.analyzer.command.OpenEventTrackerFileCommand;
import org.jrebirth.analyzer.ui.editor.EditorWaves;
import org.jrebirth.analyzer.ui.workbench.WorkbenchModel;
import org.jrebirth.core.application.AbstractApplication;
import org.jrebirth.core.ui.Model;
import org.jrebirth.core.wave.Wave;
import org.jrebirth.core.wave.WaveBuilder;
import org.jrebirth.core.wave.WaveData;
import org.jrebirth.core.wave.WaveGroup;

/**
 * The class <strong>JRebirthAnalyzer</strong>.
 * 
 * The application that demonstrate how to use JRebirth Framework.
 * 
 * It also allows showing all JRebirth events.
 * 
 * @author Sébastien Bordes
 */
public final class JRebirthAnalyzer extends AbstractApplication<StackPane> {

    /**
     * Application launcher.
     * 
     * @param args the command line arguments
     */
    public static void main(final String... args) {
        Application.launch(JRebirthAnalyzer.class, args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends Model> getFirstModelClass() {
        return WorkbenchModel.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getApplicationTitle() {
        return "JavaFX 2.0 - JRebirth Event Analyzer";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void customizeScene(final Scene scene) {
        scene.getStylesheets().add("css/analyzer.css");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void customizeStage(final Stage stage) {
        // Customize the current stage
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Wave getPreBootWave() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Wave getPostBootWave() {
        Wave wave = null;
        final Parameters p = getParameters();
        if (p.getRaw().size() >= 1) {
            final String etdFile = p.getRaw().get(0);
            final File file = new File(etdFile);
            if (file.exists()) {

                wave = WaveBuilder.create()
                        .waveGroup(WaveGroup.CALL_COMMAND)
                        .relatedClass(OpenEventTrackerFileCommand.class)
                        .data(WaveData.build(EditorWaves.EVENTS_FILE, file))
                        .build();
            }
        }
        return wave;
    }
}
