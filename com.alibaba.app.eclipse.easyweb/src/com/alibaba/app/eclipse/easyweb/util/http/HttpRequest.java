/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.app.eclipse.easyweb.util.http;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

import com.alibaba.app.eclipse.easyweb.util.StringUtils;

/**
 * http请求服务
 * 
 * @author joe 2013年9月17日 下午9:32:01
 */
public class HttpRequest {

    public HttpRequest(String url, Method method, HttpRequestSetting setting, Function function){
        if (!url.startsWith("http://")) {
            throw new HttpException(" the url " + url + " is not startwith http:// ");
        }

        String encodedContent = encodingParams(setting.getParams(), setting.getCharset());
        url += (null == encodedContent) ? "" : ("?" + encodedContent);

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod(method.name());
            conn.setConnectTimeout(setting.getConnectTimeout());
            conn.setReadTimeout(setting.getReadTimeout());
            setHeaders(conn, setting);

            if (method.equals(Method.POST)) {
                conn.setDoOutput(true);
                conn.setDoInput(true);
            }

            conn.connect();
            int respCode = conn.getResponseCode();
            String resp = null;

            if (HttpURLConnection.HTTP_OK == respCode) {

                resp = StringUtils.inputStream2String(conn.getInputStream(), setting.getCharset());
                function.onSuccess(resp);
            } else {
                resp = StringUtils.inputStream2String(conn.getErrorStream(), setting.getCharset());
                function.onError(respCode, resp);
            }
        } catch (Exception e) {
            if (function != null) {
                function.onError(HttpURLConnection.HTTP_BAD_REQUEST, e.getMessage());
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

    }

    public static void get(String url, HttpRequestSetting setting, Function function) {
        new HttpRequest(url, Method.GET, setting, function);
    }

    public static void post(String url, HttpRequestSetting setting, Function function) {
        new HttpRequest(url, Method.POST, setting, function);
    }

    private void setHeaders(HttpURLConnection conn, HttpRequestSetting setting) {
        if (!setting.getHeads().isEmpty()) {

            for (Map.Entry<String, String> entry : setting.getHeads().entrySet()) {
                conn.addRequestProperty(entry.getKey(), (String) entry.getValue());
            }
        }
        conn.addRequestProperty("Client-Name", "sdk");
        conn.addRequestProperty("Content-Type", setting.getContentType() + ";charset=" + setting.getCharset());
    }

    private String encodingParams(Map<String, String> paramValues, String encoding) {
        StringBuilder sb = new StringBuilder();
        if (null == paramValues || paramValues.isEmpty()) {
            return null;
        }

        for (Map.Entry<String, String> entry : paramValues.entrySet()) {
            sb.append("&");
            sb.append(entry.getKey()).append("=");
            try {
                sb.append(URLEncoder.encode(entry.getValue(), encoding));
            } catch (UnsupportedEncodingException e) {
            }
        }

        return sb.toString().substring(1);
    }

    /**
     * 下载文件，连接超时时间 100ms，读取超时时间500ms
     * 
     * @param remoteUrl
     * @param target
     */
    @SuppressWarnings("resource")
    public static void downloadNet(String remoteUrl, String target) {

        URL url;
        try {
            url = new URL(remoteUrl);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(100);
            conn.setReadTimeout(500);
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream(target);
            byte[] buffer = new byte[1204];
            int byteread = 0;
            while ((byteread = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
            }
        } catch (Exception e) {
        }
    }

    public static interface Function {

        public void onSuccess(String response);

        public void onError(int code, String msg);

    }

    public static enum Method {
        POST, GET
    }

    public static void main(String[] args) {

        HttpRequest.get("http://localhost:8080/code.html", new HttpRequestSetting(), new Function() {

            public void onSuccess(String response) {
                System.out.println(response);
            }

            public void onError(int code, String msg) {

            }
        });

    }

}
