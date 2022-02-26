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
package io.github.smart.cloud.constants;

/**
 * 通用状态码
 *
 * @author collin
 * @date 2019-03-27
 */
public interface CommonReturnCodes {

    /**
     * 成功
     */
    String SUCCESS = "200";
    /**
     * 校验失败
     */
    String VALIDATE_FAIL = "101";
    /**
     * 数据不存在
     */
    String DATA_MISSING = "102";
    /**
     * 数据已存在
     */
    String DATA_EXISTED = "103";
    /**
     * 签名错误
     */
    String SIGN_ERROR = "400";
    /**
     * 无权限访问
     */
    String NO_ACCESS = "401";
    /**
     * 请求url错误
     */
    String REQUEST_URL_ERROR = "404";
    /**
     * 请求超时
     */
    String REQUEST_TIMEOUT = "408";
    /**
     * 重复提交
     */
    String REPEAT_SUBMIT = "409";
    /**
     * 参数不全
     */
    String PARAMETERS_MISSING = "412";
    /**
     * 请求方式不支持
     */
    String REQUEST_METHOD_NOT_SUPPORTED = "415";
    /**
     * 请求类型不支持
     */
    String UNSUPPORTED_MEDIA_TYPE = "416";
    /**
     * 获取锁失败
     */
    String GET_LOCK_FAIL = "417";
    /**
     * 上传文件大小超过限制
     */
    String UPLOAD_FILE_SIZE_EXCEEDED = "418";
    /**
     * 当前会话已失效，请重新登陆
     */
    String NOT_LOGGED_IN = "419";
    /**
     * 服务器异常
     */
    String SERVER_ERROR = "500";
    /**
     * 获取Request失败
     */
    String GET_REQUEST_FAIL = "501";
    /**
     * 获取Response失败
     */
    String GET_RESPONSE_FAIL = "502";
    /**
     * rpc请求失败
     */
    String RPC_REQUEST_FAIL = "503";
    /**
     * rpc返回结果异常
     */
    String RPC_RESULT_EXCEPTION = "504";

}