package com.tauros.kaleido.core.algorithm.fitness;

import org.apache.http.util.Asserts;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhy on 2017/11/5.
 *
 * 食物目标设置
 */
public class FoodSet {

    private final Map<String, BigDecimal> maxWeight = new HashMap<>();
    /**
     * 目标蛋白质（克）
     */
    private final BigDecimal protein;
    /**
     * 目标脂肪（克）
     */
    private final BigDecimal fat;
    /**
     * 目标碳水（克）
     */
    private final BigDecimal carbohydrate;
    /**
     * 目标热量（千卡）
     */
    private final BigDecimal calorie;
    /**
     * 方差得分比率
     */
    private final BigDecimal factRatio;
    /**
     * 纤维素除数
     */
    private final Double dietaryScoreNerf;

    public FoodSet(BigDecimal protein, BigDecimal fat, BigDecimal carbohydrate, BigDecimal calorie, BigDecimal factRatio, Double dietaryScoreNerf) {
        this.protein = protein;
        this.fat = fat;
        this.carbohydrate = carbohydrate;
        this.calorie = calorie;
        this.factRatio = factRatio;
        BigDecimal targetCalorie = calorieCalculate(this.protein, this.fat, this.carbohydrate);
        BigDecimal maxCalorie = targetCalorie.multiply(new BigDecimal(1.05));
        BigDecimal minCalorie = targetCalorie.multiply(new BigDecimal(0.95));
        Asserts.check(targetCalorie.compareTo(minCalorie) >= 0 && targetCalorie.compareTo(maxCalorie) <= 0, "热量设置不合理");
        if (dietaryScoreNerf == null) {
            this.dietaryScoreNerf = 7d;
        } else {
            this.dietaryScoreNerf = dietaryScoreNerf;
        }
        Asserts.check(this.dietaryScoreNerf > 0 || Math.abs(this.dietaryScoreNerf - 0) < 1e-8, "纤维素除数必须大于1");
    }

    public static boolean isLegal(BigDecimal value, BigDecimal expect, double ratio) {
        BigDecimal max = expect.multiply(new BigDecimal(ratio));
        return max.compareTo(value) >= 0;
    }

    public static BigDecimal calorieCalculate(BigDecimal protein, BigDecimal fat, BigDecimal carbohydrate) {
        Asserts.notNull(protein, "protein is null");
        Asserts.notNull(fat, "fat is null");
        Asserts.notNull(carbohydrate, "carbohydrate is null");
        return protein.add(carbohydrate).multiply(new BigDecimal(4)).add(fat.multiply(new BigDecimal(9)));
    }

    public BigDecimal getProtein() {
        return protein;
    }

    public BigDecimal getFat() {
        return fat;
    }

    public BigDecimal getCarbohydrate() {
        return carbohydrate;
    }

    public BigDecimal getCalorie() {
        return calorie;
    }

    public BigDecimal getFactRatio() {
        return factRatio;
    }

    public Double getDietaryScoreNerf() {
        return dietaryScoreNerf;
    }

    public void setMaxWeight(String name, BigDecimal weight) {
        maxWeight.put(name, weight);
    }

    public BigDecimal getMaxWeight(String name) {
        return maxWeight.get(name);
    }
}
