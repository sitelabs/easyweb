package com.alibaba.app.eclipse.easyweb.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;

import com.alibaba.app.eclipse.easyweb.EasywebPlugin;
import com.alibaba.app.eclipse.easyweb.model.App;
import com.alibaba.app.eclipse.easyweb.model.AppJsonObject;
import com.google.gson.Gson;

public class EclipseUtils {

    public static boolean isWindows() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().indexOf("window") != -1) {
            return true;
        }
        return false;
    }

    public static List<IJavaProject> getAllJavaProjects(boolean isOpen) {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject[] allProjects = root.getProjects();
        List<IJavaProject> tempList = new ArrayList();
        for (int i = 0; i < allProjects.length; i++) {
            try {
                boolean filter = allProjects[i].hasNature("org.eclipse.jdt.core.javanature");
                if (isOpen) {
                    filter = (filter) && (allProjects[i].isOpen());
                }
                if (filter) {
                    tempList.add((IJavaProject) allProjects[i].getNature("org.eclipse.jdt.core.javanature"));
                }
            } catch (CoreException localCoreException) {
            }
        }
        return tempList;
    }

    public static String getAppPropsPath() {
        return getUserEasywebPath() + File.separator + "app";
    }

    public static void saveAppProps(App app) {
        String filepath = getAppPropsPath();
        AppJsonObject obj = new AppJsonObject();
        obj.setAppPort(app.getAppPort());
        obj.setName(app.getName());
        obj.setJvm(app.getJvm());
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        Properties p = new Properties();
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                file.createNewFile();
            }
            p.load(new FileInputStream(filepath));
            p.setProperty(obj.getName(), json);
            OutputStream out = new FileOutputStream(filepath);
            p.store(out, ".app");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String saveAppStartup(String appName, String classPath) {
        String filepath = getUserEasywebPath() + File.separator + "." + appName + ".classpath";
        try {
            File file = new File(filepath);
            synchronized (file) {
                if (!file.exists()) {
                    file.createNewFile();
                }
                OutputStream out = new FileOutputStream(filepath);
                try {
                    PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(out));
                    printWriter.println(classPath);
                    printWriter.flush();
                    out.flush();
                } finally {
                    try {
                        out.close();
                    } catch (Exception localException1) {
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filepath;
    }

    public static Map<String, AppJsonObject> getAppProps() {
        String filepath = getAppPropsPath();
        Map<String, AppJsonObject> map = new HashMap();
        Gson gson = new Gson();
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                file.createNewFile();
            }
            Properties p = new Properties();
            p.load(new FileInputStream(filepath));
            Iterator<Map.Entry<Object, Object>> it = p.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Object, Object> entry = (Map.Entry) it.next();
                String json = (String) entry.getValue();
                if (json != null) {
                    AppJsonObject obj = (AppJsonObject) gson.fromJson(json, AppJsonObject.class);
                    map.put(obj.getName(), obj);
                }
            }
        } catch (FileNotFoundException e) {
            EasywebPlugin.log(e);
        } catch (IOException e) {
            EasywebPlugin.log(e);
        }
        return map;
    }

    public static String getUserEasywebPath() {
        String homedir = System.getProperty("user.home");
        String dir = homedir + File.separator + ".easyweb";
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        return dir;
    }

    public static String getDefaultAppName(IJavaProject prj) {
        String name = prj.getElementName();
        return name;
    }

    public static void main(String[] args) {
        App app = new App();
        app.setApjPort(80);
        app.setName("vaspool.all");
        saveAppProps(app);

        app = new App();
        app.setApjPort(8443);
        app.setName("kylin");
        saveAppProps(app);
    }
}
