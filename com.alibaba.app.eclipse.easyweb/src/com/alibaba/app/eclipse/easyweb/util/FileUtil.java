package com.alibaba.app.eclipse.easyweb.util;

/*
 * (c) Copyright Sysdeo SA 2001, 2002. All Rights Reserved.
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;

import com.alibaba.app.eclipse.easyweb.EasywebPlugin;

/**
 * Utility class for Files operation Use UTF-8 encoding for text file.
 */

public class FileUtil {

    public static void coppy(String source, String target) throws IOException {
        File file = new File(source);
        if (!file.exists()) {
            return;
        } else {
            if (file.isFile()) {
                copyFile(file, new File(target));
            } else {
                copyDirectiory(source, target);
            }
        }
    }

    // �����ļ�
    public static void copyFile(File sourceFile, File targetFile) {
        if (sourceFile.isDirectory() || targetFile.isDirectory()) {
            return;
        }
        try {
            // �½��ļ����������������л���
            FileInputStream input = new FileInputStream(sourceFile);

            BufferedInputStream inBuff = new BufferedInputStream(input);
            // �½��ļ���������������л���
            if (targetFile.exists()) {
                targetFile.delete();
            }
            FileOutputStream output = new FileOutputStream(targetFile);
            BufferedOutputStream outBuff = new BufferedOutputStream(output);
            // ��������
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // ˢ�´˻���������
            outBuff.flush();
            // �ر���
            inBuff.close();
            outBuff.close();
            output.close();
            input.close();
        } catch (Exception e) {
        }
    }

