//Authors: Wesley Angus

package csci4100.uoit.ca.csci4100_final_project;

import java.util.List;

public interface DatabaseListener
{
    void syncGames(List<Game> games, boolean addedItems);
}
