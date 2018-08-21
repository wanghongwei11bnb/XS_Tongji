package com.xiangshui.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
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
                cell.setCellValue(value == null || value.equals("null") ? "" : value);
            }
        }
        return workbook;
    }

    public static <T> XSSFWorkbook export(List<Column<T>> columnList, List<T> data) {
        List<List<String>> data2 = new ArrayList<List<String>>();
        List<String> header = new ArrayList<String>();
        for (Column<T> column : columnList) {
            if (column != null) {
                header.add(column.title);
            }
        }
        data2.add(header);
        for (T t : data) {
            List<String> row = new ArrayList<String>();
            for (Column<T> column : columnList) {
                if (column != null) {
                    row.add(column.render(t));
                }
            }
            data2.add(row);
        }
        return export(data2);
    }

    public static <T> void export(List<Column<T>> columnList, List<T> data, HttpServletResponse response, String fileName) throws IOException {
        XSSFWorkbook workbook = export(columnList, data);
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes()));
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();
    }

    public static void export(List<List<String>> data, HttpServletResponse response, String fileName) throws IOException {
        XSSFWorkbook workbook = export(data);
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes()));
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();
    }

    public static abstract class Column<T> {
        String title;

        public Column(String title) {
            this.title = title;
        }

        public abstract String render(T t);

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
        if (cell != null) {
            cell.setCellType(CellType.STRING);
            switch (cell.getCellTypeEnum()) {
                case _NONE:
                    break;
                case NUMERIC:
                    return String.valueOf(cell.getNumericCellValue());
                case STRING:
                    return cell.getStringCellValue();
                case FORMULA:
                    break;
                case BLANK:
                    break;
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case ERROR:
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {

        List<List<String>> data = read(new FileInputStream("/Users/whw/Downloads/分账信息.xlsx"), 0);

        System.out.println();
    }

}
