package com.home.small.sso.server.service.impl;

import com.home.small.sso.client.rpc.Result;
import com.home.small.sso.server.model.App;
import com.home.small.sso.server.service.AppService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GTsung
 * @date 2021/10/31
 */
@Service("appService")
public class AppServiceImpl implements AppService {

    /**
     * app应用可以从数据库中查询，这里写死
     */
    private static List<App> appList;

    static {
        appList = new ArrayList<>();
        appList.add(new App("服务端1", "server1", "123456"));
        appList.add(new App("客户端1", "demo1", "123456"));
    }

    @Override
    public boolean exists(String appId) {
        return appList.stream().anyMatch(app -> app.getAppId().equals(appId));
    }

    @Override
    public Result<Void> validate(String appId, String appSecret) {
        for (App app : appList) {
            if (app.getAppId().equals(appId)) {
                if (app.getAppSecret().equals(appSecret)) {
                    return Result.success();
                } else {
                    return Result.createError("appSecret错误");
                }
            }
        }
        return Result.createError("appId不存在");
    }
}
