/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.app.eclipse.easyweb.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * ��StringUtils.java��ʵ��������TODO ��ʵ������
 * 
 * @author Administrator 2011-1-5 ����01:05:39
 */
public class StringUtils {

    public static final String EMPTY_STRING = "";

    /**
     * ������ת��Ϊ�ַ��� ����new String[]{"1","2"}) ���� [1, 2]
     * 
     * @param args
     * @return
     */
    public static String join(String[] args) {
        if (args == null) {
            return EMPTY_STRING;
        }
        String str = Arrays.toString(args);
        return str.substring(1, str.length() - 1);
    }

    /**
     * ��ȡ�����ַ���
     * 
     * @param is
     * @param charset
     * @return
     */
    public static String inputStream2String(InputStream is, String charset) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(is, charset));
        } catch (UnsupportedEncodingException e) {
        }
        StringBuffer buffer = new StringBuffer();
        String line = "";
        try {
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException e) {
        }
        return buffer.toString();
    }

    public static void main(String[] args) {

        System.out.println(join(new String[] {}));

    }
}
