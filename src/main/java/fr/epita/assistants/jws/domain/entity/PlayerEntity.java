package fr.epita.assistants.jws.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.security.PublicKey;
import java.sql.Timestamp;
import java.time.Instant;

@Getter
@Setter

public class PlayerEntity {
    public Long id;
    public Timestamp lastbomb;
    public Timestamp lastmovement;
    public Integer  lives;
    String name;
    Integer posx;
    Integer posy;
    Integer position;
}
