package com.walker.common.util;

import java.io.*;
import java.util.*;

import org.apache.commons.lang3.*;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;


public class ExcelUtil {
 
	private static Logger log = Logger.getLogger(ExcelUtil.class);
 
    private static final String SHEET_NAME = "new sheet";

    public static void saveToExcel(
            List<List<Object>> objList,
            List<Object> titleList,
            String sheetName,
            String saveFilePath
    ) throws IOException {
        Object[][] ll = new Object[objList.size()][];
        int i = 0;
        for(List<Object> line : objList){
            ll[i] = line.toArray();
        }
        saveToExcel(ll, titleList.toArray(), sheetName, saveFilePath);

    }
    public static void saveToExcel(
            Object[][] objList,
            Object[] titleList,
            String sheetName,
            String saveFilePath
    ) throws IOException {
    	File file = new File(saveFilePath);
    	if(file.exists()) {
    		log.warn("file is exist " + file.toString());
    	}
    	FileOutputStream os = new FileOutputStream(file);
    	toExcel(objList, titleList, sheetName, os);
    	os.flush();
    	os.close();
    }
    public static void saveToExcel(
            List<Bean> objList,
            List<String> keyList,
            List<String> titleList,
            String sheetName,
            String saveFilePath
    ) throws IOException {
    	File file = new File(saveFilePath);
    	if(file.exists()) {
    		log.warn("file is exist " + file.toString());
    	}
    	FileOutputStream os = new FileOutputStream(file);
    	toExcel(objList, keyList, titleList, sheetName, os);
    	os.flush();
    	os.close();
    }
    
    /**
     * @param objList 动态二维数组
     * @param keyList  键名列表
     * @param titleList  中文title列表
     * @MethodName : listToExcel
     * @Description : 导出Excel（可以导出到本地文件系统，也可以导出到浏览器，工作表大小为2003支持的最大值）
     */
    public static void toExcel(
            List<Bean> objList,
            List<String> keyList,
            List<String> titleList,
            String sheetName,
            OutputStream outputStream
    ) {
    	Watch w = new Watch("To Excel");
    	titleList = titleList == null ? keyList : titleList;
        if (objList == null || keyList == null) {
        	w.put("参数异常");
        }
    	
        sheetName = StringUtils.isNotBlank(sheetName) ? sheetName : SHEET_NAME;
 
        XSSFWorkbook wb = new XSSFWorkbook();
 
        //sheet样式定义【getColumnTopStyle()/getStyle()均为自定义方法 - 在下面  - 可扩展】
        //获取列头样式对象
        XSSFCellStyle columnTopStyle = getColumnTopStyle(wb);
        //单元格样式对象
        XSSFCellStyle style = getStyle(wb);
 
        XSSFSheet sheet = wb.createSheet(sheetName);
        XSSFRow row = sheet.createRow(0);
 
        //写入第一行
        for (Integer i = 0; i < titleList.size(); i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(titleList.get(i));
            cell.setCellStyle(columnTopStyle);
        }
 
        //遍历对象值
        try {
           Integer rowNum = 1;
           Iterator<Bean> it = objList.iterator();
            while (it.hasNext()) {
            	Bean bean = it.next();
                XSSFRow row1 = sheet.createRow(rowNum++);
                for (Integer col = 0; col < keyList.size(); col++) {
                    //值写入excel
                    XSSFCell cell = row1.createCell(col);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue(bean.get(keyList.get(col), ""));
                    cell.setCellStyle(style);
                }
            }
 
            try {
                wb.write(outputStream);
                w.cost("write");
            } catch (IOException e) {
                w.exceptionWithThrow(e, log);
            }
 
        } catch ( Exception e) {
            w.exceptionWithThrow(e, log);
        } finally {
 
        }
 
    }
    /**
     * @param objList 动态二维数组
     * @param titleList  中文title列表 第一行
     * @MethodName : listToExcel
     * @Description : 导出Excel（可以导出到本地文件系统，也可以导出到浏览器，工作表大小为2003支持的最大值）
     */
    public static void toExcel(
            Object[][] objList,
            Object[] titleList,
            String sheetName,
            OutputStream outputStream
    ) {
    	Watch w = new Watch("To Excel");
        if (objList == null) {
            w.put("参数异常");
        }
    	
        sheetName = StringUtils.isNotBlank(sheetName) ? sheetName : SHEET_NAME;
 
        XSSFWorkbook wb = new XSSFWorkbook();
 
        //sheet样式定义【getColumnTopStyle()/getStyle()均为自定义方法 - 在下面  - 可扩展】
        //获取列头样式对象
        XSSFCellStyle columnTopStyle = getColumnTopStyle(wb);
        //单元格样式对象
        XSSFCellStyle style = getStyle(wb);
 
        XSSFSheet sheet = wb.createSheet(sheetName);
        XSSFRow row = sheet.createRow(0);
 
        //写入第一行
        for (Integer i = 0; i < titleList.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(String.valueOf(titleList[i]));
            cell.setCellStyle(columnTopStyle);
        }
 
        //遍历对象值
        try {
           for(Integer rowNum = 0; rowNum < objList.length; rowNum++) {
            	Object[] line = objList[rowNum];
                XSSFRow row1 = sheet.createRow(rowNum);
                for (Integer col = 0; col < line.length; col++) {
                    //值写入excel
                    XSSFCell cell = row1.createCell(col);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue(String.valueOf(line[col]));
                    cell.setCellStyle(style);
                }
            }
 
            try {
                wb.write(outputStream);
                w.cost("write");
            } catch (IOException e) {
            	w.exceptionWithThrow(e, log);
            }
 
        } catch ( Exception e) {
           w.exceptionWithThrow(e, log);
        } finally {
 
        }
 
    }


