package com.rubbertranslator.modules.history;

import java.util.LinkedList;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/9 10:32
 * XXX: 可以自己手写一个数据结构，加入cache机制，加快查询速度
 */
public class LimitedLinkedList<T> extends LinkedList<T> {
    private int capacity;

    public LimitedLinkedList(int capacity) {
        assert capacity > 0 : "capacity should > 0";
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    // 只能追加
    public void append(T t) {
        if (this.size() == capacity) {
            this.remove();
        }
        this.add(t);
    }

}
