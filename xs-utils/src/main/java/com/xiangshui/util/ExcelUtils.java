package com.xiangshui.util;

import org.apache.poi.hssf.usermodel.*;

import java.util.List;

public class ExcelUtils {

    public static HSSFWorkbook export(List<List<String>> data) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        for (int i = 0; i < data.size(); i++) {
            List<String> item = data.get(i);
            HSSFRow row = sheet.createRow(i);
            for (int j = 0; j < item.size(); j++) {
                String value = item.get(j);
                HSSFCell cell = row.createCell(j);
                cell.setCellValue(value);
            }
        }
        return workbook;
    }


}
