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
package org.jrebirth.core.application;

import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URL;
import java.util.List;

import javafx.application.Application;
import javafx.application.Preloader;
import javafx.application.Preloader.ProgressNotification;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.SceneBuilder;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import com.sun.javafx.application.LauncherImpl;

import org.jrebirth.core.concurrent.AbstractJrbRunnable;
import org.jrebirth.core.concurrent.JRebirth;
import org.jrebirth.core.concurrent.JRebirthThread;
import org.jrebirth.core.exception.CoreException;
import org.jrebirth.core.exception.JRebirthThreadException;
import org.jrebirth.core.exception.handler.DefaultUncaughtExceptionHandler;
import org.jrebirth.core.exception.handler.JatUncaughtExceptionHandler;
import org.jrebirth.core.exception.handler.JitUncaughtExceptionHandler;
import org.jrebirth.core.exception.handler.PoolUncaughtExceptionHandler;
import org.jrebirth.core.log.JRLogger;
import org.jrebirth.core.log.JRLoggerFactory;
import org.jrebirth.core.resource.ResourceBuilders;
import org.jrebirth.core.resource.font.FontItem;
import org.jrebirth.core.resource.provided.JRebirthColors;
import org.jrebirth.core.resource.provided.JRebirthParameters;
import org.jrebirth.core.resource.provided.JRebirthStyles;
import org.jrebirth.core.resource.style.StyleSheetItem;
import org.jrebirth.core.util.ClassUtility;
import org.jrebirth.preloader.JRebirthPreloader;

/**
 * 
 * The abstract class <strong>AbstractApplication</strong> is the base class of a JRebirth Application.
 * 
 * This the class to extend if you want to build an application using JRebirth WCS-MVC (Wave-Command-Service-Model-View-Controller).
 * 
 * @author Sébastien Bordes
 * 
 * @param <P> The root node of the stage, must extends Pane to allow children management
 */
@Configuration(".*jrebirth")
@Localized(".*_rb")
public abstract class AbstractApplication<P extends Pane> extends Application implements JRebirthApplication<P>, ApplicationMessages {

    /** Default parameter replacement string. */
    private static final String PARAM = "{}";

    /** The default suffix for Application main class. */
    private static final String APP_SUFFIX_CLASSNAME = "Application";

    /** The class logger. */
    private static final JRLogger LOGGER = JRLoggerFactory.getLogger(AbstractApplication.class);

    /** The application primary stage. */
    private transient Stage stage;

    /** The application scene. */
    private transient Scene scene;

    /** The root node of the scene built by reflection. */
    private transient P rootNode;

    /**
     * Launch the Current JavaFX Application with Default JRebirth preloader.
     * 
     * @param args arguments passed to java command line
     */
    protected static void preloadAndLaunch(final String... args) {
        preloadAndLaunch(ClassUtility.getClassFromStaticMethod(3), JRebirthPreloader.class, args);
    }

    /**
     * Launch the Current JavaFX Application with given preloader.
     * 
     * @param preloaderClass the preloader class used as splash screen with progress
     * @param args arguments passed to java command line
     */
    protected static void preloadAndLaunch(final Class<? extends Preloader> preloaderClass, final String... args) {
        preloadAndLaunch(ClassUtility.getClassFromStaticMethod(3), preloaderClass, args);
    }

    /**
     * Launch the given JavaFX Application with given preloader.
     * 
     * @param appClass the JavaFX application class to launch
     * @param preloaderClass the preloader class used as splash screen with progress
     * @param args arguments passed to java command line
     */
    protected static void preloadAndLaunch(final Class<? extends Application> appClass, final Class<? extends Preloader> preloaderClass, final String... args) {
        LauncherImpl.launchApplication(appClass, preloaderClass, args);
    }

    /**
     * Launch the Current JavaFX Application (without any preloader).
     * 
     * @param args arguments passed to java command line
     */
    protected static void launchNow(final String... args) {
        launchNow(ClassUtility.getClassFromStaticMethod(3), args);
    }

