package com.zxj.hashmap;


public class MyClass {

    public static void main(String[] args) {
        MyHashMap myHashMap = new MyHashMap();
        for (int i= 0 ; i< 100 ;i++){
            myHashMap.put("key "+i,"value "+i);
        }

    }
}
