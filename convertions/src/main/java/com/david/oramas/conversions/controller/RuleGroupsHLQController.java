package com.david.oramas.conversions.controller;

import com.david.oramas.conversions.entity.Data;
import com.david.oramas.conversions.service.RuleDataServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/part2")
@Slf4j
public class RuleGroupsHLQController {

    RuleDataServiceImpl ruleDataServiceImpl;
    @Autowired
    public RuleGroupsHLQController(RuleDataServiceImpl ruleDataServiceImpl){
        this.ruleDataServiceImpl = ruleDataServiceImpl;
    }

    @PostMapping(value = "/addRuleAndConditions", consumes = "application/json", produces = "application/xml")
    public ResponseEntity<Data> addConditionToRuleGroup(@RequestBody Data data){
        log.info(String.valueOf(data));
        return ResponseEntity.ok(ruleDataServiceImpl.addRuleData(data));
    }
}
