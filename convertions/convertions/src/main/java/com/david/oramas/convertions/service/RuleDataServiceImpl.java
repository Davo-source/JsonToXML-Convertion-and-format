package com.david.oramas.convertions.service;

import com.david.oramas.convertions.entity.Data;
import com.david.oramas.convertions.entity.RuleActionData;
import com.david.oramas.convertions.entity.RuleData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RuleDataServiceImpl implements RuleDataService{

    public List<RuleData> ruleData = new ArrayList<>();
    public List<RuleActionData> ruleActionData = new ArrayList<>();
    public int sequence=1;


    @Override
    public Data addRuleData(Data data) {
        //This seems like is only good for sequence and disabled,
        // but ALIP needs other boilerplate information that will be set
        //TODO: add the other boilerplate information ALIP needs into the entities and set here
        data.setDescription(data.getName());
        data.setExecutionOrder(0);
        data.setConditionOperator("AND");
        data.setStatus(1);
        data.setFunctionId(-1);
        this.ruleData = data.getRuleData().stream()
                .peek((ruleDataElement) -> {
                    ruleDataElement.setDescription(ruleDataElement.getName());
                    ruleDataElement.setLogicOperator("AND");
                    ruleDataElement.setDisabled(false);
                    ruleDataElement.setSequence(this.sequence);
                    int savedSequence = this.sequence;
                    Optional.ofNullable(ruleDataElement.getConditionData())
                            .map(ruleConditionDataElement -> {
                                this.sequence=1;
                                ruleConditionDataElement.forEach(element ->{
                                    element.setSequence(this.sequence);
                                    element.setDescription(element.getName());
                                    element.setNumberOfDays(0);
                                    element.setCurrentDateInd(false);
                                    element.setEvaluateContext(false);
                                    sequence++;
                                });
                                return ruleConditionDataElement;
                            });

                    if(!ruleDataElement.getRuleActionData().isEmpty()){
                        this.sequence = 1;
                        ruleDataElement.getRuleActionData()
                                        .forEach(ruleActionDataElement -> {
                                            ruleActionDataElement.setSequence(this.sequence);
                                            this.sequence++;
                                        });
                    }
                    this.sequence = savedSequence;
                    this.sequence++;
                })
                .collect(Collectors.toList());
        data.setRuleData(this.ruleData);
        this.sequence = 1;
        return data;
    }
}
