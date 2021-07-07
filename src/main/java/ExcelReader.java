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
                            } else if(isMaxRuns(partner, "heeler",heelerNames, heelerDraw2, heelerDraw3, partners) && heelerNames.size() > 1) {
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
                System.out.println("HEADER DRAW 3");
                int attempts = 1000;
                while (heelerNames.size() > 0) {
                    if(attempts < 1) {
                        System.out.println("BREAKING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        break;
                    }
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
                                        attempts --;
                                    } else if (Float.parseFloat(rank1) + rank2 > maxRank) {
                                        System.out.println("Tried " + headerDraw3.get(i).toString() + " " + partner + " which exceeds " + maxRank);
                                        //continue;
                                        attempts --;
                                    } else if (partner.equals(headerDraw3.get(i).toString())) {
                                        System.out.println("Same person, trying again. Tried " + partner + " and " + headerDraw3.get(i).toString());
                                        //continue;
                                        attempts --;
                                    } else if(isMaxRuns(partner, "heeler", heelerNames, heelerDraw2, heelerDraw3, partners) && heelerNames.size() > 1) {
                                        System.out.println("This person has reached their max runs. Trying again.");
                                        attempts--;
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
                }
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
                            } else if(isMaxRuns(partner, "heeler", heelerNames, heelerDraw2, heelerDraw3, partners) && heelerNames.size() > 1) {
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
                System.out.println("HEADER DRAW 2");
                int attempts = 10000;
                    while (heelerNames.size() > 0) {
                        if(attempts < 1) {
                            System.out.println("BREAKING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            break;
                        }
                        for (int i = 0; i < headerDraw2.size(); i++) {
                            for (int j = 0; j < 2; j++) {
                                String rank1 = headerDraw2.get(i).toString().substring(headerDraw2.get(i).toString().length() - 3);
                                //for each header that needs 2 partners, draw 2 heelers
                                while (true) {
                                    String partner = getRandomPartner(heelerNames);
                                    float rank2 = Float.parseFloat(partner.substring(partner.length() - 3));
                                    if (partners.contains(headerDraw2.get(i).toString() + " " + partner)) {
                                        attempts--;
                                        System.out.println("Tried to add... " + headerDraw2.get(i).toString() + " " + partner + " but that entry already exists." + attempts);
                                        if(attempts < 1) {
                                            break;
                                        }
                                        //continue;

                                    } else if (Float.parseFloat(rank1) + rank2 > maxRank) {
                                        System.out.println("Tried " + headerDraw2.get(i).toString() + " " + partner + " which exceeds " + maxRank);
                                        //continue;
                                        attempts--;
                                        if(attempts < 1) {
                                            break;
                                        }
                                    } else if (partner.equals(headerDraw2.get(i).toString())) {
                                        System.out.println("Same person, trying again. Tried " + headerDraw2.get(i).toString() + " and " + partner);
                                        //continue;
                                        attempts--;
                                        if(attempts < 1) {
                                            break;
                                        }
                                    } else if(isMaxRuns(partner, "heeler", heelerNames, heelerDraw2, heelerDraw3, partners) && j < heelerNames.size()-1) {
                                        System.out.println("This person has reached their max runs. Trying again.");
                                        attempts--;
                                    }else {
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
                    }

                }
            } else if (heelerNames.size() < headerNames.size()) {
            //more heelers than headers, so headers will have extra runs.
            //now heeler draw 2
            if(heelerDraw2.size() < heelerDraw3.size()) {
                //there are more in the draw 3 array, so loop through the draw 2 until we get 2 partners for each entry
                System.out.println("HEELER DRAW 2");
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
                            } else if(isMaxRuns(partner, "header", headerNames, headerDraw2, headerDraw3, partners) && headerNames.size() > 1) {
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
                int attempts = 1000;
                while (heelerNames.size() > 0) {
                    if(attempts < 1) {
                        System.out.println("BREAKING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        break;
                    }
            for (int i = 0; i < heelerDraw3.size(); i++) {
                for (int j = 0; j < 3; j++) {
                    String rank1 = heelerDraw3.get(i).toString().substring(heelerDraw3.get(i).toString().length() - 3);
                    //for each heeler that needs 3 partners, draw 3 headers
                    while (true) {
                        String partner = getRandomPartner(headerNames);
                        float rank2 = Float.parseFloat(partner.substring(partner.length() - 3));
                        if (partners.contains(partner + " " + heelerDraw3.get(i).toString())) {
                            System.out.println("Tried to add... " + partner + " " + heelerDraw3.get(i).toString() + " but that entry already exists.");
                           attempts--;
                        } else if (Float.parseFloat(rank1) + rank2 > maxRank) {
                            System.out.println("Tried " + partner + " " + heelerDraw3.get(i).toString() + " which exceeds " + maxRank);
                           attempts--;
                        } else if (partner.equals(heelerDraw3.get(i).toString())) {
                            System.out.println("Same person, trying again. Tried " + partner + " and " + heelerDraw3.get(i).toString());
                            attempts--;
                        } else if(isMaxRuns(partner, "header", headerNames, headerDraw2, headerDraw3, partners) && headerNames.size() > 1) {
                            System.out.println("This person has reached their max runs. Trying again.");
                            attempts--;
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
                }
            } else if(heelerDraw2.size() > heelerDraw3.size()) {
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
                            }else if(isMaxRuns(partner, "header", headerNames, headerDraw2, headerDraw3, partners) && headerNames.size() > 1) {
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
                    System.out.println("HEELER DRAW 2");
                    int attempts = 10000;
                    while (heelerNames.size() > 0) {
                        if (attempts < 1) {
                            System.out.println("BREAKING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            break;
                        }
                        for (i = 0; i < heelerDraw2.size(); i++) {
                            for (int j = 0; j < 2; j++) {
                                String rank1 = heelerDraw2.get(i).toString().substring(heelerDraw2.get(i).toString().length() - 3);
                                //for each heeler that needs 2 partners, draw 2 headers
                                while (true) {
                                    String partner = getRandomPartner(headerNames);
                                    float rank2 = Float.parseFloat(partner.substring(partner.length() - 3));
                                    if (partners.contains(partner + " " + heelerDraw2.get(i).toString())) {
                                        System.out.println("Tried to add... " + partner + " " + heelerDraw2.get(i).toString() + " but that entry already exists.");
                                        attempts--;
                                    } else if (Float.parseFloat(rank1) + rank2 > maxRank) {
                                        System.out.println("Tried " + partner + " " + heelerDraw2.get(i).toString() + " which exceeds " + maxRank);
                                        attempts--;
                                    } else if (partner.equals(heelerDraw2.get(i).toString())) {
                                        System.out.println("Same person, trying again. Tried " + partner + " and " + heelerDraw2.get(i).toString());
                                        attempts--;
                                    } else if(isMaxRuns(partner, "header", headerNames, headerDraw2, headerDraw3, partners) && headerNames.size() > 1) {
                                        System.out.println("This person has reached their max runs. Trying again.");
                                        attempts--;
                                    } else {
                                        System.out.println("Valid response. Adding " + partner + " " + heelerDraw2.get(i).toString());
                                        partners.add(partner + " " + heelerDraw2.get(i).toString());
                                        //System.out.println("Attempting removal of " + heelerDraw2.get(i).toString() + " " + partner);
                                        //attemptRemoval(partner, "header", headerNames, headerDraw2, headerDraw3, partners);
                                        break;
                                    }
                                }
                                //we have two partners for each draw2 entry now
                            }
                        }
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
                    System.out.println(roperName + " has three runs. Removing from array.");
                    return true;
                    //System.out.println("Position names: " + positionNames);
                }
            }
            if(numOfEntries == 2) {
                System.out.println("In there twice.");
                if(Collections.frequency(heelerRuns, roperName) == 6) {
                    System.out.println(roperName + " has six runs. Removing from array.");
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
