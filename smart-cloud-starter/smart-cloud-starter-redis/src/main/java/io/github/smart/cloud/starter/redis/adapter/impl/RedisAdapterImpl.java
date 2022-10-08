/*
 * Copyright © 2019 collin (1634753825@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.smart.cloud.starter.redis.adapter.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.smart.cloud.starter.redis.adapter.IRedisAdapter;
import io.github.smart.cloud.utility.JacksonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redis常用api封装
 *
 * @author collin
 * @date 2018-10-17
 */
@Slf4j
@RequiredArgsConstructor
public class RedisAdapterImpl implements IRedisAdapter {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 设置k-v对
     *
     * @param key
     * @param value
     * @param expireMillis 有效期（毫秒），为null表示不设置有效期
     */
    @Override
    public void setString(String key, String value, Long expireMillis) {
        if (expireMillis == null) {
            stringRedisTemplate.opsForValue().set(key, value);
        } else {
            stringRedisTemplate.opsForValue().set(key, value, expireMillis, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 根据key获取value
     *
     * @param key
     * @return
     */
    @Override
    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 设置key有效期
     *
     * @param key
     * @param expireMillis 有效期（毫秒）
     */
    @Override
    public void expire(String key, Long expireMillis) {
        stringRedisTemplate.expire(key, expireMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * 删除k-v对
     *
     * @param key
     * @return {@code true}表示成功；{@code false}表示失败。删除一个不存在的key，将返回{@code false}！！！
     */
    @Override
    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    /**
     * 批量删除k-v对
     *
     * @param keys
     * @return {@code true}表示成功；{@code false}表示失败。删除一个不存在的key，将返回{@code false}！！！
     */
    @Override
    public Boolean delete(Collection<String> keys) {
        Long count = stringRedisTemplate.delete(keys);
        return count != null && count == keys.size();
    }

    /**
     * 异步删除k-v对
     *
     * @param key
     * @return {@code true}表示成功；{@code false}表示失败。删除一个不存在的key，将返回{@code false}！！！
     */
    @Override
    public Boolean unlink(String key) {
        return stringRedisTemplate.unlink(key);
    }

    /**
     * 异步批量删除k-v对
     *
     * @param keys
     * @return {@code true}表示成功；{@code false}表示失败。删除一个不存在的key，将返回{@code false}！！！
     */
    @Override
    public Boolean unlink(Collection<String> keys) {
        Long count = stringRedisTemplate.unlink(keys);
        return count != null && count == keys.size();
    }

    /**
     * 设置k-v对
     *
     * @param key
     * @param value        对象
     * @param expireMillis 有效期（毫秒），为null表示不设置有效期
     */
    @Override
    public void setObject(String key, Object value, Long expireMillis) {
        setString(key, JacksonUtil.toJson(value), expireMillis);
    }

    /**
     * 根据key获取Object
     *
     * @param key
     * @param t
     * @param <T> 返回对象类型
     * @return
     */
    @Override
    public <T> T getObject(String key, TypeReference<T> t) {
        String value = getString(key);
        if (null == value) {
            return null;
        }

        return JacksonUtil.parseObject(value, t);
    }

    /**
     * 不存在则设置；存在则不设置
     *
     * @param key
     * @param value
     * @param expireMillis 有效期（毫秒）
     * @return {@code true}表示成功；{@code false}表示失败
     */
    @Override
    public boolean setNx(String key, String value, long expireMillis) {
        Boolean result = stringRedisTemplate.execute(connection -> connection.set(key.getBytes(StandardCharsets.UTF_8),
                value.getBytes(StandardCharsets.UTF_8),
                Expiration.milliseconds(expireMillis),
                RedisStringCommands.SetOption.SET_IF_ABSENT), true);
        return result != null && result;
    }

    /**
     * 设置hash结构缓存，并设置有效期
     *
     * @param hkey
     * @param data
     * @param expireSeconds
     * @return
     */
    @Override
    public boolean setHash(String hkey, Map<String, Object> data, Long expireSeconds) {
        Assert.hasText(hkey, "The arg[hkey] can not be empty");
        Assert.notEmpty(data, "The arg[data] can not be empty");
        Assert.notNull(expireSeconds, "The arg[expireSeconds] can not be null");

        List<String> keys = new ArrayList<>();
        keys.add(hkey);

        List<Object> args = new ArrayList<>();
        args.add(String.valueOf(expireSeconds));

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            keys.add(entry.getKey());
            args.add(entry.getValue());
        }

        String scriptText = buildHashLuaScript(data);
        log.debug(scriptText);

        DefaultRedisScript<Long> mapRedisScript = new DefaultRedisScript<>();
        mapRedisScript.setScriptText(scriptText);
        mapRedisScript.setResultType(Long.TYPE);
        Long result = stringRedisTemplate.execute(mapRedisScript, keys, args.toArray());
        return result != null && result.compareTo(1L) == 0;
    }

    /**
     * 构建hash结构lua脚本
     *
     * @param data
     * @return
     */
    private final String buildHashLuaScript(Map<String, Object> data) {
        StringBuilder hashLua = new StringBuilder(64);
        hashLua.append("redis.call('hmset', KEYS[1], ");

        int count = 0;
        int size = data.size();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            int index = count + 2;
            hashLua.append("KEYS[").append(index).append("], ARGV[").append(index).append("]");
            if (count < size - 1) {
                hashLua.append(" ,");
            }
            count++;
        }

        hashLua.append("); return redis.call('expire', KEYS[1], ARGV[1]);");

        return hashLua.toString();
    }

}