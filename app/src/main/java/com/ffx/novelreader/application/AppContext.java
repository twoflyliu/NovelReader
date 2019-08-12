package com.ffx.novelreader.application;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.ffx.novelreader.factory.net.NovelParserFactory;
import com.ffx.novelreader.factory.net.SearchEngineFactory;
import com.ffx.novelreader.factory.net.SearchUrlFactory;
import com.ffx.novelreader.impl.engine.net.LocalSearchEngineImpl;
import com.ffx.novelreader.impl.engine.net.MultiThreadSearchEngineImpl;
import com.ffx.novelreader.impl.engine.net.SingleThreadSearchEngineImpl;
import com.ffx.novelreader.impl.net.BiQuGeSearchUrlGeneratorParser;
import com.ffx.novelreader.impl.net.parser.BiQuGeParser;
import com.ffx.novelreader.inter.net.SearchUrlGeneratorParser;
import com.ffx.novelreader.inter.net.parser.NovelParser;
import com.ffx.novelreader.treader.Config;
import com.ffx.novelreader.treader.util.PageFactory;

import org.litepal.LitePalApplication;
import org.litepal.tablemanager.Connector;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

public class AppContext extends LitePalApplication {
    private static final String TAG = "AppContext";
    public static volatile Context applicationContext = null;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();

        applicationContext = getApplicationContext();

        // 创建数据库
        LitePalApplication.initialize(this);
        Connector.getDatabase();

        // 注册解析器
        initAllNovelParser();

        // 注册搜索生成提取器
        initAllSearchUrlGeneratorParser();

        // 注册搜索引擎
        initAllSearchEngine();

        Config.createConfig(this);
        PageFactory.createPageFactory(this);
        initialEnv();
    }

    private void initialEnv() {
    }


    private void initAllSearchEngine() {
        SearchEngineFactory factory = SearchEngineFactory.getInstance();
        //factory.registerSearchEngine(new MultiThreadSearchEngineImpl());
        factory.registerSearchEngine(new LocalSearchEngineImpl());
        factory.registerSearchEngine(new SingleThreadSearchEngineImpl());
    }

    private void initAllSearchUrlGeneratorParser() {
        SearchUrlFactory factory = SearchUrlFactory.getInstance();

        SearchUrlGeneratorParser biQuGeSearchUrlGeneratorParser = new BiQuGeSearchUrlGeneratorParser();
        factory.registerSearchUrlGeneratorParser("www.37zw.net", biQuGeSearchUrlGeneratorParser);
    }

    private void initAllNovelParser() {
        NovelParserFactory factory = NovelParserFactory.getInstance();

        NovelParser biQuGeNovelParser = new BiQuGeParser();
        factory.registerNovelParser("www.37zw.net", biQuGeNovelParser);
    }
}
