import java.io.File;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jetty.deploy.DeploymentManager;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import com.alibaba.courier.runtime.Engine;
import com.alibaba.courier.runtime.JettyWebAppClassLoader;

public class JettyBootstrap {

    private String webroot;
    private String port    = "7001";
    private String appName = "";
    private String htdocs  = "";

    public void startServer() {
        Engine.start();

        Server server = new Server();

        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(10);
        threadPool.setMaxThreads(100);
        server.setThreadPool(threadPool);

        SelectChannelConnector connector0 = new SelectChannelConnector();
        connector0.setPort(Integer.parseInt(this.port));
        connector0.setMaxIdleTime(300000);
        connector0.setAcceptors(2);
        connector0.setStatsOn(false);
        connector0.setLowResourcesConnections(20000);
        connector0.setLowResourcesMaxIdleTime(5000);

        server.addConnector(connector0);

        HandlerCollection handlers = new HandlerCollection();
        ContextHandlerCollection contexts = new ContextHandlerCollection();

        EjResourceHandler resource_handler = new EjResourceHandler();
        resource_handler.setDirectoriesListed(true);

        resource_handler.setResourceBase(this.htdocs);

        List<Handler> handlerls = new ArrayList();
        if (!this.htdocs.equals("")) {
            handlerls.add(resource_handler);
        }
        handlerls.add(contexts);
        handlerls.add(new DefaultHandler());

        handlers.setHandlers((Handler[]) handlerls.toArray(new Handler[0]));
        server.setHandler(handlers);

        DeploymentManager dm = new DeploymentManager();
        dm.setContexts(contexts);
        try {
            server.setStopAtShutdown(true);
            server.setSendServerVersion(true);
            server.setSendDateHeader(true);

            WebAppContext webapp = new WebAppContext();
            webapp.setParentLoaderPriority(false);
            webapp.setResourceBase(this.webroot);
            webapp.setContextPath("/");

            webapp.setClassLoader(new JettyWebAppClassLoader(webapp));

            webapp.setAliases(true);

            contexts.addHandler(webapp);

            server.start();
            server.join();
        } catch (Exception e) {
            System.err.println(e);
            System.exit(100);
        }
    }

    public static void main(String[] args) {
        JettyBootstrap boot = new JettyBootstrap();
        if (args.length < 3) {
            System.err.println("the start params is err!");
            return;
        }
        boot.webroot = args[0];
        boot.port = args[1];
        boot.appName = args[2];
        if ((args.length > 3) && (args[3] != null) && (!args[3].equals(""))) {
            boot.htdocs = args[3];
        }
        try {
            ServerSocket socket = new ServerSocket(Integer.parseInt(boot.port));
            socket.close();
        } catch (Exception e) {
            System.err.println("the port " + boot.port + " is in use!");

            return;
        }
        System.out.println("Sending start Request to " + boot.appName);
        boot.startServer();
    }

    public String getClassPath() {
        String filepath = getUserEasywebPath() + File.separator + "." + this.appName + ".classpath";
        Properties p = new Properties();
        try {
            File file = new File(filepath);
            synchronized (file) {
                if (!file.exists()) {
                    file.createNewFile();
                }
                p.load(new FileInputStream(filepath));
                return p.getProperty("app");
            }

        } catch (Exception e) {
            System.err.println(e);
        }
        return "";
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
}

/*
 * Location: D:\Work\Tools\com.alibaba.app.eclipse.easyweb.bootstart-1.0-SNAPSHOT.jar Qualified Name: JettyBootstrap
 * JD-Core Version: 0.7.0.1
 */
