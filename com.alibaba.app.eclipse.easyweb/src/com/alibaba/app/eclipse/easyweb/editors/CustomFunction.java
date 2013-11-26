/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.app.eclipse.easyweb.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.google.gson.Gson;

/**
 * 类CustomFunction.java的实现描述：TODO 类实现描述
 * 
 * @author joe 2013年11月10日 下午10:40:57
 */
public class CustomFunction extends BrowserFunction {

    Gson gson = new Gson();

    public CustomFunction(Browser browser, String name){
        super(browser, name);
    }

    @Override
    public Object function(Object[] arguments) {

        if (arguments == null) {
            return null;
        }

        String actionName = (String) arguments[0];

        if (actionName.equals("selectProject")) {

            return doSelectProject(arguments, gson);
        }

        if (actionName.equals("renderProject")) {

            return doSelectProject(arguments, gson);
        }

        return super.function(arguments);
    }

    /**
     * @param arguments
     * @param gson
     * @return
     */
    private Object doSelectProject(Object[] arguments, Gson gson) {
        ListSelectionDialog dlg = new ListSelectionDialog(
                                                          getBrowser().getParent().getParent().getDisplay().getActiveShell(),
                                                          ResourcesPlugin.getWorkspace().getRoot(),
                                                          new BaseWorkbenchContentProvider(),
                                                          new WorkbenchLabelProvider(), "Select the Project:");
        dlg.setTitle("Project Selection");
        dlg.open();

        if (dlg.getResult() == null) {
            return null;
        }
        List<ProjectModule> modules = new ArrayList<ProjectModule>();
        for (Object obj : dlg.getResult()) {
            Project pro = (Project) obj;
            String projectName = pro.getName();
            try {
                if (pro.hasNature(JavaCore.NATURE_ID)) {
                    // IJavaProject jpro = JavaCore.create(pro);
                    ProjectModule module = new ProjectModule();
                    module.setName(projectName);
                    File file = pro.findMember("target").getLocation().toFile();
                    File zip = null;

                    for (File child : file.listFiles()) {
                        if (child.getName().startsWith(projectName)) {
                            if (child.getName().endsWith(".zip")) {
                                zip = child;
                            }
                        }
                    }
                    if (zip != null) {
                        module.setJar(false);
                        module.setZipPath(zip.getAbsolutePath());
                    }
                    modules.add(module);
                }
            } catch (Exception e) {
            }
        }
        if (modules.isEmpty()) {
            return null;
        }

        String appName = (String) arguments[1];
        String moduleName = null;
        if (arguments.length > 2) {
            moduleName = (String) arguments[2];
        }

        return gson.toJson(modules);
    }

    public String doRenderProject(Object[] arguments) {

        return null;
    }

    public class ProjectModule {

        String  name;
        boolean isJar = true;
        String  zipPath;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isJar() {
            return isJar;
        }

        public void setJar(boolean isJar) {
            this.isJar = isJar;
        }

        public String getZipPath() {
            return zipPath;
        }

        public void setZipPath(String zipPath) {
            this.zipPath = zipPath;
        }

    }

}
