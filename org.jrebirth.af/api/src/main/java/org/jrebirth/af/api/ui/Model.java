/**
 * Get more info at : www.jrebirth.org .
 * Copyright JRebirth.org © 2011-2016
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
package org.jrebirth.af.api.ui;

import javafx.scene.Node;

import org.jrebirth.af.api.component.behavior.BehavioredComponent;
import org.jrebirth.af.api.wave.Wave;
import org.jrebirth.af.api.wave.WaveConstants;
import org.jrebirth.af.api.wave.annotation.OnWave;

/**
 * The interface <strong>Model</strong>.
 *
 * The contract for the model layer.
 *
 * @author Sébastien Bordes
 */
public interface Model extends BehavioredComponent<Model> {

    /**
     * Return the view.
     *
     * @return the view managed
     */
    View<?, ?, ?> view();

    /**
     * Return the root node.
     *
     * @return the root node of the managed view
     */
    Node node();

    /**
     * Perform the show view action triggered by a wave.
     *
     * Method handler for Wave JRebirthWaves.SHOW_VIEW
     *
     * @param wave the wave that trigger the action
     */
    @OnWave(WaveConstants.SHOW_VIEW)
    void doShowView(final Wave wave);

    /**
     * Perform the hide view action triggered by a wave.
     *
     * Method handler for Wave JRebirthWaves.HIDE_VIEW
     *
     * @param wave the wave that trigger the action
     */
    @OnWave(WaveConstants.HIDE_VIEW)
    void doHideView(final Wave wave);
}
