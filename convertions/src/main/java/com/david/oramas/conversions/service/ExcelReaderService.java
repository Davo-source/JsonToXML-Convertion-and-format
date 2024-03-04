package com.david.oramas.conversions.service;

import com.david.oramas.conversions.entity.ConditionData;
import com.david.oramas.conversions.entity.Data;
import com.david.oramas.conversions.entity.RuleActionData;
import com.david.oramas.conversions.entity.RuleData;
import com.david.oramas.conversions.mapper.StringToObjectDataConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExcelReaderService {

    public String RULE_GROUP_NAME= "rule group name";
    public String CONDITION_NAME= "condition name";
    public String RULE_NAME= "rule name";
    public int indexOfRuleConditionAndAction=0;
    @Autowired
    public StringToObjectDataConverter stringToObjectDataConverter;

    public Data readExcel(MultipartFile file) throws IOException {

        List<List<String>> excelData = new ArrayList<>();
        Data data = new Data();
        RuleData ruleData = new RuleData();


        List<RuleData> ruleDataList = new ArrayList<>();
        BufferedReader fileReader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));

        List<String> lines = fileReader.lines().collect(Collectors.toList());

            lines.stream().forEach(row -> {
                List<String> eachElementPerRow_List = List.of(row.split(","));
                excelData.add(eachElementPerRow_List);
                if(eachElementPerRow_List.get(0).toLowerCase().equals(RULE_NAME))
                    indexOfRuleConditionAndAction = lines.indexOf(row);
            });
        //TODO:
        // Join both methods inside one "for -> stream", no need to repeat the same "for into stream.peak" logic
        returnConditionAndActionDataFromRuleGroup(excelData.subList(0, indexOfRuleConditionAndAction), data);
        if(indexOfRuleConditionAndAction !=0) {
            ruleDataList =
                    returnConditionAndActionDataFromRule(excelData.subList(indexOfRuleConditionAndAction+1,
                                    excelData.size()), ruleData);
        }
        data.setRuleData(ruleDataList);
        return data;
    }

    public void returnConditionAndActionDataFromRuleGroup(List<List<String>> excelData, Data data){
        Set<ConditionData> conditionDataList = new LinkedHashSet<>();

        for(List<String> rowData : excelData){
                   if(rowData.get(0).toLowerCase().equals(RULE_GROUP_NAME))
                       data.setName(rowData.get(1));
                   if(rowData.get(0).isEmpty() && !rowData.get(1).toLowerCase().equals(CONDITION_NAME)){
                       conditionDataList.add(stringToObjectDataConverter.toConditionData(rowData));
                   }
                };
        List<ConditionData> unicConditionDataList = new ArrayList<>(conditionDataList);
        data.setConditionData(unicConditionDataList);
    }

    public List<RuleData> returnConditionAndActionDataFromRule(List<List<String>> excelData, RuleData previousRuleData){
        List<ConditionData> conditionData = new ArrayList<>();
        List<RuleActionData> ruleActionData = new ArrayList<>();
        List<RuleData> ruleDataList = new ArrayList<>();

        for(List<String> rowData : excelData){
            boolean newRuleDataFlag = !rowData.get(0).isEmpty();
            if((newRuleDataFlag && excelData.indexOf(rowData)!=0) || excelData.size() == excelData.indexOf(rowData)+1 )
            {
                previousRuleData.setConditionData(conditionData);
                previousRuleData.setRuleActionData(ruleActionData);
                ruleDataList.add(previousRuleData);
            }
            RuleData newRuleData = (newRuleDataFlag) ? new RuleData() : previousRuleData;
            if(newRuleDataFlag && rowData.size()>6){
                newRuleData.setName(rowData.get(0));
                conditionData.add(stringToObjectDataConverter.toConditionData(rowData));
                ruleActionData.add(stringToObjectDataConverter.toRuleActionData(rowData.subList(6,rowData.size())));
                previousRuleData=newRuleData;
            }
            else{
                conditionData.add(stringToObjectDataConverter.toConditionData(rowData));
            }
        }
        return ruleDataList;
    }

}
