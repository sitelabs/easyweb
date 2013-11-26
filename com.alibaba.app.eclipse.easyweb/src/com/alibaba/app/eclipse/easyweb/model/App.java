package com.alibaba.app.eclipse.easyweb.model;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Model;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.alibaba.app.eclipse.easyweb.EasywebPlugin;

/**
 * 类App.java的实现描述：包含java工程信息的app定义
 * 
 * @author joe 2013-1-4 下午11:17:48
 */
public class App extends SimpleApp {

    private List<IJavaProject> prjs = new ArrayList<IJavaProject>();

    private IJavaProject       warPrj;

    private IJavaProject       deployPrj;

    private Model              warPomModel;                         // pom的model对象

    /**
         * 
         */
    public App(){

    }

    public void addPrj(IJavaProject prj) {
        if (!prjs.contains(prj)) {
            prjs.add(prj);
        }
    }

    public List<IJavaProject> getPrjs() {
        return prjs;
    }

    /**
     * 获取工程名称
     */
    public List<String> getPrjNames() {
        List<String> ls = new ArrayList<String>();

        for (IJavaProject prj : prjs) {
            ls.add(prj.getElementName());
        }
        return ls;
    }

    public void setPrjs(List<IJavaProject> prjs) {
        this.prjs = prjs;
    }

    public IJavaProject getWarPrj() {
        return warPrj;
    }

    public void setWarPrj(IJavaProject warPrj) {
        this.warPrj = warPrj;
    }

    public IJavaProject getDeployPrj() {
        return deployPrj;
    }

    public void setDeployPrj(IJavaProject deployPrj) {
        this.deployPrj = deployPrj;
    }

    public IJavaProject[] getJavaProjectInApp() {
        IJavaProject[] _prjs = new IJavaProject[prjs.size()];
        return (IJavaProject[]) prjs.toArray(_prjs);
    }

    public Model getWarPomModel() {
        return warPomModel;
    }

    public void setWarPomModel(Model warPomModel) {
        this.warPomModel = warPomModel;
    }

    /**
     * 得到该App依赖的jar或者工程的绝对路径
     * 
     * @param app
     * @return
     */
    public List<String> getJars() {
        List<String> result = new ArrayList<String>();
        IJavaProject warPrj = this.getWarPrj();
        try {
            IClasspathEntry[] cls = warPrj.getRawClasspath();
            for (IClasspathEntry ic : cls) {
                String _path = ic.getPath().toString();
                if (_path.indexOf(M2_REPO) != -1) {
                    result.add(getRelJarPath(_path));
                }
            }
        } catch (JavaModelException e) {
            EasywebPlugin.log(e);
        }
        return result;
    }

    public String getRelJarPath(String path) {
        String relpath = JavaCore.getClasspathVariable(M2_REPO).toString();
        if (path.indexOf(M2_REPO) != -1) {
            path = path.replaceAll(M2_REPO, relpath);

        }
        return path;
    }

    public SimpleApp getSimple() {

        SimpleApp app = new SimpleApp();
        app.setApjPort(apjPort);
        app.setAppPort(appPort);
        app.setCarMaps(carMaps);
        app.setName(name);
        app.setPath(path);
        app.setPort(port);
        app.setJettyStarted(jettyStarted);
        app.setWebinf(webinf);
        app.setPrjNames(getPrjNames());
        app.setJvm(jvm);
        try {
            ServerSocket socket = new ServerSocket(appPort);
            socket.close();
        } catch (Exception e) {
            if (e.getMessage().indexOf("Address already in use") != -1) {
                app.setJettyStarted(true);
            }
        }

        return app;
    }

    private static final String M2_REPO = "M2_REPO";

}
