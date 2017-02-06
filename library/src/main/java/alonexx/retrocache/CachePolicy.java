package alonexx.retrocache;

public enum CachePolicy {

    PREFER_CACHE {
        @Override
        public boolean canReadFromCache() {
            return true;
        }

        @Override
        public boolean canReadFromExpiredCache() {
            return true;
        }

        @Override
        public boolean canStoreData() {
            return true;
        }
    },

    PREFER_NETWORK {
        @Override
        public boolean canReadFromCache() {
            return false;
        }

        @Override
        public boolean canReadFromExpiredCache() {
            return true;
        }

        @Override
        public boolean canStoreData() {
            return true;
        }
    },

    IGNORE_CACHE {
        @Override
        public boolean canReadFromCache() {
            return false;
        }

        @Override
        public boolean canReadFromExpiredCache() {
            return false;
        }

        @Override
        public boolean canStoreData() {
            return false;
        }
    },

    STORE_ONLY {
        @Override
        public boolean canReadFromCache() {
            return false;
        }

        @Override
        public boolean canReadFromExpiredCache() {
            return false;
        }

        @Override
        public boolean canStoreData() {
            return true;
        }
    },

    UNSPECIFIED {
        @Override
        public boolean canReadFromCache() {
            return IGNORE_CACHE.canReadFromCache();
        }

        @Override
        public boolean canReadFromExpiredCache() {
            return IGNORE_CACHE.canReadFromExpiredCache();
        }

        @Override
        public boolean canStoreData() {
            return IGNORE_CACHE.canStoreData();
        }
    };

    public abstract boolean canReadFromCache();

    public abstract boolean canReadFromExpiredCache();

    public abstract boolean canStoreData();
}
