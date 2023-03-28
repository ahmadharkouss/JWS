package fr.epita.assistants.jws.presentation.rest.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class GameListResponse
{
    private Long id;
    private int players;
    private String state;


}
//we can add a function that return a list : hamelist response to simplify code
