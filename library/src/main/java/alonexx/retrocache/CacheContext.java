package alonexx.retrocache;

import java.lang.reflect.Type;
import java.util.Arrays;

public class CacheContext {

    private final ServiceMethodInfo serviceMethodInfo;
    private final Object[] args;
    private static final Object[] EMPTY_ARRAY = new Object[0];

    CacheContext(ServiceMethodInfo serviceMethodInfo, Object[] args) {
        this.serviceMethodInfo = serviceMethodInfo;
        this.args = args == null ? EMPTY_ARRAY : Arrays.copyOf(args, args.length);
    }

    public long getExpirationMillis() {
        return serviceMethodInfo.getExpirationMillis();
    }

    public boolean canReadFromCache() {
        return serviceMethodInfo.canReadFromCache();
    }

    public boolean canReadFromExpiredCache() {
        return serviceMethodInfo.canReadFromExpiredCache();
    }

    public boolean canStoreData() {
        return serviceMethodInfo.canStoreData();
    }

    public Type getGenericReturnType() {
        return serviceMethodInfo.getGenericReturnType();
    }

    ServiceMethodInfo getServiceMethodInfo() {
        return this.serviceMethodInfo;
    }

    public Object[] getArgs() {
        return args;
    }
}
