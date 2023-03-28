package fr.epita.assistants.jws.data.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity @Table(name = "game")
@Getter
@Setter

public class GameModel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Timestamp starttime;
    public String state;

    @ElementCollection @CollectionTable(name = "game_map" , joinColumns = @JoinColumn(name = "gamemodel_id"))
    public List<String> map;


    @OneToMany(mappedBy = "game")
    private List<PlayerModel> players = new ArrayList<>();


    public void addPlayer(PlayerModel player)
    {
        players.add(player);
    }

    public List<PlayerModel> getPlayers()
    {
        return players;
    }

}
