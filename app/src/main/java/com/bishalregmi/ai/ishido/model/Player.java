package com.bishalregmi.ai.ishido.model;

/************************************************************
 * Name:  Bishal Regmi                                      *
 * Project:  Project 3 - Ishido for Two                     *
 * Class:  CMPS331 - Artificial Intelligence                *
 * Date:  03/22/2016                                        *
 ************************************************************/

/**
 * Class to hold the score of the player
 * Created by Bishal on 1/23/2016.
 */
public class Player {
    private int score = 0;

    /*
   * Description: Public function to get playerScore.
   * Parameters: Nothing.
   * Returns: score as an integer.
    */
    public int getScore() {
        return score;
    }

    /*
   * Description:Public function to add the score to the existing score.
   * Parameters: score to be added as integer.
   * Returns: nothing.
    */
    public void up(int moveScore) {
        score += moveScore;
    }

    /*
* Description:Public function to subtract the score to the existing score.
* Parameters: score to be subtract as integer.
* Returns: nothing.
 */
    public void down(int moveScore) {
        score -= moveScore;
    }

    /*
   * Description: Public function to reset the score to 0.
   * Parameters: Nothing.
   * Returns: nothing.
    */
    public void reset() {
        score = 0;
    }
}