    /**
     * Launch the Given JavaFX Application (without any preloader).
     * 
     * @param appClass the JavaFX application class to launch
     * @param args arguments passed to java command line
     */
    protected static void launchNow(final Class<? extends Application> appClass, final String... args) {
        Application.launch(appClass, args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void init() throws CoreException {
        try {
            super.init();

            notifyPreloader(new ProgressNotification(0.1));

            notifyPreloader(new ProgressNotification(100));
            preInit();// 200 , 300
            notifyPreloader(new ProgressNotification(0.3));

            // Load messages Files
            notifyPreloader(new ProgressNotification(400));
            loadMessagesFiles();
            notifyPreloader(new ProgressNotification(0.4));

            // Load configuration Files
            notifyPreloader(new ProgressNotification(500));
            loadConfigurationFiles();
            notifyPreloader(new ProgressNotification(0.5));

            // Build the JRebirth Thread before attaching uncaught Exception Handler
            notifyPreloader(new ProgressNotification(600));
            final JRebirthThread jrt = JRebirthThread.getThread();

            // Attach exception handlers
            initializeExceptionHandler();

            // Start the JRebirthThread, if an error occurred it will be processed by predefined handler
            // It will create all facades and trigger the pre and post boot waves and will alost attach the first model view
            jrt.prepare(this);
            notifyPreloader(new ProgressNotification(0.6));

            // Preload fonts to allow them to be used by CSS
            notifyPreloader(new ProgressNotification(700));
            preloadFonts();
            notifyPreloader(new ProgressNotification(0.7));

            postInit();// 800 , 900

            notifyPreloader(new ProgressNotification(1000));
            notifyPreloader(new ProgressNotification(1.0));

        } catch (final Exception e) {
            LOGGER.error(ApplicationMessages.INIT_ERROR, e, this.getClass().getSimpleName());
            throw new CoreException(e);
        }
    }

    /**
     * Perform custom task before application initialization phase.
     */
    protected abstract void preInit();

    /**
     * Perform custom task after application initialization phase and before starting phase.
     */
    protected abstract void postInit();

    /**
     * {@inheritDoc}
     */
    @Override
    public final void start(final Stage primaryStage) throws CoreException {

        try {
            LOGGER.log(START_APPLICATION, this.getClass().getSimpleName());

            // Attach the primary stage for later customization
            this.stage = primaryStage;

            // Customize the primary stage
            initializeStage();

            // Build and customize the default scene
            this.scene = buildScene();
            initializeScene();

            // Attach the scene
            primaryStage.setScene(this.scene);

            JRebirthThread.getThread().start();

            // Let the stage visible for users
            primaryStage.show();

            LOGGER.log(STARTED_SUCCESSFULLY, this.getClass().getSimpleName());

        } catch (final CoreException ce) {
            LOGGER.error(START_ERROR, ce, this.getClass().getSimpleName());
            throw new CoreException(ce);
        }
    }

    /**
     * Load all configuration files before showing anything.
     */
    private void loadConfigurationFiles() {

        // Parse the first annotation found (manage overriding)
        final Configuration conf = ClassUtility.extractAnnotation(this.getClass(), Configuration.class);

        // Conf variable cannot be null because it was defined in this class
        // It's possible to discard default behaviour by setting an empty string to the value.

        // launch the configuration search engine
        ResourceBuilders.PARAMETER_BUILDER.searchConfigurationFiles(conf.value(), conf.extension());

        // Take into account the log resolution parameter
        // ResourceBuilders.MESSAGE_BUILDER.setLogResolutionActivated(JRebirthParameters.LOG_RESOLUTION.get());
    }

    /**
     * Load all Messages files before showing anything.
     */
    private void loadMessagesFiles() {

        // Parse the first annotation found (manage overriding)
        final Localized local = ClassUtility.extractAnnotation(this.getClass(), Localized.class);

        // Conf variable cannot be null because it was defined in this class
        // It's possible to discard default behavior by setting an empty string to the value.

        // launch the configuration search engine
        ResourceBuilders.MESSAGE_BUILDER.searchMessagesFiles(local.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void stop() throws CoreException {
        try {
            LOGGER.log(STOP_APPLICATION, this.getClass().getSimpleName());
            super.stop();

            // Hide the stage is this method wasn't call by user
            if (getStage().isShowing()) {
                getStage().hide();
            }

            // Now nothing is visible by users, Let's kill and release all JRebirth folks
            // without loosing or corrupting something

            // Be Careful done into the JAT
            // Should create a progress bar to control the closure process

            // Flag used to have 2 different waiting times
            boolean firstTime = true;
            do {
                // Try to stop the JRebirth Thread
                JRebirthThread.getThread().close();

                // Wait parameterized delay before retrying to close if the thread is still alive
                Thread.sleep(firstTime ? JRebirthParameters.CLOSE_RETRY_DELAY_FIRST.get() : JRebirthParameters.CLOSE_RETRY_DELAY_OTHER.get());

                if (firstTime) {
                    firstTime = false;
                }
            } while (JRebirthThread.getThread().isAlive());

            LOGGER.log(STOPPED_SUCCESSFULLY, this.getClass().getSimpleName());

        } catch (final Exception e) {
            LOGGER.error(STOP_ERROR, e, this.getClass().getSimpleName());
            throw new CoreException(e);
        }
    }

    /**
     * Customize the primary Stage.
     */
    private void initializeStage() {
        // Define the stage title
        this.stage.setTitle(getApplicationTitle());

        // and allow customization
        customizeStage(this.stage);
    }

    /**
     * Customize the primary stage.
     * 
     * @param stage the primary stage to customize
     */
    protected abstract void customizeStage(final Stage stage);

    /**
     * Initialize the default scene.
     */
    private void initializeScene() {

        final Stage currentStage = this.stage;

        final KeyCode fullKeyCode = getFullScreenKeyCode();
        final KeyCode iconKeyCode = getIconifiedKeyCode();

        // Attach the handler only if necessary, these 2 method can be overridden to return null
        if (fullKeyCode != null && iconKeyCode != null) {

            this.scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

                @Override
                public void handle(final KeyEvent event) {
                    // Manage F11 button to switch full screen
                    if (fullKeyCode != null && fullKeyCode == event.getCode()) {
                        currentStage.setFullScreen(!currentStage.isFullScreen());
                        event.consume();
                        // Manage F10 button to iconify
                    } else if (iconKeyCode != null && iconKeyCode == event.getCode()) {
                        currentStage.setIconified(!currentStage.isIconified());
                        event.consume();
                    }

                }
            });
        }

        // The call customize method to allow extension by sub class
        customizeScene(this.scene);

        // Add the default Style Sheet if none have been added
        manageDefaultStyleSheet(this.scene);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preloadFonts() {
        final List<FontItem> fontList = getFontToPreload();
        if (fontList != null) {
            for (final FontItem font : fontList) {
                // Access the font to load it and allow it to be used by CSS
                font.get();
            }
        }
    }

    /**
     * Return the list of FontEnum to load for CSS.
     * 
     * @return the list of fontEnum to load
     */
    protected abstract List<FontItem> getFontToPreload();

    /**
     * Customize the default scene.
     * 
     * @param scene the scene to customize
     */
    protected abstract void customizeScene(final Scene scene);

    /**
     * Attach a new CSS file to the scene using the default classloader.
     * 
     * @param scene the scene that will hold this new CSS file
     * @param styleSheetItem the stylesheet item to add
     */
    protected void addCSS(final Scene scene, final StyleSheetItem styleSheetItem) {

        final URL styleSheetURL = styleSheetItem.get();
        if (styleSheetURL == null) {
            LOGGER.error(CSS_LOADING_ERROR, styleSheetItem.toString(), JRebirthParameters.STYLE_FOLDER.get());
        } else {
            scene.getStylesheets().add(styleSheetURL.toExternalForm());
        }

    }

    /**
     * Return the application title.
     * 
     * This method could be overridden.
     * 
     * By default it will will return {@link JRebirthParameters.APPLICATION_NAME} {@link JRebirthParameters.APPLICATION_VERSION} string.
     * 
     * The default application is: ApplicationClass powered by JRebirth <br />
     * If version is equals to "0.0.0", it will not be appended
     * 
     * @return the application title
     */
    protected String getApplicationTitle() {
        // Add application Name
        String name = JRebirthParameters.APPLICATION_NAME.get();
        if (name.contains(PARAM)) {
            name = name.replace(PARAM, getShortClassName());
        }
        // Add version with a space before
        final String version = JRebirthParameters.APPLICATION_VERSION.get();
        final StringBuilder sb = new StringBuilder(name);
        if (!"0.0.0".equals(version)) {
            sb.append(" ").append(version);
        }
        return sb.toString();
    }

    /**
     * Return the application class name without the Application suffix.
     * 
     * @return the application class short name
     */
    private String getShortClassName() {
        String name = this.getClass().getSimpleName();
        if (name.endsWith(APP_SUFFIX_CLASSNAME)) {
            name = name.substring(0, name.indexOf(APP_SUFFIX_CLASSNAME));
        }
        return name;
    }

    /**
     * Attach default CSS file if none have been previously attached.
     * 
     * @param scene the scene to check
     */
    private void manageDefaultStyleSheet(final Scene scene) {
        if (scene.getStylesheets().size() < 1) {
            // No style sheet has been added to the scene
            LOGGER.log(NO_CSS_DEFINED);
            addCSS(scene, JRebirthStyles.DEFAULT);
        }

    }

    /**
     * Initialize the properties of the scene.
     * 
     * 800x600 with transparent background and a Region as Parent Node
     * 
     * @return the scene built
     * 
     * @throws CoreException if build fails
     */
    protected final Scene buildScene() throws CoreException {
        return SceneBuilder.create()
                .root(buildRootPane())
                .width(JRebirthParameters.APPLICATION_SCENE_WIDTH.get())
                .height(JRebirthParameters.APPLICATION_SCENE_HEIGHT.get())
                .fill(JRebirthColors.SCENE_BG_COLOR.get())
                .build();
    }

    /**
     * Build dynamically the root pane.
     * 
     * @return the root pane
     * @throws CoreException if build fails
     */
    @SuppressWarnings("unchecked")
    protected P buildRootPane() throws CoreException {
        this.rootNode = (P) ClassUtility.buildGenericType(this.getClass(), Pane.class);
        return this.rootNode;
    }

    /**
     * Initialize all Uncaught Exception Handler.
     */
    protected void initializeExceptionHandler() {

        // Initialize the default uncaught exception handler for all other threads
        Thread.setDefaultUncaughtExceptionHandler(getDefaultUncaughtExceptionHandler());

        // Initialize the uncaught exception handler for JavaFX Application Thread
        JRebirth.runIntoJAT(new AbstractJrbRunnable(ATTACH_JAT_UEH.get()) {
            @Override
            public void runInto() throws JRebirthThreadException {
                Thread.currentThread().setUncaughtExceptionHandler(getJatUncaughtExceptionHandler());
            }
        });

        // Initialize the uncaught exception handler for JRebirth Internal Thread
        JRebirth.runIntoJIT(new AbstractJrbRunnable(ATTACH_JIT_UEH.get()) {
            @Override
            public void runInto() throws JRebirthThreadException {
                Thread.currentThread().setUncaughtExceptionHandler(getJitUncaughtExceptionHandler());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stage getStage() {
        return this.stage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Scene getScene() {
        return this.scene;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public P getRootNode() {
        return this.rootNode;
    }

    /**
     * Return the #KeyCode used to put the application in full screen mode.<br />
     * Can be overridden<br />
     * Default is F11<br />
     * 
     * @return the full screen shortcut
     */
    protected KeyCode getFullScreenKeyCode() {
        return KeyCode.F11;
    }

    /**
     * Return the #KeyCode used to iconify the application.<br />
     * Can be overridden<br />
     * Default is F10<br />
     * 
     * @return the iconify shortcut
     */
    protected KeyCode getIconifiedKeyCode() {
        return KeyCode.F10;
    }

    /**
     * Build and return the Default Uncaught Exception Handler for All threads which don't have any handler.
     * 
     * @return the uncaught exception handler for All threads which don't have any handler.
     */
    protected UncaughtExceptionHandler getDefaultUncaughtExceptionHandler() {
        return new DefaultUncaughtExceptionHandler();
    }

    /**
     * Build and return the Uncaught Exception Handler for JavaFX Application Thread.
     * 
     * @return the uncaught exception handler for JavaFX Application Thread
     */
    protected UncaughtExceptionHandler getJatUncaughtExceptionHandler() {
        return new JatUncaughtExceptionHandler();
    }

    /**
     * Build and return the Uncaught Exception Handler for JRebirth Internal Thread.
     * 
     * @return the uncaught exception handler for JRebirth Internal Thread
     */
    protected UncaughtExceptionHandler getJitUncaughtExceptionHandler() {
        return new JitUncaughtExceptionHandler();
    }

    /**
     * Build and return the Uncaught Exception Handler for JRebirth Thread Pool.
     * 
     * @return the uncaught exception handler for JRebirth Thread Pool
     */
    public UncaughtExceptionHandler getPoolUncaughtExceptionHandler() {
        return new PoolUncaughtExceptionHandler();
    }
}
