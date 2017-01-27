package com.ashervardi.diabetesapp2;

/**
 * Created by Asher on 1/17/2017.
 */

public class InitData {
    private int _from, _to, _val;

    public InitData(int from, int to, int val){
        this._from = from;
        this._to = to;
        this._val = val;
    }

    public int get_from(){
        return this._from;
    }
    public int get_to(){
        return this._to;
    }
    public int get_val(){
        return this._val;
    }
}
