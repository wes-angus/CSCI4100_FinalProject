//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import java.util.ArrayList;

public interface DatabaseListener
{
    void syncGames(ArrayList<Game> games, short option);
}
