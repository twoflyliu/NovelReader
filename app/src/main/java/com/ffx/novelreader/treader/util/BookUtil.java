package com.ffx.novelreader.treader.util;

import android.content.ContentValues;
import android.os.Environment;
import android.text.TextUtils;

import com.ffx.novelreader.treader.bean.Cache;
import com.ffx.novelreader.treader.db.BookCatalogue;
import com.ffx.novelreader.treader.db.BookList;

import org.litepal.crud.DataSupport;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/11 0011.
 */

// 这个类主要提供小说内容（章节信息）
public class BookUtil implements ContentProvider{
    private static final String cachedPath = Environment.getExternalStorageDirectory() + "/novelreader/"; //缓存写到sdk中
    //存储的字符数
    public static final int cachedSize = 30000;
//    protected final ArrayList<WeakReference<char[]>> myArray = new ArrayList<>();

    protected final ArrayList<Cache> myArray = new ArrayList<>(); //缓存列表

    private List<BookCatalogue> directoryList = new ArrayList<>(); //目录列表

    private String m_strCharsetName;        //字符集名称
    private String bookName;                //小说名称
    private String bookPath;                //小说文本路径
    private long bookLen;                   //小说总的字符长度
    private long position;                  //当前游标位置
    private BookList bookList;               //小说引用

    public BookUtil(){
        // 构建缓存目录
        File file = new File(cachedPath);
        if (!file.exists()){
            file.mkdir();
        }
    }

    //获取整本书字符长度
    @Override
    public long getBookLen(){
        return bookLen;
    }

    //获取章节目录列表
    @Override
    public List<BookCatalogue> getDirectoryList(){
        return directoryList;
    }

    // 打开书本（主要重新进行缓存）
    @Override
    public synchronized void openBook(BookList bookList) throws IOException {
        this.bookList = bookList;
        //如果当前缓存不是要打开的书本就缓存书本同时删除缓存

        if (bookPath == null || !bookPath.equals(bookList.getBookpath())) {
            cleanCacheFile();
            this.bookPath = bookList.getBookpath();
            bookName = FileUtils.getFileName(bookPath);
            cacheBook();
        }
    }

    // 下一个字符
    // 如果back=true, 则当前位置不变
    @Override
    public int next(boolean back){
        position += 1;
        if (position > bookLen){
            position = bookLen;
            return -1;
        }
        char result = current();
        if (back) {
            position -= 1;
        }
        return result;
    }

    // 获取前一个字符
    // 如果back为true，则当前字符位置不变
    @Override
    public int pre(boolean back){
        position -= 1;
        if (position < 0){
            position = 0;
            return -1;
        }
        char result = current();
        if (back) {
            position += 1;
        }
        return result;
    }

    // 当前position对应的字符
    @Override
    public char current(){
//        int pos = (int) (position % cachedSize);
//        int cachePos = (int) (position / cachedSize);
        int cachePos = 0; //缓存块索引
        int pos = 0;      //相对于某个缓存块的索引
        int len = 0;      //

        // 根据position来定位到指定的缓存块
        for (int i = 0;i < myArray.size();i++){
            long size = myArray.get(i).getSize();
            if (position <= size + len - 1){ //position是相对于整本数的长度
                cachePos = i;
                pos = (int) (position - len);
                break;
            }
            len += size;
        }

        char[] charArray = block(cachePos);
        return charArray[pos];
    }

    // 当前位置到下一行内容(不包含换行符号)
    @Override
    public char[] nextLine(){
        if (position >= bookLen){
            return null;
        }
        String line = "";
        while (position < bookLen){
            int word = next(false);
            if (word == -1){
                break;
            }
            char wordChar = (char) word;
            if ((wordChar + "").equals("\r") && (((char)next(true)) + "").equals("\n")){
                next(false);
                break;
            }
            line += wordChar;
        }
        return line.toCharArray();
    }

    // 当前位置到前一行内容(不包含换行符号)
    @Override
    public char[] preLine(){
        if (position <= 0){
            return null;
        }
        String line = "";
        while (position >= 0){
            int word = pre(false);
            if (word == -1){
                break;
            }
            char wordChar = (char) word;
            if ((wordChar + "").equals("\n") && (((char)pre(true)) + "").equals("\r")){
                pre(false);
//                line = "\r\n" + line;
                break;
            }
            line = wordChar + line;
        }
        return line.toCharArray();
    }

    // 获取当前位置
    @Override
    public long getPosition(){
        return position;
    }

    // 设置当前位置
    @Override
    public void setPostition(long position){
        this.position = position;
    }

    // 清除缓存文件
    private void cleanCacheFile(){
        File file = new File(cachedPath);
        if (!file.exists()){
            file.mkdir();
        }else{
            File[] files = file.listFiles();
            for (int i = 0; i < files.length;i++){
                files[i].delete();
            }
        }
    }

