package com.xtm.call.importcontacts;

import android.text.TextUtils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Function:
 * Created by TianMing.Xiong on 18-5-25.
 */

public class ExcelUtils {
    /**
     * 解析csv文件获取联系人信息
     * @param file
     * @return
     */
    public static List<Contact> parseContactsByCsv(File file) {
        if(null==file) return null ;
        try {
            ArrayList<Contact> contacts = new ArrayList<>();
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();
            String line ;
            while ((line = bufferedReader.readLine())!=null){
                String[] split = line.split(",");
                String name ;
                String phone1;
                String phone2 = "";
                Contact contact ;
                if(split.length<2){
                    continue;
                }else if(split.length==2){//一个号码
                    name=split[0];
                    phone1=split[1];
                }else {//两个号码
                    name=split[0];
                    phone1=split[1];
                    phone2=split[2];
                }
                contact = new Contact(name, phone1, phone2);
                contacts.add(contact);
            }
            return contacts;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 解析xlsx文件
     * @param file
     * @return
     */
    public static List<Contact> parseContactsByXlsx(File file) {
        List<Contact> lists = new ArrayList();
        try {
            InputStream stream = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(stream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            for (int r = 0; r < rowsCount; r++) {
                Row row = sheet.getRow(r);
                int cellsCount = row.getPhysicalNumberOfCells();
                String name = getCellAsString(row, 0, formulaEvaluator);//第一列
                String phone1 = getCellAsString(row, 1, formulaEvaluator);//第二列
                String phone2 = getCellAsString(row, 2, formulaEvaluator);//第三列
                Contact contact = new Contact(name, phone1, phone2);
                lists.add(contact);
//                for (int c = 0; c<cellsCount; c++) {
//                    String value = getCellAsString(row, c, formulaEvaluator);
//                    String cellInfo = "r:"+r+"; c:"+c+"; v:"+value;
//                    if (BuildConfig.DEBUG) Log.d("ExcelUtils", cellInfo);

//                }
            }

        } catch (Exception e) {
            /* proper exception handling to be here */
            e.printStackTrace();
        }
        return lists;
    }

    private static String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = ""+cellValue.getBooleanValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    long numericValue = (long) cellValue.getNumberValue();
//                    if(HSSFDateUtil.isCellDateFormatted(cell)) {
//                        double date = cellValue.getNumberValue();
//                        SimpleDateFormat formatter =
//                                new SimpleDateFormat("dd/MM/yy");
//                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
//                    } else {
                        value = ""+numericValue;
//                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = ""+cellValue.getStringValue();
                    break;
                default:
            }
        } catch (NullPointerException e) {
            /* proper error handling should be here */
            e.printStackTrace();

        }
        return value;
    }

    /**
     * 解析csv文件获取联系人信息
     * @param file
     * @param fileEncode
     * @return
     */
    public static List<Contact> parseContactsByCsv(File file, String fileEncode) {
        if(null==file) return null ;
        try {
            ArrayList<Contact> contacts = new ArrayList<>();
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), fileEncode);
//            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(isr);
            Contact contact ;
            StringBuilder sbName = new StringBuilder();
            StringBuilder sbPhone1 = new StringBuilder();
            StringBuilder sbPhone2 = new StringBuilder();
            String line ;
            while ((line = bufferedReader.readLine())!=null){
                String[] split = line.split(",");
                sbName.setLength(0);
                sbPhone1.setLength(0);
                sbPhone2.setLength(0);

                if(split.length<2 || TextUtils.isEmpty(split[0])
                        || TextUtils.isEmpty(split[1])
                        || !isNumeric(split[1])
                        ){
                    continue;
                }else if(split.length==2){//一个号码
                    sbName.append(split[0]);
                    sbPhone1.append(split[1]);
                }else {//两个号码
                    sbName.append(split[0]);
                    sbPhone1.append(split[1]);
                    sbPhone2.append(split[2]);
                }

                contact = new Contact(sbName.toString(), sbPhone1.toString(), sbPhone2.toString());
                contacts.add(contact);

            }
            bufferedReader.close();
            return contacts;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }


}
