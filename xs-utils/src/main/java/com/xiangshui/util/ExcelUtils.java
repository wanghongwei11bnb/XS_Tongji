package com.xiangshui.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
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

    public static List<List<String>> read(InputStream inputStream, int sheetIndex) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        if (workbook == null) {
            System.out.println("未读取到内容,请检查路径！");
            return null;
        }
        XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
        return read(sheet);
    }

    public static List<List<String>> read(InputStream inputStream, String sheetName) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        if (workbook == null) {
            System.out.println("未读取到内容,请检查路径！");
            return null;
        }
        XSSFSheet sheet = workbook.getSheet(sheetName);
        return read(sheet);
    }

    public static List<List<String>> read(XSSFSheet sheet) throws IOException {
        if (sheet == null) return null;
        List<List<String>> data = new ArrayList<List<String>>();
        if (sheet != null) {
            for (Iterator<Row> rowIterator = sheet.iterator(); rowIterator.hasNext(); ) {
                Row row = rowIterator.next();
                List<String> rowDate = new ArrayList<String>();
                for (Iterator<Cell> cellIterator = row.cellIterator(); cellIterator.hasNext(); ) {
                    Cell cell = cellIterator.next();
                    rowDate.add(getValue((XSSFCell) cell));
                }
                data.add(rowDate);
            }
        }
        return data;
    }

    private static String getValue(XSSFCell cell) {
        if (cell == null) {
            return "---";
        }
        if (cell.getCellType() == cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == cell.CELL_TYPE_NUMERIC) {
            double cur = cell.getNumericCellValue();
            long longVal = Math.round(cur);
            Object inputValue = null;
            if (Double.parseDouble(longVal + ".0") == cur)
                inputValue = longVal;
            else
                inputValue = cur;
            return String.valueOf(inputValue);
        } else if (cell.getCellType() == cell.CELL_TYPE_BLANK || cell.getCellType() == cell.CELL_TYPE_ERROR) {
            return "---";
        } else {
            return String.valueOf(cell.getStringCellValue());
        }
    }

    public static void main(String[] args) throws IOException {

        List<List<String>> data = read(new FileInputStream("/Users/whw/Downloads/分账信息.xlsx"), 0);

        System.out.println();
    }

}
