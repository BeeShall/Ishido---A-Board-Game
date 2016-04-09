package com.bishalregmi.ai.ishido.model;

/************************************************************
 * Name:  Bishal Regmi                                      *
 * Project:  Project 3 - Ishido for Two                     *
 * Class:  CMPS331 - Artificial Intelligence                *
 * Date:  03/22/2016                                        *
 ************************************************************/

/**
 * Class to hold the tile color and symbol of a tile
 * Created by Bishal on 1/24/2016.
 */

public class Tile {

    /*
    * Description: Default Constructor for the tile class.
    * Parameters: Nothing.
    * Returns: nothing.
     */
    public Tile() {
        this.color = -1;
        this.symbol = ' ';

    }

    /*
  * Description: Non-Default Constructor for the tile class.
  * Parameters: Nothing.
  * Returns: nothing.
   */
    public Tile(int color, char symbol) {
        this.color = color;
        this.symbol = symbol;
    }

    public int color;
    public char symbol;
}

