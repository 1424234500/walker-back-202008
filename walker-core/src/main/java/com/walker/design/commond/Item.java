package com.walker.design.commond;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 命令模式
 *              多插槽开关会滚问题
 *
 *
 */
public abstract class Item {
    Logger log = LoggerFactory.getLogger(Item.class);

    public void on() {
        log.info(this.toString() + " on");
    }


    public void off() {
        log.info(this.toString() + " off");
    }



    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
