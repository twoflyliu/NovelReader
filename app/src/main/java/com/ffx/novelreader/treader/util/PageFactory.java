package com.ffx.novelreader.treader.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.ffx.novelreader.R;
import com.ffx.novelreader.treader.Config;
import com.ffx.novelreader.treader.db.BookCatalogue;
import com.ffx.novelreader.treader.db.BookList;
import com.ffx.novelreader.treader.view.PageWidget;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/20 0020.
 */
public class PageFactory {
    private static final String TAG = "PageFactory";
    private static PageFactory pageFactory;

    private Context mContext;
    private Config config;
    //当前的书本
//    private File book_file = null;
    // 默认背景颜色
    private int m_backColor = 0xffff9e85;
    //页面宽
    private int mWidth;
    //页面高
    private int mHeight;
    //文字字体大小
    private float m_fontSize ;
    //时间格式
    private SimpleDateFormat sdf;
    //时间
    private String date;
    //进度格式
    private DecimalFormat df ;

    //电池边界宽度
    private float mBorderWidth;

    // 上下与边缘的距离
    private float marginHeight ;
    // 左右与边缘的距离
    private float measureMarginWidth ;
    // 左右与边缘的距离
    private float marginWidth ;

    //状态栏距离底部高度
    private float statusMarginBottom;

    //行间距
    private float lineSpace;
    //段间距
    private float paragraphSpace;

    //字高度
    private float fontHeight;
    //字体
    private Typeface typeface;

    //文字画笔
    private Paint mPaint;
    //加载画笔
    private Paint waitPaint;

    //文字颜色
    private int m_textColor = Color.rgb(50, 65, 78);

    // 绘制内容的宽
    private float mVisibleHeight;
    // 绘制内容的宽
    private float mVisibleWidth;

    // 每页可以显示的行数
    private int mLineCount;

    //电池画笔
    private Paint mBatterryPaint ;

    //电池字体大小
    private float mBatterryFontSize;

    //背景图片
    private Bitmap m_book_bg = null;

    //当前显示的文字
//    private StringBuilder word = new StringBuilder();
    //当前总共的行
//    private Vector<String> m_lines = new Vector<>();
//    // 当前页起始位置
//    private long m_mbBufBegin = 0;
//    // 当前页终点位置
//    private long m_mbBufEnd = 0;
//    // 之前页起始位置
//    private long m_preBegin = 0;
//    // 之前页终点位置
//    private long m_preEnd = 0;
    // 图书总长度
//    private long m_mbBufLen = 0;
    private Intent batteryInfoIntent;
    //电池电量百分比
    private float mBatteryPercentage;
    //电池外边框
    private RectF rect1 = new RectF();
    //电池内边框
    private RectF rect2 = new RectF();

    //文件编码
//    private String m_strCharsetName = "GBK";
    //当前是否为第一页
    private boolean m_isfirstPage;

    //当前是否为最后一页
    private boolean m_islastPage;

    //书本widget
    private PageWidget mBookPageWidget;

//    //书本所有段
//    List<String> allParagraph;
//    //书本所有行
//    List<String> allLines = new ArrayList<>();
    //现在的进度
    private float currentProgress;
    //目录
//    private List<BookCatalogue> directoryList = new ArrayList<>();

    //书本路径
    private String bookPath = "";

    //书本名字
    private String bookName = "";

    private BookList bookList;

    //书本章节(章节索引位置)
    private int currentCharter = 0;
    //当前电量
    private int level = 0;

    private ContentProvider mBookUtil;
    private PageEvent mPageEvent;

    private TRPage currentPage;
    private TRPage prePage;
    private TRPage cancelPage;

    private BookTask bookTask;

    ContentValues values = new ContentValues();

    private static Status mStatus = Status.OPENING;

    public enum Status {
        OPENING,
        FINISH,
        FAIL,
    }

    public void oScreenPixelChange(int widthPixel, int heightPixel) {
        if (mBookPageWidget != null) {
            init(mBookPageWidget.getContext());
            currentPage = getPageForBegin(currentPage.getBegin());
            currentPage(true);
        }
    }

    public static synchronized PageFactory getInstance(){
        return pageFactory;
    }

