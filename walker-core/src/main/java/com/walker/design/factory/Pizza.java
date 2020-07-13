package com.walker.design.factory;


import com.walker.design.observe.DataCenterObservable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *  工厂模式        抽象工厂模式 (工厂基础上再抽象?)
 *
 *  披萨店问题   类别扩展问题
 *  pizzaStore
 *
 * newyork      chicago
 * 每个城市都有两种款式的披萨
 * cheese  clam
 * 每个城市相同款式披萨原料不同
 * ingredientSauce     ingredientDough
 *
 *
 *
 */
public abstract class Pizza {
    private static Logger log = LoggerFactory.getLogger(Pizza.class);

    /**
     * 名字
     */
    String name;
    /**
     *使用什么面团
     */
    IngredientDough ingredientDough;
    /**
     * 加多少盐
     */
    IngredientSauce ingredientSauce;

    /**
     * 额外操作
     */
    List<String> toppings = new ArrayList<>();

    @Override
    public String toString() {
        return "Pizza{" +
                "name='" + name + '\'' +
                ", ingredientDough=" + ingredientDough +
                ", ingredientSauce=" + ingredientSauce +
                '}';
    }

    //    abstract void prepare();
    void prepare(){
        log.info("prepare " + this.toString());
        for(String topp : toppings){
            log.info("\t" + topp);
        }
    }
    void bake(){
        log.info("bake " + name + "");
    }
    void cut(){
        log.info("cut " + name + "");
    }
    void box(){
        log.info("box " + name + "");
    }


    public String getName() {
        return name;
    }

    public Pizza setName(String name) {
        this.name = name;
        return this;
    }

    public IngredientDough getIngredientDough() {
        return ingredientDough;
    }

    public Pizza setIngredientDough(IngredientDough ingredientDough) {
        this.ingredientDough = ingredientDough;
        return this;
    }

    public IngredientSauce getIngredientSauce() {
        return ingredientSauce;
    }

    public Pizza setIngredientSauce(IngredientSauce ingredientSauce) {
        this.ingredientSauce = ingredientSauce;
        return this;
    }

    public List<String> getToppings() {
        return toppings;
    }

    public Pizza setToppings(List<String> toppings) {
        this.toppings = toppings;
        return this;
    }

    public Pizza addToppings(String topping) {
        this.toppings.add(topping);
        return this;
    }

}



