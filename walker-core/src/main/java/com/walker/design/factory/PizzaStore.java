package com.walker.design.factory;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 披萨店
 */
public abstract class PizzaStore {
    private static Logger log = LoggerFactory.getLogger(PizzaStore.class);

    /**
     * 默认原料工厂newyork
     * @return
     */
    public IngredientFactory getIngredientFactory(){
        return new IngredientFactoryNewyork();
    }

    public Pizza createPizza(String style){
        Pizza pizza = null;

        IngredientFactory ingredientFactory = getIngredientFactory();
        switch (style){
            case "clam":
                pizza = new PizzaClam(ingredientFactory);
                break;
            case "cheese":
            default:
                pizza = new PizzaCheese(ingredientFactory);
                break;
        }

        pizza.prepare();
        pizza.bake();
        pizza.cut();
        pizza.box();


        log.info("createPizza ok " + style + " " + pizza);
        log.info("----------------------");
        return pizza;
    }



}



