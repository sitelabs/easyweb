package com.alibaba.app.eclipse.easyweb.actionsets;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputer;
import org.eclipse.debug.core.sourcelookup.containers.DefaultSourceContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.launching.JavaSourceLookupDirector;
import org.eclipse.jdt.internal.launching.RuntimeClasspathEntry;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaProjectSourceContainer;
import org.eclipse.jdt.launching.sourcelookup.containers.PackageFragmentRootSourceContainer;
import org.eclipse.jface.action.Action;

import com.alibaba.app.eclipse.easyweb.EasywebPlugin;
import com.alibaba.app.eclipse.easyweb.model.App;
import com.alibaba.app.eclipse.easyweb.util.EclipseUtils;
import com.alibaba.app.eclipse.easyweb.util.FileUtil;

public class ServerBootstrapHandler extends Action {

    private App app;
    ILaunch     launch;

    public ServerBootstrapHandler(App app){
        this.app = app;
        setText("Start App");
    }

    public void run() {
        String jvmArguments = this.app.getJvm();

        File sdkHome = new File(System.getProperty("user.home"), "sdk.home");
        String sdkPath = null;
        if (sdkHome.exists()) {
            sdkPath = FileUtil.readFile(sdkHome).trim();
            jvmArguments = jvmArguments + " -Dsdk.home=" + sdkPath + "  ";
        }
        executeCar();

        String webInfo = this.app.getWebInfPath();

        List<String> prgArgs = new ArrayList();
        prgArgs.add(webInfo);
        prgArgs.add(String.valueOf(this.app.getAppPort()));
        prgArgs.add(this.app.getName());
        if (this.app.getDeployPrj() != null) {
            String path = this.app.getDeployPrj().getResource().getLocation().toString();
            path = path.replaceAll("\\\\", "/");
            path = path + "/htdocs";
            prgArgs.add(path);
        }
        try {
            ILaunchConfigurationWorkingCopy config = createConfig("Easyweb Jetty", "JettyBootstrap",
                                                                  getClasspath(sdkPath), getBootClassPath(),
                                                                  jvmArguments.toString(),
                                                                  StringUtils.join(prgArgs, " "));
            this.launch = config.launch("debug", null);
        } catch (Exception e) {
            EasywebPlugin.log(e);
        }
    }

