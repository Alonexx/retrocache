package alonexx.retrocache;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Defines an interface that maps keys to disk IO streams or memory streams.
 *
 * <p>The key is generated by a specified {@link KeyTransformer}, with the method
 * signature and the arguments passed by the call. Implementations should store
 * the creation time for each key.
 *
 * <p>Note that any resources associated with the returned {@code OutputStream}s
 * or {@code InputStream}s such as files, database cursors, and memory streams
 * should be correctly disposed when the clients call {@link Closeable#close()}.
 */
public interface CacheInterface extends Closeable {

    /**
     * Returns a buffered OutputStream for a specified key.
     */
    OutputStream newOutputStreamForKey(String key) throws IOException;

    /**
     * Returns a buffered InputStream for a specified key. If the cache can't find
     * a corresponding record, throw a KeyNotFoundException.
     */
    InputStream getInputStreamForKey(String key) throws IOException;

    /**
     * Returns the creation time for a specified key.
     * @throws IOException If an IO error occurs, throw an IOException.
     * @throws KeyNotFoundException If the record does not exist, throw a KeyNotFoundException.
     */
    long getCreationTimeForKey(String key) throws IOException;
}
