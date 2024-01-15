package com.david.oramas.convertions.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class ConditionData {
    private String name;
    private String description;
    private int sequence;
    private String operator;
    private String dataType;
    private String xmlTag;
    private String value;
    private int numberOfDays;
    private boolean currentDateInd;
    private boolean evaluateContext;

}
