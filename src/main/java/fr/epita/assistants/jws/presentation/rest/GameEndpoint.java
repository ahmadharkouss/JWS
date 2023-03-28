package fr.epita.assistants.jws.presentation.rest;

import fr.epita.assistants.jws.domain.entity.GameEntity;


import fr.epita.assistants.jws.domain.entity.PlayerEntity;
import fr.epita.assistants.jws.domain.service.GameService;
import fr.epita.assistants.jws.errors.Errors;
import fr.epita.assistants.jws.presentation.rest.request.CreateGameRequest;
import fr.epita.assistants.jws.presentation.rest.request.JoinGameRequest;
import fr.epita.assistants.jws.presentation.rest.request.MovePlayerRequest;
import fr.epita.assistants.jws.presentation.rest.request.PutBombRequest;
import fr.epita.assistants.jws.presentation.rest.response.GameDetailResponse;
import fr.epita.assistants.jws.presentation.rest.response.GameListResponse;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GameEndpoint {



    @Inject
            GameService gs;

    @GET
    @Path("/games")
    @Produces(MediaType.APPLICATION_JSON)
    public Response game() {
        List<GameEntity> games = gs.getallgames();
        List<GameListResponse> gameResponses = new ArrayList<>();
        for (GameEntity game : games) {
            GameListResponse gameResponse = new GameListResponse();
            gameResponse.setId(game.getId());
            gameResponse.setPlayers(game.getPlayers().size());
            gameResponse.setState(game.getState());
            gameResponses.add(gameResponse);
        }

        return Response.ok(gameResponses).build();
    }

    @GET
    @Path("/games/{gameId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response game(@PathParam("gameId") Long id)
    {
        GameEntity ge = gs.getspecificgame(id);
        if (ge == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Cannot find game with this id")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        
        GameDetailResponse res = new GameDetailResponse();
        res.setId(ge.getId());
        res.setStartTime(ge.getStarttime());
        for(int i=0 ; i <ge.getPlayers().size();i++)
        {
            GameDetailResponse.player nn = new  GameDetailResponse.player();
            nn.setName(ge.getPlayers().get(i).getName());
            nn.setId(ge.getPlayers().get(i).getId());
            nn.setLives(ge.getPlayers().get(i).getLives());
            nn.setPosX(ge.getPlayers().get(i).getPosx());
            nn.setPosY(ge.getPlayers().get(i).getPosy());
            res.getPlayers().add(nn);
        }

        res.setMap(ge.getMap());
        res.setState(ge.getState());
        return Response.ok(res).build();
    }

    @POST
    @Path("/games")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response game(CreateGameRequest createGameRequest) throws IOException {
        if (createGameRequest.getName() == null || createGameRequest.getName().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("request or name is null")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        return  game(gs.creategame(createGameRequest.getName()).getId());
    }


    @POST
    @Path("/games/{gameId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response game(@PathParam("gameId") Long id , JoinGameRequest joinGameRequest)  {

        if(joinGameRequest.getName() == null || joinGameRequest.getName().isEmpty())
        {
             return Response.status(Response.Status.BAD_REQUEST).entity("The request is null, or the player name is null")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        GameEntity ge = gs.getspecificgame(id);

        if (ge == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Game with this ID does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        ge = gs.join_game(joinGameRequest.getName() ,id , ge);
        if (ge==null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("the game cannot be started (already started, too many players)")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        GameDetailResponse res = new GameDetailResponse();
        res.setId(ge.getId());
        res.setStartTime(ge.getStarttime());
        for(int i=0 ; i <ge.getPlayers().size();i++)
        {
            GameDetailResponse.player nn = new  GameDetailResponse.player();
            nn.setName(ge.getPlayers().get(i).getName());
            nn.setId(ge.getPlayers().get(i).getId());
            nn.setLives(ge.getPlayers().get(i).getLives());
            nn.setPosX(ge.getPlayers().get(i).getPosx());
            nn.setPosY(ge.getPlayers().get(i).getPosy());
            res.getPlayers().add(nn);
        }

        res.setMap(ge.getMap());
        res.setState(ge.getState());
        return Response.ok(res).build();

    }



    @PATCH
    @Path("/games/{gameId}/start")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)

    public Response gamestart(@PathParam("gameId") Long id)
    {
        GameEntity ge = gs.getspecificgame(id);

        if (ge == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Game with this ID does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        ge = gs.start_game(ge,id);
        if (ge==null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("the game cannot be started (already started, too many players)")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        GameDetailResponse res = new GameDetailResponse();
        res.setId(ge.getId());
        res.setStartTime(ge.getStarttime());
        for(int i=0 ; i <ge.getPlayers().size();i++)
        {
            GameDetailResponse.player nn = new  GameDetailResponse.player();
            nn.setName(ge.getPlayers().get(i).getName());
            nn.setId(ge.getPlayers().get(i).getId());
            nn.setLives(ge.getPlayers().get(i).getLives());
            nn.setPosX(ge.getPlayers().get(i).getPosx());
            nn.setPosY(ge.getPlayers().get(i).getPosy());
            res.getPlayers().add(nn);
        }

        res.setMap(ge.getMap());
        res.setState(ge.getState());
        return Response.ok(res).build();


    }
    @POST
    @Path("/games/{gameId}/players/{playerId}/move")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response movePlayer(@PathParam("gameId") Long gameId, @PathParam("playerId") Long playerId, MovePlayerRequest movePlayerRequest)
    {
        //the request is null
        if(movePlayerRequest.getPosX() == null || movePlayerRequest.getPosY() == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("The request is null")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        //the game do not exist
         GameEntity ge = gs.getspecificgame(gameId);
         if(ge==null)
         {
             return Response.status(404)
                     .entity("no game with this id")
                     .build();
         }
         //the player do not exist

        int c=0;
        PlayerEntity pe = new PlayerEntity();
        for(int i=0 ; i<ge.getPlayers().size() ; i++)
        {
            if(playerId.compareTo(ge.getPlayers().get(i).getId())==0)
            {
                pe=ge.getPlayers().get(i);
                c=1;
            }
        }

        if(c==0)
        {
            return Response.status(404)
                    .entity("no player with this id")
                    .build();
        }
        //the game finished or already started or the player is already dead
        if(ge.getState().equals("STARTING") ||ge.getState().equals("FINISHED") || (pe.getLives()==0) )
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Game did not started yet OR is finished, please join another game")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        Errors err= new Errors();
        err.setErr(0);
        ge=gs.isvalid_move(movePlayerRequest.getPosX(),movePlayerRequest.getPosY(),playerId,gameId ,ge,err);

        //NOT VALID MOVE
        if(ge==null)
        {
            if(err.getErr()==1)
            {
                return Response.status(429)
                        .entity("The player has already moved in the last X ticks")
                        .build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid move")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        GameDetailResponse res = new GameDetailResponse();
        res.setId(ge.getId());
        res.setStartTime(ge.getStarttime());
        for(int i=0 ; i <ge.getPlayers().size();i++)
        {
            GameDetailResponse.player nn = new  GameDetailResponse.player();
            nn.setName(ge.getPlayers().get(i).getName());
            nn.setId(ge.getPlayers().get(i).getId());
            nn.setLives(ge.getPlayers().get(i).getLives());
            nn.setPosX(ge.getPlayers().get(i).getPosx());
            nn.setPosY(ge.getPlayers().get(i).getPosy());
            res.getPlayers().add(nn);
        }

        res.setMap(ge.getMap());
        res.setState(ge.getState());
        return Response.ok(res).build();
    }

    @POST
    @Path("/games/{gameId}/players/{playerId}/bomb")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setbomb(@PathParam("gameId") Long gameId, @PathParam("playerId") Long playerId, PutBombRequest putBombRequest) throws InterruptedException {

        //the game do not exist
        GameEntity ge = gs.getspecificgame(gameId);
        if(ge==null)
        {
            return Response.status(404)
                    .entity("no game with this id")
                    .build();
        }
        //the player do not exist

        int c=0;
        PlayerEntity pe = new PlayerEntity();
        for(int i=0 ; i<ge.getPlayers().size() ; i++)
        {
            if(playerId.compareTo(ge.getPlayers().get(i).getId())==0)
            {
                pe=ge.getPlayers().get(i);
                c=1;
            }
        }

        if(c==0)
        {
            return Response.status(404)
                    .entity("no player with this id")
                    .build();
        }
        //the request is null
        if(putBombRequest.getPosX() == null || putBombRequest.getPosY() == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("The request is null")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        //the game finished or already started or the player is already dead
        if(ge.getState().equals("STARTING") ||ge.getState().equals("FINISHED") || (pe.getLives()==0) )
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Game did not started yet OR is finished, please join another game")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        Errors err= new Errors();
        err.setErr(0);

        ge=gs.putbomb(putBombRequest.getPosX(),putBombRequest.getPosY(),playerId,gameId ,ge,err);
        //NOT VALID BOMB
        if(ge==null)
        {
            if(err.getErr()==1)
            {
                return Response.status(429)
                        .entity("The player has already bomb in the last X ticks")
                        .build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid move")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        GameDetailResponse res = new GameDetailResponse();
        res.setId(ge.getId());
        res.setStartTime(ge.getStarttime());
        for(int i=0 ; i <ge.getPlayers().size();i++)
        {
            GameDetailResponse.player nn = new  GameDetailResponse.player();
            nn.setName(ge.getPlayers().get(i).getName());
            nn.setId(ge.getPlayers().get(i).getId());
            nn.setLives(ge.getPlayers().get(i).getLives());
            nn.setPosX(ge.getPlayers().get(i).getPosx());
            nn.setPosY(ge.getPlayers().get(i).getPosy());
            res.getPlayers().add(nn);
        }

        res.setMap(ge.getMap());
        res.setState(ge.getState());
        return Response.ok(res).build();


    }


}


