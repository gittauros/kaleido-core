package com.tauros.kaleido.core.algorithm.fitness;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.util.Asserts;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by zhy on 2017/11/5.
 */
public class FoodPlanCaculator {

    private List<FoodServ>            foodList;
    private Set<String>               tagSet;
    private PriorityQueue<SearchNode> planQueue;
    private List<SearchNode>          resultList;
    private FoodSet                   target;
    private int                       maxResult;
    private int                       maxKickedCount;
    private BigDecimal                minCalorie;

    public FoodPlanCaculator(List<FoodServ> foodList, FoodSet target, int maxResult) {
        Asserts.check(foodList != null && !foodList.isEmpty(), "foodList is null or empty");
        Asserts.notNull(target, "target is null");
        this.foodList = foodList;
        this.target = target;
        this.planQueue = new PriorityQueue<>(new Comparator<SearchNode>() {
            @Override
            public int compare(SearchNode o1, SearchNode o2) {
                return o2.score.compareTo(o1.score);
            }
        });
        this.resultList = new ArrayList<>();
        this.tagSet = new TreeSet<>();
        this.maxResult = maxResult;
        this.minCalorie = target.getCalorie().multiply(new BigDecimal(0.75));
    }

    public void searchResult() {
        FoodPlan initPlan = new FoodPlan();
        initPlan.setFactRatio(target.getFactRatio());
        planQueue.add(new SearchNode(initPlan, calculateScore(initPlan)));
        SearchNode top;
        int kickedCount = 0;
        while (!planQueue.isEmpty()) {
            top = planQueue.poll();
            if (top == null) {
                break;
            }
            boolean allIllegal = true;
            for (FoodServ foodServ : foodList) {
                FoodPlan newPlan = top.foodPlan.obtain();
                if (!newPlan.contains(foodServ.getFood().getName())) {
                    newPlan.addFood(foodServ.getFood(), foodServ.getFirstServ());
                } else {
                    newPlan.addFood(foodServ.getFood(), foodServ.getPerServ());
                }
                SearchNode newNode = new SearchNode(newPlan, calculateScore(newPlan));
                if (!tagSet.contains(newPlan.getTag()) && !resultList.contains(newNode) && newNode.score.compareTo(BigDecimal.ZERO) >= 0 && isPlanLegal(newPlan)) {
                    planQueue.offer(newNode);
                    tagSet.add(newPlan.getTag());
                    allIllegal = false;
                }
            }
            if (allIllegal && !planQueue.contains(top) && !resultList.contains(top)) {
                if (this.minCalorie != null && top.getFoodPlan().getCalorie().compareTo(this.minCalorie) < 0) {
                    continue;
                }
                resultList.add(top);
                resultList.sort(new Comparator<SearchNode>() {
                    @Override
                    public int compare(SearchNode o1, SearchNode o2) {
                        return o2.score.compareTo(o1.score);
                    }
                });
                if (resultList.size() > maxResult) {
                    resultList.remove(resultList.size() - 1);
                    kickedCount++;
                    if (maxKickedCount <= 0 || kickedCount > maxKickedCount) {
                        break;
                    }
                }
            }
        }
    }

    public void clear() {
        this.planQueue.clear();
        this.tagSet.clear();
        this.resultList.clear();
    }

    private boolean isPlanLegal(FoodPlan foodPlan) {
        boolean p1 = FoodSet.isLegal(foodPlan.getCalorie(), target.getCalorie(), 1.02) &&
                     FoodSet.isLegal(foodPlan.getProtein(), target.getProtein(), 1.05) &&
                     FoodSet.isLegal(foodPlan.getFat(), target.getFat(), 1) &&
                     FoodSet.isLegal(foodPlan.getCarbohydrate(), target.getCarbohydrate(), 1.02);
        boolean p2 = true;
        for (FoodPlan.FoodNode foodNode : foodPlan.getFoodNodes()) {
            BigDecimal maxWeight = target.getMaxWeight(foodNode.getFood().getName());
            if (maxWeight != null && foodNode.getWeight().compareTo(maxWeight) > 0) {
                p2 = false;
                break;
            }
        }
        return p1 && p2;
    }

