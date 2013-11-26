/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.app.eclipse.easyweb.editors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.EditorPart;

import com.alibaba.app.eclipse.easyweb.actionsets.AutoConfigHandler;
import com.alibaba.app.eclipse.easyweb.actionsets.BuildClassPathHandler;
import com.alibaba.app.eclipse.easyweb.actionsets.ServerBootstrapHandler;
import com.alibaba.app.eclipse.easyweb.model.App;
import com.alibaba.app.eclipse.easyweb.model.SimpleApp;
import com.alibaba.app.eclipse.easyweb.util.ApplicationProjectUtil;
import com.alibaba.app.eclipse.easyweb.util.EclipseUtils;
import com.alibaba.app.eclipse.easyweb.util.FileUtil;
import com.google.gson.Gson;

/**
 * 类AppBowserEditor.java的实现描述：TODO 类实现描述
 * 
 * @author joe 2013-1-2 下午7:35:52
 */
public class AppBowserEditor extends EditorPart {

    public static final String                              ID                      = "com.alibaba.app.eclipse.easyweb.appBowserEditor";

    public static final Map<String, ServerBootstrapHandler> serverBootstrapHandlers = new HashMap<String, ServerBootstrapHandler>();

    Gson                                                    gson                    = new Gson();

    /**
     * 
     */
    public AppBowserEditor(){

    }

    @Override
    public void createPartControl(Composite parent) {

        File rdk = new File(System.getProperty("user.home"), "rdk");

        String platform = SWT.getPlatform();

        Browser _browser = null;

        if (platform.equals("win32")) {
            System.setProperty("org.eclipse.swt.browser.XULRunnerPath", "c:/xulrunner");
            _browser = new Browser(parent, SWT.MOZILLA);
        } else {
            _browser = new Browser(parent, SWT.NULL);
        }
        final Browser browser = _browser;
        browser.setLayout(new FillLayout());

        String url = FileUtil.getDocLocation() + "html/index.html?_=" + System.currentTimeMillis();

        String[] headers = { "Cache-Control: no-cache", "Pragma:no-cache", "Expires:0" };

        browser.setUrl("file://" + url, null, headers);

        final Shell shell = parent.getDisplay().getActiveShell();

        new CustomFunction(browser, "Java");

        final AppEditorInput input = (AppEditorInput) getEditorInput();

        browser.addTitleListener(new TitleListener() {

            @Override
            public void changed(TitleEvent event) {
                String text = event.title;
                handler(shell, browser, input.getModel(), text);
            }
        });

    }

    /**
     * @param browser
     * @param input
     * @param text
     */
    private void handler(Shell shell, Browser browser, ApplicationProjectUtil applicationProjectUtil, String text) {

        if (!text.startsWith("function:")) {
            return;
        }
        String function = text.replaceAll("function:", "");

        if (function.equals("domOnReady")) {
            String appsJson = getAppJson(applicationProjectUtil);
            browser.execute("initAppData(" + appsJson + ");");
            browser.execute("initApps(" + appsJson + ");");

        } else if (function.startsWith("srartApp")) {// 启动jetty服务
            String appName = function.replaceAll("srartApp\\|", "");
            App app = applicationProjectUtil.get(appName);
            ServerBootstrapHandler server = new ServerBootstrapHandler(app);
            serverBootstrapHandlers.put(appName, server);
            server.run();
        } else if (function.startsWith("stopApp")) {// 关闭jetty服务
            String appName = function.replaceAll("stopApp\\|", "");
            ServerBootstrapHandler server = serverBootstrapHandlers.get(appName);
            if (server != null) {
                server.stop();
            }
        } else if (function.startsWith("autoconf")) {// 执行autoconf
            String appName = function.replaceAll("autoconf\\|", "");
            App app = applicationProjectUtil.get(appName);
            AutoConfigHandler autoconfig = new AutoConfigHandler(app);
            autoconfig.run();
        } else if (function.startsWith("buildclasspath")) {// 编译依赖
            String appName = function.replaceAll("buildclasspath\\|", "");
            App app = applicationProjectUtil.get(appName);
            BuildClassPathHandler build = new BuildClassPathHandler(app);
            build.run();
        } else if (function.startsWith("jettyport")) {//
            String[] temps = function.substring(10).split("\\|");
            String appName = temps[0];
            String port = temps[1];
            App app = applicationProjectUtil.get(appName);
            app.setAppPort(Integer.parseInt(port));
            EclipseUtils.saveAppProps(app);
            String appsJson = getAppJson(applicationProjectUtil);
            browser.execute("initAppData(" + appsJson + ");");
        } else if (function.startsWith("jvm")) {//
            String[] temps = function.substring(4).split("\\|");
            String appName = temps[0];
            String jvm = temps[1];
            App app = applicationProjectUtil.get(appName);
            app.setJvm(jvm);
            EclipseUtils.saveAppProps(app);
            String appsJson = getAppJson(applicationProjectUtil);
            browser.execute("initAppData(" + appsJson + ");");
        } else if (function.equals("sync")) {// 同步配置
            String appsJson = getAppJson(applicationProjectUtil);
            browser.execute("initAppData(" + appsJson + ");");
        } else if (function.startsWith("selectModule")) {// 打开模块管理器
            String appName = function.replaceAll("selectModule\\|", "");
            ListSelectionDialog dlg = new ListSelectionDialog(shell, ResourcesPlugin.getWorkspace().getRoot(),
                                                              new BaseWorkbenchContentProvider(),
                                                              new WorkbenchLabelProvider(), "Select the Project:");
            dlg.setTitle("Project Selection");
            dlg.open();

            if (dlg.getResult() == null) {
                return;
            }

            for (Object obj : dlg.getResult()) {
                Project pro = (Project) obj;
                String projectName = pro.getName();
                try {
                    if (pro.hasNature(JavaCore.NATURE_ID)) {
                        // IJavaProject jpro = JavaCore.create(pro);
                        File file = pro.findMember("target").getFullPath().toFile();
                        File zip = new File(file, projectName + "-bin.zip");
                        File jar = new File(file, projectName + ".jar");

                        System.out.println(zip.exists() + "--" + jar.exists());
                    }
                } catch (Exception e) {
                }
            }

        }
    }

    /**
     * @return
     */
    private Properties getWpstaticProperties() {
        String filepath = getWpstaticPath();
        File file = new File(filepath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Properties p = new Properties();
        try {
            p.load(new FileInputStream(filepath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    /**
     * @return
     */
    private String getWpstaticPath() {
        String filepath = EclipseUtils.getUserEasywebPath() + File.separator + "wpstatic";
        return filepath;
    }

    /**
     * @param applicationProjectUtil
     * @return
     */
    private String getAppJson(ApplicationProjectUtil applicationProjectUtil) {
        App[] apps = applicationProjectUtil.getApps();
        List<SimpleApp> simpleApps = new ArrayList<SimpleApp>();
        for (App app : apps) {
            simpleApps.add(applicationProjectUtil.get(app.getName()).getSimple());
        }

        String appsJson = gson.toJson(simpleApps);
        return appsJson;
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
        setPartName("Easy web");
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void setFocus() {

    }

    public static void main(String[] args) {
        AppBowserEditor ab = new AppBowserEditor();
        Properties p = ab.getWpstaticProperties();
        System.out.println(ab.gson.toJson(p));
    }

}
