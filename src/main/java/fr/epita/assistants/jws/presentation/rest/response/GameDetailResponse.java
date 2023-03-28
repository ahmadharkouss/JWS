package fr.epita.assistants.jws.presentation.rest.response;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter


public class GameDetailResponse
{
    private Timestamp startTime;

    private String state;
    private List<player> players = new ArrayList<>();

    private List<String> map;
    private Long id;


    @Getter
    @Setter
    public static class player {
        private Long id;
        private String name;
        private Integer lives;
        private Integer posX;
        private Integer posY;
    }
}
