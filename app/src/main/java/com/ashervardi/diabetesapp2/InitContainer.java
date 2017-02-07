package com.ashervardi.diabetesapp2;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by Asher on 1/17/2017.
 * simple container to hold initialization data
 */

public class InitContainer {
    private String _Name;
    private ArrayList _list ;
    private int _length;

    public InitContainer(String name) {
        // This constructor has one parameter, name.
        this._Name = name;
        this._length = 0;
        this._list = new ArrayList();    }

    public String getName (){
        return this._Name;
    }

    public void insert(int from, int to, int val){
        InitData d = new InitData(from, to,val);
        this._list.add(d);
        this._length = _list.size();

    }

    public Iterator getIterator (){
        return this._list.iterator();
    }

    public void delete (){
        this._list.clear();
        this._length = 0;
    }

    public boolean checkCoverage(){
        int covered [] = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        // load data
        Iterator it = this.getIterator();
        while(it.hasNext()){
            InitData d = (InitData) it.next();
            int from = d.get_from();
            int to = d.get_to();
            if (to > from) {
                for (int i = from; i < to; i++) {
                    covered[i] = 1;
                }
            } else {
                for (int i = 0; i < from; i++) {
                    covered[i] = 1;
                }
                for (int i = from; i < 24; i++) {
                    covered[i] = 1;
                }
            }
        }
        int sum = 0;
        for (int i = 0; i < covered.length; i++){
            sum += covered[i];
        }
        if(sum == 24){
            return true;
        } else return false;
    }
}
