import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

import static org.apache.poi.ss.usermodel.CellType.*;

public class ExcelReader {
    public static void main(String[] args) throws IOException {
        ArrayList partners =new ArrayList();
        ArrayList headerNames = new ArrayList();
        ArrayList heelerNames = new ArrayList();
        ArrayList headerDraw2 = new ArrayList();
        ArrayList heelerDraw2 = new ArrayList();
        ArrayList headerDraw3 = new ArrayList();
        ArrayList heelerDraw3 = new ArrayList();
        File myFile = new File("C:\\Users\\Daniel\\Documents\\2616C600.xlsx");
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

        // Return first sheet from the XLSX workbook
        XSSFSheet rawEntries = myWorkBook.getSheetAt(0);

        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = rawEntries.iterator();


        // Traversing over each row of XLSX file
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if(row.getRowNum() == 0) {
                //let's skip our workbook header
                continue;
            }
            //pull the header's name from the first two cells
            if(row.getCell(0) != null && row.getCell(0).getCellType() != BLANK) {
                if(!headerNames.contains(row.getCell(0).toString() + " " + row.getCell(1).toString() + " " + row.getCell(2))) {
                    headerNames.add(row.getCell(0).toString() + " " + row.getCell(1).toString() + " " + row.getCell(2));
                }
                if(row.getCell(3) != null && row.getCell(3).getCellType() != BLANK) {
                    //header has a heeler. put header into the draw2
                    headerDraw2.add(row.getCell(0).toString() + " " + row.getCell(1).toString() + " " + row.getCell(2));
                }
                else {
                    //they entered as a header without a partner. put them in the draw 3
                    headerDraw3.add(row.getCell(0).toString() + " " + row.getCell(1).toString() + " " + row.getCell(2));
                }

            }
            //pull the heeler's name
            if(row.getCell(3) != null && row.getCell(3).getCellType() != BLANK) {
                if(!heelerNames.contains(row.getCell(3).toString() + " " + row.getCell(4).toString() + " " + row.getCell(5))) {
                    heelerNames.add(row.getCell(3).toString() + " " + row.getCell(4).toString() + " " + row.getCell(5));
                }

                if(row.getCell(0) != null && row.getCell(1).getCellType() != BLANK) {
                    //heeler has a header. put heeler into the draw2
                    heelerDraw2.add(row.getCell(3).toString() + " " + row.getCell(4).toString() + " " + row.getCell(5));
                }
                else {
                    //they entered as a header without a partner. put them in the draw 3
                    heelerDraw3.add(row.getCell(3).toString() + " " + row.getCell(4).toString() + " " + row.getCell(5));
                }
            }

            if(row.getCell(0) != null && row.getCell(3) != null && row.getCell(0).getCellType() != BLANK && row.getCell(3).getCellType() != BLANK ) {
                    System.out.println(row.getCell(0).toString() + " " + row.getCell(1).toString()
                            + " and " + row.getCell(3).toString() + " "
                            + row.getCell(4).toString() + " have entered together.");
                    partners.add(row.getCell(0).toString() + " " + row.getCell(1).toString() + " " + row.getCell(2)
                            + " " + row.getCell(3).toString() + " " + row.getCell(4).toString() + " " + row.getCell(5));
                }
                    else {
                        //add to draw 3
                }
            }
        //System.out.println(headerNames);
        //System.out.println("-----");
        //System.out.println(heelerNames);
        //remove the excel sheet header row real quick
        System.out.println("Header Draw 2: " + headerDraw2);
        System.out.println("Header Draw 3: " + headerDraw3);
        System.out.println("Heeler Draw 2: " + heelerDraw2);
        System.out.println("Heeler Draw 3: " + heelerDraw3);

        partners = generatePartners(headerDraw2, headerDraw3, heelerDraw2, heelerDraw3, headerNames, heelerNames, partners);
        for(Object partner : partners) {
            System.out.println(partner.toString());
        }

