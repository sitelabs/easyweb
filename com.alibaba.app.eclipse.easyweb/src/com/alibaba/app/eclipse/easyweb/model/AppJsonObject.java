/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.app.eclipse.easyweb.model;

/**
 * 类AppObject.java的实现描述：TODO 类实现描述
 * 
 * @author yingjun.jiaoyj@alibaba-inc.com 2011-2-14 下午07:03:16
 */
public class AppJsonObject {

    private String name;       // 应用名称

    private int    appPort = 0; // 应用端口

    private String jvm;        // jvm参数

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAppPort() {
        return appPort;
    }

    public void setAppPort(int appPort) {
        this.appPort = appPort;
    }

    public String getJvm() {
        return jvm;
    }

    public void setJvm(String jvm) {
        this.jvm = jvm;
    }

}
