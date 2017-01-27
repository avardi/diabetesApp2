package com.ashervardi.diabetesapp2;

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
}