    /**
     * 读入excel文件，解析后返回
     * @param file
     * @throws IOException
     */
    public static List<List<String>> readExcelList(File file) throws IOException{
        List<List<String>> list = new ArrayList<>();

        for(String[] line : readExcel(file)){
            list.add(Arrays.asList(line));
        }

        return list;
    }
    /** 
     * 读入excel文件，解析后返回 
     * @param file 
     * @throws IOException  
     */  
    public static List<String[]> readExcel(File file) throws IOException{  
        //获得Workbook工作薄对象  
        Workbook workbook = getWorkBook(file);  
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回  
        List<String[]> list = new ArrayList<String[]>();  
        if(workbook != null){  
            for(int sheetNum = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){  
                //获得当前sheet工作表  
                Sheet sheet = workbook.getSheetAt(sheetNum);  
                if(sheet == null){  
                    continue;  
                }  
                //获得当前sheet的开始行  
                int firstRowNum  = sheet.getFirstRowNum();  
                //获得当前sheet的结束行  
                int lastRowNum = sheet.getLastRowNum();  
                //循环除了第一行的所有行  
                for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++){  
                    //获得当前行  
                    Row row = sheet.getRow(rowNum);  
                    if(row == null){  
                        continue;  
                    }  
                    //获得当前行的开始列  
                    int firstCellNum = row.getFirstCellNum();  
                    //获得当前行的列数  
                    int lastCellNum = row.getPhysicalNumberOfCells();  
                    String[] cells = new String[row.getPhysicalNumberOfCells()];  
                    //循环当前行  
                    for(int cellNum = firstCellNum; cellNum < lastCellNum;cellNum++){  
                        Cell cell = row.getCell(cellNum);  
                        cells[cellNum] = getCellValue(cell);  
                    }  
                    list.add(cells);  
                }  
            }  
        }  
        return list;  
    }  
    private static Workbook getWorkBook(File file) {  
        //获得文件名  
        String fileName = file.getName();  
        //创建Workbook工作薄对象，表示整个excel  
        Workbook workbook = null;  
        try {  
            //获取excel文件的io流  
            InputStream is = new FileInputStream(file);  
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象  
            if(fileName.endsWith("xls")){  
                //2003  
                workbook = new HSSFWorkbook(is);  
            }else if(fileName.endsWith("xlsx")){  
                //2007  
                workbook = new XSSFWorkbook(is);  
            }  
        } catch (IOException e) {  
            log.info(e.getMessage());  
        }  
        return workbook;  
    }  
    public static String getCellValue(Cell cell){  
        String cellValue = "";  
        if(cell == null){  
            return cellValue;  
        }  
        //把数字当成String来读，避免出现1读成1.0的情况  
        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){  
            cell.setCellType(Cell.CELL_TYPE_STRING);  
        }  
        //判断数据的类型  
        switch (cell.getCellType()){  
            case Cell.CELL_TYPE_NUMERIC: //数字  
                cellValue = String.valueOf(cell.getNumericCellValue());  
                break;  
            case Cell.CELL_TYPE_STRING: //字符串  
                cellValue = String.valueOf(cell.getStringCellValue());  
                break;  
            case Cell.CELL_TYPE_BOOLEAN: //Boolean  
                cellValue = String.valueOf(cell.getBooleanCellValue());  
                break;  
            case Cell.CELL_TYPE_FORMULA: //公式  
                cellValue = String.valueOf(cell.getCellFormula());  
                break;  
            case Cell.CELL_TYPE_BLANK: //空值   
                cellValue = "";  
                break;  
            case Cell.CELL_TYPE_ERROR: //故障  
                cellValue = "非法字符";  
                break;  
            default:  
                cellValue = "未知类型";  
                break;  
        }  
        return cellValue;  
    }  
    
    
    /**
     * 列头单元格样式
     */
    private static XSSFCellStyle getColumnTopStyle(XSSFWorkbook workbook) {
 
        // 设置字体
        XSSFFont font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short) 11);
        //字体加粗
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        //设置字体名字
        font.setFontName("Courier New");
        //设置样式;
        XSSFCellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        //设置底边框颜色;
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        //设置左边框;
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        //设置左边框颜色;
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        //设置右边框;
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        //设置右边框颜色;
        style.setRightBorderColor(HSSFColor.BLACK.index);
        //设置顶边框;
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        //设置顶边框颜色;
        style.setTopBorderColor(HSSFColor.BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
 
        return style;
 
    }
 
    /*
     * 列数据信息单元格样式
     */
    private static XSSFCellStyle getStyle(XSSFWorkbook workbook) {
        // 设置字体
        XSSFFont font = workbook.createFont();
        //设置字体大小
        //font.setFontHeightInPoints((short)10);
        //字体加粗
        //font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        //设置字体名字
        font.setFontName("Courier New");
        //设置样式;
        XSSFCellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        //设置底边框颜色;
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        //设置左边框;
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        //设置左边框颜色;
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        //设置右边框;
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        //设置右边框颜色;
        style.setRightBorderColor(HSSFColor.BLACK.index);
        //设置顶边框;
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        //设置顶边框颜色;
        style.setTopBorderColor(HSSFColor.BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
 
        return style;
 
    }

    public static void main(String [] argv) throws IOException {
        Integer n = 9;
        Integer arr[][] = new Integer[n][n];
        Integer title[] = new Integer[n];
        for(Integer i = 1; i < n+1; i++) {
            title[i-1] = i;
            arr[i-1] = new Integer[n];
            for(Integer j = 1; j < n + 1 - i; j++) {
                arr[i-1][j-1] = i * j;
            }
        }
        String path = "/home/walker/e/testExcel.xls";
        saveToExcel(arr, title, n+"x"+n, path);

        List<String[]> list = readExcel(new File(path));
        for(String[] ss : list) {
            Tools.out(ss);
        }
    }

}