    public static synchronized PageFactory createPageFactory(Context context){
        if (pageFactory == null){
            pageFactory = new PageFactory(context);
        }
        return pageFactory;
    }

    private PageFactory(Context context) {
        init(context);
    }

    private void init(Context context) {
        //mBookUtil = new BookUtil();
        mContext = context.getApplicationContext();
        config = Config.getInstance();
        //获取屏幕宽高
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        mWidth = metric.widthPixels;
        mHeight = metric.heightPixels;

        sdf = new SimpleDateFormat("HH:mm");//HH:mm为24小时制,hh:mm为12小时制
        date = sdf.format(new java.util.Date());
        df = new DecimalFormat("#0.0");

        marginWidth = mContext.getResources().getDimension(R.dimen.readingMarginWidth);
        marginHeight = mContext.getResources().getDimension(R.dimen.readingMarginHeight);
        statusMarginBottom = mContext.getResources().getDimension(R.dimen.reading_status_margin_bottom);
        lineSpace = context.getResources().getDimension(R.dimen.reading_line_spacing);
        paragraphSpace = context.getResources().getDimension(R.dimen.reading_paragraph_spacing);
        mVisibleWidth = mWidth - marginWidth * 2;
        mVisibleHeight = mHeight - marginHeight * 2;

        typeface = config.getTypeface();
        m_fontSize = config.getFontSize();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 画笔
        mPaint.setTextAlign(Paint.Align.LEFT);// 左对齐
        mPaint.setTextSize(m_fontSize);// 字体大小
        mPaint.setColor(m_textColor);// 字体颜色
        mPaint.setTypeface(typeface);
        mPaint.setSubpixelText(true);// 设置该项为true，将有助于文本在LCD屏幕上的显示效果

        waitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 画笔
        waitPaint.setTextAlign(Paint.Align.LEFT);// 左对齐
        waitPaint.setTextSize(mContext.getResources().getDimension(R.dimen.reading_max_text_size));// 字体大小
        waitPaint.setColor(m_textColor);// 字体颜色
        waitPaint.setTypeface(typeface);
        waitPaint.setSubpixelText(true);// 设置该项为true，将有助于文本在LCD屏幕上的显示效果
        calculateLineCount();

        mBorderWidth = mContext.getResources().getDimension(R.dimen.reading_board_battery_border_width);
        mBatterryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBatterryFontSize = CommonUtil.sp2px(context, 12);
        mBatterryPaint.setTextSize(mBatterryFontSize);
        mBatterryPaint.setTypeface(typeface);
        mBatterryPaint.setTextAlign(Paint.Align.LEFT);
        mBatterryPaint.setColor(m_textColor);
        batteryInfoIntent = context.getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED)) ;//注册广播,随时获取到电池电量信息

        initBg(config.getDayOrNight());
        measureMarginWidth();
    }

    public void setContentProvider(ContentProvider contentProvider) {
        this.mBookUtil = contentProvider;
    }

    // 计算实际margin width
    private void measureMarginWidth(){
        float wordWidth =mPaint.measureText("\u3000");
        float width = mVisibleWidth % wordWidth;
        measureMarginWidth = marginWidth + width / 2;

//        Rect rect = new Rect();
//        mPaint.getTextBounds("好", 0, 1, rect);
//        float wordHeight = rect.height();
//        float wordW = rect.width();
//        Paint.FontMetrics fm = mPaint.getFontMetrics();
//        float wrodH = (float) (Math.ceil(fm.top + fm.bottom + fm.leading));
//        String a = "";

    }

    //初始化背景
    private void initBg(Boolean isNight){
        if (isNight) {
            //设置背景
//            setBgBitmap(BitmapUtil.decodeSampledBitmapFromResource(
//                    mContext.getResources(), R.drawable.main_bg, mWidth, mHeight));
            Bitmap bitmap = Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.BLACK);
            setBgBitmap(bitmap);
            //设置字体颜色
            setM_textColor(Color.rgb(128, 128, 128));
            setBookPageBg(Color.BLACK);
        } else {
            //设置背景
            setBookBg(config.getBookBgType());
        }
    }

    //计算可现实行数
    private void calculateLineCount(){
        mLineCount = (int) (mVisibleHeight / (m_fontSize + lineSpace));// 可显示的行数
    }

    //绘制状态文本（正在打开,失败），并且会通知PageWidget刷新页面
    private void drawStatus(Bitmap bitmap){
        String status = "";
        switch (mStatus){
            case OPENING:
                status = "正在打开书本...";
                break;
            case FAIL:
                status = "打开书本失败！";
                break;
        }

        Canvas c = new Canvas(bitmap);
        c.drawBitmap(getBgBitmap(), 0, 0, null);
        waitPaint.setColor(getTextColor());
        waitPaint.setTextAlign(Paint.Align.CENTER);

        Rect targetRect = new Rect(0, 0, mWidth, mHeight);
//        c.drawRect(targetRect, waitPaint);
        Paint.FontMetricsInt fontMetrics = waitPaint.getFontMetricsInt();
        // 转载请注明出处：http://blog.csdn.net/hursing
        int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        waitPaint.setTextAlign(Paint.Align.CENTER);
        c.drawText(status, targetRect.centerX(), baseline, waitPaint);
//        c.drawText("正在打开书本...", mHeight / 2, 0, waitPaint);
        mBookPageWidget.postInvalidate();
    }

    //绘制内容 （结果绘制到bitmap中）
    public void onDraw(Bitmap bitmap, List<String> m_lines, Boolean updateCharter) {
        if (getDirectoryList().size() > 0 && updateCharter) {
            currentCharter = getCurrentCharter();
        }
        //更新数据库进度
        if (currentPage != null && bookList != null){
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    values.put("begin",currentPage.getBegin());
                    DataSupport.update(BookList.class,values,bookList.getId());
                }
            }.start();
        }

        Canvas c = new Canvas(bitmap);
        c.drawBitmap(getBgBitmap(), 0, 0, null);
