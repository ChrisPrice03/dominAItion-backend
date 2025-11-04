package com.dominAItionBackend.repository;
import java.util.Optional;
import com.dominAItionBackend.models.Lobby;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LobbyRepository extends MongoRepository<Lobby, String> {
    Lobby findLobbyById(String id);
    Optional<Lobby> findByCode(String code);
}