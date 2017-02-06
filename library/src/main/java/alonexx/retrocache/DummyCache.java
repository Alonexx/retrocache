package alonexx.retrocache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A do-nothing cache. Any operations would cause an Exception.
 */
public class DummyCache implements CacheInterface {
    @Override
    public OutputStream newOutputStreamForKey(String key) throws IOException {
        throw new IOException("Can't write to a dummy cache.");
    }

    @Override
    public InputStream getInputStreamForKey(String key) throws IOException {
        throw new KeyNotFoundException();
    }

    @Override
    public long getCreationTimeForKey(String key) throws IOException {
        throw new KeyNotFoundException();
    }

    @Override
    public void close() throws IOException {

    }
}
