package com.david.oramas.conversions.entity;

import lombok.Data;

@Data
public class RuleActionData {
    private String name;
    private String operator;
    private String xmlTag;
    private String value;
    private int sequence;
}
