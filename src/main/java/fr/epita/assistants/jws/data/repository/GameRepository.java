package fr.epita.assistants.jws.data.repository;

import fr.epita.assistants.jws.data.model.GameModel;
import fr.epita.assistants.jws.data.model.PlayerModel;

import fr.epita.assistants.jws.errors.Errors;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import org.codehaus.groovy.transform.sc.transformers.RangeExpressionTransformer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static fr.epita.assistants.jws.converter.Converter.*;
import javax.persistence.EntityManager;


//return a game model = represente une table dans la base de donne
//creer une ressource  dans la base de donne
//sauvgarder
//passer au service si l'operation a ete bine faite



@ApplicationScoped
public class GameRepository implements PanacheRepositoryBase<GameModel, Long>
{


    @Inject EntityManager entityManager;


    public List<GameModel> findAllGames() {
        return findAll().list();
    }


    /*
    public List<GameModel> findByState(String state) {
        return find("state", state).list();
    }
     */

    public GameModel findSpecificById(Long gameId) {
        return findById(gameId);
    }




   @Transactional
public GameModel createGame(String playerName) throws IOException {
    GameModel game = new GameModel();
    game.setState("STARTING");

       String mapPath = System.getenv("JWS_MAP_PATH");
       game.setMap(loadMapFromFile(mapPath));

    game.setStarttime(new Timestamp(System.currentTimeMillis()));


    PlayerModel player = new PlayerModel();
    player.setName(playerName);
    player.setLives(3);
    player.setPosition(1);
    player.setPosx(1);
    player.setPosy(1);
    player.setGame(game);
    entityManager.persist(player);
    game.addPlayer(player);
    entityManager.persist(game);
    return game;
}


public void setpos (PlayerModel pp , GameModel gm)
{
    if(gm.getPlayers().size()==0)
    {
        pp.setPosition(1);
        pp.setPosx(1);
        pp.setPosy(1);
    }
    else if(gm.getPlayers().size()==1)
    {
        pp.setPosition(2);
       pp.setPosx(15);
        pp.setPosy(1);
    }
    else if(gm.getPlayers().size()==2)
    {
        pp.setPosition(3);
        pp.setPosx(15);
        pp.setPosy(13);
    }
    else
    {
        pp.setPosition(4);
        pp.setPosx(1);
        pp.setPosy(13);
    }

}


@Transactional
public GameModel join_game(String name ,Long id)
{
    GameModel gm = findById(id);
    if(gm==null)
    {
        return null;
    }
    if(gm.getPlayers().size()==4 || gm.getState().equals("FINISHED") ||  gm.getState().equals("RUNNING"))
    {
        return null;
    }
    PlayerModel newp= new PlayerModel();
    setpos(newp , gm);
    newp.setName(name);
    newp.setLives(3);
     newp.setGame(gm);
    entityManager.persist(newp);
    gm.addPlayer(newp);
    entityManager.flush();
    entityManager.clear();
    //entityManager.persist(gm);
    return  gm;



}


    @Transactional
    public GameModel start_game(Long id)
    {
        GameModel gm = findById(id);
        if(gm==null)
        {
            return null;
        }
        if(gm.getPlayers().size()==1 || gm.getPlayers().size()==0 )
        {
            gm.setState("FINISHED");
            return gm;
        }
        gm.setState("RUNNING");
        return gm;
    }

    public PlayerModel find_player(List<PlayerModel> list , Long playerid)
    {
        for(int i=0 ; i<list.size() ; i++)
        {
            if(list.get(i).getId().compareTo(playerid)==0)
            {
                return  list.get(i);
            }
        }
        return null;
    }

    @Transactional
    public GameModel isvalid_move(int posx , int posy , Long playerid ,Long gameid , Errors err )
    {
        //Get the game
         GameModel gm = findById(gameid);
         //Get the map
         char [][]matrixmap = decode_rle(gm.getMap());
         //Get the player
         PlayerModel player = find_player(gm.getPlayers(),playerid);
         if(matrixmap[posx][posy]=='B' || matrixmap[posx][posy]=='M' || matrixmap[posx][posy]=='W')
        {
            if(matrixmap[posx][posy]=='B')
            {
                player.setLives(player.getLives()-1);
            }
            return null;
        }
         //position is valid
         player.setPosx(posx);
         player.setPosy(posy);

         //Get the tick duration
        String tickDurationString = System.getenv("JWS_TICK_DURATION");
        int tickDuration = Integer.parseInt(tickDurationString);


        //Get the delay mouvement duration in tick
        String delay_mvm= System.getenv("JWS_DELAY_MOVEMENT");
        int delay = Integer.parseInt(delay_mvm)*tickDuration;


        //setstarttime(Timestamp.valueof(LocalDateTime.now()))

        // Get current timestamp
        Timestamp now = new Timestamp(System.currentTimeMillis());


        //Get player last mouvement
        Timestamp playerlastmvmt= player.getLastmovement();

        //first move
        if(playerlastmvmt==null)
        {
            player.setLastmovement(now);
            return gm;
        }

        //get the diff between the player current mouvement and last mouvement
        long timeDiff = now.getTime() - playerlastmvmt.getTime();

        if(timeDiff <delay)
        {
            err.setErr(1);
            return null;
        }
        player.setLastmovement(now);
        return gm;
    }




