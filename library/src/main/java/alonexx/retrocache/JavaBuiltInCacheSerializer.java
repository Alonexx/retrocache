package alonexx.retrocache;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * A serializer that uses Java built-in serialization and deserialization methods for
 * transformations between Java Objects and streams.
 *
 * <p>Note that all the objects to be serialized should implement {@link java.io.Serializable}
 * interface. Serialization results may differ in different version of Android OS or JVM.
 * This may lead to cache failures when users update their Android OS or JVM.
 */
public class JavaBuiltInCacheSerializer implements CacheSerializer {
    @Override
    public Object readFromCache(InputStream in, CacheContext context) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(in);
        Object obj = ois.readObject();
        return obj;
    }

    @Override
    public void writeToCache(OutputStream out, CacheContext context, Object obj) throws Exception {
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(obj);
        oos.flush();
    }
}
