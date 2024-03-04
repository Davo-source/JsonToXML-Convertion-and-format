package com.david.oramas.conversions.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@lombok.Data
@JacksonXmlRootElement(namespace = "ruleData")
public class RuleData {
    private String name;
    private String description;
    private String logicOperator;
    private boolean disabled;
    private List<ConditionData> conditionData;
    private List<RuleActionData> ruleActionData;
    private int sequence;

}
