package alonexx.retrocache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An implementation of {@code CacheInterface} using Jake Wharton's DiskLruCache.
 *
 * <p>There's many alternative choices of an LRU disk cache. You can find one inside
 * OkHttp library, another inside Glide, or the source code of Android Jelly Bean MR1.
 * This depends on the library integrated in your app.
 */
public class DiskLruCacheJakeWhartonImpl implements CacheInterface, Closeable {

    private static final int DISK_VALUE_COUNT = 2;

    private final DiskLruCache cache;

    public DiskLruCacheJakeWhartonImpl(
            File directory,
            int appVersion,
            long maxSize) throws IOException {
        cache = DiskLruCache.open(directory, appVersion, DISK_VALUE_COUNT, maxSize);
    }

    @Override
    public OutputStream newOutputStreamForKey(String key) throws IOException {
        DiskLruCache.Editor editor = cache.edit(key);
        if (editor == null) {
            throw new IOException("Cache is unavailable for editing.");
        }
        return new AutoCommitOutputStream(new BufferedOutputStream(editor.newOutputStream(1)), editor);
    }

    @Override
    public InputStream getInputStreamForKey(String key) throws IOException {
        DiskLruCache.Snapshot snapshot = cache.get(key);
        if (snapshot == null) {
            throw new KeyNotFoundException("key : " + key);
        }
        return new AutoCloseInputStream(new BufferedInputStream(snapshot.getInputStream(1)), snapshot);
    }

    @Override
    public long getCreationTimeForKey(String key) throws IOException {
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = cache.get(key);
            if (snapshot == null) {
                throw new KeyNotFoundException("key : " + key);
            }
            return Long.parseLong(snapshot.getString(0));
        } finally {
            closeQuietly(snapshot);
        }
    }

    @Override
    public void close() throws IOException {
        if (cache != null) {
            cache.close();
        }
    }

    private final class AutoCommitOutputStream extends FilterOutputStream {

        final DiskLruCache.Editor editor;
        boolean closed = false;

        private AutoCommitOutputStream(OutputStream out, DiskLruCache.Editor editor) {
            super(out);
            this.editor = editor;
        }

        @Override
        public void close() throws IOException {
            synchronized (AutoCommitOutputStream.class) {

                /*
                 * If this stream is closed more than once, it should behave like doing-nothing,
                 * not throwing an exception.
                 */
                if (closed) {
                    return;
                }
                try {
                    super.close();
                } finally {
                    try {
                        editor.set(0, String.valueOf(System.currentTimeMillis()));
                        editor.commit();
                        cache.flush();
                    } catch (IOException e) {
                        // ignore
                    }
                    closed = true;
                }
            }
        }
    }

    private final class AutoCloseInputStream extends FilterInputStream {

        final DiskLruCache.Snapshot snapshot;
        boolean closed = false;

        private AutoCloseInputStream(InputStream in, DiskLruCache.Snapshot snapshot) {
            super(in);
            this.snapshot = snapshot;
        }

        @Override
        public void close() throws IOException {
            if (closed) {
                return;
            }
            try {
                super.close();
            } finally {
                closeQuietly(snapshot);
                closed = true;
            }
        }
    }

    /**
     * Closes 'closeable', ignoring any checked exceptions. Does nothing if 'closeable' is null.
     */
    static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }
}
