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
        if(sortField.equals("teamNumber")) {
            for(int i = 0; i < inputArray.size(); i++) {
                //sorting through full partner data to sort by team number
                String[] splitNames = inputArray.get(i).toString().split("\\s+");
                System.out.println("We're working with " + inputArray.get(i).toString());
                String headerName = splitNames[0] + " " + splitNames[1] + " "  + splitNames[2];
                String heelerName = splitNames[3] + " " + splitNames[4] + " "  + splitNames[5];
                String teamNumber = splitNames[6];
                System.out.println(headerName + " " + heelerName + " " + teamNumber);
                //System.out.println(Float.valueOf(splitNames[2]) + Float.valueOf(splitNames[5])));
                String totalRank = String.valueOf(Float.valueOf(Float.valueOf(splitNames[2]) + Float.valueOf(splitNames[5])));

                tempArray.add(teamNumber + " " + headerName + " " + heelerName + " " + totalRank);
            }

            //tempArray.sort(Comparator.comparingInt(Integer::valueOf));
            Collections.sort(tempArray, String.CASE_INSENSITIVE_ORDER);
            //System.out.println(tempArray);
            for(int j = 0; j < tempArray.size(); j++) {
                String[] splitNames = tempArray.get(j).toString().split("\\s+");
                String entry1 = splitNames[0];
                String entry2 = splitNames[1] + " "  + splitNames[2] + " "  + splitNames[3];
                String entry3 = splitNames[4] + " "  + splitNames[5] + " "  + splitNames[6];
                sortedArray.add(entry2 + " " +  entry3 + " " + entry1);
            }
            //System.out.println(sortedArray);
            return sortedArray;
        } else {
            for(int i = 0; i < inputArray.size(); i++) {
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
        if(sortField.equals("rank")) {
            Collections.sort(tempArray, Collections.reverseOrder());
        }

        for(int j = 0; j < tempArray.size(); j++) {
            String[] splitNames = tempArray.get(j).toString().split("\\s+");
            String entry1 = splitNames[0];
            String entry2 = splitNames[1];
            String entry3 = splitNames[2];
            if(sortField.equals("lastName")) {
                sortedArray.add(entry2 + " " + entry1 + " " + entry3);
            }
            if(sortField.equals("rank")) {
                sortedArray.add(entry2 + " " + entry3 + " " + entry1);
            }
            if(sortField.equals("firstName")) {
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


}
