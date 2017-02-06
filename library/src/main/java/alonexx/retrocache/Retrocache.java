package alonexx.retrocache;

import java.lang.reflect.Proxy;

import static alonexx.retrocache.internal.Preconditions.checkNotNull;

public class Retrocache<T> {

    private final Class<T> service;
    private final T target;
    private KeyTransformer keyTransformer;
    private CacheInterface cache;
    private CachePolicy overrideCachePolicy;
    private CacheSerializer cacheSerializer;

    public static <T> Retrocache<T> cache(Class<T> service, T target) {
        return new Retrocache<>(service, target);
    }

    Retrocache(Class<T> service, T target) {
        this.service = service;
        this.target = target;
    }

    public Retrocache keyTransformer(KeyTransformer transformer) {
        this.keyTransformer = checkNotNull(transformer);
        return this;
    }

    public Retrocache cache(CacheInterface cache) {
        this.cache = checkNotNull(cache);
        return this;
    }

    public Retrocache overrideCachePolicy(CachePolicy cachePolicy) {
        this.overrideCachePolicy = cachePolicy;
        return this;
    }

    public Retrocache cacheParser(CacheSerializer parser) {
        this.cacheSerializer = checkNotNull(parser);
        return this;
    }

    public T create() {
        if (cache == null) {
            throw new IllegalStateException("Cache is not set.");
        }
        if (cacheSerializer == null) {
            cacheSerializer = new JavaBuiltInCacheSerializer();
        }
        if (overrideCachePolicy == null) {
            overrideCachePolicy = CachePolicy.UNSPECIFIED;
        }
        if (keyTransformer == null) {
            keyTransformer = DefaultKeyTransformer.instance();
        }
        return (T) Proxy.newProxyInstance(
                service.getClassLoader(),
                new Class[]{service},
                new CacheProxyHandler(
                        target, overrideCachePolicy, cache, keyTransformer, cacheSerializer));
    }
}