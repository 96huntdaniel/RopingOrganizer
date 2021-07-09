import java.io.*;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;


public class ExcelWriter {
    public static void main(String[] args) throws Exception
    {


    }



    public static void populateEntries(ArrayList partnerData) throws IOException {
        File myFile = new File(System.getProperty("user.dir"), "InputWorkbook.xlsx");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(myFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Finds the workbook instance for XLSX file
        XSSFWorkbook myWorkBook = null;
        try {
            myWorkBook = new XSSFWorkbook(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // spreadsheet object
        XSSFSheet spreadsheet = myWorkBook.getSheetAt(1);
        CellStyle numericStyle = myWorkBook.createCellStyle();
        //numericStyle.setDataFormat();
        //spreadsheet.setDefaultColumnStyle(3, numericStyle);
        // creating a row object
        XSSFRow row;

        // This data needs to be written (Object[])
        Map<String, Object[]> roperData
                = new TreeMap<String, Object[]>();

        roperData.put(
                "0",
                new Object[] { "Header", "Heeler", "Total Rank", "Team Number" });

        System.out.println("Excel writer partner data: " + partnerData);
        //Collections.shuffle(partnerData);
        for(int i = 0; i < partnerData.size(); i++) {
            String[] splitNames = partnerData.get(i).toString().split("\\s+");
            String headerName = splitNames[0] + " " + splitNames[1];
            String heelerName = splitNames[3] + " " + splitNames[4];
            String teamNumber = splitNames[6];
            String totalRank = String.valueOf(Float.valueOf(splitNames[2]) + Float.valueOf(splitNames[5]));
            roperData.put(String.valueOf(i+1), new Object[] {headerName, heelerName, totalRank, teamNumber});
        }

        Set<String> keyid = roperData.keySet();

        int rowid = 0;

        // writing the data into the sheets...

        for (String key : keyid) {

            row = spreadsheet.createRow(rowid++);
            Object[] objectArr = roperData.get(key);
            int cellid = 0;

            for (Object obj : objectArr) {
                Cell cell = row.createCell(cellid++);
                cell.setCellValue((String)obj);
            }
        }

        // .xlsx is the format for Excel Sheets...
        // writing the workbook into the file...
        FileOutputStream out = new FileOutputStream(
                new File("OutputWorkbook.xlsx"));

        myWorkBook.write(out);
        out.close();
    }
}
