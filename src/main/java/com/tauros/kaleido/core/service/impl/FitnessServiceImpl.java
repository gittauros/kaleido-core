package com.tauros.kaleido.core.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.tauros.kaleido.core.algorithm.fitness.FoodPlanCalculator;
import com.tauros.kaleido.core.algorithm.fitness.FoodServ;
import com.tauros.kaleido.core.algorithm.fitness.FoodSet;
import com.tauros.kaleido.core.service.FitnessService;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Created by zhy on 2017/11/5.
 */
public class FitnessServiceImpl implements FitnessService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ConcurrentHashMap<String, FoodPlanCalculator> calculatorPool = new ConcurrentHashMap<>();
    private Executor                                      executor       = new ThreadPoolExecutor(5, 10, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

    @Override
    public String generateFoodPlanCalculator(List<FoodServ> foodList, FoodSet target, int maxResult, int maxKickedCount) {
        FoodPlanCalculator calculator = new FoodPlanCalculator(foodList, target, maxResult);
        calculator.setMaxKickedCount(maxKickedCount);
        String foodPlanCalculatorId;
        do {
            foodPlanCalculatorId = UUID.randomUUID().toString();
        } while (calculatorPool.get(foodPlanCalculatorId) != null);
        calculatorPool.put(foodPlanCalculatorId, calculator);
        return foodPlanCalculatorId;
    }

    @Override
    public void asyncFoodPlanCalculatorSearch(final String foodPlanCalculatorId) {
        FoodPlanCalculator calculator = calculatorPool.get(foodPlanCalculatorId);
        Asserts.notNull(calculator, String.format("id[%s] does not exist", foodPlanCalculatorId));
        executor.execute(new Runnable() {
            @Override
            public void run() {
                logger.info("id[{}] calculator start", foodPlanCalculatorId);
                calculator.searchResult();
                logger.info("id[{}] calculator end", foodPlanCalculatorId);
            }
        });
    }

    @Override
    public void changeFoodPlanCalculatorMaxKickedCount(String foodPlanCalculatorId, int maxKickedCount) {
        FoodPlanCalculator calculator = calculatorPool.get(foodPlanCalculatorId);
        Asserts.notNull(calculator, String.format("id[%s] does not exist", foodPlanCalculatorId));
        calculator.setMaxKickedCount(maxKickedCount);
    }

    @Override
    public BigDecimal getFoodPlanCalculatorProcess(String foodPlanCalculatorId) {
        FoodPlanCalculator calculator = calculatorPool.get(foodPlanCalculatorId);
        Asserts.notNull(calculator, String.format("id[%s] does not exist", foodPlanCalculatorId));
        return calculator.getProcess();
    }

    @Override
    public JSONArray getFoodPlanCalculatorResult(String foodPlanCalculatorId) {
        FoodPlanCalculator calculator = calculatorPool.get(foodPlanCalculatorId);
        Asserts.notNull(calculator, String.format("id[%s] does not exist", foodPlanCalculatorId));
        return calculator.getResultJSONArray();
    }
}
