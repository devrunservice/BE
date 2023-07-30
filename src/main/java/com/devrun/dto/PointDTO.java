package com.devrun.dto;

import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class PointDTO {
	
    private String id;
    private int amount;
    private int userPoint;

    public String getid() {
        return id;
    }

    public void setUserNo(String id) {
        this.id = id;
    }
    
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getUserPoint() {
        return userPoint;
    }

    public void setUserPoint(int userPoint) {
        this.userPoint = userPoint;
    }
}
