/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.app.eclipse.easyweb.editors;

import com.alibaba.app.eclipse.easyweb.util.ApplicationProjectUtil;

/**
 * 类AppEditorInput.java的实现描述：TODO 类实现描述
 * 
 * @author Administrator 2011-1-10 下午10:37:59
 */
public class AppEditorInput extends FormEditorInput {

    private ApplicationProjectUtil model;

    /**
     * 
     */
    public AppEditorInput(String name){
        super(name);
        model = new ApplicationProjectUtil();
    }

    public ApplicationProjectUtil getModel() {
        return this.model;
    }

}
