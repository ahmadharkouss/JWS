package fr.epita.assistants.jws.data.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity @Table(name = "player")
@Getter
@Setter
public class PlayerModel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Timestamp lastbomb;
    public Timestamp lastmovement;
    public Integer  lives;
    String name;
    Integer posx;
    Integer posy;
    Integer position;
    @ManyToOne
    @JoinColumn(name = "game_id")
    private GameModel game;
}
