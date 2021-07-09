import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
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
    public static void main(String[] args) throws IOException, InvalidFormatException {
        ArrayList partners = new ArrayList();
        ArrayList headerNames = new ArrayList();
        ArrayList heelerNames = new ArrayList();
        ArrayList headerDraw2 = new ArrayList();
        ArrayList heelerDraw2 = new ArrayList();
        ArrayList headerDraw3 = new ArrayList();
        ArrayList heelerDraw3 = new ArrayList();
        float maxRank = (float) 9.5;
        File myFile = new File(System.getProperty("user.dir"), "InputWorkbook.xlsx");
        System.out.println("File: " + myFile);
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
            if (row.getRowNum() == 0) {
                maxRank = Float.valueOf(String.valueOf(row.getCell(8)));
                System.out.println("Max Rank: " + row.getCell(8).toString());
                //let's skip our workbook header
                continue;
            }
            //pull the header's name from the first two cells
            if (row.getCell(0) != null && row.getCell(0).getCellType() != BLANK) {
                if (!headerNames.contains(row.getCell(0).toString() + " " + row.getCell(1).toString() + " " + row.getCell(2))) {
                    headerNames.add(row.getCell(0).toString() + " " + row.getCell(1).toString() + " " + row.getCell(2));
                }
                if (row.getCell(3) != null && row.getCell(3).getCellType() != BLANK) {
                    //header has a heeler. put header into the draw2
                    headerDraw2.add(row.getCell(0).toString() + " " + row.getCell(1).toString() + " " + row.getCell(2));
                } else {
                    //they entered as a header without a partner. put them in the draw 3
                    headerDraw3.add(row.getCell(0).toString() + " " + row.getCell(1).toString() + " " + row.getCell(2));
                }

            }
            //pull the heeler's name
            if (row.getCell(3) != null && row.getCell(3).getCellType() != BLANK) {
                if (!heelerNames.contains(row.getCell(3).toString() + " " + row.getCell(4).toString() + " " + row.getCell(5))) {
                    heelerNames.add(row.getCell(3).toString() + " " + row.getCell(4).toString() + " " + row.getCell(5));
                }

                if (row.getCell(0) != null && row.getCell(1).getCellType() != BLANK) {
                    //heeler has a header. put heeler into the draw2
                    heelerDraw2.add(row.getCell(3).toString() + " " + row.getCell(4).toString() + " " + row.getCell(5));
                } else {
                    //they entered as a header without a partner. put them in the draw 3
                    heelerDraw3.add(row.getCell(3).toString() + " " + row.getCell(4).toString() + " " + row.getCell(5));
                }
            }

            if (row.getCell(0) != null && row.getCell(3) != null && row.getCell(0).getCellType() != BLANK && row.getCell(3).getCellType() != BLANK) {
                System.out.println(row.getCell(0).toString() + " " + row.getCell(1).toString()
                        + " and " + row.getCell(3).toString() + " "
                        + row.getCell(4).toString() + " have entered together.");
                partners.add(row.getCell(0).toString() + " " + row.getCell(1).toString() + " " + row.getCell(2)
                        + " " + row.getCell(3).toString() + " " + row.getCell(4).toString() + " " + row.getCell(5));
            } else {
                //add to draw 3
            }
        }
        //System.out.println(headerNames);
        //System.out.println("-----");
        //System.out.println(heelerNames);
        //remove the excel sheet header row real quick
        headerDraw3 = ArraySorter.sortArray(headerDraw3, "rank");
        headerDraw2 = ArraySorter.sortArray(headerDraw2, "rank");
        heelerDraw3 = ArraySorter.sortArray(heelerDraw3, "rank");
        heelerDraw2 = ArraySorter.sortArray(heelerDraw2, "rank");
        System.out.println("Header Draw 2: " + headerDraw2);
        System.out.println("Header Draw 3: " + headerDraw3);
        System.out.println("Heeler Draw 2: " + heelerDraw2);
        System.out.println("Heeler Draw 3: " + heelerDraw3);

        partners = generatePartners(headerDraw2, headerDraw3, heelerDraw2, heelerDraw3, headerNames, heelerNames, partners, maxRank);
        for (Object partner : partners) {
            System.out.println(partner.toString());
        }

        ExcelWriter.populateEntries(partners);
        WordWriter.generatePrintoff(partners, headerDraw2, headerDraw3, heelerDraw2, heelerDraw3);

    }

    public static ArrayList<String> generatePartners(ArrayList headerDraw2, ArrayList headerDraw3, ArrayList heelerDraw2, ArrayList heelerDraw3, ArrayList headerNames,
                                                     ArrayList heelerNames, ArrayList partners, float maxRank) {
        //function to take our draw 2 and draw 3 arrays and pair everyone up

        if (headerNames.size() < heelerNames.size()) {
            if (headerDraw2.size() < headerDraw3.size()) {
                System.out.println("HEADER DRAW 2");
                for (int i = 0; i < headerDraw2.size(); i++) {
                    for (int j = 0; j < 2; j++) {
                        String rank1 = headerDraw2.get(i).toString().substring(headerDraw2.get(i).toString().length() - 3);
                        //for each header that needs 2 partners, draw 2 heelers
                        while (true) {
                            String partner = getRandomPartner(heelerNames);
                            float rank2 = Float.parseFloat(partner.substring(partner.length() - 3));
                            if (partners.contains(headerDraw2.get(i).toString() + " " + partner)) {
                                System.out.println("Tried to add... " + headerDraw2.get(i).toString() + " " + partner + " but that entry already exists.");
                                //continue;
                            } else if (Float.parseFloat(rank1) + rank2 > maxRank) {
                                System.out.println("Tried " + headerDraw2.get(i).toString() + " " + partner + " which exceeds " + maxRank);
                                //continue;
                            } else if (partner.equals(headerDraw2.get(i).toString())) {
                                System.out.println("Same person, trying again. Tried " + headerDraw2.get(i).toString() + " and " + partner);
                                //continue;
                            } else if (isMaxRuns(partner, "heeler", heelerNames, heelerDraw2, heelerDraw3, partners) && j < heelerNames.size() - 1) {
                                System.out.println("This person has reached their max runs. Trying again.");
                            } else {
                                System.out.println("Valid response. Adding " + headerDraw2.get(i).toString() + " " + partner);
                                partners.add(headerDraw2.get(i).toString() + " " + partner);
                                //System.out.println("Attempting removal of " + partner);
                                //attemptRemoval(partner, "heeler", heelerNames, heelerDraw2, heelerDraw3, partners);
                                break;
                            }
                        }
                        //we have two partners for each draw2 entry now
                    }
                }
                //now header draw 3
                //base off this one
                //Second one------------
                System.out.println("HEADER DRAW 3");
                int attempts = 1000;
                ArrayList totalRuns = (ArrayList) heelerNames.clone();
                for (int i = 0; i < headerDraw3.size(); i++) {
                    ArrayList tempArray = new ArrayList();
                    tempArray = (ArrayList) heelerNames.clone();
                    for (int j = 0; j < 3; j++) {
                        String rank1 = headerDraw3.get(i).toString().substring(headerDraw3.get(i).toString().length() - 3);
                        //for each header that needs 2 partners, draw 2 headers
                        while (true) {
                            if (tempArray.size() < 1) {
                                break;
                            }
                            String partner = getRandomPartner(heelerNames);
                            float rank2 = Float.parseFloat(partner.substring(partner.length() - 3));
                            if (partners.contains(headerDraw3.get(i).toString() + " " + partner)) {
                                System.out.println("Tried to add... " + headerDraw3.get(i).toString() + " " + partner + " but that entry already exists.");
                                //continue;
                                attempts--;
                                tempArray.remove(partner);
                            } else if (Float.parseFloat(rank1) + rank2 > maxRank) {
                                System.out.println("Tried " + headerDraw3.get(i).toString() + " " + partner + " which exceeds " + maxRank);
                                //continue;
                                attempts--;
                                tempArray.remove(partner);
                            } else if (partner.equals(headerDraw3.get(i).toString())) {
                                System.out.println("Same person, trying again. Tried " + partner + " and " + headerDraw3.get(i).toString());
                                //continue;
                                attempts--;
                                tempArray.remove(partner);
                            } else if (isMaxRuns(partner, "heeler", heelerNames, heelerDraw2, heelerDraw3, partners) && j < heelerNames.size() - 1) {
                                System.out.println("This person has reached their max runs. Trying again.");
                                attempts--;
                                totalRuns.remove(partner);
                                tempArray.remove(partner);
                            } else {
                                System.out.println("Valid response. Adding " + headerDraw3.get(i).toString() + " " + partner);
                                partners.add(headerDraw3.get(i).toString() + " " + partner);
                                //System.out.println("Attempting removal of " + partner);
                                //attemptRemoval(partner, "heeler", heelerNames, heelerDraw2, heelerDraw3, partners);
                                break;
                            }
                        }
                    }
                }
                partners = cleanup("headerDraw2", totalRuns, heelerDraw2, heelerDraw3, headerDraw2, headerDraw3,
                        maxRank, headerNames, heelerNames, partners);
            } else if (headerDraw2.size() > headerDraw3.size()) {
                //now header draw 3
                System.out.println("HEADER DRAW 3");
                for (int i = 0; i < headerDraw3.size(); i++) {
                    for (int j = 0; j < 3; j++) {
                        String rank1 = headerDraw3.get(i).toString().substring(headerDraw3.get(i).toString().length() - 3);
                        //for each header that needs 2 partners, draw 2 headers
                        while (true) {
                            String partner = getRandomPartner(heelerNames);
                            float rank2 = Float.parseFloat(partner.substring(partner.length() - 3));
                            if (partners.contains(headerDraw3.get(i).toString() + " " + partner)) {
                                System.out.println("Tried to add... " + headerDraw3.get(i).toString() + " " + partner + " but that entry already exists.");
                                //continue;
                            } else if (Float.parseFloat(rank1) + rank2 > maxRank) {
                                System.out.println("Tried " + headerDraw3.get(i).toString() + " " + partner + " which exceeds " + maxRank);
                                //continue;
                            } else if (partner.equals(headerDraw3.get(i).toString())) {
                                System.out.println("Same person, trying again. Tried " + partner + " and " + headerDraw3.get(i).toString());
                                //continue;
                            } else if (isMaxRuns(partner, "heeler", heelerNames, heelerDraw2, heelerDraw3, partners) && j < heelerNames.size() - 1) {
                                System.out.println("This person has reached their max runs. Trying again.");
                            } else {
                                System.out.println("Valid response. Adding " + headerDraw3.get(i).toString() + " " + partner);
                                partners.add(headerDraw3.get(i).toString() + " " + partner);
                                //System.out.println("Attempting removal of " + partner);
                                //attemptRemoval(partner, "heeler", heelerNames, heelerDraw2, heelerDraw3, partners);
                                break;
                            }
                        }
                    }
                }
                //Second one-----------
                System.out.println("HEADER DRAW 2");
                int attempts = 10000;
                ArrayList totalRuns = (ArrayList) heelerNames.clone();
                for (int i = 0; i < headerDraw2.size(); i++) {
                    ArrayList tempArray = new ArrayList();
                    tempArray = (ArrayList) heelerNames.clone();
                    for (int j = 0; j < 2; j++) {
                        String rank1 = headerDraw2.get(i).toString().substring(headerDraw2.get(i).toString().length() - 3);
                        //for each header that needs 2 partners, draw 2 heelers
                        while (true) {
                            if (tempArray.size() < 1) {
                                break;
                            }
                            String partner = getRandomPartner(heelerNames);
                            float rank2 = Float.parseFloat(partner.substring(partner.length() - 3));
                            if (partners.contains(headerDraw2.get(i).toString() + " " + partner)) {
                                attempts--;
                                System.out.println("Tried to add... " + headerDraw2.get(i).toString() + " " + partner + " but that entry already exists." + attempts);
                                if (attempts < 1) {
                                    break;
                                }
                                tempArray.remove(partner);
                                //continue;

                            } else if (Float.parseFloat(rank1) + rank2 > maxRank) {
                                System.out.println("Tried " + headerDraw2.get(i).toString() + " " + partner + " which exceeds " + maxRank);
                                //continue;
                                attempts--;
                                if (attempts < 1) {
                                    break;
                                }
                                tempArray.remove(partner);
                            } else if (partner.equals(headerDraw2.get(i).toString())) {
                                System.out.println("Same person, trying again. Tried " + headerDraw2.get(i).toString() + " and " + partner);
                                //continue;
                                attempts--;
                                if (attempts < 1) {
                                    break;
                                }
                                tempArray.remove(partner);
                            } else if (isMaxRuns(partner, "heeler", heelerNames, heelerDraw2, heelerDraw3, partners) && j < heelerNames.size() - 1) {
                                System.out.println("This person has reached their max runs. Trying again.");
                                attempts--;
                                totalRuns.remove(partner);
                                tempArray.remove(partner);
                            } else {
                                System.out.println("Valid response. Adding " + headerDraw2.get(i).toString() + " " + partner);
                                partners.add(headerDraw2.get(i).toString() + " " + partner);
                                //System.out.println("Attempting removal of " + partner);
                                //attemptRemoval(partner, "heeler", heelerNames, heelerDraw2, heelerDraw3, partners);
                                break;
                            }
                        }
                        //we have two partners for each draw2 entry now
                    }
                }
                partners = cleanup("headerDraw2", totalRuns, heelerDraw2, heelerDraw3, headerDraw2, headerDraw3,
                        maxRank, headerNames, heelerNames, partners);
            }

        } else if (heelerNames.size() < headerNames.size()) {
            //more heelers than headers, so headers will have extra runs.
            //now heeler draw 2
            if (heelerDraw2.size() < heelerDraw3.size()) {
                //there are more in the draw 3 array, so loop through the draw 2 until we get 2 partners for each entry
                System.out.println("HEELER DRAW 2");
                ArrayList totalRuns = (ArrayList) headerNames.clone();
                for (int i = 0; i < heelerDraw2.size(); i++) {
                    for (int j = 0; j < 2; j++) {
                        String rank1 = heelerDraw2.get(i).toString().substring(heelerDraw2.get(i).toString().length() - 3);
                        //for each heeler that needs 2 partners, draw 2 headers
                        while (true) {
                            String partner = getRandomPartner(headerNames);
                            float rank2 = Float.parseFloat(partner.substring(partner.length() - 3));
                            if (partners.contains(partner + " " + heelerDraw2.get(i).toString())) {
                                System.out.println("Tried to add... " + partner + " " + heelerDraw2.get(i).toString() + " but that entry already exists.");
                                //continue;
                            } else if (Float.parseFloat(rank1) + rank2 > maxRank) {
                                System.out.println("Tried " + partner + " " + heelerDraw2.get(i).toString() + " which exceeds " + maxRank);
                                //continue;
                            } else if (partner.equals(heelerDraw2.get(i).toString())) {
                                System.out.println("Same person, trying again. Tried " + partner + " and " + heelerDraw2.get(i).toString());
                                //continue;
                            } else if (isMaxRuns(partner, "header", headerNames, headerDraw2, headerDraw3, partners) && j < headerNames.size() - 1) {
                                System.out.println("This person has reached their max runs. Trying again.");
                            } else {
                                System.out.println("Valid response. Adding " + partner + " " + heelerDraw2.get(i).toString());
                                partners.add(partner + " " + heelerDraw2.get(i).toString());
                                //System.out.println("Attempting removal of " + partner);
                                //attemptRemoval(partner, "header", headerNames, headerDraw2, headerDraw3, partners);
                                break;
                            }
                        }
                        //we have two partners for each draw2 entry now
                    }
                }
                //now heeler draw 3
                System.out.println("HEELER DRAW 3");
                int attempts = 10000;
                totalRuns = (ArrayList) headerNames.clone();
                for (int i = 0; i < heelerDraw3.size(); i++) {
                    ArrayList tempArray = new ArrayList();
                    tempArray = (ArrayList) headerNames.clone();
                    for (int j = 0; j < 3; j++) {
                        String rank1 = heelerDraw3.get(i).toString().substring(heelerDraw3.get(i).toString().length() - 3);
                        //for each heeler that needs 3 partners, draw 3 headers
                        while (true) {
                            if (tempArray.size() < 1) {
                                break;
                            }
                            String partner = getRandomPartner(headerNames);
                            float rank2 = Float.parseFloat(partner.substring(partner.length() - 3));
                            if (partners.contains(partner + " " + heelerDraw3.get(i).toString())) {
                                System.out.println("Tried to add... " + partner + " " + heelerDraw3.get(i).toString() + " but that entry already exists.");
                                attempts--;
                                if (attempts < 1) {
                                    break;
                                }
                                tempArray.remove(partner);
                            } else if (Float.parseFloat(rank1) + rank2 > maxRank) {
                                System.out.println("Tried " + partner + " " + heelerDraw3.get(i).toString() + " which exceeds " + maxRank);
                                attempts--;
                                if (attempts < 1) {
                                    break;
                                }
                                tempArray.remove(partner);
                            } else if (partner.equals(heelerDraw3.get(i).toString())) {
                                System.out.println("Same person, trying again. Tried " + partner + " and " + heelerDraw3.get(i).toString());
                                attempts--;
                                if (attempts < 1) {
                                    break;
                                }
                                tempArray.remove(partner);
                            } else if (isMaxRuns(partner, "header", headerNames, headerDraw2, headerDraw3, partners) && j < headerNames.size() - 1) {
                                System.out.println("This person has reached their max runs. Trying again.");
                                totalRuns.remove(partner);
                                tempArray.remove(partner);
                                attempts--;
                                if (attempts < 1) {
                                    break;
                                }
                            } else {
                                System.out.println("Valid response. Adding " + partner + " " + heelerDraw3.get(i).toString());
                                partners.add(partner + " " + heelerDraw3.get(i).toString());
                                tempArray.remove(partner);
                                //System.out.println("Attempting removal of " + partner);
                                //attemptRemoval(partner, "header", headerNames, headerDraw2, headerDraw3, partners);
                                break;
                            }
                        }
                    }
                }
                partners = cleanup("heelerDraw3", totalRuns, heelerDraw2, heelerDraw3, headerDraw2, headerDraw3,
                        maxRank, headerNames, heelerNames, partners);
            } else if (heelerDraw2.size() > heelerDraw3.size()) {
                //there are more in the draw 2 array, so loop through the draw 3 until we get 3 partners for each entry
                //now heeler draw 3
                System.out.println("HEELER DRAW 3");
                for (int i = 0; i < heelerDraw3.size(); i++) {
                    for (int j = 0; j < 3; j++) {
                        String rank1 = heelerDraw3.get(i).toString().substring(heelerDraw3.get(i).toString().length() - 3);
                        //for each heeler that needs 3 partners, draw 3 headers
                        while (true) {
                            String partner = getRandomPartner(headerNames);
                            float rank2 = Float.parseFloat(partner.substring(partner.length() - 3));
                            if (partners.contains(partner + " " + heelerDraw3.get(i).toString())) {
                                System.out.println("Tried to add... " + partner + " " + heelerDraw3.get(i).toString() + " but that entry already exists.");
                                //continue;
                            } else if (Float.parseFloat(rank1) + rank2 > maxRank) {
                                System.out.println("Tried " + partner + " " + heelerDraw3.get(i).toString() + " which exceeds " + maxRank);
                                //continue;
                            } else if (partner.equals(heelerDraw3.get(i).toString())) {
                                System.out.println("Same person, trying again. Tried " + partner + " and " + heelerDraw3.get(i).toString());
                                //continue;
                            } else if (isMaxRuns(partner, "header", headerNames, headerDraw2, headerDraw3, partners) && j < headerNames.size() - 1) {
                                System.out.println("This person has reached their max runs. Trying again.");
                            } else {
                                System.out.println("Valid response. Adding " + partner + " " + heelerDraw3.get(i).toString());
                                partners.add(partner + " " + heelerDraw3.get(i).toString());
                                //System.out.println("Attempting removal of " + partner);
                                //attemptRemoval(partner, "header", headerNames, headerDraw2, headerDraw3, partners);
                                break;
                            }
                        }
                    }
                }
                //SECOND ONE-------
                System.out.println("HEELER DRAW 2");
                int attempts = 10000;
                ArrayList totalRuns = (ArrayList) headerNames.clone();
                for (int i = 0; i < heelerDraw2.size(); i++) {
                    //System.out.println(" 2 Total runs: " + totalRuns);
                    //System.out.println("i is " + i + " and heelerDraw2 size is " + heelerDraw2.size());
                    ArrayList tempArray = new ArrayList();
                    tempArray = (ArrayList) headerNames.clone();
                    for (int j = 0; j < 2; j++) {
                        String rank1 = heelerDraw2.get(i).toString().substring(heelerDraw2.get(i).toString().length() - 3);
                        //for each heeler that needs 2 partners, draw 2 headers
                        while (true) {
                            if (tempArray.size() < 1) {
                                break;
                            }
                            System.out.println("Trying to get " + heelerDraw2.get(i).toString() + "'s " + j + " run.");
                            System.out.println(tempArray);
                            //System.out.println(tempArray);
                            String partner = getRandomPartner(tempArray);
                            float rank2 = Float.parseFloat(partner.substring(partner.length() - 3));
                            if (partners.contains(partner + " " + heelerDraw2.get(i).toString())) {
                                System.out.println("Tried to add... " + partner + " " + heelerDraw2.get(i).toString() + " but that entry already exists.");
                                attempts--;
                                if (attempts < 1) {
                                    break;
                                }
                                tempArray.remove(partner);
                            } else if (Float.parseFloat(rank1) + rank2 > maxRank) {
                                System.out.println("Tried " + partner + " " + heelerDraw2.get(i).toString() + " which exceeds " + maxRank + " with " + attempts + " attempts left");
                                attempts--;
                                if (attempts < 1) {
                                    break;
                                }
                                tempArray.remove(partner);
                            } else if (partner.equals(heelerDraw2.get(i).toString())) {
                                System.out.println("Same person, trying again. Tried " + partner + " and " + heelerDraw2.get(i).toString() + " with " + attempts + " attempts left");
                                attempts--;
                                if (attempts < 1) {
                                    break;
                                }
                                tempArray.remove(partner);
                            } else if (isMaxRuns(partner, "header", headerNames, headerDraw2, headerDraw3, partners)) {
                                //System.out.println("Hit max run check. i is " + i + " and headerNames.size()-1 is " + Integer.valueOf(headerNames.size()-1));
                                System.out.println("This person has reached their max runs. Trying again." + " with " + attempts + " attempts left");
                                totalRuns.remove(partner);
                                attempts--;
                                if (attempts < 1) {
                                    break;
                                }
                            } else {
                                System.out.println("Valid response. Adding " + partner + " " + heelerDraw2.get(i).toString());
                                partners.add(partner + " " + heelerDraw2.get(i).toString());
                                //System.out.println("Attempting removal of " + heelerDraw2.get(i).toString() + " " + partner);
                                //attemptRemoval(partner, "header", headerNames, headerDraw2, headerDraw3, partners);
                                tempArray.remove(partner);
                                break;
                            }
                        }
                        //we have two partners for each draw2 entry now
                    }
                }
                partners = cleanup("heelerDraw2", totalRuns, heelerDraw2, heelerDraw3, headerDraw2, headerDraw3,
                        maxRank, headerNames, heelerNames, partners);
            }
        }

        return partners;
    }

    public static ArrayList cleanup(String cleanupFor, ArrayList totalRuns, ArrayList heelerDraw2, ArrayList heelerDraw3,
                                    ArrayList headerDraw2, ArrayList headerDraw3, float maxRank,
                                    ArrayList headerNames, ArrayList heelerNames, ArrayList partners) {
        System.out.println("CLEANUP--------------------------");
        ArrayList maxRunDraw2 = new ArrayList(); //set this to opposite of your cleanupFor
        ArrayList maxRunDraw3 = new ArrayList(); //set this to opposite of your cleanupFor
        ArrayList cloneArray = new ArrayList(); //set this to cleanupFor.clone(); ex: heelerDraw2 would be heelerDraw2.clone();
        ArrayList maxRunNames = new ArrayList(); //set this to the opposite of your cleanUpFor names
        int currentMaxExtras = 0; //keep track of the current highest number of extra runs anyone has
        String isMaxPositionName = new String(); //set this to be opposite of your cleanUpFor position, ex: heelerDraw2 would be "header"
        String getExtrasPosition = null;
        ArrayList extraRunsArray = new ArrayList();
        if(cleanupFor.equals("heelerDraw2")) {
            //coming here from a case where we did heeler draw 3, then heeler draw 2. cleaning up now
            maxRunDraw2 = headerDraw2;
            maxRunDraw3 = headerDraw3;
            cloneArray = (ArrayList) heelerDraw2.clone();
            maxRunNames = headerNames;
            isMaxPositionName = "header";
            getExtrasPosition = "heeler";
        }
        if(cleanupFor.equals("heelerDraw3")) {
            maxRunDraw2 = headerDraw2;
            maxRunDraw3 = headerDraw3;
            cloneArray = (ArrayList) heelerDraw3.clone();
            maxRunNames = headerNames;
            isMaxPositionName = "header";
            getExtrasPosition = "heeler";
        }
        if(cleanupFor.equals("headerDraw2")) {
            maxRunDraw2 = heelerDraw2;
            maxRunDraw3 = heelerDraw3;
            cloneArray = (ArrayList) headerDraw2.clone();
            maxRunNames = heelerNames;
            isMaxPositionName = "heeler";
            getExtrasPosition = "header";
        }
        if(cleanupFor.equals("headerDraw3")) {
            maxRunDraw2 = heelerDraw2;
            maxRunDraw3 = heelerDraw3;
            cloneArray = (ArrayList) headerDraw3.clone();
            maxRunNames = heelerNames;
            isMaxPositionName = "heeler";
            getExtrasPosition = "header";
        }
        for(int x = 0; x < totalRuns.size(); x++) {
            //cleaning up
            int maxRuns = Collections.frequency(maxRunDraw2, totalRuns.get(x)) + Collections.frequency(maxRunDraw3, totalRuns.get(x));
            maxRuns *= 3;
            System.out.println(totalRuns.get(x) + "'s max runs is " + maxRuns);
            ArrayList allEntries = new ArrayList();

            for (int j = 0; j < partners.size(); j++) {
                String[] splitNames = partners.get(j).toString().split("\\s+");
                String headerName = splitNames[0] + " " + splitNames[1] + " " + splitNames[2];
                String heelerName = splitNames[3] + " " + splitNames[4] + " " + splitNames[5];
                if(isMaxPositionName.equals("header")) {
                    allEntries.add(headerName);
                }
                if(isMaxPositionName.equals("heeler")) {
                    allEntries.add(heelerName);
                }
            }
            int runsNeeded = maxRuns - Collections.frequency(allEntries, totalRuns.get(x));
            System.out.println(totalRuns.get(x) + "'s runs needed is " + runsNeeded);
            ArrayList tempArray = (ArrayList) cloneArray.clone();
            for (int y = 0; y < runsNeeded; y++) {
                int attempts = 10000;
                //System.out.println(" 1 Total runs: " + totalRuns);
                String rank1 = totalRuns.get(x).toString().substring(totalRuns.get(x).toString().length() - 3);
                while (true) {
                    if (tempArray.size() < 1) {
                        break;
                    }
                    System.out.println(tempArray);
                    String partner = getRandomPartner(tempArray);
                    System.out.println(totalRuns.get(x).toString() + " picking from " + tempArray);

                    String partnerEntry = null;
                    if(isMaxPositionName.equals("header")) {
                        if(getNumberOfExtraRuns(partner, "heeler", heelerNames, heelerDraw2, heelerDraw3, partners) > currentMaxExtras) {
                            //System.out.println("At least we got here");
                            currentMaxExtras = getNumberOfExtraRuns(partner, "heeler", heelerNames, heelerDraw2, heelerDraw3, partners);
                            //System.out.println("Their number of extras is " + getNumberOfExtraRuns(partner, "heeler", heelerNames, heelerDraw2, heelerDraw3, partners)
                                   // + " and the current is " + currentMaxExtras);
                        }
                        //if you're pulling a header, your partner should go first
                        partnerEntry = totalRuns.get(x).toString() + " " + partner;
                        System.out.println("Partner entry: " + partnerEntry);
                    }

                    if(isMaxPositionName.equals("heeler")) {
                        //System.out.println(partner + " 's extra runs are " + getNumberOfExtraRuns(partner, "header", headerNames, headerDraw2, headerDraw3, partners));
                        if(getNumberOfExtraRuns(partner, "header", headerNames, headerDraw2, headerDraw3, partners) > currentMaxExtras) {
                            System.out.println("At least we got here");
                            currentMaxExtras = getNumberOfExtraRuns(partner, "header", headerNames, headerDraw2, headerDraw3, partners);
                            //System.out.println("Their number of extras is " + getNumberOfExtraRuns(partner, "header", headerNames, headerDraw2, headerDraw3, partners)
                                    //+ " and the current is " + currentMaxExtras);
                        }
                        partnerEntry = partner + " " + totalRuns.get(x).toString();
                    }
                    //System.out.println("We will be checking " + getNumberOfExtraRuns(partner, "header", headerNames, headerDraw2, headerDraw3, partners) + " against " + currentMaxExtras);
                    float rank2 = Float.parseFloat(partner.substring(partner.length() - 3));
                    if (partners.contains(partnerEntry)) {
                        System.out.println("Tried to add... " + partner + " " + totalRuns.get(x).toString() + " but that entry already exists.");
                        tempArray.remove(partner);
                    } else if (Float.parseFloat(rank1) + rank2 > maxRank) {
                        System.out.println("Tried " + partner + " " + totalRuns.get(x).toString() + " which exceeds " + maxRank + " with " + attempts + " attempts left");
                        tempArray.remove(partner);
                        attempts--;
                    } else if (partner.equals(totalRuns.get(x).toString())) {
                        System.out.println("Same person, trying again. Tried " + partner + " and " + totalRuns.get(x).toString() + " with " + attempts + " attempts left");
                        tempArray.remove(partner);
                        attempts--;
                    } else if(Collections.frequency(extraRunsArray, partner) == Collections.frequency(extraRunsArray,ArraySorter.mostCommon(extraRunsArray)) && !extraRunsArray.isEmpty() && attempts > 10) {
                        //they have more than the current max extras
                        //System.out.println(partner + "'s frequency is " + Collections.frequency(extraRunsArray, partner) + " and the most common is " +
                        //        ArraySorter.mostCommon(extraRunsArray) + " with " + Collections.frequency(extraRunsArray,ArraySorter.mostCommon(extraRunsArray)));
                        System.out.println(partner + " had too many extras, removing");
                        //tempArray.remove(partner);
                        attempts--;

                    } else if (isMaxRuns((String) maxRunNames.get(x), isMaxPositionName, maxRunNames, maxRunDraw2, maxRunDraw3, partners) && attempts > 10000) {
                        //System.out.println("Hit max run check. i is " + i + " and headerNames.size()-1 is " + Integer.valueOf(headerNames.size()-1));
                        //System.out.println("This person has reached their max runs. Trying again." + " with " + attempts + " attempts left");
                        //totalRuns.remove(partner);
                        attempts--;
                    } else {
                        System.out.println("Valid response. Adding " + partnerEntry);
                        partners.add(partnerEntry);
                        extraRunsArray.add(partner);
                        tempArray.remove(partner);
                        break;
                    }
                }
            }
        }

        return partners;
    }

    public static String getRandomPartner(ArrayList positionNames) {
        int index = (int) (Math.random() * positionNames.size());
        String partner = (String) positionNames.get(index);

        return partner;
    }

    public static boolean isMaxRuns(String roperName, String positionName, ArrayList positionNames, ArrayList positionDraw2,
                                  ArrayList positionDraw3, ArrayList partnerList) {
        ArrayList headerRuns = new ArrayList();
        ArrayList heelerRuns = new ArrayList();
        for (int i = 0; i < partnerList.size(); i++) {
            String[] splitNames = partnerList.get(i).toString().split("\\s+");
            String headerName = splitNames[0] + " " + splitNames[1] + " " + splitNames[2];
            String heelerName = splitNames[3] + " " + splitNames[4] + " " + splitNames[5];
            headerRuns.add(headerName);
            heelerRuns.add(heelerName);
            //System.out.println("Added " + headerName + " and " + heelerName);
        }
        int numOfEntries = Collections.frequency(positionDraw2, roperName) + Collections.frequency(positionDraw3, roperName);
        System.out.println(roperName + " is in draw 2 " + Collections.frequency(positionDraw2, roperName) + " time and draw 3 " +
                Collections.frequency(positionDraw3, roperName) + " times.");
        if(positionName == "heeler") {
            //if we're here, it means the heelerlist was bigger. We're looping the headerDrawX arrays and trying to remove from heelerNames
            if(numOfEntries == 1) {
                System.out.println("In there once, with " + Collections.frequency(heelerRuns, roperName) + " current runs.");
                if (Collections.frequency(heelerRuns, roperName) == 3) {
                    //System.out.println(roperName + " has three runs. Removing from array.");
                    return true;
                    //System.out.println("Position names: " + positionNames);
                }
            }
            if(numOfEntries == 2) {
                System.out.println("In there twice.");
                if(Collections.frequency(heelerRuns, roperName) == 6) {
                    //System.out.println(roperName + " has six runs. Removing from array.");
                    return true;
                }
            }
            else {
                return false;
            }
        }
        if(positionName == "header") {
            //if we're here, it means the headerlist was bigger. We're looping the heelerDrawX arrays and trying to remove from headerNames
            if(numOfEntries == 1) {
                System.out.println("In there once, with " + Collections.frequency(headerRuns, roperName) + " current runs.");
                if(Collections.frequency(headerRuns, roperName) == 3) {
                    System.out.println(roperName + " has three runs. Removing from array.");
                    return true;
                    //System.out.println("Position names: " + positionNames);
                }
            }
            if(numOfEntries == 2) {
                System.out.println("In there twice.");
                if(Collections.frequency(headerRuns, roperName) == 6) {
                    System.out.println(roperName + " has six runs. Removing from array.");
                    return true;
                }
            }
        }
        return false;
    }

    public static int getNumberOfExtraRuns(String roperName, String positionName, ArrayList positionNames, ArrayList positionDraw2,
                                    ArrayList positionDraw3, ArrayList partnerList) {
        int numberOfExtras = 0;
        ArrayList headerRuns = new ArrayList();
        ArrayList heelerRuns = new ArrayList();
        for (int i = 0; i < partnerList.size(); i++) {
            String[] splitNames = partnerList.get(i).toString().split("\\s+");
            String headerName = splitNames[0] + " " + splitNames[1] + " " + splitNames[2];
            String heelerName = splitNames[3] + " " + splitNames[4] + " " + splitNames[5];
            headerRuns.add(headerName);
            heelerRuns.add(heelerName);
            //System.out.println("Added " + headerName + " and " + heelerName);
        }
        int numOfEntries = Collections.frequency(positionDraw2, roperName) + Collections.frequency(positionDraw3, roperName);
        //System.out.println("getNoExtras log: Name: " + roperName + " numOfEntries: " + numOfEntries + " positon name: " + positionName);
        //System.out.println(roperName + " is in draw 2 " + Collections.frequency(positionDraw2, roperName) + " time and draw 3 " +
                //Collections.frequency(positionDraw3, roperName) + " times.");
        if(positionName.equals("heeler")) {
            //if we're here, it means the heelerlist was bigger. We're looping the headerDrawX arrays and trying to remove from heelerNames
            if(numOfEntries == 1) {
                //System.out.println("Should have three total runs.");
                numberOfExtras = Collections.frequency(heelerRuns, roperName) - 3;
                //System.out.println("Math is:  " + roperName + " " + Integer.valueOf(Collections.frequency(heelerRuns, roperName) - 3));
                    return numberOfExtras;
            }
            if(numOfEntries == 2) {
                //System.out.println("Should have six.");
                numberOfExtras = Collections.frequency(heelerRuns, roperName) - 6;
                    //System.out.println(roperName + " has six runs. Removing from array.");
                //System.out.println("Math is:  " + roperName + " " + Integer.valueOf(Collections.frequency(heelerRuns, roperName) - 3));
                    return numberOfExtras;
                }
            }
        if(positionName.equals("header")) {
            //System.out.println("They're a header");
            if(numOfEntries == 1) {
                //System.out.println("Should have three total runs.");
                numberOfExtras = Collections.frequency(headerRuns, roperName) - 3;
                //System.out.println("Math is:  " + roperName + " " + Integer.valueOf(Collections.frequency(headerRuns, roperName) - 3));
                return numberOfExtras;
            }
            if(numOfEntries == 2) {
                //System.out.println("Should have six.");
                numberOfExtras = Collections.frequency(headerRuns, roperName) - 6;
                //System.out.println(roperName + " has six runs. Removing from array.");
                //System.out.println("Math is:  " + roperName + " " + Integer.valueOf(Collections.frequency(headerRuns, roperName) - 3));
                return numberOfExtras;
            }
        }
        else {
            return numberOfExtras;
        }
        return numberOfExtras;
    }

    public static void attemptRemoval(String roperName, String positionName, ArrayList positionNames, ArrayList positionDraw2, ArrayList positionDraw3, ArrayList partnerList) {
        ArrayList headerRuns = new ArrayList();
        ArrayList heelerRuns = new ArrayList();
        for (int i = 0; i < partnerList.size(); i++) {
            String[] splitNames = partnerList.get(i).toString().split("\\s+");
            String headerName = splitNames[0] + " " + splitNames[1] + " " + splitNames[2];
            String heelerName = splitNames[3] + " " + splitNames[4] + " " + splitNames[5];
            headerRuns.add(headerName);
            heelerRuns.add(heelerName);
            //System.out.println("Added " + headerName + " and " + heelerName);
        }
        int numOfEntries = Collections.frequency(positionDraw2, roperName) + Collections.frequency(positionDraw3, roperName);
        System.out.println(roperName + " is in draw 2 " + Collections.frequency(positionDraw2, roperName) + " time and draw 3 " +
                Collections.frequency(positionDraw3, roperName) + " times.");
        if(positionName == "heeler") {
            //if we're here, it means the heelerlist was bigger. We're looping the headerDrawX arrays and trying to remove from heelerNames
            if(numOfEntries == 1) {
                System.out.println("In there once, with " + Collections.frequency(heelerRuns, roperName) + " current runs.");
                if (Collections.frequency(heelerRuns, roperName) == 3) {
                    System.out.println(roperName + " has three runs. Removing from array.");
                    positionNames.remove(roperName);
                    //System.out.println("Position names: " + positionNames);
                }
            }
            if(numOfEntries == 2) {
                System.out.println("In there twice.");
                if(Collections.frequency(heelerRuns, roperName) == 6) {
                    System.out.println(roperName + " has six runs. Removing from array.");
                    positionNames.remove(roperName);
                }
            }
        }
        if(positionName == "header") {
            //if we're here, it means the headerlist was bigger. We're looping the heelerDrawX arrays and trying to remove from headerNames
            if(numOfEntries == 1) {
                System.out.println("In there once, with " + Collections.frequency(headerRuns, roperName) + " current runs.");
                if(Collections.frequency(headerRuns, roperName) == 3) {
                    System.out.println(roperName + " has three runs. Removing from array.");
                    positionNames.remove(roperName);
                    //System.out.println("Position names: " + positionNames);
                }
            }
            if(numOfEntries == 2) {
                System.out.println("In there twice.");
                if(Collections.frequency(headerRuns, roperName) == 6) {
                    System.out.println(roperName + " has six runs. Removing from array.");
                    positionNames.remove(roperName);
                }
            }
        }

    }







}
