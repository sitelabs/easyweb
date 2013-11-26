/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.app.eclipse.easyweb.model;

import java.io.Serializable;

/**
 * @author joe 2013年11月11日 上午12:16:26
 */
public class ModuleJsonObject implements Serializable {

    private static final long serialVersionUID = 7607827493880216158L;

    private String            name;
    private String            version;
    private String            type;                                   // module, application
    private String            source;                                 // local,remote

}
