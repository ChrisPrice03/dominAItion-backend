package com.dominAItionBackend.repository;

import com.dominAItionBackend.models.Game;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends MongoRepository<Game, String> {
    Game findGameById(String id);
    @Query("{ 'status': { $ne: 'done' } }")
    List<Game> findAllByStatusNotDone();
}
