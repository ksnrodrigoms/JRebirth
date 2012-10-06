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
package org.jrebirth.presentation;

import javafx.scene.text.Font;

import org.jrebirth.core.resource.ResourceFactories;
import org.jrebirth.core.resource.font.FontEnum;
import org.jrebirth.core.resource.font.FontFactory;
import org.jrebirth.core.resource.font.FontParams;
import org.jrebirth.core.resource.font.RealFont;

/**
 * The class <strong>PrezFonts</strong>.
 * 
 * @author Sébastien Bordes
 * 
 */
public enum PrezFonts implements FontEnum {

    /** The splash font. */
    PAGE(new RealFont(FontsLoader.BorisBlackBloxx, 36)),

    /** The slide title font. */
    SLIDE_TITLE(new RealFont(FontsLoader.Harabara, 45)),

    /** The slide title font. */
    SLIDE_SUB_TITLE(new RealFont(FontsLoader.Harabara, 30)),

    /** The typewriter font. */
    TYPEWRITER(new RealFont(FontsLoader.Report_1942, 72)),

    /** The splash font. */
    SPLASH(new RealFont(FontsLoader.BorisBlackBloxx, 30));

    /**
     * Default Constructor.
     * 
     * @param fontParams the font size
     */
    PrezFonts(final FontParams fontParams) {
        factory().storeParams(this, fontParams);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Font get() {
        return factory().get(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FontFactory factory() {
        return (FontFactory) ResourceFactories.FONT_FACTORY.use();
    }

}
