package com.example.lib.util;

/**
 * 問い合わせの成否をカウントするための簡易フレームワーク.
 */
public final class Counter {

    public int notfound = 0;
    public int match = 0;
    public int unmatch = 0;

    public Counter() {
    }

    public Counter(Counter src) {
        add(src);
    }


    public void reset() {
        this.notfound = 0;
        this.match = 0;
        this.unmatch = 0;
    }

    public void add(Counter target) {
        this.notfound += target.notfound;
        this.match += target.match;
        this.unmatch += target.unmatch;
    }

    public int getFound() {
        return this.match + this.unmatch;
    }

    public int getTotal() {
        return this.notfound + this.match + this.unmatch;
    }

    public float getKeyFoundPercent() {
        return this.getFound() * 100f / this.getTotal();
    }

    public float getValueMatchPercent() {
        return this.match * 100f / this.getFound();
    }

}
