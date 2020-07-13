package com.walker.design.factory;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 原料 面团 dough
 */
public abstract class IngredientDough {

    protected String getName(){
        return getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return getName();
    }
}



