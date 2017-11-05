package com.tauros.kaleido.core.service;

import com.alibaba.fastjson.JSONArray;
import com.tauros.kaleido.core.algorithm.fitness.FoodServ;
import com.tauros.kaleido.core.algorithm.fitness.FoodSet;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by zhy on 2017/11/5.
 */
public interface FitnessService {

    /**
     * 创建饮食计划计算器
     *
     * @param foodList       食物列表
     * @param target         饮食计划目标
     * @param maxResult      最大保留结果数
     * @param maxKickedCount 最大淘汰结果数，数字越大越精确
     * @return 饮食计划计算器唯一id
     */
    String generateFoodPlanCalculator(List<FoodServ> foodList, FoodSet target, int maxResult, int maxKickedCount);

    /**
     * 异步开启饮食计划计算器计算
     *
     * @param foodPlanCalculatorId 饮食计划计算器唯一id
     */
    void asyncFoodPlanCalculatorSearch(String foodPlanCalculatorId);

    /**
     * 修改饮食计划计算器最大淘汰结果数
     *
     * @param foodPlanCalculatorId 饮食计划计算器唯一id
     * @param maxKickedCount       最大淘汰结果数，数字越大越精确
     */
    void changeFoodPlanCalculatorMaxKickedCount(String foodPlanCalculatorId, int maxKickedCount);

    /**
     * 获取饮食计划计算器计算进度
     *
     * @param foodPlanCalculatorId 饮食计划计算器唯一id
     * @return 进度百分数
     */
    BigDecimal getFoodPlanCalculatorProcess(String foodPlanCalculatorId);

    /**
     * 获取饮食计划计算器结果
     *
     * @param foodPlanCalculatorId 饮食计划计算器唯一id
     * @return 计算结果json格式
     */
    JSONArray getFoodPlanCalculatorResult(String foodPlanCalculatorId);
}
