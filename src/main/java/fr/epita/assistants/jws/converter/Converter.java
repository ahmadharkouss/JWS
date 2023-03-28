package fr.epita.assistants.jws.converter;


import fr.epita.assistants.jws.data.model.GameModel;
import fr.epita.assistants.jws.data.model.PlayerModel;
import fr.epita.assistants.jws.domain.entity.GameEntity;
import fr.epita.assistants.jws.domain.entity.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

//convertir model en entity
public class Converter {


    public static GameEntity toGameEnity(GameModel gm)
    {

        GameEntity res = new GameEntity();
        res.setId(gm.getId());
        res.setMap(gm.getMap());
        res.setState(gm.getState());
        res.setStarttime(gm.getStarttime());
        res.setPlayers(tolistPlayerEntity(gm.getPlayers()));
        return  res;
    }

    public static List<GameEntity> tolistGameEntity(List<GameModel> glm)
    {
        List<GameEntity> res = new ArrayList<>();
        for (GameModel gm : glm)
        {
            res.add(toGameEnity(gm));
        }
        return res;
    }


    public static PlayerEntity toPlayerEnity(PlayerModel pm)
    {
        PlayerEntity res= new PlayerEntity();
        res.setId(pm.getId());
        res.setName(pm.getName());
        res.setPosition(pm.getPosition());
        res.setLives(pm.getLives());
        res.setPosx(pm.getPosx());
        res.setPosy(pm.getPosy());
        res.setLastbomb(pm.getLastbomb());
        res.setLastmovement(pm.getLastmovement());
        return  res;
    }



    public static List<PlayerEntity> tolistPlayerEntity(List<PlayerModel> glm)
    {
        List<PlayerEntity> res = new ArrayList<>();
        for (PlayerModel gm : glm)
        {
            res.add(toPlayerEnity(gm));
        }
        return res;
    }

    /////////////////////////////////////


    public static GameEntity toGameEnity2(GameModel gm , GameEntity gi)
    {

        gi.setPlayers(tolistPlayerEntity2(gm.getPlayers(), gi.getPlayers()));
        return  gi;
    }

    public static GameEntity toGameEnity3(GameModel gm , GameEntity gi)
    {

        gi.setState(gm.getState());
        return  gi;
    }




    public static GameEntity toGameEnity4(GameModel gm , GameEntity gi ,Long playerid )
    {

        PlayerModel pm = new PlayerModel();
        for(int i=0 ; i<gm.getPlayers().size() ; i++)
        {
            if(playerid.compareTo(gm.getPlayers().get(i).getId())==0)
            {
                pm=gm.getPlayers().get(i);
            }
        }


        PlayerEntity pe = new PlayerEntity();
        for(int i=0 ; i<gi.getPlayers().size() ; i++)
        {
            if(playerid.compareTo(gi.getPlayers().get(i).getId())==0)
            {
                pe=gi.getPlayers().get(i);
            }
        }
        pe.setPosx(pm.getPosx());
        pe.setPosy(pm.getPosy());
        return  gi;
    }

    public static GameEntity toGameEnity5(GameModel gm , GameEntity gi ,Long playerid )
    {
        PlayerModel pm = new PlayerModel();
        for(int i=0 ; i<gm.getPlayers().size() ; i++)
        {
            if(playerid.compareTo(gm.getPlayers().get(i).getId())==0)
            {
                pm=gm.getPlayers().get(i);
            }
        }


        PlayerEntity pe = new PlayerEntity();
        for(int i=0 ; i<gi.getPlayers().size() ; i++)
        {
            if(playerid.compareTo(gi.getPlayers().get(i).getId())==0)
            {
                pe=gi.getPlayers().get(i);
            }
        }
        pe.setPosx(pm.getPosx());
        pe.setPosy(pm.getPosy());
        gi.setMap(gm.getMap());
        gi.setState(gm.getState());
        return  gi;

    }



    public PlayerEntity find_player(List<PlayerEntity> list , Long playerid)
    {
        for(int i=0 ; i<list.size() ; i++)
        {
            if(playerid.compareTo(list.get(i).getId())==0)
            {
                return  list.get(i);
            }
        }
        return null;
    }
    public static List<GameEntity> tolistGameEntity2(List<GameModel> glm , List<GameEntity> res)
    {
        int i=0;
        for(; i <res.size() ; i++)
        {
            res.set(i,toGameEnity2(glm.get(i), res.get(i)));
        }
        for(int j=i ;j < glm.size() ; j++)
        {
            res.add(toGameEnity(glm.get(j)));
        }
        return res;
    }

    public static PlayerEntity toPlayerEnity2(PlayerModel pm ,PlayerEntity pi)
    {

        pi.setId(pm.getId());
        pi.setName(pm.getName());
        pi.setPosition(pm.getPosition());
        pi.setLives(pm.getLives());
        pi.setPosx(pm.getPosx());
        pi.setPosy(pm.getPosy());
        pi.setLastbomb(pm.getLastbomb());
        pi.setLastmovement(pm.getLastmovement());
        return  pi;
    }



    public static List<PlayerEntity> tolistPlayerEntity2(List<PlayerModel> glm , List<PlayerEntity> res )
    {
        int i=0;
        for(; i <res.size() ; i++)
        {
            res.set(i,toPlayerEnity2(glm.get(i), res.get(i)));
        }
        for(int j=i ;j < glm.size() ; j++)
        {
            res.add(toPlayerEnity(glm.get(j)));
        }
        return res;
    }



}
