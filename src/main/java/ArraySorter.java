import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ArraySorter {


    public static void main(String args[]) {

    }

    public static ArrayList sortArray(ArrayList inputArray, String sortField) {
        ArrayList sortedArray = new ArrayList();
        ArrayList tempArray = new ArrayList();
        if (sortField.equals("teamNumber")) {
            for (int i = 0; i < inputArray.size(); i++) {
                //sorting through full partner data to sort by team number
                String[] splitNames = inputArray.get(i).toString().split("\\s+");
                System.out.println("We're working with " + inputArray.get(i).toString());
                String headerName = splitNames[0] + " " + splitNames[1] + " " + splitNames[2];
                String heelerName = splitNames[3] + " " + splitNames[4] + " " + splitNames[5];
                String teamNumber = splitNames[6];
                System.out.println(headerName + " " + heelerName + " " + teamNumber);
                //System.out.println(Float.valueOf(splitNames[2]) + Float.valueOf(splitNames[5])));
                String totalRank = String.valueOf(Float.valueOf(Float.valueOf(splitNames[2]) + Float.valueOf(splitNames[5])));

                tempArray.add(teamNumber + " " + headerName + " " + heelerName + " " + totalRank);
            }

            //tempArray.sort(Comparator.comparingInt(Integer::valueOf));
            Collections.sort(tempArray, String.CASE_INSENSITIVE_ORDER);
            //System.out.println(tempArray);
            for (int j = 0; j < tempArray.size(); j++) {
                String[] splitNames = tempArray.get(j).toString().split("\\s+");
                String entry1 = splitNames[0];
                String entry2 = splitNames[1] + " " + splitNames[2] + " " + splitNames[3];
                String entry3 = splitNames[4] + " " + splitNames[5] + " " + splitNames[6];
                sortedArray.add(entry2 + " " + entry3 + " " + entry1);
            }
            //System.out.println(sortedArray);
            return sortedArray;
        } else {
            for (int i = 0; i < inputArray.size(); i++) {
                String[] splitNames = inputArray.get(i).toString().split("\\s+");
                String firstName = splitNames[0];
                String lastName = splitNames[1];
                String rank = splitNames[2];
                if (sortField.equals("lastName")) {
                    //System.out.println("Sorting on " + sortField + " " + lastName);
                    //sort on last name
                    tempArray.add(lastName + " " + firstName + " " + rank);
                }
                if (sortField.equals("rank")) {
                    tempArray.add(rank + " " + firstName + " " + lastName);
                }
                if (sortField.equals("firstName")) {
                    tempArray.add(firstName + " " + lastName + " " + rank);
                }
            }

        }

        Collections.sort(tempArray, String.CASE_INSENSITIVE_ORDER);
        if (sortField.equals("rank")) {
            Collections.sort(tempArray, Collections.reverseOrder());
        }

        for (int j = 0; j < tempArray.size(); j++) {
            String[] splitNames = tempArray.get(j).toString().split("\\s+");
            String entry1 = splitNames[0];
            String entry2 = splitNames[1];
            String entry3 = splitNames[2];
            if (sortField.equals("lastName")) {
                sortedArray.add(entry2 + " " + entry1 + " " + entry3);
            }
            if (sortField.equals("rank")) {
                sortedArray.add(entry2 + " " + entry3 + " " + entry1);
            }
            if (sortField.equals("firstName")) {
                sortedArray.add(entry1 + " " + entry2 + " " + entry3);
            }

        }
        //System.out.println(sortedArray);
        return sortedArray;
    }

    public static String mostCommon(ArrayList inputArray) {
        String mostCommonElement = null;
        ArrayList sortedArray = new ArrayList();
        ArrayList tempArray = new ArrayList();
        int maxOccurences = 0;
        for (int i = 0; i < inputArray.size(); i++) {
            if (Collections.frequency(inputArray, inputArray.get(i)) > maxOccurences) {
                maxOccurences = Collections.frequency(inputArray, (inputArray.get(i)));
                mostCommonElement = (String) inputArray.get(i);
            }
        }
        System.out.println("Most common element is " + mostCommonElement + " with " + Collections.frequency(inputArray, mostCommonElement));
        return mostCommonElement;
    }

    public static ArrayList evenlySpace(ArrayList inputArray, int previousEntries) {
        System.out.println("Evenly spacing array. Original size: " + inputArray.size());
        ArrayList evenlySpacedArray = new ArrayList();
        ArrayList tempArray = new ArrayList();
        int attempts = 100;
        attempts = 10000;
        for (int i = 0; i < inputArray.size(); i++) {

            boolean inLastX = false;
                    inLastX = false;
                    String[] splitNames = inputArray.get(i).toString().split("\\s+");
                    String fullHeaderName = splitNames[0] + " " + splitNames[1] + " " + splitNames[2];
                    String fullHeelerName = splitNames[3] + " " + splitNames[4] + " " + splitNames[5];
                    String headerName = splitNames[0] + " " + splitNames[1];
                    String heelerName = splitNames[3] + " " + splitNames[4];
                    for (int j = 0; j <= previousEntries; j++) {
                        if(i == 0) {
                            //add the first for free to establish evenlySpaced size
                            System.out.println("Adding first one for free");
                            evenlySpacedArray.add(inputArray.get(i));
                            //inputArray.remove(i);
                            inLastX = false;
                            break;
                        }
                        int indexCheck = 0;
                        if(evenlySpacedArray.size() - j < 0) {
                            indexCheck = 0;
                        } else {
                            System.out.println("Not less than 0");
                            indexCheck = evenlySpacedArray.size() - j;
                            indexCheck -= 1;
                            if(indexCheck < 0) {
                                indexCheck = 0;
                            }
                        }

                        String[] checkNames = evenlySpacedArray.get(indexCheck).toString().split("\\s+");
                        String nameCheck = checkNames[0] + " " + checkNames[1] + " " + checkNames[3] + " " + checkNames[4];


                        //System.out.println("break test: " + evenlySpacedArray.get(indexCheck));

                        System.out.println("Array size: " + evenlySpacedArray.size() + " i: " + i + " j: " + j + " index check: " + indexCheck + " attempts: " + attempts);
                        System.out.println("Checking if " + nameCheck + " contains " + headerName + " or " + heelerName);
                        //System.out.println(evenlySpacedArray.get(evenlySpacedArray.size() - j));
                        if (nameCheck.contains(headerName) || nameCheck.contains(heelerName)) {
                            //one of the last X entries contains either the header or heeler name. break out of it
                            attempts--;
                            //System.out.println("Attempts: " + attempts);
                            System.out.println("It does contain it.");
                            inLastX = true;
                            if(!tempArray.contains(headerName + " " + heelerName)) {
                                if(attempts > 1) {
                                    inputArray.add(fullHeaderName + " " + fullHeelerName);
                                    System.out.println("We're going to try " + headerName + " " + heelerName + " again.");
                                } else {
                                    tempArray.add(fullHeaderName + " " + fullHeelerName);
                                }
                            }
                            break;
                            //i++;
                        }
            }
            if(!inLastX && attempts > 3) {
                //isn't in the last X. let's remove it
                if(!evenlySpacedArray.contains(inputArray.get(i))) {
                    evenlySpacedArray.add(inputArray.get(i));
                }
                //inputArray.remove(i);
                inLastX = false;
                //break;
            }

        }
        System.out.println("Temp array: " + tempArray);
        evenlySpacedArray.addAll(tempArray);
        System.out.println("Evenly spaced: " + evenlySpacedArray);
        System.out.println("Ending size: " + evenlySpacedArray.size());
        return evenlySpacedArray;
    }



}