    public void stop() {
        System.err.println("stop app!!!");
        if (this.launch != null) {
            try {
                this.launch.terminate();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public String[] getClasspath(String sdkPath) {
        List<String> classpath = new ArrayList();

        File sdk = new File(sdkPath, "engine");
        for (File jarFile : sdk.listFiles()) {
            if (jarFile.getName().endsWith(".jar")) {
                classpath.add(jarFile.getAbsolutePath());
            }
        }
        String toolsJarLocation = JavaRuntime.getDefaultVMInstall().getInstallLocation() + File.separator + "lib"
                                  + File.separator + "tools.jar";
        if (new File(toolsJarLocation).exists()) {
            classpath.add(toolsJarLocation);
        }
        return (String[]) classpath.toArray(new String[0]);
    }

    public ILaunchConfigurationWorkingCopy createConfig(String label, String classToLaunch, String[] classpath,
                                                        IRuntimeClasspathEntry[] bootClasspath, String vmArgs,
                                                        String prgArgs) throws CoreException {
        ILaunchConfigurationType launchType = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType("org.eclipse.jdt.launching.localJavaApplication");
        ILaunchConfigurationWorkingCopy config = launchType.newInstance(null, label);
        config.setAttribute("org.eclipse.debug.ui.private", true);
        config.setAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_ID,
                            "org.eclipse.jdt.launching.sourceLocator.JavaSourceLookupDirector");

        ISourceLookupDirector locator = (ISourceLookupDirector) getSourceLocator(config);
        config.setAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_MEMENTO, locator.getMemento());

        ArrayList classpathMementos = new ArrayList();
        for (int i = 0; i < classpath.length; i++) {
            IRuntimeClasspathEntry cpEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(classpath[i]));
            cpEntry.setClasspathProperty(3);
            classpathMementos.add(cpEntry.getMemento());
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bootClasspath.length; i++) {
            IRuntimeClasspathEntry cpEntry = bootClasspath[i];
            String path = cpEntry.getLocation().toString();
            sb.append(path);
            sb.append(";");
        }
        String classPath = EclipseUtils.saveAppStartup(this.app.getName(), sb.toString());

        vmArgs = vmArgs + " -Dapplication.home=" + classPath;

        config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
        config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classpathMementos);
        config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, prgArgs);
        config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArgs);
        config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, classToLaunch);

        return config;
    }

    private ISourceLocator getSourceLocator(ILaunchConfiguration configuration) throws CoreException {
        ISourceLookupDirector sourceLocator = new JavaSourceLookupDirector();
        ISourcePathComputer computer = DebugPlugin.getDefault().getLaunchManager().getSourcePathComputer("org.eclipse.jdt.launching.sourceLookup.javaSourcePathComputer");
        sourceLocator.setSourcePathComputer(computer);

        ArrayList sourceContainers = new ArrayList();

        IJavaProject[] javaProjects = this.app.getJavaProjectInApp();
        for (int i = 0; i < javaProjects.length; i++) {
            IJavaProject project = javaProjects[i];
            sourceContainers.add(new JavaProjectSourceContainer(project));
        }
        HashSet external = new HashSet();
        IJavaProject project = this.app.getWarPrj();
        IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
        for (int ri = 0; ri < roots.length; ri++) {
            IPackageFragmentRoot root = roots[ri];
            if (root.isExternal()) {
                IPath location = root.getPath();
                if (!external.contains(location)) {
                    external.add(location);
                }
            } else {
                sourceContainers.add(new PackageFragmentRootSourceContainer(root));
            }
        }
        sourceContainers.add(new DefaultSourceContainer());

        sourceLocator.setSourceContainers((ISourceContainer[]) sourceContainers.toArray(new ISourceContainer[sourceContainers.size()]));
        sourceLocator.initializeParticipants();

        return sourceLocator;
    }

    public IRuntimeClasspathEntry[] getBootClassPath() {
        IJavaProject warPrj = this.app.getWarPrj();

        List<IRuntimeClasspathEntry> clsData = new ArrayList();
        for (IJavaProject prj : this.app.getPrjs()) {
            try {
                String path = prj.getResource().getLocation().toString();
                path = path.replaceAll("\\\\", "/");
                IRuntimeClasspathEntry cpEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(prj.getOutputLocation());
                File file = new File(cpEntry.getLocation());
                if (file.exists()) {
                    add(clsData, cpEntry);
                }
            } catch (JavaModelException e) {
                EasywebPlugin.log(e);
            }
        }
        getClassPath(warPrj, clsData);

        return (IRuntimeClasspathEntry[]) clsData.toArray(new IRuntimeClasspathEntry[0]);
    }

    public void getRefClassPath(List<IRuntimeClasspathEntry> data) {
        for (IJavaProject prj : this.app.getPrjs()) {
            String prjName = prj.getElementName();
            if (prjName.indexOf("deploy") == -1) {
                getClassPath(prj, data);
            }
        }
    }

    public void getClassPath(IJavaProject prj, List<IRuntimeClasspathEntry> data) {
        try {
            IClasspathEntry[] cls = prj.getRawClasspath();
            for (IClasspathEntry entry : cls) {
                IPath path = entry.getPath();
                boolean hasCls = false;
                for (IJavaProject _prj : this.app.getPrjs()) {
                    String prjName = _prj.getElementName();
                    String ipath = path.toString();
                    if (ipath.indexOf(prjName) != -1) {
                        hasCls = true;
                        break;
                    }
                }
                if (!hasCls) {
                    if (entry.getEntryKind() != 2) {
                        if (entry.getEntryKind() == 1) {
                            IRuntimeClasspathEntry cpEntry = new RuntimeClasspathEntry(entry);
                            add(data, cpEntry);
                        } else if (entry.getEntryKind() == 3) {
                            IRuntimeClasspathEntry cpEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(prj.getOutputLocation());
                            add(data, cpEntry);
                        } else if (entry.getEntryKind() != 5) {
                            IRuntimeClasspathEntry cpEntry = new RuntimeClasspathEntry(entry);
                            add(data, cpEntry);
                        }
                    }
                }
            }
        } catch (JavaModelException e) {
            EasywebPlugin.log(e);
        }
    }

    private void add(List<IRuntimeClasspathEntry> data, IRuntimeClasspathEntry entry) {
        String path = entry.getLocation().replace('\\', '/');
        if ((!data.contains(path)) && (path.indexOf("JRE_CONTAINER") == -1) && (path.indexOf("JRE_LIB") == -1)
            && (path.indexOf("/test-classes") == -1) && (path.indexOf("java.servlet") == -1)) {
            data.add(entry);
        }
    }

    private void executeCar() {
        Model model = this.app.getWarPomModel();
        for (Dependency dep : model.getDependencies()) {
            if (dep.getType().equals("car")) {
                String name = dep.getArtifactId();
                String carName = name.substring(name.lastIndexOf(".") + 1);
                IJavaProject prj = null;
                for (IJavaProject _prj : this.app.getPrjs()) {
                    if (_prj.getElementName().indexOf(name) != -1) {
                        prj = _prj;
                    }
                }
                if (prj == null) {
                    try {
                        IClasspathEntry[] ics = this.app.getWarPrj().getRawClasspath();
                        String carPath = null;
                        for (IClasspathEntry ic : ics) {
                            if (ic.getPath().toString().indexOf(name) != -1) {
                                carPath = ic.getPath().toString();
                                break;
                            }
                        }
                        if (carPath == null) {
                            continue;
                        }
                        carPath = this.app.getRelJarPath(carPath);
                        unPack(new File(this.app.getWebinf()), carName, carPath);
                    } catch (JavaModelException e) {
                        EasywebPlugin.log(e);
                    }
                } else {
                    String prjPath = prj.getProject().getRawLocation().toString();
                    String webInfPath = FileUtil.findWebInfPath(prjPath);
                    FileUtil.copyDirectiory(webInfPath + File.separator + "WEB-INF", this.app.getWebinf()
                                                                                     + File.separator + "WEB-INF"
                                                                                     + File.separator + carName);

                    FileUtil.copyDirectiory(this.app.getWebinf() + File.separator + "WEB-INF" + File.separator
                                            + carName + File.separator + carName, this.app.getWebinf() + File.separator
                                                                                  + "WEB-INF" + File.separator
                                                                                  + carName);
                }
            }
        }
    }

    private void unPack(File explordedDirectory, String moduleName, String filePath) {
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(filePath));
            ZipEntry zipEntry = null;
            ZipEntry targetEntry = null;
            while ((zipEntry = zis.getNextEntry()) != null) {
                if ((!zipEntry.getName().startsWith("WEB-INF/lib")) && (!zipEntry.getName().startsWith("META-INF"))) {
                    if (zipEntry.getName().startsWith("WEB-INF")) {
                        if (zipEntry.getName().startsWith("WEB-INF/classes")) {
                            targetEntry = new ZipEntry(zipEntry);
                        } else if (zipEntry.getName().indexOf(moduleName) != -1) {
                            targetEntry = new ZipEntry(zipEntry.getName());
                        } else {
                            targetEntry = new ZipEntry(zipEntry.getName().replace("WEB-INF", "WEB-INF/" + moduleName));
                        }
                    } else {
                        targetEntry = new ZipEntry(moduleName + "/" + zipEntry.getName());
                    }
                    targetEntry.setTime(zipEntry.getTime());
                    extractFile(explordedDirectory, zis, targetEntry, true);
                }
            }
            zis.close();
        } catch (Exception e) {
            EasywebPlugin.log(e);
        }
    }

    protected void extractFile(File todir, InputStream zipStream, ZipEntry zipEntry, boolean overwrite)
                                                                                                       throws IOException {
        String entryName = zipEntry.getName();
        Date entryDate = new Date(zipEntry.getTime());
        boolean isDirectory = zipEntry.isDirectory();
        File targetFile = getFile(todir, entryName);
        if ((!overwrite) && (targetFile.exists()) && (targetFile.lastModified() >= entryDate.getTime())) {
            return;
        }
        if (isDirectory) {
            targetFile.mkdirs();
        } else {
            File dir = targetFile.getParentFile();
            dir.mkdirs();
            byte[] buffer = new byte[8192];
            int length = 0;
            OutputStream ostream = null;
            try {
                ostream = new BufferedOutputStream(new FileOutputStream(targetFile), 8192);
                while ((length = zipStream.read(buffer)) >= 0) {
                    ostream.write(buffer, 0, length);
                }
            } finally {
                if (ostream != null) {
                    try {
                        ostream.close();
                    } catch (IOException localIOException1) {
                    }
                }
            }
        }
        targetFile.setLastModified(entryDate.getTime());
    }

    public File getFile(File basedir, String path) {
        File file = new File(path);
        if (file.isAbsolute()) {
            return file;
        }
        return new File(basedir, path);
    }

    public static void main(String[] args) {
        String a = "-Xms512m -Xmx768m |-XX:PermSize=128m -XX:MaxPermSize=256m |-Dfile.encoding=UTF8 |-Dorg.eclipse.jetty.util.URI.charset=GBK|";
        System.out.println(a.replaceAll("\\|", ""));
    }
}

/*
 * Location: D:\Work\Tools\easyweb.jar Qualified Name: com.alibaba.app.eclipse.easyweb.actionsets.ServerBootstrapHandler
 * JD-Core Version: 0.7.0.1
 */
