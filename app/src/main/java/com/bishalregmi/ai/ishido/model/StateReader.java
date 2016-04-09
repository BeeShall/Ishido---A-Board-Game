package com.bishalregmi.ai.ishido.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/************************************************************
 * Name:  Bishal Regmi                                      *
 * Project:  Project 3 - Ishido for Two                     *
 * Class:  CMPS331 - Artificial Intelligence                *
 * Date:  03/22/2016                                        *
 ************************************************************/

/**
 * Created by Bishal on 2/13/2016.
 * class to get the state from the file
 * will have two arraylists of layout ans stock which can be gotten using the provided method
 * Extracts the values from the serialized file and stores values in these lists
 */
public class StateReader {
    //Default constructor for the StateReader class
    public StateReader() {
        layout = new ArrayList<Integer>();
        stock = new ArrayList<Integer>();
        humanScore = 0;
        computerScore = 0;
        turn = -1;
    }

    /*
    * Description: Sets the inputstream for the file reading operation
    * Parameters: @InputStream is --> the inputstream for the file
    * Returns: nothing
     */
    public void setInputStream(InputStream is) {
        this.is = is;
        getData();

    }

    /*
    * Description: Returns the gameboard from state
    * Parameters: Nothing
    * Returns: Integer Array of tiles in every single position in the game1 in row major order
     */

    public Integer[] getLayout() {
        Integer[] layoutArray = new Integer[layout.size()];
        layoutArray = layout.toArray(layoutArray);
        return layoutArray;
    }

    /*
    * Description: Return the stock of available tile combinations
    * Parameters: Nothing
    * Returns: Integer Array of stock
     */

    public Integer[] getStock() {
        Integer[] stockArray = new Integer[stock.size()];
        stockArray = stock.toArray(stockArray);
        return stockArray;
    }

      /*
    * Description: Return the human score from the state
    * Parameters: Nothing
    * Returns: score as int.
     */

    public int getHumanScore() {
        return humanScore;
    }

    /*
  * Description: Return the computer score from the state
  * Parameters: Nothing
  * Returns: score as int.
   */
    public int getComputerScore(){
        return computerScore;
    }

    /*
  * Description: Return the next player turn from the state
  * Parameters: Nothing
  * Returns: turn as int.
   */
    public int getTurn(){
        return turn;
    }

    /*
  * Description: private method  that uses BufferReader to read all the lines from the inputStream
  * Parameters: Nothing
  * Returns: Nothing
   */
    private void getData() {
        Log.v("at","gameboard");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String[] text = new String[21];
        try {
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                text[index++] = line;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        setLayoutData(text);
        setStockData(text[11]);
        //Parsing the last line of the file which is the score
        humanScore = Integer.parseInt(text[14].trim());
        computerScore = Integer.parseInt(text[17].trim());

        String player = text[20].trim().toLowerCase();
        if(player.equals("human")){
            turn = 0;
        }
        else{
            turn = 1;
        }

    }

    /*
  * Description: Sets the tiles from the game1 to the existing list of layout
  * Parameters: String array of the lines from the files that have the layout
  * Returns: Nothing
   */
    private void setLayoutData(String[] text) {
        for (int i = 1; i < 9; i++) {
            layout.addAll(getParsedData(text[i]));
        }

    }

    /*
  * Description: Sets the remaining tile combinations from the game1 to the existing list of stock.
  * Parameters: String line that is read from the file
  * Returns: Nothing
   */
    private void setStockData(String text) {
        stock.addAll(getParsedData(text));
    }


    /*
  * Description: Return the List of Integers parsed from the given string
  * Parameters: String Line to parse
  * Returns: List of Integers parsed
   */
    private List<Integer> getParsedData(String parseString) {
        List<Integer> tempList = new ArrayList<Integer>();
        //using regex pattern matcher to split the string by space
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(parseString);
        while (matcher.find()) {
            if (matcher.group().length() != 0) {
                tempList.add(Integer.parseInt(matcher.group().trim()));
            }
        }
        return tempList;
    }


    private List<Integer> layout;
    private List<Integer> stock;
    private int humanScore;
    private int computerScore;
    private int turn;
    InputStream is;
}
