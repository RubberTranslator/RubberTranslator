package com.rubbertranslator.modules.history;

import java.util.LinkedList;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/9 10:32
 * XXX: 可以自己手写一个数据结构，加入cache机制，加快查询速度
 */
public class LimitedLinkedList<T> extends LinkedList<T> {
    private int maxCapacity;

    public LimitedLinkedList(int maxCapacity) {
        assert maxCapacity > 0 : "capacity should > 0";
        this.maxCapacity = maxCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int newCapacity) {
        if(newCapacity < this.size()){
            // remove前  this.size() - newCapacity各元素
            removeRange(0,this.size()-newCapacity);
        }
        this.maxCapacity = newCapacity;
    }

    // 只能追加
    public void append(T t) {
        if (this.size() == maxCapacity) {
            this.remove();
        }
        this.add(t);
    }


}
