package com.xiangshui.util;

import com.alibaba.fastjson.JSON;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

public class ExcelUtils {

    public static XSSFWorkbook export(List<List<Object>> data) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sheet1");
        for (int i = 0; i < data.size(); i++) {
            List<Object> item = data.get(i);
            XSSFRow row = sheet.createRow(i);
            for (int j = 0; j < item.size(); j++) {
                Object value = item.get(j);
                XSSFCell cell = row.createCell(j);
                if (value == null) continue;
                Class<?> valueClass = value.getClass();
                if (valueClass == String.class && "null".equals(value)) continue;
                if (false
                        || valueClass == byte.class || valueClass == Byte.class
                        || valueClass == short.class || valueClass == Short.class
                        || valueClass == int.class || valueClass == Integer.class
                        || valueClass == long.class || valueClass == Long.class
                        || valueClass == float.class || valueClass == Float.class
                        || valueClass == double.class || valueClass == Double.class
                        ) {
                    cell.setCellValue(Double.valueOf(value.toString()));
                } else if (valueClass == boolean.class || valueClass == Boolean.class) {
                    cell.setCellValue((boolean) value);
                } else if (valueClass == String.class || valueClass == char.class || valueClass == Character.class) {
                    cell.setCellValue(String.valueOf(value));
                } else if (valueClass == Date.class) {
                    cell.setCellValue((Date) value);
                } else {
                    cell.setCellValue(JSON.toJSONString(value));
                }
            }
        }
        return workbook;
    }

    public static <T> XSSFWorkbook export(Collection<Column<T>> columnList, Collection<T> data) {
        List<List<Object>> listDate = new ArrayList<List<Object>>();
        List<Object> header = new ArrayList<Object>();
        for (Column<T> column : columnList) {
            if (column != null) {
                header.add(column.title);
            }
        }
        listDate.add(header);
        for (T t : data) {
            List<Object> row = new ArrayList<Object>();
            for (Column<T> column : columnList) {
                if (column != null) {
                    row.add(column.render(t));
                    if (column.totalCallBack != null) {
                        column.total = column.totalCallBack.run(column.total != null ? column.total : 0, t);
                    }
                }
            }
            listDate.add(row);
        }
        List<Object> totalRow = new ArrayList<Object>();
        for (Column<T> column : columnList) {
            if (column != null) {
                if (column.total != null) {
                    totalRow.add(String.valueOf(column.total % 1 == 0 ? column.total.intValue() : column.total));
                } else {
                    totalRow.add(null);
                }
            }
        }
        listDate.add(totalRow);
        return export(listDate);
    }

    public static <T> void export(Collection<Column<T>> columnList, Collection<T> data, File file) throws IOException {
        XSSFWorkbook workbook = export(columnList, data);
        OutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();
    }

    public static <T> void export(Collection<Column<T>> columnList, Collection<T> data, HttpServletResponse response, String fileName) throws IOException {
        XSSFWorkbook workbook = export(columnList, data);
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("utf-8"), "ISO8859-1"));
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();
    }

    public static void export(List<List<Object>> data, HttpServletResponse response, String fileName) throws IOException {
        XSSFWorkbook workbook = export(data);
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("utf-8"), "ISO8859-1"));
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();
    }


    public static byte[] toBytes(Workbook workbook) throws IOException {
        if (workbook == null) return null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        workbook.write(os);
        return os.toByteArray();
    }

    public static abstract class Column<T> {
        String title;
        Float total;
        CallBack2ForResult<Float, T, Float> totalCallBack;

        public Column(String title) {
            this.title = title;
        }

        public Column(String title, CallBack2ForResult<Float, T, Float> totalCallBack) {
            this.title = title;
            this.totalCallBack = totalCallBack;
        }

        public abstract Object render(T t);

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
