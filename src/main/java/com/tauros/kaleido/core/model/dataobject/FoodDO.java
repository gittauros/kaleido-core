package com.tauros.kaleido.core.model.dataobject;

import java.math.BigDecimal;

/**
 * Created by zhy on 2017/11/5.
 */
public class FoodDO {

    /**
     * 名称
     */
    private String     name;
    /**
     * 蛋白质每百克含量（克）
     */
    private BigDecimal protein;
    /**
     * 脂肪每百克含量（克）
     */
    private BigDecimal fat;
    /**
     * 碳水每百克含量（克）
     */
    private BigDecimal carbohydrate;
    /**
     * 每百克热量（千卡）
     */
    private BigDecimal calorie;
    /**
     * 纤维素每百克含量（克）
     */
    private BigDecimal dietaryFiber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getProtein() {
        return protein;
    }

    public void setProtein(BigDecimal protein) {
        this.protein = protein;
    }

    public BigDecimal getFat() {
        return fat;
    }

    public void setFat(BigDecimal fat) {
        this.fat = fat;
    }

    public BigDecimal getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(BigDecimal carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public BigDecimal getCalorie() {
        return calorie;
    }

    public void setCalorie(BigDecimal calorie) {
        this.calorie = calorie;
    }

    public BigDecimal getDietaryFiber() {
        return dietaryFiber;
    }

    public void setDietaryFiber(BigDecimal dietaryFiber) {
        this.dietaryFiber = dietaryFiber;
    }

    public FoodDO obtain() {
        FoodDO food = new FoodDO();
        food.name = name;
        food.protein = protein;
        food.fat = fat;
        food.carbohydrate = carbohydrate;
        food.calorie = calorie;
        food.dietaryFiber = dietaryFiber;
        return food;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FoodDO food = (FoodDO) o;

        return name.equals(food.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
