package alonexx.retrocache;

import java.util.Arrays;

final class DefaultKeyTransformer implements KeyTransformer {

    private static final DefaultKeyTransformer INSTANCE = new DefaultKeyTransformer();

    static DefaultKeyTransformer instance() {
        return INSTANCE;
    }

    private DefaultKeyTransformer() {}

    @Override
    public String transform(CacheContext context) {
        return String.valueOf(context.getServiceMethodInfo().getMethod().hashCode())
                + Arrays.hashCode(context.getArgs());
    }
}
