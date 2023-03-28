package fr.epita.assistants.jws.presentation.rest.request;


//fait appel au seervice  plutot que  repository

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter


public class CreateGameRequest {

    private String name;

}
