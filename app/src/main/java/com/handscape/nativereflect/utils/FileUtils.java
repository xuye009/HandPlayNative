package com.handscape.nativereflect.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 文件工具类
 */
public class FileUtils {


    /**
     * 复制文件，从fromPath复制到toPath
     *
     * @param fromPath
     * @param toPath
     * @return
     */
    public static boolean copy(String fromPath, String toPath) {
        try {
            File outfile = new File(toPath);
            if (outfile != null && outfile.exists()) {
                outfile.deleteOnExit();
            }
            outfile.createNewFile();
            FileInputStream fins = new FileInputStream(new File(fromPath));
            FileOutputStream fous = new FileOutputStream(outfile);
            byte[] data = new byte[1024];
            int length = 0;
            while ((length = fins.read(data)) > 0) {
                fous.write(data, 0, length);
            }
            fous.flush();
            fous.close();
            fins.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param fins
     * @param toPath
     * @return
     */
    public static boolean copy(InputStream fins, String toPath) {
        try {
            File outfile = new File(toPath);
            if (outfile != null && outfile.exists()) {
                outfile.deleteOnExit();
            }
            outfile.createNewFile();
            FileOutputStream fous = new FileOutputStream(outfile);
            byte[] data = new byte[1024];
            int length = 0;
            while ((length = fins.read(data)) > 0) {
                fous.write(data, 0, length);
            }
            fous.flush();
            fous.close();
            fins.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
