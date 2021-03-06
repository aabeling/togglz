package org.togglz.servlet.spi;

import java.util.concurrent.ConcurrentHashMap;

import org.togglz.core.manager.FeatureManager;
import org.togglz.core.spi.FeatureManagerProvider;
import org.togglz.servlet.TogglzFilter;

/**
 * 
 * This implementation of {@link FeatureManagerProvider} stores one {@link FeatureManager} for each context classloader. It is
 * used by {@link TogglzFilter} which binds the configuration in {@link TogglzFilter#init(javax.servlet.FilterConfig)} and
 * unbinds it in {@link TogglzFilter#destroy()}
 * 
 * @author Christian Kaltepoth
 * 
 */
public class WebAppFeatureManagerProvider implements FeatureManagerProvider {

    private static final ConcurrentHashMap<ClassLoader, FeatureManager> managerMap = new ConcurrentHashMap<ClassLoader, FeatureManager>();

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public FeatureManager getFeatureManager() {
        return managerMap.get(getContextClassLoader());
    }

    /**
     * Binds the {@link FeatureManager} to the current context classloader.
     * 
     * @param featureManager The manager to store
     */
    public static void bind(FeatureManager featureManager) {
        Object old = managerMap.putIfAbsent(getContextClassLoader(), featureManager);
        if (old != null) {
            throw new IllegalStateException(
                    "There is already a FeatureManager associated with the context ClassLoader of the current thread!");
        }
    }

    /**
     * Removes the {@link FeatureManager} associated with the current context classloader from the internal datastructure.
     */
    public static void release() {
        managerMap.remove(getContextClassLoader());
    }

    private static ClassLoader getContextClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            throw new IllegalStateException("Unable to get the context ClassLoader for the current thread!");
        }
        return classLoader;
    }

}
