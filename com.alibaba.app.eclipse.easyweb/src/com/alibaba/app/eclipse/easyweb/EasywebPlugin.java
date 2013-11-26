package com.alibaba.app.eclipse.easyweb;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class EasywebPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String   PLUGIN_ID      = "com.alibaba.app.eclipse.easyweb"; //$NON-NLS-1$

    public static final String   IMG_HORIZONTAL = "horizontal";                     //$NON-NLS-1$
    public static final String   IMG_VERTICAL   = "vertical";                       //$NON-NLS-1$

    // The shared instance
    private static EasywebPlugin plugin;

    /**
     * The constructor
     */
    public EasywebPlugin(){

    }

    protected void initializeImageRegistry(ImageRegistry registry) {
        registerImage(registry, IMG_HORIZONTAL, "th_horizontal.gif"); //$NON-NLS-1$
        registerImage(registry, IMG_VERTICAL, "th_vertical.gif"); //$NON-NLS-1$
    }

    private void registerImage(ImageRegistry registry, String key, String fileName) {
        try {
            IPath path = new Path("icons/" + fileName); //$NON-NLS-1$
            URL url = find(path);
            if (url != null) {
                ImageDescriptor desc = ImageDescriptor.createFromURL(url);
                registry.put(key, desc);
            }
        } catch (Exception e) {
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static EasywebPlugin getDefault() {
        // plugin.getPreferenceStore().setDefault(WorkbenchPreferencePage.VMKEY,
        // "-Xms512m -Xmx768m |-XX:PermSize=128m -XX:MaxPermSize=256m |-Dfile.encoding=UTF8 |-Dorg.eclipse.jetty.util.URI.charset=GBK");

        return plugin;
    }

    static public void log(String msg) {
        ILog log = EasywebPlugin.getDefault().getLog();
        Status status = new Status(IStatus.ERROR, EasywebPlugin.getDefault().getDescriptor().getUniqueIdentifier(),
                                   IStatus.ERROR, msg + "\n", null);
        log.log(status);
    }

    static public void log(Exception ex) {
        ILog log = EasywebPlugin.getDefault().getLog();
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        String msg = stringWriter.getBuffer().toString();

        Status status = new Status(IStatus.ERROR, EasywebPlugin.getDefault().getDescriptor().getUniqueIdentifier(),
                                   IStatus.ERROR, msg, null);
        log.log(status);
    }

    /**
     * 刷新工作空间
     */
    public static void refeshWorkspace(IJavaProject prj) {
        try {
            prj.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);

            IProject[] projects = prj.getProject().getReferencedProjects();
            for (int i = 0; i < projects.length; i++) {
                projects[i].refreshLocal(IResource.DEPTH_INFINITE, null);
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    public static IOConsole getIoConsole(String consoleName) {
        IOConsole mc = null;
        IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
        IConsole[] consoles = consoleManager.getConsoles();
        if (consoles != null) {
            for (IConsole console : consoles) {
                if (consoleName.equalsIgnoreCase(console.getName())) {
                    mc = (IOConsole) console;
                    break;
                }
            }
        }
        if (mc == null) {
            mc = new IOConsole(
                               consoleName,
                               JavaPlugin.getDefault().getWorkbench().getSharedImages().getImageDescriptor("IMG_OBJS_TASK_TSK"));
            ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { mc });
        }
        ConsolePlugin.getDefault().getConsoleManager().showConsoleView(mc);
        return mc;
    }

    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public static Image getCheckImg(boolean checked) {
        String gif = "checked.gif";
        if (!checked) {
            gif = "un" + gif;
        }
        return getImageDescriptor("/icons/" + gif).createImage();
    }

}
