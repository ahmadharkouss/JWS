package fr.epita.assistants.jws.data.repository;

import fr.epita.assistants.jws.data.model.PlayerModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

public class PlayerRepository implements PanacheRepositoryBase<PlayerModel,Long>
{

}
