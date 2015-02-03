package org.priorityhealth.stab.pdiff.view.headless;

import java.lang.reflect.Field;
import java.util.Objects;
import javafx.application.Application;

public class HeadlessAppLauncher {

    //---------------------------------------------------------------------------------------------
    // CONSTANTS.
    //---------------------------------------------------------------------------------------------

    public static final String PROPERTY_JAVAFX_MACOSX_EMBEDDED = "javafx.macosx.embedded";
    public static final String PROPERTY_TESTFX_HEADLESS = "javafx.monocle.headless";

    public static final String PLATFORM_FACTORY_CLASS =
            "com.sun.glass.ui.PlatformFactory";
    public static final String PLATFORM_FACTORY_MONOCLE_IMPL =
            "com.sun.glass.ui.monocle.MonoclePlatformFactory";

    public static final String NATIVE_PLATFORM_FACTORY_CLASS =
            "com.sun.glass.ui.monocle.NativePlatformFactory";
    public static final String NATIVE_PLATFORM_HEADLESS_IMPL =
            "com.sun.glass.ui.monocle.headless.HeadlessPlatform";

    //---------------------------------------------------------------------------------------------
    // METHODS.
    //---------------------------------------------------------------------------------------------

    public void launch(Class<? extends Application> appClass,
                       String... appArgs) {
        initMacosxEmbedded();
        initMonocleHeadless();
        Application.launch(appClass, appArgs);
    }

    //---------------------------------------------------------------------------------------------
    // PRIVATE METHODS.
    //---------------------------------------------------------------------------------------------

    private void initMacosxEmbedded() {
        if (checkSystemPropertyEquals(PROPERTY_JAVAFX_MACOSX_EMBEDDED, null)) {
            System.setProperty(PROPERTY_JAVAFX_MACOSX_EMBEDDED, "true");
        }
    }

    private void initMonocleHeadless() {
        if (checkSystemPropertyEquals(PROPERTY_TESTFX_HEADLESS, "true")) {
            try {
                assignMonoclePlatform();
                assignHeadlessPlatform();
            }
            catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    private boolean checkSystemPropertyEquals(String propertyName,
                                              String valueOrNull) {
        return Objects.equals(System.getProperty(propertyName, null), valueOrNull);
    }

    private void assignMonoclePlatform()
            throws Exception {
        Class<?> platformFactoryClass = Class.forName(PLATFORM_FACTORY_CLASS);
        Object platformFactoryImpl = Class.forName(PLATFORM_FACTORY_MONOCLE_IMPL).newInstance();
        assignPrivateStaticField(platformFactoryClass, "instance", platformFactoryImpl);
    }

    private void assignHeadlessPlatform()
            throws Exception {
        Class<?> nativePlatformFactoryClass = Class.forName(NATIVE_PLATFORM_FACTORY_CLASS);
        Object nativePlatformImpl = Class.forName(NATIVE_PLATFORM_HEADLESS_IMPL).newInstance();
        assignPrivateStaticField(nativePlatformFactoryClass, "platform", nativePlatformImpl);
    }

    private void assignPrivateStaticField(Class<?> cls,
                                          String name,
                                          Object value)
            throws Exception {
        Field field = cls.getDeclaredField(name);
        field.setAccessible(true);
        field.set(cls, value);
        field.setAccessible(false);
    }

}
