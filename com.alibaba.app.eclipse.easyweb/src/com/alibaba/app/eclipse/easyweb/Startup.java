/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.app.eclipse.easyweb;

import org.eclipse.swt.SWT;
import org.eclipse.ui.IStartup;

/**
 * ��Startup.java��ʵ��������TODO ��ʵ������
 * 
 * @author joe 2013��11��10�� ����12:55:51
 */
public class Startup implements IStartup {

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IStartup#earlyStartup()
     */
    @Override
    public void earlyStartup() {
        String platform = SWT.getPlatform();
        // System.out.println("Easyweb in " + platform + " startup! ");

    }

}
