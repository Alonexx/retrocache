package alonexx.retrocache;

/**
 * A holder that contains the result object and where it is from.
 *
 * <p>If the object is from cache, there is no need to store the object again.
 * If the object is from network, it should be considered that the object
 * should be stored to cache.
 */
final class Record {

    static final int ORIGIN_NETWORK = 1 << 0;

    static final int ORIGIN_CACHE = 1 << 1;

    final Object object;

    final int origin;

    Record(Object obj, int origin) {
        if (origin != ORIGIN_NETWORK && origin != ORIGIN_CACHE) {
            throw new IllegalArgumentException("Origin must be either ORIGIN_NETWORK" +
                    " or ORIGIN_CACHE");
        }
        this.object = obj;
        this.origin = origin;
    }
}