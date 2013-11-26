/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.app.eclipse.easyweb.util;

import java.io.File;
import java.io.IOException;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.DefaultModelReader;
import org.apache.maven.model.io.ModelReader;

/**
 * 类MavenUtil.java的实现描述：TODO 类实现描述
 * 
 * @author joe 2013年11月12日 下午10:43:38
 */
public class MavenUtil {

    public static void main(String[] args) throws Exception, IOException {
        ModelReader reader = new DefaultModelReader();

        Model model = reader.read(new File("/Users/joe/work/4dev/modules-1688-offerdetail/module.member/pom.xml"), null);
        System.out.println(model.getParent().getVersion());
    }

}