    @Transactional
    public GameModel putbomb(int posx , int posy , Long playerid , Long gameid, Errors err) throws InterruptedException {

        //Get the game
        GameModel gm = findById(gameid);
        //get the player
        PlayerModel player = find_player(gm.getPlayers(),playerid);

        //coordinate are wrong
        if(player.getPosx()!=posx || player.getPosy()!=posy)
        {
            return null;
        }
        //Get the map
        char [][]matrixmap = decode_rle(gm.getMap());

        //coordinate are valid


        //store before the modification
        String all_line = gm.getMap().get(posy);
        char c = matrixmap[posx][posy];


        //PLace the bomb
        List<String> newmap  = gm.getMap();
        StringBuilder sb = new StringBuilder(newmap.get(posy));
        sb.setCharAt(posx, 'B');
        newmap.set(posy, sb.toString());
        gm.setMap(newmap);


        //Get the tick duration
        String tickDurationString = System.getenv("JWS_TICK_DURATION");
        int tickDuration = Integer.parseInt(tickDurationString);


        //Get the delay mouvement tick duration between 2 bombs
        // it is also the delay before the bomb explode
        String bomdelay =System.getenv("JWS_DELAY_BOMB");
        int delay = Integer.parseInt(bomdelay)*tickDuration;


        // Get current timestamp
        Timestamp now = new Timestamp(System.currentTimeMillis());


        //Get player last mouvement
        Timestamp playerlastbomb= player.getLastbomb();


        if(playerlastbomb==null)
        {
            player.setLastbomb(now);
        }

        if(playerlastbomb!=null)
        {
            long timeDiff = now.getTime() - playerlastbomb.getTime();

            if(timeDiff <delay )
            {
                err.setErr(1);
                return null;
            }
            //set the player last bomb time
            player.setLastbomb(now);

        }


        //ADD delay for explosion
        Thread.sleep(delay);


        //consequences


        //convert wooden block to floor
        //decrease live of players next
        //after
        for(int i=posx ; i<all_line.length() ;i++)
        {
            if( all_line.charAt(i) ==c)
            {
                for(int j=0; j<gm.getPlayers().size();j++)
                {

                        gm.getPlayers().get(j).setLives(2);

                }
              if(all_line.charAt(i)=='W')
                {
                    List<String> newmap2  = gm.getMap();
                    StringBuilder sb2 = new StringBuilder(newmap2.get(posy));
                    sb2.setCharAt(i, 'G');
                    newmap2.set(posy, sb2.toString());
                    gm.setMap(newmap2);
                }
            }
        }
        //opposite side
        for(int i=posx ; i>=0 ;i--)
        {
            if( all_line.charAt(i) ==c)
            {
                for(int j=0; j<gm.getPlayers().size();j++)
                {

                        gm.getPlayers().get(j).setLives(2);

                }
                if(all_line.charAt(i)=='W')
                {
                    List<String> newmap2  = gm.getMap();
                    StringBuilder sb2 = new StringBuilder(newmap2.get(posy));
                    sb2.setCharAt(i, 'G');
                    newmap2.set(posy, sb2.toString());
                    gm.setMap(newmap2);
                }
            }
        }
        int i=0;
        for(PlayerModel pm : gm.getPlayers())
        {
            if(pm.getLives()==0)
            {
                i+=1;
            }
        }
        if(i==gm.getPlayers().size())
        {
            gm.setState("FINISHED");
        }

        return gm;

    }


    public static char[][] decode_rle(List<String> todecode) {
        char[][] map = new char[todecode.size()][];
        int row = 0;
        for (String encodedRow : todecode) {
            List<Character> decodedRow = new ArrayList<>();
            boolean isDigit = false;
            int count = 0;
            for (char c : encodedRow.toCharArray()) {
                if (Character.isDigit(c)) {
                    if (!isDigit) {
                        count = 0;
                        isDigit = true;
                    }
                    count = count * 10 + Character.getNumericValue(c);
                } else {
                    isDigit = false;
                    count = (count == 0) ? 1 : count;
                    for (int i = 0; i < count; i++) {
                        decodedRow.add(c);
                    }
                    count = 0;
                }
            }
            map[row] = new char[decodedRow.size()];
            for (int i = 0; i < decodedRow.size(); i++) {
                map[row][i] = decodedRow.get(i);
            }
            row++;
        }
        return map;
    }


    //testing function for decode

    public static void printArray(char[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
    }



    public static List<String> loadMapFromFile(String mapPath) throws IOException {
        List<String> map = new ArrayList<String>();

        BufferedReader reader = new BufferedReader(new FileReader(mapPath));

        String line;
        while ((line = reader.readLine()) != null) {
            map.add(line);
        }

        reader.close();

        return map;
    }



}
