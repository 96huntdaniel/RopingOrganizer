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
        ArrayList partners = new ArrayList();
        ArrayList headerNames = new ArrayList();
        ArrayList heelerNames = new ArrayList();
        ArrayList headerDraw2 = new ArrayList();
        ArrayList heelerDraw2 = new ArrayList();
        ArrayList headerDraw3 = new ArrayList();
        ArrayList heelerDraw3 = new ArrayList();
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
        System.out.println("Header Draw 2: " + headerDraw2);
        System.out.println("Header Draw 3: " + headerDraw3);
        System.out.println("Heeler Draw 2: " + heelerDraw2);
        System.out.println("Heeler Draw 3: " + heelerDraw3);

        partners = generatePartners(headerDraw2, headerDraw3, heelerDraw2, heelerDraw3, headerNames, heelerNames, partners);
        for (Object partner : partners) {
            System.out.println(partner.toString());
        }

        ExcelWriter.populateEntries(partners);

    }

    public static ArrayList<String> generatePartners(ArrayList headerDraw2, ArrayList headerDraw3, ArrayList heelerDraw2, ArrayList heelerDraw3, ArrayList headerNames,
                                                     ArrayList heelerNames, ArrayList partners) {
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
                            } else if (Float.parseFloat(rank1) + rank2 > 9.5) {
                                System.out.println("Tried " + headerDraw2.get(i).toString() + " " + partner + " which exceeds 9.5");
                                //continue;
                            } else if (partner.equals(headerDraw2.get(i).toString())) {
                                System.out.println("Same person, trying again. Tried " + headerDraw2.get(i).toString() + " and " + partner);
                                //continue;
                            } else {
                                System.out.println("Valid response. Adding " + headerDraw2.get(i).toString() + " " + partner);
                                partners.add(headerDraw2.get(i).toString() + " " + partner);

                                System.out.println("Attempting removal of " + partner);

                                attemptRemoval(partner, "heeler", heelerNames, heelerDraw2, partners);

                                break;
                            }
                        }
                        //we have two partners for each draw2 entry now
                    }
                }
                //now header draw 3
                //base off this one
                System.out.println("HEADER DRAW 3");
                int attempts = 100;
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
                                    } else if (Float.parseFloat(rank1) + rank2 > 9.5) {
                                        System.out.println("Tried " + headerDraw3.get(i).toString() + " " + partner + " which exceeds 9.5");
                                        //continue;
                                        attempts --;
                                    } else if (partner.equals(headerDraw3.get(i).toString())) {
                                        System.out.println("Same person, trying again. Tried " + partner + " and " + headerDraw3.get(i).toString());
                                        //continue;
                                        attempts --;
                                    } else {
                                        System.out.println("Valid response. Adding " + headerDraw3.get(i).toString() + " " + partner);
                                        partners.add(headerDraw3.get(i).toString() + " " + partner);

                                        System.out.println("Attempting removal of " + partner);

                                        attemptRemoval(partner, "heeler", heelerNames, heelerDraw3, partners);

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
                            } else if (Float.parseFloat(rank1) + rank2 > 9.5) {
                                System.out.println("Tried " + headerDraw3.get(i).toString() + " " + partner + " which exceeds 9.5");
                                //continue;
                            } else if (partner.equals(headerDraw3.get(i).toString())) {
                                System.out.println("Same person, trying again. Tried " + partner + " and " + headerDraw3.get(i).toString());
                                //continue;
                            } else {
                                System.out.println("Valid response. Adding " + headerDraw3.get(i).toString() + " " + partner);
                                partners.add(headerDraw3.get(i).toString() + " " + partner);

                                System.out.println("Attempting removal of " + partner);

                                attemptRemoval(partner, "heeler", heelerNames, heelerDraw3, partners);

                                break;
                            }
                        }
                    }
                }
                System.out.println("HEADER DRAW 2");
                int attempts = 100;
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

                                    } else if (Float.parseFloat(rank1) + rank2 > 9.5) {
                                        System.out.println("Tried " + headerDraw2.get(i).toString() + " " + partner + " which exceeds 9.5");
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
                                    } else {
                                        System.out.println("Valid response. Adding " + headerDraw2.get(i).toString() + " " + partner);
                                        partners.add(headerDraw2.get(i).toString() + " " + partner);

                                        System.out.println("Attempting removal of " + partner);

                                        attemptRemoval(partner, "heeler", heelerNames, heelerDraw2, partners);

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
                            } else if (Float.parseFloat(rank1) + rank2 > 9.5) {
                                System.out.println("Tried " + partner + " " + heelerDraw2.get(i).toString() + " which exceeds 9.5");
                                //continue;
                            } else if (partner.equals(heelerDraw2.get(i).toString())) {
                                System.out.println("Same person, trying again. Tried " + partner + " and " + heelerDraw2.get(i).toString());
                                //continue;
                            } else {
                                System.out.println("Valid response. Adding " + partner + " " + heelerDraw2.get(i).toString());
                                partners.add(partner + " " + heelerDraw2.get(i).toString());

                                System.out.println("Attempting removal of " + partner);

                                attemptRemoval(partner, "header", headerNames, headerDraw2, partners);

                                break;
                            }
                        }
                        //we have two partners for each draw2 entry now
                    }
                }
            //now heeler draw 3
            System.out.println("HEELER DRAW 3");
                int attempts = 100;
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
                            //continue;
                        } else if (Float.parseFloat(rank1) + rank2 > 9.5) {
                            System.out.println("Tried " + partner + " " + heelerDraw3.get(i).toString() + " which exceeds 9.5");
                            //continue;
                        } else if (partner.equals(heelerDraw3.get(i).toString())) {
                            System.out.println("Same person, trying again. Tried " + partner + " and " + heelerDraw3.get(i).toString());
                            //continue;
                        } else {
                            System.out.println("Valid response. Adding " + partner + " " + heelerDraw3.get(i).toString());
                            partners.add(partner + " " + heelerDraw3.get(i).toString());
                            System.out.println("Attempting removal of " + partner);
                            attemptRemoval(partner, "header", headerNames, headerDraw3, partners);
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
                            } else if (Float.parseFloat(rank1) + rank2 > 9.5) {
                                System.out.println("Tried " + partner + " " + heelerDraw3.get(i).toString() + " which exceeds 9.5");
                                //continue;
                            } else if (partner.equals(heelerDraw3.get(i).toString())) {
                                System.out.println("Same person, trying again. Tried " + partner + " and " + heelerDraw3.get(i).toString());
                                //continue;
                            } else {
                                System.out.println("Valid response. Adding " + partner + " " + heelerDraw3.get(i).toString());
                                partners.add(partner + " " + heelerDraw3.get(i).toString());
                                System.out.println("Attempting removal of " + partner);
                                attemptRemoval(partner, "header", headerNames, headerDraw3, partners);
                                break;
                            }
                        }
                    }
                    System.out.println("HEELER DRAW 2");
                    int attempts = 100;
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
                                        //continue;
                                    } else if (Float.parseFloat(rank1) + rank2 > 9.5) {
                                        System.out.println("Tried " + partner + " " + heelerDraw2.get(i).toString() + " which exceeds 9.5");
                                        //continue;
                                    } else if (partner.equals(heelerDraw2.get(i).toString())) {
                                        System.out.println("Same person, trying again. Tried " + partner + " and " + heelerDraw2.get(i).toString());
                                        //continue;
                                    } else {
                                        System.out.println("Valid response. Adding " + partner + " " + heelerDraw2.get(i).toString());
                                        partners.add(partner + " " + heelerDraw2.get(i).toString());

                                        System.out.println("Attempting removal of " + heelerDraw2.get(i).toString() + " " + partner);

                                        attemptRemoval(partner, "header", headerNames, headerDraw2, partners);

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

    public static void attemptRemoval(String roperName, String positionName, ArrayList positionNames, ArrayList positionDrawX, ArrayList partnerList) {
        int numOfEntries = Collections.frequency(positionDrawX, roperName);
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
        if(positionName == "heeler") {
            //if we're here, it means the heelerlist was bigger. We're looping the headerDrawX arrays and trying to remove from heelerNames
            if(Collections.frequency(positionDrawX, roperName) == 1) {
                System.out.println("In there once, with " + Collections.frequency(heelerRuns, roperName) + " current runs.");
                if(Collections.frequency(heelerRuns, roperName) == 3) {
                    System.out.println(roperName + " has three runs. Removing from array.");
                    positionNames.remove(roperName);
                    //System.out.println("Position names: " + positionNames);
                }
            }
            if(Collections.frequency(positionDrawX, roperName) == 2) {
                System.out.println("In there twice.");
                if(Collections.frequency(heelerRuns, roperName) == 6) {
                    System.out.println(roperName + " has six runs. Removing from array.");
                    positionNames.remove(roperName);
                }
            }


        }
        if(positionName == "header") {
            //if we're here, it means the headerlist was bigger. We're looping the heelerDrawX arrays and trying to remove from headerNames
            if(Collections.frequency(headerRuns, roperName) == 3) {
                positionNames.remove(roperName);
            }
        }

    }


    public static void attemptRemovalDeprecated2(String pairName, String positionName, ArrayList positionNames, ArrayList draw2, ArrayList draw3,
                                      ArrayList partnerList) {
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
        String[] splitNames = pairName.split("\\s+");
        String headerName = splitNames[0] + " " + splitNames[1] + " " + splitNames[2];
        String heelerName = splitNames[3] + " " + splitNames[4] + " " + splitNames[5];
        if (positionName == "heeler") {
            positionName = heelerName;
            System.out.println("Frequency of " + positionName + " in draw2 is " + Collections.frequency(draw2, positionName) + " and draw 3 is " +
                    Collections.frequency(draw3, positionName));
            if (Collections.frequency(draw2, positionName) == 2 || Collections.frequency(draw3, positionName) == 2) {
                System.out.println(positionName + " was in Draw2 twice, so he gets six heads");
                if (Collections.frequency(heelerRuns, heelerName) == 6) {
                    System.out.println(heelerName + " has six entries, remove from list.");
                    positionNames.remove(positionName);
                }
            } else {
                //they get three entries
                System.out.println(heelerName + " was in Draw2 once, so he gets 3 heads");
                if (Collections.frequency(heelerRuns, positionName) == 3) {
                    System.out.println(positionName + " has 3 entries, remove from list.");
                    positionNames.remove(positionName);
                }
            }
        }
        if (positionName == "header") {
            positionName = headerName;
            System.out.println("Frequency of " + positionName + " in draw2 is " + Collections.frequency(draw2, positionName) + " and draw 3 is " +
                    Collections.frequency(draw3, positionName));
            if (Collections.frequency(draw2, positionName) == 2 || Collections.frequency(draw3, positionName) == 2) {
                System.out.println(positionName + " was in Draw2 twice, so he gets six heads");
                if (Collections.frequency(headerRuns, headerName) == 6) {
                    System.out.println(headerName + " has six entries, remove from list.");
                    positionNames.remove(positionName);
                }
            } else {
                //they get three entries
                System.out.println(headerName + " was in Draw2 once, so he gets 3 heads");
                if (Collections.frequency(headerRuns, positionName) == 3) {
                    System.out.println(positionName + " has 3 entries, remove from list.");
                    positionNames.remove(positionName);
                }
            }
        }
    }



    public static void attemptRemovalDeprecated(String pairName, ArrayList headerNames, ArrayList heelerNames, ArrayList headerDraw2, ArrayList headerDraw3,
                                                ArrayList heelerDraw2, ArrayList heelerDraw3, ArrayList partnerList) {
        ArrayList headerRuns = new ArrayList();
        ArrayList heelerRuns = new ArrayList();
        for(int i = 0; i < partnerList.size(); i++) {
            String[] splitNames = partnerList.get(i).toString().split("\\s+");
            String headerName = splitNames[0] + " " + splitNames[1] + " " + splitNames[2];
            String heelerName = splitNames[3] + " " + splitNames[4] + " " + splitNames[5];
            headerRuns.add(headerName);
            heelerRuns.add(heelerName);
            //System.out.println("Added " + headerName + " and " + heelerName);
        }
        String[] splitNames = pairName.split("\\s+");
        String headerName = splitNames[0] + " " + splitNames[1] + " " + splitNames[2];
        String heelerName = splitNames[3] + " " + splitNames[4] + " " + splitNames[5];
        if (headerNames.size() > heelerNames.size()) {
            System.out.println("Frequency of " + headerName + " in draw2 is " + Collections.frequency(headerDraw2, headerName) + " and draw 3 is " +
                    Collections.frequency(headerDraw3, headerName));
            //more headers than heelers. Now check if they should have 3 or 6 header entries
            if (Collections.frequency(headerDraw2, headerName) == 2 || Collections.frequency(headerDraw3, headerName) == 2) {
                System.out.println(headerName + " was in headerDraw2 twice, so he gets six heads");
                if (Collections.frequency(headerRuns, headerName) == 6) {
                    System.out.println(headerName + " has six entries, remove from list.");
                    headerNames.remove(headerName);
                }
            } else {
                //they get three entries
                System.out.println(headerName + " was in headerDraw2 once, so he gets 3 heads");
                if (Collections.frequency(headerRuns, headerName) == 3) {
                    System.out.println(headerName + " has 3 entries, remove from list.");
                    headerNames.remove(headerName);
                }
            }
        }
        if(heelerNames.size() > headerNames.size()) {
            System.out.println("Frequency of " + heelerName + " in draw2 is " + Collections.frequency(heelerDraw2, heelerName) + " and draw 3 is " +
                    Collections.frequency(heelerDraw3, heelerName));
            //heeler name list was bigger
            if (Collections.frequency(heelerDraw2, heelerName) == 2 || Collections.frequency(heelerDraw3, heelerName) == 2) {
                System.out.println(heelerName + " was in heelerDraw2 or 3 twice, so he gets six heels. Currently at "
                        + Collections.frequency(heelerRuns, heelerName));
                if (Collections.frequency(heelerRuns, heelerName) == 6) {
                    System.out.println(heelerName + " has six entries, remove from list. " + Collections.frequency(heelerRuns, heelerName));
                    heelerNames.remove(heelerName);
                }
            } else {
                //they get three entries
                System.out.println(heelerName + " was in heelerDraw2 once, so he gets 3 heels. Currently at " + Collections.frequency(heelerRuns, heelerName));
                if (Collections.frequency(heelerRuns, heelerName) == 3) {
                    System.out.println(heelerName + " has 3 entries, remove from list. " + Collections.frequency(heelerRuns, heelerName));
                    heelerNames.remove(heelerName);
                }
            }
        } else {
            //arrays are same size, might need this for something
            System.out.println("Arrays same size.");
            System.out.println("Frequency of " + headerName + " in draw2 is " + Collections.frequency(headerDraw2, headerName) + " and draw 3 is " +
                    Collections.frequency(headerDraw3, headerName));
            //more headers than heelers. Now check if they should have 3 or 6 header entries
            if (Collections.frequency(headerDraw2, headerName) == 2 || Collections.frequency(headerDraw3, headerName) == 2) {
                System.out.println(headerName + " was in headerDraw2 twice, so he gets six heads");
                if (Collections.frequency(headerRuns, headerName) == 6) {
                    System.out.println(headerName + " has six entries, remove from list.");
                    headerNames.remove(headerName);
                }
            } else {
                //they get three entries
                System.out.println(headerName + " was in headerDraw2 once, so he gets 3 heads");
                if (Collections.frequency(headerRuns, headerName) == 3) {
                    System.out.println(headerName + " has 3 entries, remove from list.");
                    headerNames.remove(headerName);
                }
            }
        }
    }



}
