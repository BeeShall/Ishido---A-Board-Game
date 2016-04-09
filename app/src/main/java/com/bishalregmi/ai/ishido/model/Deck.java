package com.bishalregmi.ai.ishido.model;

/************************************************************
 * Name:  Bishal Regmi                                      *
 * Project:  Project 3 - Ishido for Two                     *
 * Class:  CMPS331 - Artificial Intelligence                *
 * Date:  03/22/2016                                        *
 ************************************************************/

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Bishal on 1/23/2016.
 */
public class Deck {

    private char[] symbols = {'+', '*', '%', '!', '?', '@'};
    private int[] colors = {Color.WHITE, Color.BLUE, Color.GREEN, Color.YELLOW, 0XFFFFA500, Color.RED};

    //2-D array to keep track of the combinations of the symbols and color dealt
    private int[][] deck = new int[6][6];
    private List<Tile> stock = new ArrayList<>();
    private int remainingTileCount = 2 * deck.length * deck[0].length;

    public int stockIndex = 0;

     /*
    * Description: public function to build a new randonly generated stock
    * Parameters: nothing.
    * Returns: nothing
     */
    public void buildNewStock(){
        Random ran = new Random();
        int symbolIndex, colorIndex;
        for(int i=0;i<72;i++) {
            do {
                symbolIndex = Math.abs(ran.nextInt() % symbols.length);
                colorIndex = Math.abs(ran.nextInt() % colors.length);

            } while (deck[symbolIndex][colorIndex] > 1);

            stock.add(i,new Tile(colors[colorIndex], symbols[symbolIndex]));
        }
        stockIndex=0;
        remainingTileCount = 2 * deck.length * deck[0].length;
    }

    /*
   * Description: method to update the stock with the given stock
   * Parameters: Tile array as stock
   * Returns:  nothing
    */
    public void setStock(Tile[] stock) {
        this.stock.addAll(Arrays.asList(stock));
        remainingTileCount = this.stock.size();
    }

    /*
   * Description: method to get tile at a specific index in the stock.
   * Parameters: int index as the index to get the tile from.
   * Returns:  the required tile as a Tile object.
    */
    public Tile getTileAt(int index){
        return stock.get(stockIndex+index);
    }

    /*
   * Description: method to reset the stock.
   * Parameters: nothing.
   * Returns:  nothing.
    */
    public void resetStock() {
        remainingTileCount = 2 * deck.length * deck[0].length;
        this.stockIndex = 0;
    }

    /*
   * Description: method to decrement the stock index;
   * Parameters: nothing.
   * Returns:  nothing
    */
    public void decrementStockIndex() {
        remainingTileCount++;
        stockIndex--;
    }

    /*
       * Description: mehtod to increment the stockIndex.
       * Parameters: nothing.
       * Returns:  nothing
        */
    public void incrementStockIndex() {
        remainingTileCount--;
        stockIndex++;
    }

    /*
   * Description: method to get the colros.
   * Parameters: nothing.
   * Returns:  integer array of colors.
    */
    public int[] getColors() {
        return colors;
    }

    /*
   * Description: method to get the symbols
   * Parameters: nothing.
   * Returns:  char array of symbols
    */
    public char[] getSymbols() {
        return symbols;
    }

    /*
   * Description: method to get the next tile on top of the stock.
   * Parameters: nothing.
   * Returns:  next tile as a Tile object.
    */
    public Tile getNextTile() {
        remainingTileCount--;
        return stock.get(stockIndex++);
    }

    /*
   * Description: method to get the remaining stock
   * Parameters: nothing.
   * Returns:  list of remaining tiles as a List<Tile>.
    */
    public List<Tile> getRemainingStock(){
        return stock.subList(stockIndex,stock.size());
    }

    /*
       * Description: public function to mark a specific tile combination as used.
       * Parameters: tile as a Tile object.
       * Returns: nothing.
        */
    public void markCombo(Tile tile) {
        int row = getIndexForSymbol(tile.symbol);
        int column = getIndexForColor(tile.color);
        deck[row][column]++;

    }


    /*
       * Description: public function to unmark a specific tile combination as used.
       * Parameters: tile as a Tile object.
       * Returns: nothing.
        */
    public void deleteCombo(Tile tile) {
        int row = getIndexForSymbol(tile.symbol);
        int column = getIndexForColor(tile.color);
        deck[row][column]--;
    }

    /*
       * Description: Private function to find the array index for given symbol.
       * Parameters: symbol as char.
       * Returns: arrayindex for the symbol as int, -1 if not found.
        */
    public int getIndexForSymbol(char x) {
        for (int i = 0; i < symbols.length; i++) {
            if (symbols[i] == x) {
                return i;
            }
        }
        return -1;
    }

    /*
         * Description: Private function to find the array index for given color.
         * Parameters: color as int.
         * Returns: arrayindex for the color as int, -1 if not found.
          */
    public int getIndexForColor(int color) {
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == color) {
                return i;
            }
        }
        return -1;
    }

    /*
   * Description: public function to get the remaining tile count
   * Parameters: nothing.
   * Returns: nothing.
    */
    public int getRemainingTileCount() {
        return remainingTileCount;
    }
}
