
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;


public class WordWriter {
    public static void main(String[] args) throws Exception {


    }

    public static void generatePrintoff(ArrayList partnerData, ArrayList headerDraw2, ArrayList headerDraw3, ArrayList heelerDraw2,
                                        ArrayList heelerDraw3) throws IOException, InvalidFormatException {
      XWPFDocument myDocument = new XWPFDocument();
      FileOutputStream out = new FileOutputStream(new File("printout.docx"));
      XWPFParagraph paragraph;
      XWPFParagraph logo = myDocument.createParagraph();
      logo.setAlignment(ParagraphAlignment.CENTER);
      XWPFRun logoRun = logo.createRun();
        System.out.println("Logo file: " + String.valueOf(WordWriter.class.getClass().getResourceAsStream("/GBKArena-LogoHorizontal-PRINT.jpeg")));
      String logoFile = String.valueOf(WordWriter.class.getClass().getResourceAsStream("/GBKArena-LogoHorizontal-PRINT.jpeg")).substring(5);

     InputStream fis = WordWriter.class.getClass().getResourceAsStream("/GBKArena-LogoHorizontal-PRINT.jpeg");
      logoRun.addPicture(fis, XWPFDocument.PICTURE_TYPE_JPEG, logoFile, Units.toEMU(500), Units.toEMU(120));
      logoRun.addBreak();



      ArrayList allEntries = new ArrayList();
        Collections.shuffle(partnerData);
        for(int j = 0; j < partnerData.size(); j++) {
            String[] splitNames = partnerData.get(j).toString().split("\\s+");
            String headerName = splitNames[0] + " " + splitNames[1] + " " + splitNames[2];
            String heelerName = splitNames[3] + " " + splitNames[4] + " " + splitNames[5];
            if(!allEntries.contains(headerName)) {
                allEntries.add(headerName);
            }
            if(!allEntries.contains(heelerName)) {
                allEntries.add(heelerName);
            }

            float totalRank = Float.valueOf(splitNames[2]) + Float.valueOf(splitNames[5]);
        }

        allEntries = ArraySorter.sortArray(allEntries, "lastName");

      for(int i = 0; i < allEntries.size(); i++) {
          paragraph = myDocument.createParagraph();
          XWPFRun paragraphRunOne = paragraph.createRun();
          paragraphRunOne.setBold(true);
          paragraphRunOne.setFontSize(14);
          paragraphRunOne.setText(allEntries.get(i).toString());
          paragraphRunOne.addBreak();
          ArrayList headingFor =  getHeadingFor(allEntries.get(i).toString(), partnerData);
          ArrayList heelingFor =  getHeelingFor(allEntries.get(i).toString(), partnerData);
          for(int k = 0; k < headingFor.size(); k++) {
              int numOfRuns = getNumberOfRuns(allEntries.get(i).toString(), headerDraw2, headerDraw3);
              numOfRuns *= 3;
              System.out.println(allEntries.get(i).toString() + " " + numOfRuns);
              XWPFRun paragraphRunTwo = paragraph.createRun();
              if(k >= numOfRuns) {
                  paragraphRunTwo.setText("\t\theading for... " + headingFor.get(k) + "***\n");
              } else {
                  paragraphRunTwo.setText("\t\theading for... " + headingFor.get(k) + "\n");
              }
              paragraphRunTwo.addBreak();


          }
          for(int k = 0; k < heelingFor.size(); k++) {
              int numOfRuns = getNumberOfRuns(allEntries.get(i).toString(), heelerDraw2, heelerDraw3);
              numOfRuns *= 3;
              System.out.println(allEntries.get(i).toString() + " " + numOfRuns);
              XWPFRun paragraphRunTwo = paragraph.createRun();
              if(k >= numOfRuns) {
                  paragraphRunTwo.setText("\t\theeling for... " + heelingFor.get(k) + "***\n");
              } else {
                  paragraphRunTwo.setText("\t\theeling for... " + heelingFor.get(k) + "\n");
              }
              paragraphRunTwo.addBreak();
          }
      }

      myDocument.write(out);
      out.close();


    }

    public static int getNumberOfRuns(String roperName, ArrayList positionDraw2, ArrayList positionDraw3) {
        int numOfRuns = Collections.frequency(positionDraw2, roperName) + Collections.frequency(positionDraw3, roperName);
        return numOfRuns;
    }

    public static ArrayList getHeadingFor(String roperName, ArrayList partnerData) {
        ArrayList headingForNames = new ArrayList();
        for(int i = 0; i < partnerData.size(); i++) {
            String[] splitNames = partnerData.get(i).toString().split("\\s+");
            String headerName = splitNames[0] + " " + splitNames[1] + " " + splitNames[2];
            String heelerName = splitNames[3] + " " + splitNames[4] + " " + splitNames[5];
            if(headerName.equals(roperName)) {
                //they're heading for this heeler!
                headingForNames.add(heelerName  + " | Team #" + splitNames [6]);
            }
        }
        return headingForNames;
    }
    public static ArrayList getHeelingFor(String roperName, ArrayList partnerData) {
        ArrayList heelingForNames = new ArrayList();
        for(int i = 0; i < partnerData.size(); i++) {
            String[] splitNames = partnerData.get(i).toString().split("\\s+");
            String headerName = splitNames[0] + " " + splitNames[1] + " " + splitNames[2];
            String heelerName = splitNames[3] + " " + splitNames[4] + " " + splitNames[5];
            if(heelerName.equals(roperName)) {
                //they're heeling for this header!
                heelingForNames.add(headerName + " | Team #" + splitNames [6]);
            }
        }
        return heelingForNames;
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
        //System.out.println(roperName + " is in draw 2 " + Collections.frequency(positionDraw2, roperName) + " time and draw 3 " +
                //Collections.frequency(positionDraw3, roperName) + " times.");
        if(positionName == "heeler") {
            //if we're here, it means the heelerlist was bigger. We're looping the headerDrawX arrays and trying to remove from heelerNames
            if(numOfEntries == 1) {
                //System.out.println("In there once, with " + Collections.frequency(heelerRuns, roperName) + " current runs.");
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
}
