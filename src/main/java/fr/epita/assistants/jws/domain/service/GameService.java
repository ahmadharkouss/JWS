package fr.epita.assistants.jws.domain.service;



//recuperer repository et le donner a rssource pour saavoir si c une bonne ou mauvaise request//return entity
//convert before model to entity
//apres on le renvoie dans la response


//toute la loique du program
//exemple : comment les players ce dep-lasse
//inject

// ex : create game  : fait appel a repo

//transactional : modificaton de la base de donne


import fr.epita.assistants.jws.data.model.GameModel;
import fr.epita.assistants.jws.data.repository.GameRepository;
import fr.epita.assistants.jws.domain.entity.GameEntity;
import fr.epita.assistants.jws.errors.Errors;
import io.netty.util.NetUtil;
import org.apache.http.conn.util.PublicSuffixList;

import javax.inject.Inject;


import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import java.io.IOException;
import java.time.format.SignStyle;
import java.util.List;

import static fr.epita.assistants.jws.converter.Converter.*;

@ApplicationScoped
public class GameService
{

    @Inject
    GameRepository gameRepository;


    public List<GameEntity> getallgames()
    {
        List<GameModel> res= gameRepository.findAllGames();
        return tolistGameEntity(res);
    }


    public GameEntity   getspecificgame(Long game_id)
    {
        GameModel gm =  gameRepository.findSpecificById(game_id);
        if(gm==null)
        {
            return  null;
        }
        return  toGameEnity(gm);
    }


    @Transactional
    public GameEntity creategame( String name) throws IOException {
        GameModel gm =  gameRepository.createGame(name);
        return  toGameEnity(gm);

    }

    @Transactional
    public GameEntity  join_game(String name ,Long id , GameEntity gi)
    {

        GameModel go= gameRepository.join_game(name , id);
        if(go== null)
        {
            return null;
        }
        return  toGameEnity2(go,gi);

    }

    @Transactional
     public GameEntity start_game(GameEntity gi , Long id)
    {
        GameModel go= gameRepository.start_game(id);
        if(go== null)
        {
            return null;
        }
        return  toGameEnity3(go,gi);
    }

    @Transactional
    public GameEntity isvalid_move(int posx , int posy , Long playerid , Long gameid , GameEntity gi , Errors err)
    {
        GameModel go= gameRepository.isvalid_move(posx,posy,playerid,gameid,err);
        if(go==null)
        {
            return null;
        }
        return toGameEnity4(go,gi,playerid);
    }
    @Transactional
    public GameEntity putbomb(int posx , int posy , Long playerid ,Long gameid , GameEntity gi ,  Errors err) throws InterruptedException {
        GameModel go= gameRepository.putbomb(posx,posy,playerid,gameid,err);
        if(go==null)
        {
            return null;
        }
        return toGameEnity5(go,gi,playerid);
    }

}
