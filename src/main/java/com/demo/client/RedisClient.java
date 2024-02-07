package com.demo.client;

import java.util.List;

public interface RedisClient {
    String get(String key);
    List<String> mGet(String keys);

    void delete(String key);

    String set(String key, String value);

    String setEx(String key, int seconds, String value);

    long addToSets(String key, String member);

    void setList(String key, List<String> values);

    List<String> getList(String key);
}
