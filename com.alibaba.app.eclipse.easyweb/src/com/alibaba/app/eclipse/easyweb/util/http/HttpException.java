/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.app.eclipse.easyweb.util.http;

/**
 * 类HttpException.java的实现描述：TODO 类实现描述
 * 
 * @author joe 2013年9月17日 下午9:40:53
 */
public class HttpException extends RuntimeException {

    public HttpException(String string){
        super(string);
    }

    /**
     * @param string
     * @param e
     */
    public HttpException(String string, Exception e){
        super(string, e);
    }

    /**
     * 
     */
    private static final long serialVersionUID = -5525175708642118619L;

}
