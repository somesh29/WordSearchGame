package helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


@Slf4j
@Singleton
@Data
public class GridHelper {

    private static String dictFilePath = "/usr/share/dict/words";
    private static  List<String> directions = Arrays.asList("east","southEast", "south");
    private static int gridSize = 15;
    private static int maxWordSize = 7;

    public GridHelper() {

    }

    public static Character[][] getNewGrid() {

        Character[][] grid = new Character[gridSize][gridSize];
        List<String> wordList = getWordList().stream().filter(each -> each.length() <= 7).collect(Collectors.toList());
        int i=0;
        boolean curWordInserted = false;
        while(i<=10) {

            int randomX, randomY;
            int retryword=1;
            int randomNum = ThreadLocalRandom.current().nextInt(0, wordList.size());
            String word = wordList.get(randomNum);
            word = word.toUpperCase();
            randomX = ThreadLocalRandom.current().nextInt(0, gridSize);
            randomY = ThreadLocalRandom.current().nextInt(0, gridSize);
            while(retryword <=3 ) {
                if(word.length() <=maxWordSize)
                    break;
                randomNum = ThreadLocalRandom.current().nextInt(0, wordList.size());
                word = wordList.get(randomNum);
                word = word.toUpperCase();
                randomX = ThreadLocalRandom.current().nextInt(0, gridSize);
                randomY = ThreadLocalRandom.current().nextInt(0, gridSize);
                retryword++;
            }

            int retryCount=1;
            while(retryCount <=3) {
                int randomDirection = ThreadLocalRandom.current().nextInt(0, directions.size());
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

        for(i = 0; i<15; i++) {
           System.out.println();
           for(int j=0;j<15;j++) {
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


    private static boolean checkValid(int x, int y) {

        if(x>=0 && x<gridSize && y>=0 && y<gridSize)
            return true;
        return false;
    }

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

    public static String getGridAsString(Character[][] c) {
        String s = "";
        int i, j;
        for(i=0;i<15;i++) {
            for(j=0;j<15;j++) {
                s = s + c[i][j] + ", ";
            }
            s = s + "#";
        }
        return s;
    }

    public static Character[][] getGrid(String s) {
        String[] rows = s.split("#");
        Character[][] grid = new Character[15][15];
        int i, j;
        for(i=0;i<15;i++) {
            String[] row = rows[i].split(", ");
            for(j=0;j<15;j++) {
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
