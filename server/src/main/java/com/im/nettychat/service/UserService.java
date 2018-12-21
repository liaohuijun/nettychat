/*
 * Project: com.im.nettychat.service
 * 
 * File Created at 2018/12/20
 * 
 * Copyright 2018 CMCC Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ZYHY Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.im.nettychat.service;

import com.im.nettychat.cache.CacheName;
import com.im.nettychat.common.ConstantError;
import com.im.nettychat.common.GenerateID;
import com.im.nettychat.config.ErrorConfig;
import com.im.nettychat.executor.ThreadPoolService;
import com.im.nettychat.model.UID;
import com.im.nettychat.model.User;
import com.im.nettychat.protocol.request.LoginRequest;
import com.im.nettychat.protocol.request.RegisterRequest;
import com.im.nettychat.protocol.response.LoginResponse;
import com.im.nettychat.protocol.response.RegisterResponse;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.util.StringUtils;

import static com.im.nettychat.common.ConstantError.NEED_USERNAME_PASSWORD;
import static com.im.nettychat.service.RedisService.redisService;

/**
 * @author hejianglong
 * @Desc
 * @date 2018/12/20 下午8:21
 */
public class UserService {

    public final static UserService userService = new UserService();

    public void login(ChannelHandlerContext ctx, LoginRequest msg) {
        ThreadPoolService.execute(new Runnable() {
            @Override
            public void run() {
                LoginResponse response = new LoginResponse();
                UID uid = (UID)redisService.hGet(CacheName.USERNAME_ID, msg.getUsername());
                // 用户不存在
                if (uid == null) {
                    response.setError(true);
                    response.setErrorInfo(ErrorConfig.getError(ConstantError.USER_NOT_FOUND));
                    ctx.writeAndFlush(response);
                    return;
                }
                User user = (User)redisService.vGet(CacheName.USER_INFO, String.valueOf(uid.getUserId()));
                response.setUserId(user.getId());
                response.setName(user.getName());
                response.setDesc(user.getDesc());
                response.setIcon(user.getIcon());
                ctx.writeAndFlush(response);
            }
        });
    }

    public void register(ChannelHandlerContext ctx, RegisterRequest msg) {
        ThreadPoolService.execute(new Runnable() {
            @Override
            public void run() {
                RegisterResponse response = new RegisterResponse();
                if (StringUtils.isEmpty(msg.getUsername()) || StringUtils.isEmpty(msg.getPassword())) {
                    response.setError(true);
                    response.setErrorInfo(NEED_USERNAME_PASSWORD);
                    ctx.writeAndFlush(response);
                    return;
                }
                Object obj = redisService.hGet(CacheName.USERNAME_ID, msg.getUsername());
                if (obj != null) {
                    response.setError(true);
                    response.setErrorInfo(ErrorConfig.getError(ConstantError.USER_ALREADY_EXIST));
                    ctx.writeAndFlush(response);
                    ctx.fireChannelReadComplete();
                    return;
                }
                Long userId = GenerateID.generateUserID();
                User user = new User();
                user.setId(userId);
                user.setName(msg.getName());
                user.setUsername(msg.getUsername());
                user.setPassword(msg.getPassword());
                redisService.vSet(CacheName.USER_INFO, String.valueOf(userId), user);
                redisService.hSet(CacheName.USERNAME_ID, msg.getUsername(), String.valueOf(userId));
                response.setUserId(userId);
                response.setName(user.getName());
                ctx.writeAndFlush(response);
            }
        });
    }
}