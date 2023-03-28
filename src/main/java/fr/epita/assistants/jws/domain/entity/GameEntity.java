package fr.epita.assistants.jws.domain.entity;


import fr.epita.assistants.jws.data.model.PlayerModel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// contient les champ qui nous interesse du model
@Getter
@Setter

public class GameEntity
{
    public long id;
    public Timestamp starttime;
    public String state;
    public List<String> map;
    private List<PlayerEntity> players = new ArrayList<>();

    public void addPlayer(PlayerEntity player)
    {
        players.add(player);
    }
    public List<PlayerEntity> getPlayers()
    {
        return players;
    }
}
