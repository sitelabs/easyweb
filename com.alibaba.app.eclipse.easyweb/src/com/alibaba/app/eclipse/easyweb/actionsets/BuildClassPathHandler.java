/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.app.eclipse.easyweb.actionsets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.progress.UIJob;

import com.alibaba.app.eclipse.easyweb.EasywebPlugin;
import com.alibaba.app.eclipse.easyweb.model.App;
import com.alibaba.app.eclipse.easyweb.util.EclipseUtils;

/**
 * 类AutoConfigHandler.java的实现描述：TODO 类实现描述
 * 
 * @author yingjun.jiaoyj@alibaba-inc.com 2011-2-12 下午04:30:06
 */
public class BuildClassPathHandler extends Action {

    private App app;

    /**
     * 
     */
    public BuildClassPathHandler(App app){
        this.app = app;
        setText("Start BuildClasspath");
    }

    public void run() {

        UIJob uijob = new UIJob("build  app classpath") {

            public IStatus runInUIThread(IProgressMonitor monitor) {
                IOConsole mc = EasywebPlugin.getIoConsole("Easyweb BuildClassPath");
                mc.clearConsole();
                IOConsoleOutputStream consoleStream = mc.newOutputStream();

                try {
                    consoleStream.write("start build app project classpath...\n");
                } catch (IOException e) {
                }
                afterRun();
                try {
                    consoleStream.write("build app project classpath end!\n");
                } catch (IOException e) {
                }
                return Status.OK_STATUS;
            }
        };
        uijob.schedule();

    }

    public void afterRun() {

        for (IJavaProject prj : app.getPrjs()) {

            if (app.getDeployPrj() != null && app.getDeployPrj().equals(prj)) {
                continue;
            }

            if (app.getWarPrj() != null && app.getWarPrj().equals(prj)) {
                continue;
            }

            List<IClasspathEntry> _entries = new ArrayList<IClasspathEntry>();

            try {
                IClasspathEntry[] entries = prj.getRawClasspath();
                boolean isRef = false;// 是否关联项目
                for (IClasspathEntry iClasspathEntry : entries) {
                    String path = iClasspathEntry.getPath().toString();
                    IJavaProject rp = getRefProject(path);
                    if (rp == null) {
                        _entries.add(iClasspathEntry);
                        continue;
                    } else {
                        IClasspathEntry pce = JavaCore.newProjectEntry(rp.getPath(), false);
                        _entries.add(pce);
                        isRef = true;
                    }
                }
                if (!isRef) {
                    continue;
                }
                if (EclipseUtils.isWindows()) {
                    // 解决window平台替换classpath的bug
                    String classpath = prj.getResource().getLocation().toString() + ".classpath";
                    Runtime.getRuntime().exec("cmd /c attrib -H " + classpath);
                }
                prj.setRawClasspath((IClasspathEntry[]) _entries.toArray(new IClasspathEntry[_entries.size()]), true,
                                    null);
            } catch (Exception e) {
                EasywebPlugin.log(e);
            }
        }
    }

    /**
     * 如果依赖的jar，在工作空间中有工程
     * 
     * @param path
     * @return
     */
    public IJavaProject getRefProject(String path) {
        if (!path.endsWith(".jar")) return null;
        for (IJavaProject pro : app.getPrjs()) {
            if (path.indexOf(pro.getProject().getName()) != -1) {
                return pro;
            }
        }
        return null;
    }

}
