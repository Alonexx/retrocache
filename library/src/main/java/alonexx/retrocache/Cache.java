package alonexx.retrocache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that represents the default cache policy of a service method.
 * This is typically used on methods if methods should have a default cache policy.
 * The default value is {@link CachePolicy#PREFER_CACHE} which means the clients
 * always read the cached data in the period of validity.
 *
 * <p>Clients can choose one of the pre-defined enums from {@code CachePolicy}.
 * It should meet the needs of most scenes. Methods without this annotation are
 * regarded as no cache needed, and the {@code Expiration} annotation is ignored as well.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
    CachePolicy value() default CachePolicy.PREFER_CACHE;
}