    private BigDecimal calculateScore(FoodPlan foodPlan) {
        boolean isLegal = FoodSet.isLegal(foodPlan.getCalorie(), target.getCalorie(), 1.02);
        if (!isLegal) {
            return BigDecimal.ZERO;
        }
        BigDecimal score = BigDecimal.ZERO;
        score = score.add(calculateDistance(foodPlan.getProtein(), target.getProtein(), 7))
                     .add(calculateDistance(foodPlan.getCalorie(), target.getCalorie(), 8))
                     .add(calculateDistance(foodPlan.getCarbohydrate(), target.getCarbohydrate(), 5))
                     .add(calculateDistance(foodPlan.getFat(), target.getFat(), 1));

        if (score.equals(BigDecimal.ZERO)) {
            score = new BigDecimal(9999);
        }
        score = new BigDecimal(1000).divide(score, 9, BigDecimal.ROUND_HALF_UP);
        if (foodPlan.getFact() != null) {
            score = score.subtract(foodPlan.getFact().divide(foodPlan.getFactRatio(), 4, BigDecimal.ROUND_HALF_UP));
        }
        double minDietary = 25;
        double targetCarbohydrate = target.getCarbohydrate().doubleValue();
        double dietaryFiberVal = foodPlan.getDietaryFiber().doubleValue();
        double expectDietaryFiber = Math.max(minDietary, mid(mid((targetCarbohydrate / 4), minDietary), minDietary));
        double dietaryScore = expectDietaryFiber - (Math.abs(dietaryFiberVal - expectDietaryFiber));
        return score.add(new BigDecimal(dietaryScore / target.getDietaryScoreNerf())).setScale(14, BigDecimal.ROUND_HALF_UP);
    }

    private double mid(double a, double b) {
        return (a + b) / 2;
    }

    private static BigDecimal calculateDistance(BigDecimal value, BigDecimal target, double weight) {
        boolean isLegal = FoodSet.isLegal(value, target, 1.05);
        if (!isLegal) {
            return new BigDecimal(99999999);
        }
        if (value.compareTo(target) >= 0) {
            return BigDecimal.ZERO;
        }
        return target.subtract(value).multiply(new BigDecimal(weight));
    }

    private static class SearchNode {
        private FoodPlan   foodPlan;
        private BigDecimal score;

        public SearchNode(FoodPlan foodPlan, BigDecimal score) {
            this.foodPlan = foodPlan;
            this.score = score;
        }

        public FoodPlan getFoodPlan() {
            return foodPlan;
        }

        public BigDecimal getScore() {
            return score;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SearchNode that = (SearchNode) o;

            if (foodPlan != null ? !foodPlan.equals(that.foodPlan) : that.foodPlan != null) return false;
            return score != null ? score.equals(that.score) : that.score == null;
        }

        @Override
        public int hashCode() {
            int result = foodPlan != null ? foodPlan.hashCode() : 0;
            result = 31 * result + (score != null ? score.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            JSONObject object = new JSONObject();
            object.put("foodPlan", JSONObject.parseObject(foodPlan.toString()));
            object.put("score", score.toPlainString());
            return object.toJSONString();
        }
    }

    public int getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(int maxResult) {
        this.maxResult = maxResult;
    }

    public int getMaxKickedCount() {
        return maxKickedCount;
    }

    public void setMaxKickedCount(int maxKickedCount) {
        this.maxKickedCount = maxKickedCount;
    }

    public FoodSet getTarget() {
        return target;
    }

    public void setTarget(FoodSet target) {
        this.target = target;
    }

    public List<FoodServ> getFoodList() {
        return foodList;
    }

    public void setFoodList(List<FoodServ> foodList) {
        this.foodList = foodList;
    }

    public List<SearchNode> getResultList() {
        return resultList;
    }

    public JSONArray getResultJSONArray() {
        JSONArray resultArray = new JSONArray();
        for (FoodPlanCaculator.SearchNode node : resultList) {
            resultArray.add(JSON.parseObject(node.toString()));
        }
        return resultArray;
    }

    public String getResultString() {
        return getResultJSONArray().toJSONString();
    }

    public BigDecimal getMinCalorie() {
        return minCalorie;
    }

    public void setMinCalorie(BigDecimal minCalorie) {
        this.minCalorie = minCalorie;
    }
}
