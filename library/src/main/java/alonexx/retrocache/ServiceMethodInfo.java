package alonexx.retrocache;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import static alonexx.retrocache.internal.Preconditions.checkNotNull;

final class ServiceMethodInfo {
    private final Method method;
    private final Type genericReturnType;
    private final long expirationMillis;
    private final CachePolicy cachePolicy;

    ServiceMethodInfo(Method method, CachePolicy cachePolicy) {
        this.method = checkNotNull(method);
        checkNotNull(cachePolicy);
        ParameterizedType genericReturnType;
        try {
            genericReturnType = (ParameterizedType) method.getGenericReturnType();
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Method " + method.toString()
                    + " doesn't return a generic type.", e);
        }
        Type[] types = genericReturnType.getActualTypeArguments();
        if (types.length != 1) {
            throw new IllegalArgumentException(
                    "Expected one type argument but got: " + Arrays.toString(types));
        }
        this.genericReturnType = types[0];
        if (method.isAnnotationPresent(Expiration.class)) {
            Expiration expiration = method.getAnnotation(Expiration.class);
            expirationMillis = expiration.timeUnit().toMillis(expiration.value());
        } else {
            expirationMillis = 0L;
        }

        if (cachePolicy == CachePolicy.UNSPECIFIED) {
            if (method.isAnnotationPresent(Cache.class)) {
                this.cachePolicy = method.getAnnotation(Cache.class).value();
            } else {
                this.cachePolicy = CachePolicy.IGNORE_CACHE;
            }
        } else {
            this.cachePolicy = cachePolicy;
        }
    }

    Method getMethod() {
        return method;
    }

    Type getGenericReturnType() {
        return genericReturnType;
    }

    long getExpirationMillis() {
        return expirationMillis;
    }

    boolean canReadFromCache() {
        return cachePolicy.canReadFromCache();
    }

    boolean canReadFromExpiredCache() {
        return cachePolicy.canReadFromExpiredCache();
    }

    boolean canStoreData() {
        return cachePolicy.canStoreData();
    }

}