    //缓存书本
    private void cacheBook() throws IOException {
        // 获取字符集
        if (TextUtils.isEmpty(bookList.getCharset())) { //如果书本字符集没有设置，则更新字符集
            m_strCharsetName = FileUtils.getCharset(bookPath);
            if (m_strCharsetName == null) {
                m_strCharsetName = "utf-8";
            }
            ContentValues values = new ContentValues();
            values.put("charset",m_strCharsetName);
            DataSupport.update(BookList.class,values,bookList.getId());
        }else{
            m_strCharsetName = bookList.getCharset();
        }


        File file = new File(bookPath);
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file),m_strCharsetName);
        int index = 0;
        bookLen = 0;
        directoryList.clear();
        myArray.clear();

        // 一次性将文本内容缓存到本地(按照指定块）
        while (true){
            char[] buf = new char[cachedSize]; //默认字符为0
            int result = reader.read(buf);
            if (result == -1){
                reader.close();
                break;
            }

            String bufStr = new String(buf);
//            bufStr = bufStr.replaceAll("\r\n","\r\n\u3000\u3000");
//            bufStr = bufStr.replaceAll("\u3000\u3000+[ ]*","\u3000\u3000");
            bufStr = bufStr.replaceAll("\r\n+\\s*","\r\n\u3000\u3000"); // - 可以确保首行缩进两个字符
//            bufStr = bufStr.replaceAll("\r\n[ {0,}]","\r\n\u3000\u3000");
//            bufStr = bufStr.replaceAll(" ","");
            bufStr = bufStr.replaceAll("\u0000",""); //移除全本小说最后内容为空的字符
            buf = bufStr.toCharArray();
            bookLen += buf.length;

            Cache cache = new Cache();
            cache.setSize(buf.length);
            cache.setData(new WeakReference<char[]>(buf));

//            bookLen += result;
            myArray.add(cache);
//            myArray.add(new WeakReference<char[]>(buf));
//            myArray.set(index,);

            // 缓存文件到本地
            try {
                File cacheBook = new File(fileName(index));
                if (!cacheBook.exists()){
                    cacheBook.createNewFile();
                }
                final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName(index)), "UTF-16LE");
                writer.write(buf);
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException("Error during writing " + fileName(index));
            }
            index ++;
        }

        new Thread(){
            @Override
            public void run() {
                getChapter();
            }
        }.start();
    }

    //获取章节（解析文件,获取目录（目录名，偏移位置））
    protected synchronized void getChapter(){
        try {
            long size = 0;
            for (int i = 0; i < myArray.size(); i++) {
                char[] buf = block(i);
                String bufStr = new String(buf);
                String[] paragraphs = bufStr.split("\r\n");
                for (String str : paragraphs) {

                    //如果是目录则更新目录
                    if (str.length() <= 30 && (str.matches(".*第.{1,8}章.*") || str.matches(".*第.{1,8}节.*"))) { //标题
                        BookCatalogue bookCatalogue = new BookCatalogue(); //目录
                        bookCatalogue.setBookCatalogueStartPos(size);      //相对于整个文本的偏移位置
                        bookCatalogue.setBookCatalogue(str);               //目录名称
                        bookCatalogue.setBookpath(bookPath);               //文件路径
                        directoryList.add(bookCatalogue);                  //添加目录
                    }

                    if (str.contains("\u3000\u3000")) {
                        size += str.length() + 2;
                    } else if (str.contains("\u3000")){
                        size += str.length() + 1;
                    } else {
                        size += str.length();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //获取指定索引的缓存文件名称
    protected String fileName(int index) {
        return cachedPath + bookName + index ;
    }

    //获取弱引用数据，如果失效了，则重新加载
    protected char[] block(int index) {
        if (myArray.size() == 0){
            return new char[1];
        }
        char[] block = myArray.get(index).getData().get(); //弱引用缓存，当缓存失效，就重新加载
        if (block == null) {
            try {
                File file = new File(fileName(index));
                int size = (int)file.length();
                if (size < 0) {
                    throw new RuntimeException("Error during reading " + fileName(index));
                }
                block = new char[size / 2];
                InputStreamReader reader =
                        new InputStreamReader(
                                new FileInputStream(file),
                                "UTF-16LE"
                        );
                if (reader.read(block) != block.length) {
                    throw new RuntimeException("Error during reading " + fileName(index));
                }
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException("Error during reading " + fileName(index));
            }
            Cache cache = myArray.get(index);
            cache.setData(new WeakReference<char[]>(block));
//            myArray.set(index, new WeakReference<char[]>(block));
        }
        return block;
    }

}