    // �����ļ���
    public static void copyDirectiory(String sourceDir, String targetDir) {

        // ��ȡԴ�ļ��е�ǰ�µ��ļ���Ŀ¼

        File f = new File(sourceDir);
        if (!f.exists() || f.getName().equals(".svn")) {
            return;
        }
        File target = new File(targetDir);
        if (!target.exists()) {
            target.mkdirs();
        }
        File[] file = f.listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // Դ�ļ�
                File sourceFile = file[i];
                // Ŀ���ļ�
                File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                if (file[i].getName().equals(".svn")) {
                    continue;
                }
                // ׼�����Ƶ�Դ�ļ���
                String dir1 = sourceDir + File.separator + file[i].getName();
                // ׼�����Ƶ�Ŀ���ļ���
                String dir2 = targetDir + File.separator + file[i].getName();
                File dir2F = new File(dir2);
                if (!dir2F.exists()) dir2F.mkdir();
                copyDirectiory(dir1, dir2);
            }
        }
    }

    /**
     * ɾ���ļ��������ǵ����ļ����ļ���
     * 
     * @param fileName ��ɾ�����ļ���
     * @return �ļ�ɾ���ɹ�����true,���򷵻�false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                return deleteFile(fileName);
            } else {
                return deleteDirectory(fileName);
            }
        }
    }

    /**
     * ɾ�������ļ�
     * 
     * @param fileName ��ɾ���ļ����ļ���
     * @return �����ļ�ɾ���ɹ�����true,���򷵻�false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.isFile() && file.exists()) {
            file.delete();
            return true;
        } else {
            return false;
        }
    }

    /**
     * ɾ��Ŀ¼���ļ��У��Լ�Ŀ¼�µ��ļ�
     * 
     * @param dir ��ɾ��Ŀ¼���ļ�·��
     * @return Ŀ¼ɾ���ɹ�����true,���򷵻�false
     */
    public static boolean deleteDirectory(String dir) {
        // ���dir�����ļ��ָ�����β���Զ�����ļ��ָ���
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        // ���dir��Ӧ���ļ������ڣ����߲���һ��Ŀ¼�����˳�
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        // ɾ���ļ����µ������ļ�(������Ŀ¼)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // ɾ�����ļ�
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
            // ɾ����Ŀ¼
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        // ɾ����ǰĿ¼
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    public static void findTargetFolders(String path, String folder, List result) {
        File file = new File(path);
        if (file.exists() && file.isDirectory() && !file.getName().equals(".svn") && !file.getName().equals("classes")
            && !file.getName().equals("ROOT.war")) {
            File[] childrens = file.listFiles();
            for (int i = 0; i < childrens.length; i++) {
                File children = childrens[i];
                if (children.isDirectory() && children.list().length >= 1) {
                    if (children.getName().equals(folder)) {
                        result.add(children.getParentFile().getAbsolutePath());
                        break;
                    } else {
                        findTargetFolders(children.getAbsolutePath(), folder, result);
                    }
                } else {
                    continue;
                }
            }
        }
    }

    public static void findFolders(String path, String folder, List result) {
        File file = new File(path);
        if (file.exists() && file.isDirectory() && !file.getName().equals(".svn") && !file.getName().equals("target")
            && !file.getName().equals("classes") && !file.getName().equals("ROOT.war")) {
            File[] childrens = file.listFiles();
            for (int i = 0; i < childrens.length; i++) {
                File children = childrens[i];
                if (children.isDirectory() && children.list().length >= 1) {
                    if (children.getName().equals(folder)) {
                        result.add(children.getParentFile().getAbsolutePath());
                        break;
                    } else {
                        findFolders(children.getAbsolutePath(), folder, result);
                    }
                } else {
                    continue;
                }
            }
        }
    }

    /**
     * �õ�webinf�ľ����ļ�Ŀ¼
     * 
     * @param path
     * @param result
     */
    public static String findWebInfPath(String path) {
        List rs = new ArrayList();
        findFolders(path, "WEB-INF", rs);
        if (!rs.isEmpty()) {
            return (String) rs.get(0);
        }
        return null;
    }

    public static String findAntx(String path) {
        File file = new File(path);
        if (file.exists() && !file.getName().equals(".svn") && !file.getName().equals("target")
            && !file.getName().equals("classes")) {
            File[] childrens = file.listFiles();
            for (int i = 0; i < childrens.length; i++) {
                File children = childrens[i];
                if (children.isFile() && children.getName().equals("antx.properties")) {
                    return children.getParentFile().getAbsolutePath();
                } else {
                    continue;
                }
            }
            return findAntx(file.getParentFile().getAbsolutePath());
        }
        return null;

    }

    public static String getDocLocation() {

        String loc = "";
        URL url = EasywebPlugin.getDefault().getBundle().getEntry("/");
        try {
            loc = FileLocator.resolve(url).getPath();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return loc;
    }

    /**
     * �������ļ�
     * 
     * @param path
     * @param fileName �ļ���,���·���Ѿ������ļ�����ʡ��
     * @param content
     */
    public static void createSimpleFile(String path, String fileName, String content) {
        File target = new File(path);
        if (!target.exists() && target.isDirectory()) {
            target.mkdirs();
        }
        File file = new File(path, fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(content.getBytes());
        } catch (Exception e) {
            EasywebPlugin.log(e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
    }

    // �����ļ�
    public static String readFile(File sourceFile) {
        if (!sourceFile.exists() && !sourceFile.isFile()) {
            return null;
        }
        try {
            InputStreamReader read = new InputStreamReader(new FileInputStream(sourceFile), "GBK");
            BufferedReader input = new BufferedReader(read);

            StringBuffer buffer = new StringBuffer();
            String text;

            while ((text = input.readLine()) != null)
                buffer.append(text + "\n");

            return buffer.toString();
        } catch (Exception e) {
        }
        return null;
    }

    public static void main(String[] args) {
        /*
         * List result = new ArrayList(); findFolders("/home/jiaoyingjun/work/vaspool/bundle/war","autoconf", result);
         * System.out.println(result);
         */
        /*
         * StringBuffer sb = new StringBuffer(); String s = findAntx("/home/jiaoyingjun/work/vaspool/bundle/war");
         * System.out.println(s);
         */
        System.out.println(System.getProperty("os.name"));

    }

}
