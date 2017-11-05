package com.tauros.kaleido.core.algorithm.fitness;

import com.tauros.kaleido.core.model.dataobject.FoodDO;

import java.math.BigDecimal;

/**
 * Created by zhy on 2017/11/5.
 */
public class FoodServ {

    private FoodDO food;
    private BigDecimal firstServ;
    private BigDecimal perServ;

    public FoodServ(FoodDO food, BigDecimal firstServ, BigDecimal perServ) {
        this.food = food;
        this.firstServ = firstServ;
        this.perServ = perServ;
    }

    public FoodDO getFood() {
        return food;
    }

    public void setFood(FoodDO food) {
        this.food = food;
    }

    public BigDecimal getFirstServ() {
        return firstServ;
    }

    public void setFirstServ(BigDecimal firstServ) {
        this.firstServ = firstServ;
    }

    public BigDecimal getPerServ() {
        return perServ;
    }

    public void setPerServ(BigDecimal perServ) {
        this.perServ = perServ;
    }
}
