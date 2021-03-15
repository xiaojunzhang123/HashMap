package com.zxj.hashmap;

import java.util.HashMap;

import javax.swing.tree.TreeNode;

public class MyHashMap<K, V> implements Map<K, V> {

    static final int TREEIFY_THRESHOLD = 8;

    static final int MIN_TREEIFY_CAPACITY = 64;

    // 当红黑树中的元素小于等于6 时，红黑树转链表
    static final int UNTREEIFY_THRESHOLD = 6;

    //扩容因子，只有当集合需要扩容时，该值才会起作用
    float loadFactor = 0.75f;

    //数组的容量
    static int threshold = 0;

    //实际用到table 存储容量 大小
    Node[] table = null;

    int size;

    public MyHashMap() {
        //左移4位，数组的初始容量为 16
        threshold = 1 << 4;
    }

    public MyHashMap(int initialCapacity, float loadFactor) {
        this.loadFactor = loadFactor;
        threshold = tableSizeFor(initialCapacity);
    }

    /**
     * 函数的作用是通过位移得到最接近cap 的2次幂的数
     * 这里有一个注意点，这里为什么不通过逻辑运算得到cap的2次幂的数，而是通过位运算，因为计算机是2进制，通过位运算查找2次幂的数效率最高
     *
     * @param cap
     * @return
     */
    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return n + 1;
    }

    @Override
    public V put(K key, V value) {
        synchronized (MyHashMap.class) {
            if (table == null) {
                table = new Node[threshold];
            }
        }
        if (size >= threshold * loadFactor) {
            System.out.println("到插入第" + size + "个时发生扩容");
            //扩容发生在插入之前
            resize();
        }

        //通过key获取要放入元素的小标
        int index = hash(key) & (threshold - 1);

        //在放入新数组之前，看新数组对应的小标上是否又元素，如果有的话
        Node<K, V> originNode = table[index];

        //下面这个操作在多线程下不安全，会发生循环引用
        if (originNode == null) {
            originNode = new Node<>(key, value, null);
            size++;
        } else {
            Node<K, V> currentNode = originNode;

            if (currentNode instanceof TreeNode){
                //如果当前元素是一个红黑树，就直接插入


            }else {
                int binCount = 0;
                //如果当前元素是一个单链表
                while (currentNode != null) {
                    binCount++;
                    if (currentNode.getKey().equals(key)) {
                        return currentNode.setValue(value);
                    } else {
                        if (currentNode.next == null) {
                            //将新插入的元素采用遍历列表的尾插法进行插入
                            originNode = new Node<>(key, value, originNode);
                            size++;
                        }
                    }
                    currentNode = currentNode.next;
                }
                //如果插入列表后的长度大于等于8，就进行将单链表转成红黑树
                if (binCount >= TREEIFY_THRESHOLD -1){
                    treeifyBin(table);
                }
            }
        }
        table[index] = originNode;
        return null;
    }

    private void treeifyBin(Node<K,V>[] tab){
        if (tab.length < MIN_TREEIFY_CAPACITY){
            //进行扩容
            //resize();
        }
    }

    private void resize() {
        //扩容为原来数组的两倍
        threshold = threshold << 1;

        Node<K, V>[] newTable = new Node[threshold];
        //遍历之前的旧素组.作用是重新计算旧数组里的元素下标并重新放入新数组
        for (int i = 0; i < table.length; i++) {
            Node<K, V> oldNode = table[i];

            while (oldNode != null) {
                Node<K, V> oldNext = oldNode.next;
                int index = hash(oldNode.getKey()) & (threshold - 1);
                oldNode.next = newTable[index];
                newTable[index] = oldNode;
                oldNode = oldNext;
            }
        }

        table = newTable;
        threshold = newTable.length;
    }

    @Override
    public V get(K key) {
        Node<K, V> node = getNode(table[hash(key.hashCode() & (threshold - 1))], key);
        if (node != null) {
            return node.getValue();
        }
        return null;
    }

    public Node getNode(Node<K, V> node, K k) {
        while (node != null) {
            if (node.getKey().equals(k) || node.getKey() == k) {
                return node;
            }
            node = node.next;
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    public void print() {
        for (int i = 0; i < table.length; i++) {
            Node<K, V> node = table[i];
            System.out.print("下标位置[" + i + "]");
            while (node != null) {
                System.out.print("[ key:" + node.getKey() + ",value:" + node.getValue() + "]");
                node = node.next;

            }
            System.out.println();
        }
        System.out.println("当前数组的长度为："+threshold);
    }

    /**
     * 通过将key的 hashCode 值 的高16 位与低16位进行异或运算，得到code 值 ，这么做的目的是让生成的小标值更加均匀分布
     *
     * @param key
     * @return
     */
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

}