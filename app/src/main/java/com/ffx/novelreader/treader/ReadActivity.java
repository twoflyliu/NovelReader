package com.ffx.novelreader.treader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.SQLException;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ffx.novelreader.DBNovelContentProvider;
import com.ffx.novelreader.R;
import com.ffx.novelreader.treader.base.BaseActivity;
import com.ffx.novelreader.treader.db.BookList;
import com.ffx.novelreader.treader.db.BookMarks;
import com.ffx.novelreader.treader.dialog.PageModeDialog;
import com.ffx.novelreader.treader.dialog.SettingDialog;
import com.ffx.novelreader.treader.util.BrightnessUtil;
import com.ffx.novelreader.treader.util.PageFactory;
import com.ffx.novelreader.treader.view.PageWidget;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/7/15 0015.
 */
public class ReadActivity extends BaseActivity {
    private static final String TAG = "ReadActivity";
    private final static String EXTRA_BOOK = "bookList";
    private final static int MESSAGE_CHANGEPROGRESS = 1;

    // 显示小说内容控件
    @Bind(R.id.bookpage)
    PageWidget bookpage;


    // 小说标题栏
    @Bind(R.id.appbar)
    AppBarLayout appbar;    //容器

    @Bind(R.id.toolbar)
    Toolbar toolbar;        //工具栏

    // 语音播报状态栏
    @Bind(R.id.rl_read_bottom)
    RelativeLayout rl_read_bottom; //容器

    @Bind(R.id.tv_stop_read)
    TextView tv_stop_read; //操作按钮


    // 底部菜单
    @Bind(R.id.rl_bottom)
    RelativeLayout rl_bottom;       //（根容器）

    @Bind(R.id.rl_progress)
    RelativeLayout rl_progress;     // 读取进度菜单父容器
    @Bind(R.id.tv_progress)
    TextView tv_progress;           // 读取进度文本展示（当进度条变化的时候，是使用这个控件来进行展示的）

    @Bind(R.id.bookpop_bottom)
    LinearLayout bookpop_bottom;    // 底部弹出菜单父容器
    @Bind(R.id.tv_pre)
    TextView tv_pre;                // 上一章
    @Bind(R.id.sb_progress)
    SeekBar sb_progress;            // 显示进度(进度条展示）
    @Bind(R.id.tv_next)
    TextView tv_next;               // 下一章
    @Bind(R.id.tv_directory)
    TextView tv_directory;          // 目录菜单按钮
    @Bind(R.id.tv_dayornight)
    TextView tv_dayornight;         // 夜间模式按钮
    @Bind(R.id.tv_pagemode)
    TextView tv_pagemode;           // 翻页模式按钮
    @Bind(R.id.tv_setting)
    TextView tv_setting;            // 设置按钮

    //    @Bind(R.id.btn_return)
//    ImageButton btn_return;
//    @Bind(R.id.ll_top)
//    LinearLayout ll_top;

    private Config config;
    private WindowManager.LayoutParams lp;
    private BookList bookList;
    private PageFactory pageFactory;
    private int screenWidth, screenHeight;
    // popwindow是否显示
    private Boolean isShow = false;
    private SettingDialog mSettingDialog;
    private PageModeDialog mPageModeDialog;
    private Boolean mDayOrNight;

    private static final int EXTERNAL_STORAGE_REQUEST_CODE = 1;

