package com.alibaba.app.eclipse.easyweb.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;

import com.alibaba.app.eclipse.easyweb.EasywebPlugin;
import com.alibaba.app.eclipse.easyweb.model.App;

public class ApplicationProjectUtil {

    private List<App>        apps;

    private Map<String, App> appMaps;

    /**
     * 
     */
    public ApplicationProjectUtil(){
        apps = new ArrayList<App>();
        initialize();
    }

    public void initialize() {

        try {
            appMaps = AppUtils.init(apps);
        } catch (Exception e) {
            EasywebPlugin.log(e);
        }

    }

    public App get(String appName) {
        return appMaps.get(appName);
    }

    public IJavaProject[] getJavaProjectInApp(String appName) {
        return (IJavaProject[]) appMaps.get(appName).getJavaProjectInApp();
    }

    public App[] getApps() {
        return apps.toArray(new App[] {});
    }

}
