/**
 * Get more info at : www.jrebirth.org .
 * Copyright JRebirth.org © 2011-2013
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
package org.jrebirth.core.command;

import org.jrebirth.core.concurrent.RunInto;
import org.jrebirth.core.concurrent.RunType;
import org.jrebirth.core.exception.CoreException;
import org.jrebirth.core.wave.Wave;
import org.jrebirth.core.wave.WaveBean;

/**
 * The class <strong>DefaultPoolCommand</strong>.
 * 
 * The default empty class for commands that must be run into a new thread using a pool.
 * 
 * @author Sébastien Bordes
 */
@RunInto(RunType.JTP)
public class DefaultPoolCommand extends AbstractSingleCommand<WaveBean> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void ready() throws CoreException {
        // Nothing to do yet by the default Pool command, must be overridden
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute(final Wave wave) {
        // Nothing to do yet by the default Pool command, must be overridden
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processAction(final Wave wave) {
        // Nothing to do yet by the default Pool command, must be overridden
    }

}
