package com.ffx.novelreader.entity.vo;

import com.ffx.novelreader.entity.po.Novel;

/**
 * Created by TwoFlyLiu on 2019/8/7.
 */

public class NovelDownloadProgressVo {
    private Novel novel;
    private int progress;

    NovelDownloadProgressVo() {

    }

    public NovelDownloadProgressVo(Novel novel, int progress) {
        this.novel = novel;
        this.progress = progress;
    }

    public Novel getNovel() {
        return novel;
    }

    public void setNovel(Novel novel) {
        this.novel = novel;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
