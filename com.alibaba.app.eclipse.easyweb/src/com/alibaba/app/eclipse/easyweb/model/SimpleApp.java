/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.app.eclipse.easyweb.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * ��SimpleApp.java��ʵ��������web��Ⱥ�ĸ�Ҫ��Ϣ
 * 
 * @author joe 2013-1-2 ����11:22:30
 */
public class SimpleApp {

    protected String              name;                                                                                                                                   // Ӧ������

    protected String              path;                                                                                                                                   // Ӧ�ø�·��

    protected int                 appPort      = 8080;                                                                                                                    // Ӧ�ö˿�

    protected int                 port         = 80;                                                                                                                      // httpd�˿�

    protected int                 apjPort      = 8443;                                                                                                                    // mod_jk�˿�

    protected boolean             jettyStarted = false;                                                                                                                   // �Ƿ�����

    protected List<String>        prjNames;

    protected String              webinf;                                                                                                                                 // webinf·��

    protected Map<String, String> carMaps      = new HashMap<String, String>();

    protected String              siteDataPath = "N/A";

    protected String              tag          = "N/A";

    protected String              jvm          = "-Xms512m -Xmx768m -XX:PermSize=128m -XX:MaxPermSize=256m -Dfile.encoding=UTF8 -Dorg.eclipse.jetty.util.URI.charset=GBK"; // jvm����

    /**
     * 
     */
    public SimpleApp(){
    }

    public void format(AppJsonObject obj) {
        if (obj.getAppPort() > 0) this.appPort = obj.getAppPort();
        if (StringUtils.isNotBlank(obj.getJvm())) this.jvm = obj.getJvm();
    }

    public String getWebInfPath() {
        String webInfo = getWebinf();
        return webInfo;
    }

    public void addCarPrj(String prjName, String web) {
        carMaps.put(prjName, web);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getAppPort() {
        return appPort;
    }

    public void setAppPort(int appPort) {
        this.appPort = appPort;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getApjPort() {
        return apjPort;
    }

    public void setApjPort(int apjPort) {
        this.apjPort = apjPort;
    }

    public boolean getJettyStarted() {
        return jettyStarted;
    }

    public void setJettyStarted(boolean jettyStarted) {
        this.jettyStarted = jettyStarted;
    }

    public List<String> getPrjNames() {
        return prjNames;
    }

    public void setPrjNames(List<String> prjNames) {
        this.prjNames = prjNames;
    }

    public String getWebinf() {
        return webinf;
    }

    public void setWebinf(String webinf) {
        this.webinf = webinf;
    }

    public Map<String, String> getCarMaps() {
        return carMaps;
    }

    public void setCarMaps(Map<String, String> carMaps) {
        this.carMaps = carMaps;
    }

    public String getJvm() {
        return jvm;
    }

    public void setJvm(String jvm) {
        this.jvm = jvm;
    }

    public String getSiteDataPath() {
        return siteDataPath;
    }

    public void setSiteDataPath(String siteDataPath) {
        this.siteDataPath = siteDataPath;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

}
