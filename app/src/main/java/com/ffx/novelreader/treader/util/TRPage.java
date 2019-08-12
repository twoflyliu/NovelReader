package com.ffx.novelreader.treader.util;

import java.util.List;

/**
 * Created by Administrator on 2016/8/11 0011.
 */
public class TRPage {
    private long begin;             //当前页在整个文件中的偏移起始位置
    private long end;               //当前页在整个文件中的偏移终止位置
    private List<String> lines;     //当前页所有的行内容

    public long getBegin() {
        return begin;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public List<String> getLines() {
        return lines;
    }

    public String getLineToString(){
        String text ="";
        if (lines != null){
            for (String line : lines){
                text += line;
            }
        }
        return text;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }
}
