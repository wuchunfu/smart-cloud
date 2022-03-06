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
package io.github.smart.cloud.starter.mybatis.plus.test.cases;

import io.github.smart.cloud.starter.mybatis.plus.constants.RedisKey;
import io.github.smart.cloud.starter.mybatis.plus.test.prepare.idworker.IdworkerApp;
import io.github.smart.cloud.starter.mybatis.plus.util.IdWorker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Import(IdWorkerTest.IdWorkerValueAutoConfiguration.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = IdworkerApp.class, args = "--spring.profiles.active=idworker")
public class IdWorkerTest extends AbstractIntegrationTest {

    @Test
    void test() {
        IdWorker.getInstance().nextId();
    }

    public static class IdWorkerValueAutoConfiguration implements ApplicationContextAware {

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            RedissonClient redissonClient = applicationContext.getBean(RedissonClient.class);
            RAtomicLong workIdAtomicLong = redissonClient.getAtomicLong(RedisKey.IDWORKER_WORKERID);
            workIdAtomicLong.set(Long.MAX_VALUE);
            RAtomicLong dataCenterIdAtomicLong = redissonClient.getAtomicLong(RedisKey.IDWORKER_DATACENTERID);
            dataCenterIdAtomicLong.set(Long.MAX_VALUE);
        }
        
    }

}