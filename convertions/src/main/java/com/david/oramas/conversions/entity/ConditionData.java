package com.david.oramas.conversions.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class ConditionData {
    private String name;
    private String description;
    private String operator;
    private String dataType;
    private String xmlTag;
    private String value;
    private int sequence;
    private int numberOfDays;
    private boolean currentDateInd;
    private boolean evaluateContext;

    public ConditionData(String name, String description, String operator, String dataType, String xmlTag, String value) {
        this.name=name;
        this.description=description;
        this.operator=operator;
        this.dataType=dataType;
        this.xmlTag=xmlTag;
        this.value=value;
    }
}
