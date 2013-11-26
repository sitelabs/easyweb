/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.app.eclipse.easyweb.actionsets;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.progress.UIJob;

import com.alibaba.antx.config.ConfigRuntimeImpl;
import com.alibaba.app.eclipse.easyweb.EasywebPlugin;
import com.alibaba.app.eclipse.easyweb.model.App;
import com.alibaba.app.eclipse.easyweb.util.FileUtil;

/**
 * ��AutoConfigHandler.java��ʵ��������TODO ��ʵ������
 * 
 * @author yingjun.jiaoyj@alibaba-inc.com 2011-2-12 ����04:30:06
 */
public class AutoConfigHandler extends Action {

    private static final String CONSOLE_NAME = "Easyweb Autoconf";

    private App                 app;

    /**
     * 
     */
    public AutoConfigHandler(App app){
        this.app = app;
        setText("Start AutoConf");
    }

    public void run() {

        Job job = new Job("excute autoconfig") {

            @Override
            public IStatus run(IProgressMonitor monitor) {

                // cpCarFileStep1();
                IOConsole mc = EasywebPlugin.getIoConsole(CONSOLE_NAME);
                mc.clearConsole();
                IOConsoleOutputStream consoleStream = mc.newOutputStream();

                String welcome = "��ӭʹ��Easyweb���,���������:\n";
                String str = "http://b2b-doc.alibaba-inc.com/display/ccbu/Easyweb \n";
                try {
                    consoleStream.write(welcome);
                    consoleStream.write(str);
                    consoleStream.write("������� AliExpress ��Ŀ�ѿ���ʹ�ã�\n\n");

                    String antxFile = app.getPath() + "/antx.properties";// "d:/work/vaspool/antx.properties";//
                    // getAntXPropertiesFile(configuration);
                    File _antxFile = new File(antxFile);
                    if (!_antxFile.exists()) {
                        consoleStream.write("��ȷ���Ƿ����" + app.getPath() + "/antx.properties�ļ���ȷ���ļ����ٺ���������autoconf��\n");
                        return Status.OK_STATUS;
                    }

                    consoleStream.write("��ʼautoconfig �滻��\n");
                    consoleStream.write("��Ҫִ������Ŀ¼��\n\n");

                    long now = System.currentTimeMillis();

                    List<String> filePaths = getAutoConfigFiles();

                    String[] _filePaths = new String[filePaths.size()];
                    boolean hasWar = false;
                    for (int i = 0; i < _filePaths.length; i++) {
                        _filePaths[i] = filePaths.get(i);
                        if (_filePaths[i].indexOf(".war") != -1) {
                            hasWar = true;// �Ƿ���war
                        }
                        consoleStream.write(_filePaths[i] + "\n");
                    }
                    if (!hasWar) {
                        filePaths.add(app.getWebInfPath());
                        consoleStream.write(app.getWebInfPath() + "\n");
                    }

                    consoleStream.write("\n");

                    List<String> jars = app.getJars();
                    filePaths.addAll(jars);

                    System.setIn(mc.getInputStream());
                    System.setErr(new PrintStream(consoleStream));
                    System.setOut(new PrintStream(consoleStream));

                    final ConfigRuntimeImpl runtimeImpl = new ConfigRuntimeImpl(mc.getInputStream(), consoleStream,
                                                                                consoleStream, null);
                    runtimeImpl.setDests(filePaths.toArray(new String[] {}));

                    runtimeImpl.setDescriptorPatterns("META-INF/autoconf/auto-config.xml,autoconf/auto-config.xml,META-INF/auto-config.xml",
                                                      new String());

                    runtimeImpl.setUserPropertiesFile(antxFile, null);

                    ClassLoader ctl = Thread.currentThread().getContextClassLoader();
                    try {
                        Thread.currentThread().setContextClassLoader(ConfigRuntimeImpl.class.getClassLoader());
                        runtimeImpl.start();
                    } catch (Exception e) {
                        runtimeImpl.error(e);
                    } finally {
                        Thread.currentThread().setContextClassLoader(ctl);
                        // cpCarFileStep2();
                        long end = System.currentTimeMillis();
                        consoleStream.write("�Ѿ�ִ����Ӧ��: " + app.getName() + " ��autoconf�ű����ܹ���ʱ:" + (end - now) + "ms�� \n");
                        consoleStream.write("Easyweb���������:" + str);
                        UIJob uijob = new UIJob("refesh App") {

                            public IStatus runInUIThread(IProgressMonitor monitor) {
                                afterRun();
                                // cpClassesFile();
                                return Status.OK_STATUS;
                            }
                        };
                        uijob.schedule();
                    }
                } catch (Exception e) {
                    EasywebPlugin.log(e);
                }
                return Status.OK_STATUS;

            }
        };
        job.schedule();

    }

    public void afterRun() {
        // EclipseUtils.saveAppProps(app);
        // viewer.refresh();
        for (IJavaProject prj : app.getPrjs()) {
            EasywebPlugin.refeshWorkspace(prj);
        }
    }

    /**
     * �õ�app���а���autoconf��Ŀ¼���Ե�ַ ��
     * 
     * @param app
     * @return
     */
    public List<String> getAutoConfigFiles() {

        // Map<String, String> carMap = app.getCarMaps();
        // if (!carMap.isEmpty()) {
        // return getCarAutoConfigFiles();
        // }

        List<String> result = new ArrayList<String>();
        // �����õ����е�autoconf�ϼ��ļ���
        List<String> data = new ArrayList<String>();
        // FileUtil.findFolders(app.getPath(), "autoconf", data);

        for (IJavaProject prj : app.getJavaProjectInApp()) {
            FileUtil.findFolders(prj.getResource().getLocation().toString(), "autoconf", data);
        }

        if (!data.isEmpty()) {
            for (int i = 0; i < data.size(); i++) {
                String filePath = (String) data.get(i);
                File file = new File(filePath);
                if (file.exists()) {
                    filePath = file.getParentFile().getAbsolutePath();
                    // window�ļ��ָ����滻�ɺ�linuxһ��
                    filePath = filePath.replaceAll("\\\\", "/");
                    if (filePath.indexOf("/deploy/") == -1) result.add(filePath);
                }
            }
        }
        return result;
    }

    public List<String> getCarAutoConfigFiles() {
        List<String> result = new ArrayList<String>();
        result.add(app.getWebInfPath());

        File wfile = new File(app.getWebInfPath() + File.separator + "WEB-INF" + File.separator + "lib");
        File[] wfs = wfile.listFiles();
        for (File file : wfs) {
            result.add(file.getAbsolutePath());
        }
        result.add(app.getWebInfPath() + File.separator + "WEB-INF");
        String metainf = app.getWebInfPath() + File.separator + "META-INF";
        File file = new File(metainf);

        File[] fs = file.listFiles();

        for (File file2 : fs) {
            if (file2.isDirectory()) {
                result.add(metainf + File.separator + file2.getName());
            }
        }

        return result;
    }
}
