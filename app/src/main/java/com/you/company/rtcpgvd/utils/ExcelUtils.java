package com.you.company.rtcpgvd.utils;

import android.os.Environment;
import android.util.Log;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelUtils {

    /**
     * 创建或追加Excel文件
     *
     * @param fileName 文件名（如：example.xlsx）
     * @param append   是否追加写入（true：追加，false：重新生成）
     * @return 是否操作成功
     */
    public static boolean createOrAppendExcelFile(String fileName, boolean append) {
        Workbook workbook;
        Sheet sheet;
        File file = new File(fileName);

        try {
            if (append && file.exists()) {
                // 如果文件存在且需要追加，则加载现有文件
                FileInputStream fileIn = new FileInputStream(file);
                workbook = WorkbookFactory.create(fileIn);
                sheet = workbook.getSheetAt(0);
                fileIn.close();
            } else {
                // 如果文件不存在或需要重新生成，则创建新文件
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("Sheet1");

                // 创建列名
                Row headerRow = sheet.createRow(0);
                for (int i = 1; i <= 10; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue("zone" + i);
                }
            }

            // 获取当前最大行号
            int lastRowNum = sheet.getLastRowNum();
            int newRowNum = lastRowNum + 1;


            // 保存文件
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
                return true;
            } finally {
                workbook.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 在指定秒和列插入数据
     *
     * @param fileName 文件名（如：example.xlsx）
     * @param second   秒数（行号）
     * @param col      列号（1-10）
     * @param data     要插入的数据
     * @return 是否插入成功
     */
    public static boolean insertData(String fileName, int second, int col, String data) {
        File file = new File(fileName);
        try (FileInputStream fileIn = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fileIn)) {

            Sheet sheet = workbook.getSheetAt(0);

            // 获取当前最大行号
            int lastRowNum = sheet.getLastRowNum();
            int newRowNum = lastRowNum + 1;
            Row row = sheet.getRow(newRowNum);
            if (row == null) {
                row = sheet.createRow(newRowNum);
            }
            Cell cell = row.createCell(col);
            cell.setCellValue(data);

            // 保存修改
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
               // return true;
            }finally {
                workbook.close();
            }
        } catch (IOException e) {
            Log.i("excel",e.toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 读取Excel文件
     *
     * @param fileName 文件名（如：example.xlsx）
     */
    public static void readExcelFile(String fileName) {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                for (Cell cell : row) {
                    System.out.print(cell.toString() + "\t");
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除Excel文件
     *
     * @param fileName 文件名（如：example.xlsx）
     * @return 是否删除成功
     */
    public static boolean deleteExcelFile(String fileName) {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        return file.delete();
    }
}