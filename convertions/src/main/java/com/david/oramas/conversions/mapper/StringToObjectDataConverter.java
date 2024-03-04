package com.david.oramas.conversions.mapper;

import com.david.oramas.conversions.entity.ConditionData;
import com.david.oramas.conversions.entity.RuleActionData;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StringToObjectDataConverter {
    public ConditionData toConditionData(List<String> conditionDataAsList) {
        ConditionData conditionData = new ConditionData();
        // Custom logic to map list of strings to ConditionData object
        conditionData.setName(conditionDataAsList.get(1));
        conditionData.setDataType(conditionDataAsList.get(2));
        conditionData.setOperator(conditionDataAsList.get(3));
        conditionData.setXmlTag(conditionDataAsList.get(4));
        conditionData.setValue(conditionDataAsList.size()<6? conditionDataAsList.get(0) : conditionDataAsList.get(5));
        return conditionData;
    }
    public RuleActionData toRuleActionData(List<String> ruleActionDataAsList){
        RuleActionData ruleActionData = new RuleActionData();
        ruleActionData.setName(ruleActionDataAsList.get(0));
        ruleActionData.setOperator(ruleActionDataAsList.get(1));
        ruleActionData.setXmlTag(ruleActionDataAsList.get(2));
        ruleActionData.setValue(ruleActionDataAsList.get(3));
        return ruleActionData;
    }
}
