/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.app.eclipse.easyweb.model;

import java.io.Serializable;
import java.util.List;

/**
 * Ӧ��JSON�ṹ����
 * 
 * @author joe 2013��11��13�� ����5:47:38
 */
public class ApplicationJsonObject implements Serializable {

    private static final long      serialVersionUID = 4114334416563864428L;

    private List<ModuleJsonObject> modules;

    private String                 name;

    public List<ModuleJsonObject> getModules() {
        return modules;
    }

    public void setModules(List<ModuleJsonObject> modules) {
        this.modules = modules;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static class ModuleJsonObject implements Serializable {

        private static final long serialVersionUID = -1381351447874996526L;
        private String            name;                                                                                                                  // ģ������
        private String            repoPath         = "http://repo.alibaba-inc.com/nexus/service/local/repositories/releases/content/com/alibaba/module/"; // ģ��Զ��·��
        private String            repoUrl;                                                                                                               // ģ��Դ���ַ
        private String            version;                                                                                                               // ģ��汾
        private String            sourcePath;                                                                                                            // ģ��Դ�뱾�ص�ַ

        private String            pack;                                                                                                                  // ģ������ʽ
                                                                                                                                                          // zip,jar,war

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRepoPath() {
            return repoPath;
        }

        public void setRepoPath(String repoPath) {
            this.repoPath = repoPath;
        }

        public String getRepoUrl() {
            return repoUrl;
        }

        public void setRepoUrl(String repoUrl) {
            this.repoUrl = repoUrl;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getSourcePath() {
            return sourcePath;
        }

        public void setSourcePath(String sourcePath) {
            this.sourcePath = sourcePath;
        }

        public String getPack() {
            return pack;
        }

        public void setPack(String pack) {
            this.pack = pack;
        }

    }

}
