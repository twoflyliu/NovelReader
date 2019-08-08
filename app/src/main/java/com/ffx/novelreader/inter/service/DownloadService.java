package com.ffx.novelreader.inter.service;

import com.ffx.novelreader.entity.po.Novel;

/**
 * Created by TwoFlyLiu on 2019/8/7.
 */

public interface DownloadService {
    void downloadAndSave(Novel novel);

    void setOnDownloadProgressChangeListener(OnDownloadProgressChangeListener listener);

    void updateAndSave(Novel novel);

    interface OnDownloadProgressChangeListener {
        /**
         * 当下载进度变化的时候值调用此方法
         * @param novel 当前正在下载的小说
         * @param currentValue 当已经下载的章节数目
         * @param totalValue 总的章节数目
         */
        void onDownloadProgressChange(Novel novel, int currentValue, int totalValue);

        /**
         * 当下载完成的时候调用此方法
         * @param novel 下载完成的小说
         */
        void onDownloadDone(Novel novel);
    }
}
