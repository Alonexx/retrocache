package alonexx.retrocache;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

import static alonexx.retrocache.internal.Preconditions.checkNotNull;

/**
 * An proxy handler to decorate the source {@code Observable}. It reads the
 * annotations on the method to determine whether it should read cache or
 * store the result.
 * <code>
 *     SomeService service = ...;
 *     Observable<SomeResult> result = service.doSomething();
 *     result.observeOn(AndroidSchedulers.mainThread())
 *             .subscribe(o -> {
 *                 // do something else ...
 *             });
 * </code>
 *
 * @see CachePolicy
 * @see InvocationHandler
 */
final class CacheProxyHandler implements InvocationHandler {

    private final Object target;
    private final CacheInterface cache;
    private final CachePolicy cachePolicy;
    private final KeyTransformer keyTransformer;
    private final CacheSerializer cacheAdapter;

    private volatile ServiceMethodInfo serviceMethodInfo;
    private volatile String key;
    private volatile Throwable networkError;
    private volatile Object[] args;

    CacheProxyHandler(
            Object target,
            CachePolicy cachePolicy,
            CacheInterface cache,
            KeyTransformer keyTransformer,
            CacheSerializer cacheAdapter) {
        this.target = checkNotNull(target);
        this.cachePolicy = cachePolicy;
        this.cache = checkNotNull(cache);
        this.keyTransformer = checkNotNull(keyTransformer);
        this.cacheAdapter = checkNotNull(cacheAdapter);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logThreadInfo(method.getName());
        Object retVal = method.invoke(target, args);
        Class<?> retType = method.getReturnType();

        if (Observable.class.isAssignableFrom(retType)) {
            this.serviceMethodInfo = new ServiceMethodInfo(method, cachePolicy);
            this.key = toKey(serviceMethodInfo, args);
            this.args = args;
            return Observable.concat(
                    loadRecordFromDisk(),
                    ((Observable<?>) retVal).map(wrapObjectToRecord()))
                    .first()
                    .doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(Throwable e) {
                            networkError = e;
                        }
                    })
                    .onErrorResumeNext(loadExpiredRecord())
                    .map(resolveAndSaveObject());
        }
        return retVal;
    }

    private Observable<Record> loadRecordFromDisk() {
        return Observable.create(new Observable.OnSubscribe<Record>() {
            @Override
            public void call(Subscriber<? super Record> subscriber) {
                if (serviceMethodInfo.canReadFromCache()) {
                    Record storedRecord = CacheProxyHandler.this.restoreUnexpiredRecord();
                    if (storedRecord != null) {
                        subscriber.onNext(storedRecord);
                    }
                }
                subscriber.onCompleted();
            }
        });
    }

    private Func1<Object, Record> wrapObjectToRecord() {
        return new Func1<Object, Record>() {
            @Override
            public Record call(Object o) {
                return new Record(o, Record.ORIGIN_NETWORK);
            }
        };
    }

    private Observable<Record> loadExpiredRecord() {
        return Observable.create(new Observable.OnSubscribe<Record>() {
            @Override
            public void call(Subscriber<? super Record> subscriber) {
                if (!serviceMethodInfo.canReadFromExpiredCache()) {
                    subscriber.onError(networkError);
                } else {
                    Record record = CacheProxyHandler.this.restoreExpiredRecord();
                    if (record != null) {
                        subscriber.onNext(record);
                    } else {
                        subscriber.onError(networkError);
                    }
                }
                subscriber.onCompleted();
            }
        });
    }

    private Func1<Record, Object> resolveAndSaveObject() {
        return new Func1<Record, Object>() {
            @Override
            public Object call(Record record) {
                if (record.origin == Record.ORIGIN_NETWORK && serviceMethodInfo.canStoreData()) {
                    CacheProxyHandler.this.saveRecord(record);
                }
                return record.object;
            }
        };
    }

    private Record restoreUnexpiredRecord() {
        logThreadInfo("Restore Unexpired Record");
        try {
            long time = cache.getCreationTimeForKey(key);
            if (!isExpired(serviceMethodInfo, time)) {
                return restoreRecord();
            } else {
                return null;
            }
        } catch (Throwable e) {
            logExceptionInfo(e);
            return null;
        }
    }

    private Record restoreExpiredRecord() {
        logThreadInfo("Restore Expired Record");
        return restoreRecord();
    }

    private Record restoreRecord() {
        InputStream is = null;
        try {
            is = cache.getInputStreamForKey(key);
            Object obj = cacheAdapter.readFromCache(is, new CacheContext(serviceMethodInfo, args));
            return new Record(obj, Record.ORIGIN_CACHE);
        } catch (Throwable e) {
            logExceptionInfo(e);
        } finally {
            closeQuietly(is);
        }
        return null;
    }

    private void saveRecord(Record record) {
        logThreadInfo("Save Record");
        OutputStream os = null;
        try {
            os = cache.newOutputStreamForKey(key);
            cacheAdapter.writeToCache(os, new CacheContext(serviceMethodInfo, args), record.object);
        } catch (Throwable e) {
            logExceptionInfo(e);
        } finally {
            closeQuietly(os);
        }
    }

    private static boolean isExpired(ServiceMethodInfo serviceMethodInfo, long lastTime) {
        long currentTime = System.currentTimeMillis();
        long expireTimeMillis = serviceMethodInfo.getExpirationMillis();
        return (currentTime - lastTime >= expireTimeMillis) || (lastTime > currentTime);
    }

    private String toKey(ServiceMethodInfo serviceMethodInfo, Object[] args) {
        return keyTransformer.transform(new CacheContext(serviceMethodInfo, args));
    }

    private static void logThreadInfo(String info) {
    }

    private static void logExceptionInfo(Throwable e) {
    }

    /**
     * Closes 'closeable', ignoring any checked exceptions. Does nothing if 'closeable' is null.
     */
    private static void closeQuietly(Closeable closeable) {
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