package helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import utils.Constants;

import javax.inject.Singleton;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 *
 * helper class to check for variours edge cases of game
 */
@Slf4j
@Singleton
@Data
public class GridHelper {

    private static String dictFilePath = "en";
    private static  List<String> directions = Arrays.asList("east","southEast", "south");
    private static int maxWordSize = 7;

    public GridHelper() {

    }

    /**
     *
     * utility fuction to get a random grid at game start
     * @return
     */
    public static Character[][] getNewGrid() {

        Character[][] grid = new Character[Constants.girdSize][Constants.girdSize];

        // For simplicity of game word size is being kept as 7 as for 15*15 grid it's okay
        List<String> wordList = getWordList().stream().filter(each -> each.length() <= maxWordSize).collect(Collectors.toList());
        int i=0;
        boolean curWordInserted = false;
        while(i<=10) {

            int randomX, randomY;
            int retryword=1;
            int randomNum = ThreadLocalRandom.current().nextInt(0, wordList.size());
            String word = wordList.get(randomNum);
            word = word.toUpperCase();
            randomX = ThreadLocalRandom.current().nextInt(0, Constants.girdSize);
            randomY = ThreadLocalRandom.current().nextInt(0, Constants.girdSize);
            while(retryword <=3 ) {
                if(word.length() <=maxWordSize)
                    break;
                randomNum = ThreadLocalRandom.current().nextInt(0, wordList.size());
                word = wordList.get(randomNum);
                word = word.toUpperCase();
                randomX = ThreadLocalRandom.current().nextInt(0, Constants.girdSize);
                randomY = ThreadLocalRandom.current().nextInt(0, Constants.girdSize);
                retryword++;
            }

            int retryCount=1;
            while(retryCount <=3) {
                int randomDirection = ThreadLocalRandom.current().nextInt(0, directions.size());

                //0-> for inserting word in east 1-> inserting word in south east 2-> inserting word in south
                if (randomDirection == 0) {

                    if (insertEast(word, grid, randomX, randomY)) {
                        i++;
                        break;
                    }
                } else if(randomDirection == 1) {
                    if(insertSouthEast(word, grid, randomX, randomY)) {
                        i++;
                        break;
                    }
                } else if(randomDirection == 2) {
                    if (insertSouth(word, grid, randomX, randomY)) {
                        i++;
                        break;
                    }
                }
                retryCount++;
            }
        }

        // if grid node is empty fill it with random alphabets
        for(i = 0; i<Constants.girdSize; i++) {
           System.out.println();
           for(int j=0;j<Constants.girdSize;j++) {
               if(grid[i][j]==null) {
                   grid[i][j] = (char)ThreadLocalRandom.current().nextInt(65, 90);
                   //System.out.print(grid[i][j] + " ");
               } else {
                   //System.out.print(grid[i][j] + " ");
               }
           }
       }
        return grid;
     }

    /**
     * get all words from dictionary
     * @return
     */
     public static List<String> getWordList() {

         List<String> wordList = new ArrayList<String>();
         try (BufferedReader br = new BufferedReader(new FileReader(dictFilePath)))
         {
             String sCurrentLine;
             while ((sCurrentLine = br.readLine()) != null) {
                 wordList.add(sCurrentLine);
             }
             return wordList;

         } catch (IOException e) {
             e.printStackTrace();
         }
         return null;
     }

    /**
     * check valid co-ordinates
     * @param x
     * @param y
     * @return
     */
    private static boolean checkValid(int x, int y) {

        if(x>=0 && x<Constants.girdSize && y>=0 && y<Constants.girdSize)
            return true;
        return false;
    }

    /**
     * insert word in south-east
     * @param word
     * @param grid
     * @param x
     * @param y
     * @return
     */
    private static boolean insertSouthEast(String word, Character[][] grid, int x, int y) {

        int si = x;
        int sy = y;
        int i=0;

        while(i<word.length()) {
            if(!checkValid(si,sy))
                break;
            if(grid[si][sy]!=null && !(grid[si][sy].equals(word.charAt(i))))
                break;
            sy++;
            si++;
            i++;
        }
        if(i==word.length()) {
            i=0;
            si=x;
            sy=y;
            while (i < word.length()) {
                grid[si][sy]=word.charAt(i);
                sy++;
                si++;
                i++;
            }
            return true;
        }
        return false;
    }

    /**
     * insert word in south
     * @param word
     * @param grid
     * @param x
     * @param y
     * @return
     */
    private static boolean insertSouth(String word, Character[][] grid, int x, int y) {

        int si = x;
        int sy = y;
        int i=0;

        while(i<word.length()) {
            if(!checkValid(si,sy))
                break;
            if(grid[si][sy]!=null && !(grid[si][sy].equals(word.charAt(i))))
                break;
            si++;
            i++;
        }
        if(i==word.length()) {
            i=0;
            si=x;
            sy=y;
            while (i < word.length()) {
                grid[si][sy]=word.charAt(i);
                si++;
                i++;
            }
            return true;
        }
        return false;
    }

    /**
     * insert word in east
     * @param word
     * @param grid
     * @param x
     * @param y
     * @return
     */
    private static boolean insertEast(String word, Character[][] grid, int x, int y) {

        int si = x;
        int sy = y;
        int i=0;

        while(i<word.length()) {
            if(!checkValid(si,sy))
                break;
            if(grid[si][sy]!=null && !(grid[si][sy].equals(word.charAt(i))))
                break;
            sy++;
            i++;
        }
        if(i==word.length()) {
            i=0;
            si=x;
            sy=y;
            while (i < word.length()) {
                grid[si][sy]=word.charAt(i);
                sy++;
                i++;
            }
            return true;
        }
        return false;
    }

    /**
     * for serializing grid
     * @param c
     * @return
     */
    public static String getGridAsString(Character[][] c) {
        String s = "";
        int i, j;
        for(i=0;i<Constants.girdSize;i++) {
            for(j=0;j<Constants.girdSize;j++) {
                s = s + c[i][j] + ", ";
            }
            s = s + "#";
        }
        return s;
    }

    /**
     * for deserializing grid
     * @param s
     * @return
     */
    public static Character[][] getGrid(String s) {
        String[] rows = s.split("#");
        Character[][] grid = new Character[Constants.girdSize][Constants.girdSize];
        int i, j;
        for(i=0;i<Constants.girdSize;i++) {
            String[] row = rows[i].split(", ");
            for(j=0;j<Constants.girdSize;j++) {
                grid[i][j] = row[j].charAt(0);
            }
        }
        return grid;
    }


    //     public static void main(String args[]) throws JsonProcessingException {
//
//       Character[][] grid = getNewGrid();
//       for(int i=0;i<15;i++) {
//           System.out.println();
//           for(int j=0;j<15;j++) {
//               if(grid[i][j]==null) {
//                   grid[i][j] = (char)ThreadLocalRandom.current().nextInt(65, 90);
//                   System.out.print(grid[i][j] + " ");
//               } else {
//                   System.out.print(grid[i][j] + " ");
//               }
//           }
//       }
//
//       System.out.println("\n \n");
//       String test = getGridAsString(grid);
//       System.out.println(test + "\n \n");
//       Character[][] grids = getGrid(test);
//         for(int i=0;i<15;i++) {
//             System.out.println();
//             for (int j = 0; j < 15; j++) {
//                 System.out.print(grids[i][j] + " ");
//             }
//         }
//     }


}
