/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.app.eclipse.easyweb.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.DefaultModelReader;
import org.apache.maven.model.io.ModelParseException;
import org.apache.maven.model.io.ModelReader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

import com.alibaba.app.eclipse.easyweb.model.App;
import com.alibaba.app.eclipse.easyweb.model.AppJsonObject;

/**
 * 类MavenUtils.java的实现描述：TODO 类实现描述
 * 
 * @author yingjun.jiaoyj@alibaba-inc.com 2011-1-16 下午04:51:48
 */
public class AppUtils {

    private static Map<String, App>          pomApps     = new HashMap();

    private static Map<String, IJavaProject> pomPrjs     = new HashMap();

    public static final String               POM_EXTENTS = "pom.xml";

    public static final String               BUNDLEWAR   = "bundle.war";

    private static ModelReader               reader      = new DefaultModelReader();

    private static Map<String, Model>        models;

    private static Map<String, IJavaProject> namePrjs;

    public static Map<String, App> init(List<App> apps) throws Exception {
        models = new HashMap<String, Model>();
        pomPrjs = new HashMap<String, IJavaProject>();
        namePrjs = new HashMap();
        Map<String, AppJsonObject> appObjs = EclipseUtils.getAppProps();
        List<IJavaProject> prjs = EclipseUtils.getAllJavaProjects(false);
        List<IJavaProject> warPrjs = new ArrayList<IJavaProject>();
        List<String> webPaths = new ArrayList<String>();
        for (IJavaProject prj : prjs) {
            // 找到web-inf目录,有web-inf目录的为web主工程

            namePrjs.put(prj.getElementName(), prj);

            if (prj.getElementName().indexOf(BUNDLEWAR) != -1) {
                String webinfPath = FileUtil.findWebInfPath(prj.getResource().getLocation().toString());
                if (webinfPath != null && !"".equals(webinfPath)) {
                    warPrjs.add(prj);
                    webPaths.add(webinfPath);
                }
            }

            String pomPath = prj.getResource().getLocation().toString();
            pomPath = pomPath.replaceAll("\\\\", "/");
            pomPath += "/" + POM_EXTENTS;
            File pomFile = new File(pomPath);
            if (pomFile.exists()) {
                Model model = null;
                try {
                    model = reader.read(pomFile, null);
                } catch (Exception e) {
                }
                if (model == null) {
                    continue;
                }
                pomPrjs.put(model.getArtifactId(), prj);
                models.put(prj.getElementName(), model);
            }
        }
        for (int i = 0; i < warPrjs.size(); i++) {
            IJavaProject prj = warPrjs.get(i);
            String webinf = webPaths.get(i);
            Model model = models.get(prj.getElementName());
            if (model != null) {
                String curPath = getAppPath(model);// model.getPomFile().getParent();
                Model pmodel = null;
                try {
                    pmodel = reader.read(new File(curPath), null);
                    curPath = pmodel.getPomFile().getParent();
                    curPath = curPath.replaceAll("\\\\", "/");
                } catch (FileNotFoundException e) {
                    // continue;
                    pmodel = model;
                }

                App app = new App();
                app.setWarPomModel(model);
                app.setWebinf(webinf);
                app.setName(pmodel.getArtifactId());
                String appPath = pmodel.getPomFile().getParent();
                appPath = appPath.replaceAll("\\\\", "/");
                app.setPath(appPath);
                app.setWarPrj(prj);
                wrapRerJar(prj, app);
                List<String> modules = pmodel.getModules();
                for (String string : modules) {
                    String path = curPath + "/" + string + "/" + POM_EXTENTS;
                    Model cmodel = reader.read(new File(path), null);
                    IJavaProject _prj = pomPrjs.get(cmodel.getArtifactId());
                    if (_prj == null) {
                        continue;
                    }
                    app.addPrj(_prj);
                    if (_prj.getElementName().indexOf(".deploy") != -1) {
                        app.setDeployPrj(_prj);
                    }
                    // wrapRef(_prj, app);

                    if (_prj.getElementName().indexOf(BUNDLEWAR) != -1 || cmodel.getPackaging().equals("jar")) {
                        // System.out.println(pmodel.getVersion() + " " + cmodel.getParent().getVersion());
                        continue;
                    }
                    List<Plugin> plugins = null;
                    try {
                        plugins = cmodel.getBuild().getPlugins();
                    } catch (Exception e) {
                        continue;
                    }
                    // 读取car
                    for (Plugin plugin : plugins) {
                        if (plugin.getArtifactId().equals("maven-car-plugin")) {
                            Object container = plugin.getConfiguration();
                            if (container != null) {
                                if (container instanceof Xpp3Dom) {
                                    try {
                                        Xpp3Dom dom = (Xpp3Dom) container;
                                        Xpp3Dom webroot = dom.getChild("webrootDirectory");
                                        if (webroot != null) {
                                            String value = webroot.getValue().replaceAll("\\$\\{basedir\\}",
                                                                                         curPath + "/" + string);

                                            app.addCarPrj(cmodel.getName(), value);
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            }
                            break;
                        }
                    }
                }
                AppJsonObject obj = appObjs.get(app.getName());
                if (obj != null) {
                    app.format(obj);
                }
                if (!apps.contains(app)) {
                    apps.add(app);
                    pomApps.put(pmodel.getArtifactId(), app);
                }
            }
        }
        return pomApps;
    }

    public static void wrapRerJar(IJavaProject prj, App app) {
        IClasspathEntry[] cls;
        try {
            cls = prj.getRawClasspath();
        } catch (JavaModelException e) {
            return;
        }
        for (IClasspathEntry entry : cls) {
            String path = entry.getPath().toString();
            String[] refs = path.split("/");
            path = refs[refs.length - 1];
            if (path.endsWith(".jar")) {
                path = path.substring(0, path.lastIndexOf(".jar"));
            }
            IJavaProject _prj = namePrjs.get(path);
            if (_prj != null) {
                app.addPrj(_prj);
            }
        }
    }

    private static void wrapRef(IJavaProject prj, App app) {

        String path = prj.getResource().getLocation() + File.separator + POM_EXTENTS;
        Model model = null;
        try {
            model = reader.read(new File(path), null);

            String curPath = getAppPath(model);// model.getPomFile().getParent();
            Model pmodel = null;
            try {
                pmodel = reader.read(new File(curPath), null);

                List<Dependency> pdependencys = pmodel.getDependencies();
                for (Dependency dependency : pdependencys) {
                    String artifactId = dependency.getArtifactId();
                    if (!pmodel.getArtifactId().equals(artifactId)) {
                        IJavaProject _prj = pomPrjs.get(artifactId);
                        if (_prj != null) {
                            app.addPrj(_prj);
                            wrapRef(_prj, app);
                        }
                    }
                }

            } catch (FileNotFoundException e) {
            }

            List<Dependency> dependencys = model.getDependencies();
            for (Dependency dependency : dependencys) {
                String artifactId = dependency.getArtifactId();
                if (!model.getArtifactId().equals(artifactId)) {
                    IJavaProject _prj = pomPrjs.get(artifactId);
                    if (_prj != null) {
                        app.addPrj(_prj);
                        wrapRef(_prj, app);
                    }
                }
            }

        } catch (FileNotFoundException e) {
        } catch (ModelParseException e) {
        } catch (IOException e) {
        } catch (Exception e) {
        }
    }

    /**
     * 根据maven模型，查找出父级pom路径
     * 
     * @param model
     * @return
     * @throws Exception
     */
    public static String getAppPath(Model model) throws Exception {
        String pomPath = model.getPomFile().getAbsolutePath();
        String curLocation = pomPath.replaceAll(POM_EXTENTS, "");
        File file = new File(curLocation);
        Parent parent = model.getParent();
        String path = "";
        if (parent != null) {
            String relativePath = model.getParent().getRelativePath();
            int index = relativePath.indexOf("../");
            int begin = 3;
            while (index != -1) {
                file = file.getParentFile();
                if (file == null || !file.canRead()) {
                    path = null;
                    break;
                }
                path = file.getAbsolutePath();
                path += File.separator + relativePath.substring(index + 3);
                index = relativePath.indexOf("../", begin);
                begin += index;
            }
        }
        return path;
    }

    /**
     * 根据pom的绝对路径查找应用的顶级pom，根据webinfPath过滤掉不是web工程的应用
     * 
     * @param pomPath
     * @param webinfPath
     * @throws Exception
     */
    public static void getAppPath(String pomPath) throws Exception {
        String curLocation = pomPath.replaceAll(POM_EXTENTS, "");
        Model model = reader.read(new File(pomPath), null);
        File file = new File(curLocation);
        Parent parent = model.getParent();
        if (parent != null) {
            String relativePath = model.getParent().getRelativePath();
            String path = "";
            int index = relativePath.indexOf("../");
            int begin = 3;
            while (index != -1) {
                file = file.getParentFile();
                if (file == null || !file.canRead()) {
                    path = null;
                    break;
                }
                path = file.getAbsolutePath();
                path += File.separator + relativePath.substring(index + 3);
                index = relativePath.indexOf("../", begin);
                begin += index;
            }

            if (new File(path).canRead()) {
                getAppPath(path);
            } else {
                models.put(model.getArtifactId(), model);
            }
        }

    }

}