        ExcelWriter.populateEntries(partners);

    }

    public static ArrayList<String> generatePartners(ArrayList headerDraw2, ArrayList headerDraw3, ArrayList heelerDraw2, ArrayList heelerDraw3, ArrayList headerNames,
                                                     ArrayList heelerNames, ArrayList partners) {
        //function to take our draw 2 and draw 3 arrays and pair everyone up

        //start with header draw 2
        System.out.println("HEADER DRAW 2");
       for(int i = 0; i < headerDraw2.size(); i++) {
           for(int j = 0; j < 2; j++) {
               String rank1 = headerDraw2.get(i).toString().substring(headerDraw2.get(i).toString().length() - 3);
               //for each header that needs two partners, draw 2 heelers
               boolean looping = true;
               while (true) {
                   String partner = getRandomPartner(heelerNames);
                   String[] splitNames = (headerDraw2.get(i).toString() + " " + partner).split("\\s+");
                   String headerName = splitNames[0] + " " + splitNames[1];
                   String heelerName = splitNames[3] + " " + splitNames[4];

                   //Collections.frequency(partners, )
                   System.out.println("Header name: " + headerName + " Heeler name: " + heelerName);
                   float rank2 = Float.parseFloat(partner.substring(partner.length() - 3));
                   if (partners.contains(headerDraw2.get(i).toString() + " " + partner)) {
                       System.out.println("Tried to add... " + headerDraw2.get(i).toString() + " " + partner + " but that entry already exists.");
                   } else if(Float.parseFloat(rank1) + rank2 > 9.5) {
                       System.out.println("Tried " + headerDraw2.get(i).toString() + " " + partner
                               + " which exceeds 9.5");
                       //continue;
                   } else if (partner.equals(headerDraw2.get(i).toString())) {
                       System.out.println("Same person, trying again. Tried " + partner + " and " + headerDraw2.get(i).toString());
                       //continue;
                   }  else {
                       System.out.println("Valid response. Adding " + headerDraw2.get(i).toString() + " " + partner);
                       partners.add(headerDraw2.get(i).toString() + " " + partner);
                       looping = false;
                       break;
                   }
               }
           }
       }

       //now header draw 3
        System.out.println("HEADER DRAW 3");
        for(int i = 0; i < headerDraw3.size(); i++) {
            for(int j = 0; j < 3; j++) {
                String rank1 = headerDraw3.get(i).toString().substring(headerDraw3.get(i).toString().length() - 3);
                //for each header that needs two partners, draw 2 heelers
                boolean looping = true;
                while (true) {
                    String partner = getRandomPartner(heelerNames);
                    float rank2 = Float.parseFloat(partner.substring(partner.length() - 3));
                    if (partners.contains(headerDraw3.get(i).toString() + " " + partner)) {
                        System.out.println("Tried to add... " + headerDraw3.get(i).toString() + " " + partner + " but that entry already exists.");
                        //continue;
                    } else if(Float.parseFloat(rank1) + rank2 > 9.5) {
                        System.out.println("Tried " + headerDraw3.get(i).toString() + " " + partner
                                + " which exceeds 9.5");
                        //continue;
                    } else if (partner.equals(headerDraw3.get(i).toString())) {
                        System.out.println("Same person, trying again. Tried " + partner + " and " + headerDraw3.get(i).toString());
                        //continue;
                    } else {
                        System.out.println("Valid response. Adding " + headerDraw3.get(i).toString() + " " + partner);
                        partners.add(headerDraw3.get(i).toString() + " " + partner);
                        looping = false;
                        break;
                    }
                }
            }
        }

        //now heeler draw 2
        System.out.println("HEELER DRAW 2");
        for(int i = 0; i < heelerDraw2.size(); i++) {
            for(int j = 0; j < 2; j++) {
                String rank1 = heelerDraw2.get(i).toString().substring(heelerDraw2.get(i).toString().length() - 3);
                //for each heeler that needs 2 partners, draw 2 headers
                boolean looping = true;
                while (true) {
                    String partner = getRandomPartner(headerNames);
                    float rank2 = Float.parseFloat(partner.substring(partner.length() - 3));
                    if (partners.contains(partner + " " + heelerDraw2.get(i).toString())) {
                        System.out.println("Tried to add... " + partner + " " + heelerDraw2.get(i).toString() + " but that entry already exists.");
                        //continue;
                    } else if(Float.parseFloat(rank1) + rank2 > 9.5) {
                        System.out.println("Tried " + partner + " " + heelerDraw2.get(i).toString() + " which exceeds 9.5");
                        //continue;
                    } else if (partner.equals(heelerDraw2.get(i).toString())) {
                        System.out.println("Same person, trying again. Tried " + partner + " and " + heelerDraw2.get(i).toString());
                        //continue;
                    } else {
                        System.out.println("Valid response. Adding " + partner + " " + heelerDraw2.get(i).toString());
                        partners.add(partner + " " + heelerDraw2.get(i).toString());
                        looping = false;
                        break;
                    }
                }
            }
        }

        //now heeler draw 3
        System.out.println("HEELER DRAW 3");
        for(int i = 0; i < heelerDraw3.size(); i++) {
            for(int j = 0; j < 3; j++) {
                String rank1 = heelerDraw3.get(i).toString().substring(heelerDraw3.get(i).toString().length() - 3);
                //for each heeler that needs 3 partners, draw 3 headers
                boolean looping = true;
                while (looping) {
                    String partner = getRandomPartner(headerNames);
                    float rank2 = Float.parseFloat(partner.substring(partner.length() - 3));
                    if (partners.contains(partner + " " + heelerDraw3.get(i).toString())) {
                        System.out.println("Tried to add... " + partner + " " + heelerDraw3.get(i).toString() + " but that entry already exists.");
                        //continue;
                    } else if(Float.parseFloat(rank1) + rank2 > 9.5) {
                        System.out.println("Tried " + partner + " " + heelerDraw3.get(i).toString() + " which exceeds 9.5");
                        //continue;
                    } else if (partner.equals(heelerDraw3.get(i).toString())) {
                        System.out.println("Same person, trying again. Tried " + partner + " and " + heelerDraw3.get(i).toString());
                        //continue;
                    }  else {
                        System.out.println("Valid response. Adding " + partner + " " + heelerDraw3.get(i).toString());
                        partners.add(partner  + " " + heelerDraw3.get(i).toString());
                        break;
                    }

                }
            }
        }
        return partners;
    }

    public static String getRandomPartner(ArrayList positionNames) {
        int index = (int)(Math.random() * positionNames.size());
        String partner = (String) positionNames.get(index);

        return partner;
    }



}
