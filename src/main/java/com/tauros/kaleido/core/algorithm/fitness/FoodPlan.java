package com.tauros.kaleido.core.algorithm.fitness;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tauros.kaleido.core.model.dataobject.FoodDO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zhy on 2017/11/5.
 *
 * 饮食计划
 */
public class FoodPlan {

    private List<FoodNode> foodNodes;
    private BigDecimal     protein;
    private BigDecimal     fat;
    private BigDecimal     carbohydrate;
    private BigDecimal     calorie;
    private BigDecimal     dietaryFiber;
    private BigDecimal     fact;
    private BigDecimal     factRatio;
    private String         tag;

    public FoodPlan() {
        this.foodNodes = new ArrayList<>();
        this.calculate();
    }

    public void calculate() {
        this.protein = BigDecimal.ZERO;
        this.fat = BigDecimal.ZERO;
        this.carbohydrate = BigDecimal.ZERO;
        this.calorie = BigDecimal.ZERO;
        this.dietaryFiber = BigDecimal.ZERO;
        BigDecimal avaWeight = BigDecimal.ZERO;
        for (FoodNode foodNode : foodNodes) {
            avaWeight = avaWeight.add(foodNode.getWeight());
            this.protein = this.protein.add(foodNode.transWeight().multiply(foodNode.getFood().getProtein()));
            this.fat = this.fat.add(foodNode.transWeight().multiply(foodNode.getFood().getFat()));
            this.carbohydrate = this.carbohydrate.add(foodNode.transWeight().multiply(foodNode.getFood().getCarbohydrate()));
            this.calorie = this.calorie.add(foodNode.transWeight().multiply(foodNode.getFood().getCalorie()));
            this.dietaryFiber = this.dietaryFiber.add(foodNode.transWeight().multiply(foodNode.getFood().getDietaryFiber()));
        }
        if (this.foodNodes.size() > 1) {
            avaWeight = avaWeight.divide(new BigDecimal(foodNodes.size()), 4, BigDecimal.ROUND_HALF_UP);
            BigDecimal fact = BigDecimal.ZERO;
            for (FoodNode foodNode : foodNodes) {
                fact = fact.add(foodNode.getWeight().subtract(avaWeight).divide(new BigDecimal(100)).pow(2));
            }
            fact = fact.divide(new BigDecimal(foodNodes.size()), 4, BigDecimal.ROUND_HALF_UP);
            this.fact = fact;
        }
    }

    public int findFood(String name) {
        for (int i = 0; i < this.foodNodes.size(); i++) {
            if (this.foodNodes.get(i).getFood().getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public boolean contains(String name) {
        return findFood(name) != -1;
    }

    public void addFood(FoodDO food, BigDecimal weight) {
        int index = findFood(food.getName());
        if (index == -1) {
            this.foodNodes.add(new FoodNode(food, weight));
        } else {
            this.foodNodes.get(index).addWeight(weight);
        }
        this.calculate();
    }

    public FoodPlan obtain() {
        FoodPlan foodPlan = new FoodPlan();
        foodPlan.factRatio = this.factRatio;
        foodPlan.foodNodes = new ArrayList<>();
        for (int i = 0; i < this.foodNodes.size(); i++) {
            FoodNode foodNode = this.foodNodes.get(i);
            foodPlan.foodNodes.add(foodNode.obtain());
        }
        foodPlan.calculate();
        return foodPlan;
    }

    static class FoodNode {
        private FoodDO     food;
        private BigDecimal weight;

        public FoodNode(FoodDO food, BigDecimal weight) {
            this.food = food;
            this.weight = weight;
        }

        public BigDecimal transWeight() {
            return weight.divide(new BigDecimal(100));
        }

        public FoodDO getFood() {
            return food;
        }

        public void setFood(FoodDO food) {
            this.food = food;
        }

        public BigDecimal getWeight() {
            return weight;
        }

        public void setWeight(BigDecimal weight) {
            this.weight = weight;
        }

        public void addWeight(BigDecimal weight) {
            this.weight = this.weight.add(weight);
        }

        public FoodNode obtain() {
            return new FoodNode(this.food.obtain(), this.weight);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FoodNode foodNode = (FoodNode) o;

            if (food != null ? !food.equals(foodNode.food) : foodNode.food != null) return false;
            return weight != null ? weight.equals(foodNode.weight) : foodNode.weight == null;
        }

        @Override
        public int hashCode() {
            int result = food != null ? food.hashCode() : 0;
            result = 31 * result + (weight != null ? weight.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "{\"" + food.getName() + "\":\"" + weight + "g\"}";
        }
    }

    public List<FoodNode> getFoodNodes() {
        return foodNodes;
    }

    public void setFoodNodes(List<FoodNode> foodNodes) {
        this.foodNodes = foodNodes;
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

    public BigDecimal getFactRatio() {
        return factRatio;
    }

    public void setFactRatio(BigDecimal factRatio) {
        this.factRatio = factRatio;
    }

    public BigDecimal getFact() {
        return fact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FoodPlan foodPlan = (FoodPlan) o;

        boolean p1 = true;
        for (FoodNode foodNode : foodNodes) {
            if (!((FoodPlan) o).foodNodes.contains(foodNode)) {
                p1 = false;
                break;
            }
        }
        boolean p2 = true;
        for (FoodNode foodNode : ((FoodPlan) o).foodNodes) {
            if (!foodNodes.contains(foodNode)) {
                p2 = false;
                break;
            }
        }
        return foodNodes != null ? p1 && p2 : foodPlan.foodNodes == null;
    }

    @Override
    public int hashCode() {
        return foodNodes != null ? foodNodes.hashCode() : 0;
    }

    @Override
    public String toString() {
        JSONObject object = new JSONObject();
        JSONArray nodeArray = new JSONArray();
        for (FoodNode foodNode : foodNodes) {
            nodeArray.add(JSONObject.parseObject(foodNode.toString()));
        }
        object.put("foodNodes", nodeArray);
        object.put("protein", protein.toPlainString());
        object.put("fat", fat.toPlainString());
        object.put("carbohydrate", carbohydrate.toPlainString());
        object.put("calorie", calorie.toPlainString());
        object.put("dietaryFiber", dietaryFiber.toPlainString());
        object.put("fact", fact.toPlainString());
        return object.toJSONString();
    }

    public String getTag() {
        if (this.tag != null) {
            return this.tag;
        }
        StringBuilder sb = new StringBuilder();
        this.foodNodes.sort(new Comparator<FoodNode>() {
            @Override
            public int compare(FoodNode o1, FoodNode o2) {
                return o1.getFood().getName().hashCode() - o2.getFood().getName().hashCode();
            }
        });
        for (FoodNode foodNode : this.foodNodes) {
            sb.append(foodNode.getFood().getName())
              .append(foodNode.getWeight());
        }
        this.tag = sb.toString();
        return this.tag;
    }
}
