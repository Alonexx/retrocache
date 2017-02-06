package alonexx.retrocache;

/**
 * Defines an interface that transforms a cache context into a key.
 */
public interface KeyTransformer {

    String transform(CacheContext context);
}
