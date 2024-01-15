package com.david.oramas.convertions.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@lombok.Data
@JacksonXmlRootElement(localName = "data")
public class Data {
    private String name;
    private String description;
    private int executionOrder;
    private String conditionOperator;
    private List<ConditionData> conditionData;
    private List<RuleData> ruleData;
//    DivCatData divCatData;
//    Descriptor descriptor;
//    DescriptionMLD descriptionMLD;
    private int status;
    private float functionId;
}
