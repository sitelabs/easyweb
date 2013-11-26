import java.io.File;

import org.eclipse.jetty.webapp.WebAppClassLoader;

/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
/**
 * 类EjClassLoader.java的实现描述：TODO 类实现描述
 * 
 * @author yingjun.jiaoyj@alibaba-inc.com 2011-4-6 上午09:48:38
 */
public class DefaultEjClassLoader extends WebAppClassLoader {

    public DefaultEjClassLoader(Context context, String projectClassPath) throws Exception{
        super(context);

        int start = 0;
        int length = projectClassPath.length();
        while (start < length) {
            int index = projectClassPath.indexOf(File.pathSeparatorChar, start);
            if (index == -1) index = length;
            if (index > start) {
                String entry = projectClassPath.substring(start, index);
                // System.err.println("ProjectClassLoader: entry=" + entry);
                super.addClassPath(entry);
            }
            start = index + 1;
        }

    }

}
