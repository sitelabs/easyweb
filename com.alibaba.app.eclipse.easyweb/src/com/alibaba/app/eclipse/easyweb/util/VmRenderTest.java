/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.app.eclipse.easyweb.util;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * 类VmRenderTest.java的实现描述：TODO 类实现描述
 * 
 * @author yingjun.jiaoyj@alibaba-inc.com 2011-8-21 下午04:04:47
 */
public class VmRenderTest {

    /**
     * @param args
     */
    public static void main(String[] args) {

        Map datas = new HashMap();

        String[] offerIds = new String[] { "123", "456" };
        Map[] offer_getOffer = new HashMap[2];
        offer_getOffer[0] = new HashMap();
        offer_getOffer[0].put("name", "offer1");
        offer_getOffer[0].put("price", "10");

        offer_getOffer[1] = new HashMap();
        offer_getOffer[1].put("name", "offer2");
        offer_getOffer[1].put("price", "20");

        datas.put("offerIds", offerIds);
        datas.put("offer_getOffer", offer_getOffer);

        String json = "{\"offer_getOffer\":[{\"price\":\"10\",\"name\":\"offer1\"},{\"price\":\"20\",\"name\":\"offer2\"}],\"offerIds\":[\"123\",\"456\"]}";

        Map map = null;// (Map) JSON.parse(json);

        // System.out.println(JSON.toString(datas));

        VelocityEngine engine = new VelocityEngine();
        VelocityContext context = new VelocityContext();
        context.put("data", map);

        String vm = "$data $data.offerIds.get(0) #foreach($id in $data.offerIds) $id #end $data.offer_getOffer";
        StringWriter out = new StringWriter();
        try {
            engine.evaluate(context, out, "Error rendering Velocity template: ", vm);
            System.out.println(out);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
