package com.ffx.novelreader.util;

import android.content.Context;
import android.util.Log;

import com.ffx.novelreader.application.AppContext;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

public class FileUtil {
    private static final String TAG = "FileUtil";

    /**
     * 获取字节内容字符集
     * @param content 字节内容
     * @return 返回字符集名称
     */
    public static String getCharset(byte[] content) {
        final int BUF_SIZE = 4096;
        UniversalDetector detector = new UniversalDetector(null);
        int start = 0;
        int end = Math.min(BUF_SIZE, content.length);

        while (start < content.length && !detector.isDone()) {
            //Log.d(TAG, "getCharset: start = " + start + ", end = " + end + ", total = " + content.length);
            detector.handleData(content, start, end - start);
            start = end;
            end = Math.min(start + BUF_SIZE, content.length);
        }

        detector.dataEnd();
        String charset = detector.getDetectedCharset();
        detector.reset();
        return charset;
    }

    public static void writeFile(String fileName, String inputText){
        Context context = AppContext.applicationContext;
        if (null == context) {
            throw new NullPointerException("AppContext.applicationContext is null");
        }

        FileOutputStream out = null;
        BufferedWriter writer = null;

        try {
            out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputText);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void writeObjectToFile(String fileName, Object object) {
        Context context = AppContext.applicationContext;
        if (null == context) {
            throw new NullPointerException("AppContext.applicationContext is null");
        }

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Object readObjectFromFile(String fileName) {
        Context context = AppContext.applicationContext;
        if (null == context) {
            throw new NullPointerException("AppContext.applicationContext is null");
        }

        Object result = null;

        FileInputStream fis = null;
        ObjectInputStream ois = null;

        try {
            fis = context.openFileInput(fileName);
            ois = new ObjectInputStream(fis);
            result = ois.readObject();
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
}
