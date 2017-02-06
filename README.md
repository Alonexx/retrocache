# Retrocache

Provides a flexible cache library for Android applications integrated with RxJava.

## Introduction

Retrocache provides a flexible cache for network requests.

If you use [Retrofit](http://square.github.io/retrofit/) as your network library and use [RxJava](https://github.com/ReactiveX/RxJava) in your app, you can simply declare an interface like below.

```java
public interface GitHubService {
  @Cache(CachePolicy.PREFER_CACHE)
  @Expiration(value = 15, timeUnit = TimeUnit.MINUTES)
  @GET("users/{user}/repos")
  Observable<List<Repo>> listRepos(@Path("user") String user);
}
```

The `Retrocache` class generates an implementation that caches the source result for 15 minutes of the `GitHubService.listRepos()` method.

```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.github.com/")
    .build();

GitHubService service = retrofit.create(GitHubService.class);

CacheInterface cache = new DiskLruCacheJakeWhartonImpl(...);
GitHubService cachedService = Retrocache.with(GitHubService.class, service)
                                  .cache(cache)
                                  .create();
```



## Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/Alonexx/Retrocache/issues).

## License

```
Copyright (C) 2017 Alonexx

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

