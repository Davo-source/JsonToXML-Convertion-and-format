package com.david.oramas.conversions.controller;

import com.david.oramas.conversions.entity.Data;
import com.david.oramas.conversions.service.ExcelReaderService;
import jxl.read.biff.BiffException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;


@RestController
@RequestMapping("/excel")
public class ExcelTransformerController {
    @Autowired
    ExcelReaderService excelReaderService;

    @PostMapping(value = "/read", produces = "application/json")
    public Data readExcel(@RequestParam("file") @NotNull MultipartFile file)
            throws IOException, BiffException {
        return excelReaderService.readExcel(file);
    }
}
