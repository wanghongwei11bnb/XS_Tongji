package com.xiangshui.util;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public class ExcelUtils {

    public static XSSFWorkbook export(List<List<String>> data) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sheet1");
        for (int i = 0; i < data.size(); i++) {
            List<String> item = data.get(i);
            XSSFRow row = sheet.createRow(i);
            for (int j = 0; j < item.size(); j++) {
                String value = item.get(j);
                XSSFCell cell = row.createCell(j);
                cell.setCellValue(value);
            }
        }
        return workbook;
    }


}
