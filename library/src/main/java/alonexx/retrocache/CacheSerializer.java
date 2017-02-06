package alonexx.retrocache;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Defines a serialization interface that do transformations between Java Objects and streams.
 *
 * <p>The default serializer is set to using Java Serialization. However, there's no guarantee
 * for the serialization result compatibilities between different Java VM implementations. So
 * you should always write your own serialization method based on some compatible format, such as
 * JSON, XML or Protocol Buffers. If your data is stored on unreliable storage such as an
 * Android phone, you should encrypt sensitive data before writing them to disk.
 */
public interface CacheSerializer {

    /**
     * Reads an object from a specified input.
     *
     * @param in      a buffered InputStream
     * @param context the context that contains method and object type information.
     */
    Object readFromCache(InputStream in, CacheContext context) throws Exception;

    /**
     * Writes an object to a specified output.
     *
     * @param out     a buffered OutputStream
     * @param context the context that contains method and object type information.
     * @param obj     the object to write
     */
    void writeToCache(OutputStream out, CacheContext context, Object obj) throws Exception;
}
