package akme.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonReader;

/**
 * Dependencies to be injected via constructor, allowing ease of <code>Object get(Object key, Object default)</code>.
 * Use <code>V get(Class&lt;V&gt; key, V default)</code> to avoid casting and string names.
 * This is intended to work with constructor injection that sets private final dependency properties,
 * similar to Google Guice but just in one simple class extending Properties and supporting obtaining Singletons
 * and, via putCallableWrapped(), Prototypes.
 */
public class Dependencies extends Properties {

    private static final long serialVersionUID = -389261885864080820L;

    static Pattern FILENAME_REGEX = Pattern.compile("/[^/]+$");

    public Dependencies() {
        super();
    }

    public Dependencies(Properties props) {
        super(props);
    }

    public void loadFromJSON(String urlResource) throws IOException {
        if (urlResource == null) {
            throw new NullPointerException("urlResource");
        }
        loadFromJSON(ResourceUtil.getURL(ResourceUtil.resolveSystemProperties(urlResource)));
    }

    public void loadFromJSON(File configFile) throws IOException {
        if (configFile == null) {
            throw new NullPointerException("configFile");
        }
        loadFromJSON(configFile.toURI().toURL());
    }

    public void loadFromJSON(URL url) throws IOException {
        if (url == null) {
            throw new NullPointerException("url");
        }
        try (final InputStream ins = url.openStream();
                final JsonReader rdr = Json.createReader(new InputStreamReader(ins, StandardCharsets.UTF_8))) {

            this.putAll(rdr.readObject());
            // Save the config file path as a property
            this.put("__configDirAbsolutePath__", FILENAME_REGEX.matcher(url.toString()).replaceFirst(""));
        }
    }

    /**
     * Get by key also trying any defaults given to the constructor.
     * If the value is found, other than the given defaultValue, and that value is Callable,
     * the result of the Callable.call() will be returned rather than the Callable itself.
     * Throws an IllegalStateException if the dependency is missing/null.
     */
    @Override
    public Object get(Object key) {
        Object val = super.get(key);
        if (val == null && defaults != null) {
            val = defaults.get(key);
        }
        if (val == null) {
            throw new IllegalStateException("Missing dependency " + key);
        } else if (val instanceof Supplier<?>) {
            val = ((Supplier<?>) val).get();
        } else if (val instanceof Callable<?>) {
            try {
                val = ((Callable<?>) val).call();
            } catch (Exception ex) {
                throw new IllegalStateException(ex.getMessage() + " Cause: " + ex.getCause(), ex);
            }
        }
        return val;
    }

    /**
     * Get by key also trying any defaults given to the constructor.
     * Avoids casting if given a Class as key.
     * If the value is found, other than the given defaultValue, and that value is Callable,
     * the result of the Callable.call() will be returned rather than the Callable itself.
     * Throws an IllegalStateException if the dependency is missing/null.
     */
    @SuppressWarnings("unchecked")
    public <V> V get(Class<V> key) {
        return (V) get((Object) key);
    }

    /**
     * Get by key also trying any defaults given to the constructor, and if still null then the given default.
     * Avoids casting if given a default value.
     * If the value is found, other than the given defaultValue, and that value is Callable,
     * the result of the Callable.call() will be returned rather than the Callable itself.
     * Throws an IllegalStateException if the dependency is missing/null.
     */
    @SuppressWarnings("unchecked")
    public <V> V get(Object key, V defaultValue) {
        V val = (V) super.get(key);
        if (val == null && defaults != null) {
            val = (V) defaults.get(key);
        } else if (val instanceof Supplier<?>) {
            val = ((Supplier<V>) val).get();
        } else if (val instanceof Callable<?>) {
            try {
                val = ((Callable<V>) val).call();
            } catch (Exception ex) {
                throw new IllegalStateException(ex.getMessage() + " Cause: " + ex.getCause(), ex);
            }
        }
        return (val == null) ? defaultValue : val;
    }

    /**
     * Get by key also trying any defaults given to the constructor, and if still null then the given default.
     * Avoids casting if given a Class as key and a compatible defaultValue.
     * If the value is found, other than the given defaultValue, and that value is Callable,
     * the result of the Callable.call() will be returned rather than the Callable itself.
     * Throws an IllegalStateException if the dependency is missing/null.
     */
    public <V> V get(Class<V> key, V defaultValue) {
        return get((Object) key, defaultValue);
    }

    /**
     * Get by key and cast to the given Class<V> type.
     * 
     * @see V get(Class<V> key)
     */
    @SuppressWarnings("unchecked")
    public <V> V getAsClass(Class<V> type, Object key) {
        return (V) get(key);
    }

    /**
     * Get by key and cast to the given Class<V> type, using the given default if otherwise null.
     * 
     * @see V get(Class<V> key, V defaultValue)
     */
    public <V> V getAsClass(Class<V> type, Object key, V defaultValue) {
        return (V) get(key, defaultValue);
    }

    /**
     * Put the given value in the Dependencies with key by its class, i.e. value.getClass().
     * Throws NullPointerException if value is null.
     */
    public <V> Object putByClass(Object value) {
        return put(value.getClass(), value);
    }

    /**
     * Put the given Callable into the dependencies wrapped in another callable
     * such that a get(key) will return the original Callable itself.
     * This simplifies setting and getting a Callable itself rather than getting what it returns.
     */
    public <V> Object putCallableWrapped(Object key, Callable<V> value) {
        return super.put(key, new WrappedCallable<V>(value));
    }

    public <V> Object putSupplier(Object key, Supplier<V> value) {
        return super.put(key, value);
    }

    /**
     * Put the given Supplier into the dependencies wrapped in another supplier
     * such that a get(key) will return the original Supplier itself.
     * This simplifies setting and getting a Supplier itself rather than getting what it returns.
     */
    public <V> Object putSupplierWrapped(Object key, Supplier<V> value) {
        return super.put(key, new WrappedSupplier<V>(value));
    }

    // This avoids excess GC-effects of using closure around a final value in the inner class.
    static class WrappedCallable<V> implements Callable<Callable<V>> {
        private final Callable<V> callable;

        public WrappedCallable(Callable<V> callable) {
            this.callable = callable;
        }

        @Override
        public Callable<V> call() {
            return callable;
        }
    }

    // This avoids excess GC-effects of using closure around a final value in the inner class.
    static class WrappedSupplier<V> implements Supplier<Supplier<V>> {
        private final Supplier<V> supplier;

        public WrappedSupplier(Supplier<V> supplier) {
            this.supplier = supplier;
        }

        @Override
        public Supplier<V> get() {
            return supplier;
        }
    }

}