package com.aquino.webParser;

public class ProgressData{
    public ProgressData(int start, int current, int end) {
        this.start = start;
        this.current = current;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    private int start,current,end;
}