//        word.setLength(0);
        mPaint.setTextSize(getFontSize());
        mPaint.setColor(getTextColor());
        mBatterryPaint.setColor(getTextColor());
        if (m_lines.size() == 0) {
            return;
        }

        // 绘制所有行文本
        if (m_lines.size() > 0) {
            float y = marginHeight;
            for (String strLine : m_lines) {
                y += m_fontSize + lineSpace;
                c.drawText(strLine, measureMarginWidth, y, mPaint);
//                word.append(strLine);
            }
        }

        //画进度及时间
        int dateWith = (int) (mBatterryPaint.measureText(date)+mBorderWidth);//时间宽度
        float fPercent = (float) (currentPage.getBegin() * 1.0 / mBookUtil.getBookLen());//进度
        currentProgress = fPercent;
        if (mPageEvent != null){
            mPageEvent.changeProgress(fPercent);
        }
        String strPercent = df.format(fPercent * 100) + "%";//进度文字
        int nPercentWidth = (int) mBatterryPaint.measureText("999.9%") + 1;  //Paint.measureText直接返回參數字串所佔用的寬度
        c.drawText(strPercent, mWidth - nPercentWidth, mHeight - statusMarginBottom, mBatterryPaint);//x y为坐标值
        c.drawText(date, marginWidth ,mHeight - statusMarginBottom, mBatterryPaint);
        // 画电池
        level = batteryInfoIntent.getIntExtra( "level" , 0 );
        int scale = batteryInfoIntent.getIntExtra("scale", 100);
        mBatteryPercentage = (float) level / scale;
        float rect1Left = marginWidth + dateWith + statusMarginBottom;//电池外框left位置
        //画电池外框
        float width = CommonUtil.convertDpToPixel(mContext,20) - mBorderWidth;
        float height = CommonUtil.convertDpToPixel(mContext,10);
        rect1.set(rect1Left, mHeight - height - statusMarginBottom,rect1Left + width, mHeight - statusMarginBottom);
        rect2.set(rect1Left + mBorderWidth, mHeight - height + mBorderWidth - statusMarginBottom, rect1Left + width - mBorderWidth, mHeight - mBorderWidth - statusMarginBottom);
        c.save(Canvas.CLIP_SAVE_FLAG);
        c.clipRect(rect2, Region.Op.DIFFERENCE);
        c.drawRect(rect1, mBatterryPaint);
        c.restore();
        //画电量部分
        rect2.left += mBorderWidth;
        rect2.right -= mBorderWidth;
        rect2.right = rect2.left + rect2.width() * mBatteryPercentage;
        rect2.top += mBorderWidth;
        rect2.bottom -= mBorderWidth;
        c.drawRect(rect2, mBatterryPaint);
        //画电池头
        int poleHeight = (int) CommonUtil.convertDpToPixel(mContext,10) / 2;
        rect2.left = rect1.right;
        rect2.top = rect2.top + poleHeight / 4;
        rect2.right = rect1.right + mBorderWidth;
        rect2.bottom = rect2.bottom - poleHeight/4;
        c.drawRect(rect2, mBatterryPaint);
        //画书名
        c.drawText(CommonUtil.subString(bookName,12), marginWidth ,statusMarginBottom + mBatterryFontSize, mBatterryPaint);
        //画章
        if (getDirectoryList().size() > 0) {
            String charterName = CommonUtil.subString(getDirectoryList().get(currentCharter).getBookCatalogue(),12);
            int nChaterWidth = (int) mBatterryPaint.measureText(charterName) + 1;
            c.drawText(charterName, mWidth - marginWidth - nChaterWidth, statusMarginBottom  + mBatterryFontSize, mBatterryPaint);
        }

        mBookPageWidget.postInvalidate();
    }

   //向前翻页 - （会进行绘制）
    public void prePage(){
        if (currentPage.getBegin() <= 0) {
            Log.e(TAG,"当前是第一页");
            if (!m_isfirstPage){
                Toast.makeText(mContext, "当前是第一页", Toast.LENGTH_SHORT).show();
            }
            m_isfirstPage = true;
            return;
        } else {
            m_isfirstPage = false;
        }

        cancelPage = currentPage;
        onDraw(mBookPageWidget.getCurPage(),currentPage.getLines(),true); //绘制当前页
        currentPage = getPrePage();
        onDraw(mBookPageWidget.getNextPage(),currentPage.getLines(),true); //绘制下一页
    }

    //向后翻页 - （会进行绘制）
    public void nextPage(){
        if (currentPage.getEnd() >= mBookUtil.getBookLen()) {
            Log.e(TAG,"已经是最后一页了");
            if (!m_islastPage){
                Toast.makeText(mContext, "已经是最后一页了", Toast.LENGTH_SHORT).show();
            }
            m_islastPage = true;
            return;
        } else {
            m_islastPage = false;
        }

        cancelPage = currentPage;
        onDraw(mBookPageWidget.getCurPage(),currentPage.getLines(),true);
        prePage = currentPage;
        currentPage = getNextPage(); //获取下一页内容
        onDraw(mBookPageWidget.getNextPage(),currentPage.getLines(),true);
        Log.e("nextPage","nextPagenext");
    }

    //取消翻页 - (只是更新属性）
    public void cancelPage(){
        currentPage = cancelPage;
    }

    /**
     * 打开书本
     * @throws IOException
     */
    public void openBook(BookList bookList) throws IOException {
        //清空数据
        currentCharter = 0;
//        m_mbBufLen = 0;
        initBg(config.getDayOrNight()); //初始化PageWidget背景

        this.bookList = bookList;
        bookPath = bookList.getBookpath();
        bookName = bookList.getBookname();
        if (TextUtils.isEmpty(bookName)) {
            bookName = FileUtils.getFileName(bookPath);
        }

        mStatus = Status.OPENING;
        drawStatus(mBookPageWidget.getCurPage());
        drawStatus(mBookPageWidget.getNextPage());

        if (bookTask != null && bookTask.getStatus() != AsyncTask.Status.FINISHED){
            bookTask.cancel(true); //取消之前的加载
        }

        bookTask = new BookTask();
        bookTask.execute(bookList.getBegin());
    }

    public String getBookName() {
        return bookName;
    }

    private class BookTask extends AsyncTask<Long,Void,Boolean> {
        private long begin = 0;
        @Override
        protected void onPostExecute(Boolean result) { //之后执行
            super.onPostExecute(result);
            Log.e("onPostExecute",isCancelled() + "");
            if (isCancelled()){
                return;
            }

            if (result) { //加载成功
                PageFactory.mStatus = PageFactory.Status.FINISH;
//                m_mbBufLen = mBookUtil.getBookLen();
                currentPage = getPageForBegin(begin);   //获取起始页
                if (mBookPageWidget != null) {
                    currentPage(true);
                }
            }else{  //加载失败
                PageFactory.mStatus = PageFactory.Status.FAIL;
                drawStatus(mBookPageWidget.getCurPage());
                drawStatus(mBookPageWidget.getNextPage());
                Toast.makeText(mContext,"打开书本失败！", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(Long... params) {
            begin = params[0];
            try {
                mBookUtil.openBook(bookList); //BookUtil负责加载书籍(加载整本书籍)
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

    }

    // 获取下一页
    public TRPage getNextPage(){
        mBookUtil.setPostition(currentPage.getEnd());

        TRPage trPage = new TRPage();
        trPage.setBegin(currentPage.getEnd() + 1);
        Log.e("begin",currentPage.getEnd() + 1 + "");
        trPage.setLines(getNextLines());
        Log.e("end",mBookUtil.getPosition() + "");
        trPage.setEnd(mBookUtil.getPosition());
        return trPage;
    }

    // 获取前一页
    public TRPage getPrePage(){
        mBookUtil.setPostition(currentPage.getBegin());

        TRPage trPage = new TRPage();
        trPage.setEnd(mBookUtil.getPosition() - 1);
        Log.e("end",mBookUtil.getPosition() - 1 + "");
        trPage.setLines(getPreLines());
        Log.e("begin",mBookUtil.getPosition() + "");
        trPage.setBegin(mBookUtil.getPosition());
        return trPage;
    }

    // 获取指定页面（起始位置begin)
    public TRPage getPageForBegin(long begin){
        TRPage trPage = new TRPage();
        trPage.setBegin(begin);

        mBookUtil.setPostition(begin - 1);
        trPage.setLines(getNextLines());
        trPage.setEnd(mBookUtil.getPosition());
        return trPage;
    }

    // 获取下一页中的所有行（按可见页宽进行分行）
    public List<String> getNextLines(){
        List<String> lines = new ArrayList<>();
        float width = 0;
        float height = 0;
        String line = "";

        while (mBookUtil.next(true) != -1){
            char word = (char) mBookUtil.next(false);
            //判断是否换行
            if ((word + "" ).equals("\r") && (((char) mBookUtil.next(true)) + "").equals("\n")){
                mBookUtil.next(false);
                if (!line.isEmpty()){
                    lines.add(line);
                    line = "";
                    width = 0;
//                    height +=  paragraphSpace;
                    if (lines.size() == mLineCount){
                        break;
                    }
                }
            }else {
                float widthChar = mPaint.measureText(word + "");
                width += widthChar;
                if (width > mVisibleWidth) {
                    width = widthChar;
                    lines.add(line); //添加之前的行
                    line = word + "";
                } else {
                    line += word;
                }
            }

            // 检测高度是否已经正好存放一页了
            if (lines.size() == mLineCount){
                if (!line.isEmpty()){
                    mBookUtil.setPostition(mBookUtil.getPosition() - 1); //移除夺取的内容（留下次再取）
                }
                break;
            }
        }

        // 当取到整本书的最后内容
        if (!line.isEmpty() && lines.size() < mLineCount){
            lines.add(line);
        }

        for (String str : lines){
            Log.e(TAG,str + "   ");
        }
        return lines;
    }

    // 获取前一页中的所有内容（按可见页宽度来进行分行）
    public List<String> getPreLines(){
        List<String> lines = new ArrayList<>();
        float width = 0;
        String line = "";

        char[] par = mBookUtil.preLine();   //获取当前位置到前一行之间的内容
        while (par != null){
            List<String> preLines = new ArrayList<>();
            for (int i = 0 ; i < par.length ; i++){
                char word = par[i];
                float widthChar = mPaint.measureText(word + "");
                width += widthChar;
                if (width > mVisibleWidth) { //按照可见宽度进行分行
                    width = widthChar;
                    preLines.add(line); //添加前一行内容
                    line = word + "";
                } else {
                    line += word;
                }
            }
            if (!line.isEmpty()){ //添加最后一行内容
                preLines.add(line);
            }

            lines.addAll(0,preLines);

            //如果行高度超过一页所能存储的最大高度，则停止分行
            //这儿其实有个问题：如果TRPage中的行有可能大于一页中能够显示的行
            //应该有算法将超出的部分以到前一行中，或者重新更新BookUtil中的位置
            if (lines.size() >= mLineCount){
                break;
            }
            width = 0;
            line = "";
            par = mBookUtil.preLine();
        }

        // 这儿就是解决上面的疑问
        List<String> reLines = new ArrayList<>();
        int num = 0;
        for (int i = lines.size() -1;i >= 0;i --){
            if (reLines.size() < mLineCount) {
                reLines.add(0,lines.get(i));
            }else{
                num = num + lines.get(i).length(); //求出多加载的部分
            }
            Log.e(TAG,lines.get(i) + "   ");
        }

        if (num > 0){
            if ( mBookUtil.getPosition() > 0) { //没有到行首
                mBookUtil.setPostition(mBookUtil.getPosition() + num + 2); //+2因为有\r\n，所以占两个字符
            }else{
                mBookUtil.setPostition(mBookUtil.getPosition() + num );
            }
        }

        return reLines;
    }

    // 跳到前一章节
    public void preChapter(){
        if (mBookUtil.getDirectoryList().size() > 0){
            int num = currentCharter;
            if (num ==0){
                num =getCurrentCharter();
            }
            num --;
            if (num >= 0){
                long begin = mBookUtil.getDirectoryList().get(num).getBookCatalogueStartPos();
                currentPage = getPageForBegin(begin);
                currentPage(true);
                currentCharter = num;
            }
        }
    }

    // 跳到下一章节
    public void nextChapter(){
        int num = currentCharter;
        if (num == 0){
            num =getCurrentCharter();
        }
        num ++;
        if (num < getDirectoryList().size()){
            long begin = getDirectoryList().get(num).getBookCatalogueStartPos();
            currentPage = getPageForBegin(begin);
            currentPage(true);
            currentCharter = num;
        }
    }

    //获取现在的章(根据当前页来查找所在的章节)
    public int getCurrentCharter(){
        int num = 0;
        for (int i = 0;getDirectoryList().size() > i;i++){
            BookCatalogue bookCatalogue = getDirectoryList().get(i);
            if (currentPage.getEnd() >= bookCatalogue.getBookCatalogueStartPos()){
                num = i;
            }else{
                break;
            }
        }
        return num;
    }

    //绘制当前页面
    public void currentPage(Boolean updateChapter){
        onDraw(mBookPageWidget.getCurPage(),currentPage.getLines(),updateChapter);
        onDraw(mBookPageWidget.getNextPage(),currentPage.getLines(),updateChapter);
    }

    //更新电量
    public void updateBattery(int mLevel){
        if (currentPage != null && mBookPageWidget != null && !mBookPageWidget.isRunning()) {
            if (level != mLevel) {
                level = mLevel;
                currentPage(false);
            }
        }
    }

    // 更新时间
    public void updateTime(){
        if (currentPage != null && mBookPageWidget != null && !mBookPageWidget.isRunning()) {
            String mDate = sdf.format(new java.util.Date());
            if (date != mDate) {
                date = mDate;
                currentPage(false);
            }
        }
    }

    //改变进度
    public void changeProgress(float progress){
        long begin = (long) (mBookUtil.getBookLen() * progress); //通过小说所有内容字符长度来快速定位
        currentPage = getPageForBegin(begin);
        currentPage(true);
    }

    //改变章节（参数应该是目录起始位置）
    public void changeChapter(long begin){
        currentPage = getPageForBegin(begin);
        currentPage(true);
    }

    //改变字体大小（重新更新当前页面）
    public void changeFontSize(int fontSize){
        this.m_fontSize = fontSize;
        mPaint.setTextSize(m_fontSize);
        calculateLineCount();
        measureMarginWidth();
        currentPage = getPageForBegin(currentPage.getBegin());
        currentPage(true);
    }

    //改变字体
    public void changeTypeface(Typeface typeface){
        this.typeface = typeface;
        mPaint.setTypeface(typeface);
        mBatterryPaint.setTypeface(typeface);
        calculateLineCount();
        measureMarginWidth();
        currentPage = getPageForBegin(currentPage.getBegin());
        currentPage(true);
    }

    //改变背景
    public void changeBookBg(int type){
        setBookBg(type);
        currentPage(false);
    }

    //设置页面的背景
    public void setBookBg(int type){
        Bitmap bitmap = Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        int color = 0;
        switch (type){
            case Config.BOOK_BG_DEFAULT:
                canvas = null;
                bitmap.recycle();
                if (getBgBitmap() != null) {
                    getBgBitmap().recycle();
                }
                bitmap = BitmapUtil.decodeSampledBitmapFromResource(
                        mContext.getResources(), R.drawable.paper, mWidth, mHeight);
                color = mContext.getResources().getColor(R.color.read_font_default);
                setBookPageBg(mContext.getResources().getColor(R.color.read_bg_default));
                break;
            case Config.BOOK_BG_1:
                canvas.drawColor(mContext.getResources().getColor(R.color.read_bg_1));
                color = mContext.getResources().getColor(R.color.read_font_1);
                setBookPageBg(mContext.getResources().getColor(R.color.read_bg_1));
                break;
            case Config.BOOK_BG_2:
                canvas.drawColor(mContext.getResources().getColor(R.color.read_bg_2));
                color = mContext.getResources().getColor(R.color.read_font_2);
                setBookPageBg(mContext.getResources().getColor(R.color.read_bg_2));
                break;
            case Config.BOOK_BG_3:
                canvas.drawColor(mContext.getResources().getColor(R.color.read_bg_3));
                color = mContext.getResources().getColor(R.color.read_font_3);
                if (mBookPageWidget != null) {
                    mBookPageWidget.setBgColor(mContext.getResources().getColor(R.color.read_bg_3));
                }
                break;
            case Config.BOOK_BG_4:
                canvas.drawColor(mContext.getResources().getColor(R.color.read_bg_4));
                color = mContext.getResources().getColor(R.color.read_font_4);
                setBookPageBg(mContext.getResources().getColor(R.color.read_bg_4));
                break;
        }

        setBgBitmap(bitmap);
        //设置字体颜色
        setM_textColor(color);
    }

    //设置背景色
    public void setBookPageBg(int color){
        if (mBookPageWidget != null) {
            mBookPageWidget.setBgColor(color);
        }
    }
    //设置日间或者夜间模式
    public void setDayOrNight(Boolean isNgiht){
        initBg(isNgiht);
        currentPage(false); //重新绘制当前页
    }

    // 清除内部所有状态
    public void clear(){
        currentCharter = 0;
        bookPath = "";
        bookName = "";
        bookList = null;
        mBookPageWidget = null;
        mPageEvent = null;
        cancelPage = null;
        prePage = null;
        currentPage = null;
    }

    public static Status getStatus(){
        return mStatus;
    }

    public long getBookLen(){
        return mBookUtil.getBookLen();
    }

    public TRPage getCurrentPage(){
        return currentPage;
    }

    //获取书本的章
    public List<BookCatalogue> getDirectoryList(){
        return mBookUtil.getDirectoryList();
    }

    public String getBookPath(){
        return bookPath;
    }

    //是否是第一页
    public boolean isfirstPage() {
        return m_isfirstPage;
    }
    //是否是最后一页
    public boolean islastPage() {
        return m_islastPage;
    }
    //设置页面背景
    public void setBgBitmap(Bitmap BG) {
        m_book_bg = BG;
    }
    //设置页面背景
    public Bitmap getBgBitmap() {
        return m_book_bg;
    }
    //设置文字颜色
    public void setM_textColor(int m_textColor) {
        this.m_textColor = m_textColor;
    }
    //获取文字颜色
    public int getTextColor() {
        return this.m_textColor;
    }
    //获取文字大小
    public float getFontSize() {
        return this.m_fontSize;
    }

    public void setPageWidget(PageWidget mBookPageWidget){
        this.mBookPageWidget = mBookPageWidget;
    }

    public void setPageEvent(PageEvent pageEvent){
        this.mPageEvent = pageEvent;
    }

    public interface PageEvent{
        void changeProgress(float progress);
    }

}
