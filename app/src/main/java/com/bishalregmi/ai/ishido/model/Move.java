package com.bishalregmi.ai.ishido.model;

/************************************************************
 * Name:  Bishal Regmi                                      *
 * Project:  Project 3 - Ishido for Two                     *
 * Class:  CMPS331 - Artificial Intelligence                *
 * Date:  03/22/2016                                        *
 ************************************************************/

/**
 * Created by Bishal on 2/15/2016.
 * This class will hold the row and column of a certain made move in the game1
 * also tile combination of the move
 * score associated with that particular move
 * and its parent move
 */
public class Move {
   public Move(int row, int col, Tile tile) {
        this.row = row;
        this.col = col;
        this.tile = tile;
        score = -1;
    }

    public int row;
    public int col;
    public Tile tile;
    public int score;
}