    // 接收电池信息更新的广播(PageFactory负责页面管理和绘制)
    private BroadcastReceiver myReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
            Log.e(TAG, Intent.ACTION_BATTERY_CHANGED);
            int level = intent.getIntExtra("level", 0);
            pageFactory.updateBattery(level);
        }else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)){
            Log.e(TAG, Intent.ACTION_TIME_TICK);
            pageFactory.updateTime();
        }
        }
    };

    @Override
    public int getLayoutRes() {
        return R.layout.activity_read;
    }


    @Override
    protected void initData() {
        if(Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 19){
            bookpage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        // 初始化工具条
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.return_button);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 初始化服务类
        config = Config.getInstance();
        pageFactory = PageFactory.getInstance();
        //pageFactory.setContentProvider(new BookUtil());
        pageFactory.setContentProvider(new DBNovelContentProvider());

        // 监听服务（电池电量变化|时间变化）
        IntentFilter mfilter = new IntentFilter();
        mfilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mfilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(myReceiver, mfilter);

        //设置弹出对话框
        mSettingDialog = new SettingDialog(this);

        //页面模式对话框
        mPageModeDialog = new PageModeDialog(this);

        //获取屏幕宽高
        WindowManager manage = getWindowManager();
        Display display = manage.getDefaultDisplay();
        Point displaysize = new Point();
        display.getSize(displaysize);

        screenWidth = displaysize.x;
        screenHeight = displaysize.y;


        //保持屏幕常亮 - 技巧
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //隐藏 - (全屏显示)
        hideSystemUI();

        //改变屏幕亮度 - 技巧
        if (!config.isSystemLight()) {
            BrightnessUtil.setBrightness(this, config.getLight());
        }

        //获取intent中的携带的信息
        Intent intent = getIntent();
        bookList = (BookList) intent.getSerializableExtra(EXTRA_BOOK);

        // 初始化pageFactory, 需要两个步骤
        // 1. 设置bookpage的页面模式
        // 2. 设置pageFactory的展示页面
        // 3. 打开书籍
        bookpage.setPageMode(config.getPageMode());
        pageFactory.setPageWidget(bookpage);

        // 检测是否有读, 写外部存储权限


        // 初始化日渐/白天模式按钮内容
        initDayOrNight();

        // 打开小说
        openBook(bookList);

//        if (checkPermission(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                EXTERNAL_STORAGE_REQUEST_CODE, "需要读写外发存储权限")) {
//            openBook(bookList);
//        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case EXTERNAL_STORAGE_REQUEST_CODE:
//                int i;
//                for (i = 0; i < grantResults.length; i++) {
//                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                        break;
//                    }
//                }
//                if (i == grantResults.length) {
//                    openBook(bookList);
//                }
//                break;
//            default:
//                break;
//        }
//    }

    private void openBook(BookList bookList) {
        try {
            pageFactory.openBook(bookList); // 异步打开
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "打开电子书失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void initListener() {
        // 当进度条变化的时候，显示当前进度百分比，同步章节内容
        sb_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float pro;
            // 触发操作，拖动
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pro = (float) (progress / 10000.0);
                showProgress(pro); //显示进度百分比
            }

            // 表示进度条刚开始拖动，开始拖动时候触发的操作
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            // 停止拖动时候
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pageFactory.changeProgress(pro); //同步章节内容
            }
        });

        // 设置页面模式（取消的时候隐藏系统UI）
        mPageModeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                hideSystemUI();
            }
        });

        // 当页面模式改变的时候，设置页面模式
        mPageModeDialog.setPageModeListener(new PageModeDialog.PageModeListener() {
            @Override
            public void changePageMode(int pageMode) {
                bookpage.setPageMode(pageMode);
            }
        });

        // 设置对话框取消的时候，隐藏系统UI
        mSettingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                hideSystemUI();
            }
        });

        // 绑定设置亮度，字体尺寸，字体类型处理器
        mSettingDialog.setSettingListener(new SettingDialog.SettingListener() {
            @Override
            public void changeSystemBright(Boolean isSystem, float brightness) { // 改变系统亮度
                if (!isSystem) {
                    BrightnessUtil.setBrightness(ReadActivity.this, brightness);
                } else {
                    int bh = BrightnessUtil.getScreenBrightness(ReadActivity.this);
                    BrightnessUtil.setBrightness(ReadActivity.this, bh);
                }
            }

            @Override
            public void changeFontSize(int fontSize) {
                pageFactory.changeFontSize(fontSize);
            } //改变字体大小

            @Override
            public void changeTypeFace(Typeface typeface) {
                pageFactory.changeTypeface(typeface);
            } //改变字体类型

            @Override
            public void changeBookBg(int type) {
                pageFactory.changeBookBg(type);
            } //改变字体背景
        });

        // 页面管理类通知UI类当前进度
        pageFactory.setPageEvent(new PageFactory.PageEvent() {
            @Override
            public void changeProgress(float progress) {
                Message message = new Message();
                message.what = MESSAGE_CHANGEPROGRESS;
                message.obj = progress;
                mHandler.sendMessage(message);
            }
        });

        // 添加触摸添加（主要用于唤醒菜单，前一页，下一页...）
        bookpage.setTouchListener(new PageWidget.TouchListener() {
            @Override
            public void center() {  //在中间的时候就切换读设置菜单(有动画效果）
                if (isShow) {
                    hideReadSetting();
                } else {
                    showReadSetting();
                }
            }

            @Override
            public Boolean prePage() {  //前一页
                // 菜单显示或者正在语音读小说则不警方也
                if (isShow){
                    return false;
                }

                pageFactory.prePage(); //进行翻页

                if (pageFactory.isfirstPage()) {
                    return false;
                }

                return true;
            }

            @Override
            public Boolean nextPage() { //下一页
                Log.e("setTouchListener", "nextPage");
                if (isShow){
                    return false;
                }

                pageFactory.nextPage();
                if (pageFactory.islastPage()) {
                    return false;
                }
                return true;
            }

            @Override
            public void cancel() {
                pageFactory.cancelPage();
            }
        });

        bookpage.setScreenChangeListener(new PageWidget.ScreenChangeListener() {
            @Override
            public void onScreenPixelChange(int widthPixel, int heightPixel) {
                pageFactory.onScreenPixelChange(widthPixel, heightPixel);
            }
        });
    }

    // 同步进度条状态变化（Page页面主动传过来的）
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_CHANGEPROGRESS:
                    float progress = (float) msg.obj;
                    setSeekBarProgress(progress);
                    break;
            }
        }
    };


    @Override
    protected void onResume(){ //重新显示
        super.onResume();
        if (!isShow){          //没有设置操作的时候，隐藏相同相关的UI
            hideSystemUI();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy() { //页面被销毁, 释放相关资源
        super.onDestroy();
        pageFactory.clear();
        bookpage = null;
        unregisterReceiver(myReceiver);
    }

    //菜单栏模拟成activity一样，点击back按钮不会退出整个活动
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isShow){
                hideReadSetting();
                return true;
            }
            if (mSettingDialog.isShowing()){
                mSettingDialog.hide();
                return true;
            }
            if (mPageModeDialog.isShowing()){
                mPageModeDialog.hide();
                return true;
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.read, menu);   //创建弹出菜单（添加书签|读数）
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_bookmark){// 添加书签
            if (pageFactory.getCurrentPage() != null) {
                List<BookMarks> bookMarksList = DataSupport.where("bookpath = ? and begin = ?", pageFactory.getBookPath(),pageFactory.getCurrentPage().getBegin() + "").find(BookMarks.class);

                if (!bookMarksList.isEmpty()){
                    Toast.makeText(ReadActivity.this, "该书签已存在", Toast.LENGTH_SHORT).show();
                }else {
                    BookMarks bookMarks = new BookMarks();
                    String word = "";
                    for (String line : pageFactory.getCurrentPage().getLines()) {
                        word += line;
                    }
                    try {
                        SimpleDateFormat sf = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm ss");
                        String time = sf.format(new Date());
                        bookMarks.setTime(time);
                        bookMarks.setBegin(pageFactory.getCurrentPage().getBegin());
                        bookMarks.setText(word);
                        bookMarks.setBookpath(pageFactory.getBookPath());
                        bookMarks.save();

                        Toast.makeText(ReadActivity.this, "书签添加成功", Toast.LENGTH_SHORT).show();
                    } catch (SQLException e) {
                        Toast.makeText(ReadActivity.this, "该书签已存在", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(ReadActivity.this, "添加书签失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // 工厂方法，便于其他活动启动本活动
    public static boolean openBook(final BookList bookList, Activity context) {
        if (bookList == null){
            throw new NullPointerException("BookList can not be null");
        }

        Intent intent = new Intent(context, ReadActivity.class);
        intent.putExtra(EXTRA_BOOK, bookList);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        context.startActivity(intent);
        return true;
    }

//    public BookPageWidget getPageWidget() {
//        return bookpage;
//    }

    // 设置全屏
    /**
     * 隐藏菜单。沉浸式阅读
     */
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        //  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    // 回复为默认
    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    //显示书本百分比进度文本显示
    public void showProgress(float progress){
        if (rl_progress.getVisibility() != View.VISIBLE) {
            rl_progress.setVisibility(View.VISIBLE);
        }
        setProgress(progress);
    }

    //隐藏书本进度
    public void hideProgress(){
        rl_progress.setVisibility(View.GONE);
    }

    // 初始化夜间模式
    public void initDayOrNight(){
        mDayOrNight = config.getDayOrNight();
        if (mDayOrNight){
            tv_dayornight.setText(getResources().getString(R.string.read_setting_day));
        }else{
            tv_dayornight.setText(getResources().getString(R.string.read_setting_night));
        }
    }

    //改变显示模式
    public void changeDayOrNight(){
        if (mDayOrNight){
            mDayOrNight = false;
            tv_dayornight.setText(getResources().getString(R.string.read_setting_night));
        }else{
            mDayOrNight = true;
            tv_dayornight.setText(getResources().getString(R.string.read_setting_day));
        }
        config.setDayOrNight(mDayOrNight);
        pageFactory.setDayOrNight(mDayOrNight);
    }

    private void setProgress(float progress){
        DecimalFormat decimalFormat=new DecimalFormat("00.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p=decimalFormat.format(progress * 100.0);//format 返回的是字符串
        tv_progress.setText(p + "%");
    }

    public void setSeekBarProgress(float progress){
        sb_progress.setProgress((int) (progress * 10000));
    }

    // 显示设置页面（带有动画效果）
    private void showReadSetting(){
        isShow = true;
        rl_progress.setVisibility(View.GONE);

//        if (isSpeaking){
//            Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_top_enter);
//            rl_read_bottom.startAnimation(topAnim);
//            rl_read_bottom.setVisibility(View.VISIBLE);
//        }else {
            showSystemUI();

            Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_enter);
            Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_top_enter);
            rl_bottom.startAnimation(topAnim);
            appbar.startAnimation(topAnim);
//        ll_top.startAnimation(topAnim);
            rl_bottom.setVisibility(View.VISIBLE);
//        ll_top.setVisibility(View.VISIBLE);
            appbar.setVisibility(View.VISIBLE);
        //}
    }

    // 隐藏读设置页面（带有动画效果）
    private void hideReadSetting() {
        isShow = false;
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_exit);
        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_top_exit);
        if (rl_bottom.getVisibility() == View.VISIBLE) {
            rl_bottom.startAnimation(topAnim);
        }
        if (appbar.getVisibility() == View.VISIBLE) {
            appbar.startAnimation(topAnim);
        }
        if (rl_read_bottom.getVisibility() == View.VISIBLE) {
            rl_read_bottom.startAnimation(topAnim);
        }
//        ll_top.startAnimation(topAnim);
        rl_bottom.setVisibility(View.GONE);
        rl_read_bottom.setVisibility(View.GONE);
//        ll_top.setVisibility(View.GONE);
        appbar.setVisibility(View.GONE);
        hideSystemUI();
    }

    // 监听onClick事件
    @OnClick({R.id.tv_progress, R.id.rl_progress, R.id.tv_pre, R.id.sb_progress, R.id.tv_next, R.id.tv_directory, R.id.tv_dayornight,R.id.tv_pagemode, R.id.tv_setting, R.id.bookpop_bottom, R.id.rl_bottom,R.id.tv_stop_read})
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.btn_return:
//                finish();
//                break;
//            case R.id.ll_top:
//                break;
            case R.id.tv_progress:
                break;
            case R.id.rl_progress:
                break;
            case R.id.tv_pre:
                pageFactory.preChapter();   //前一章节
                break;
            case R.id.sb_progress:
                break;
            case R.id.tv_next:              //下一章节
                pageFactory.nextChapter();
                break;
            case R.id.tv_directory:         //目录页面（带有目录和书签）
                Intent intent = new Intent(ReadActivity.this, MarkActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_dayornight:
                changeDayOrNight();         //设置夜间模式
                break;
            case R.id.tv_pagemode:
                hideReadSetting();
                mPageModeDialog.show();     //显示/隐藏翻页对话框
                break;
            case R.id.tv_setting:
                hideReadSetting();
                mSettingDialog.show();      //显示/隐藏设置对话框
                break;
            case R.id.bookpop_bottom:
                break;
            case R.id.rl_bottom:
                break;
            case R.id.tv_stop_read:         //停止语音读
                break;
        }
    }

}
