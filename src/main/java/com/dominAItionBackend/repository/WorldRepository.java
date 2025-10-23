package com.dominAItionBackend.repository;

import com.dominAItionBackend.models.World;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorldRepository extends MongoRepository<World, String> {
    World findByCreatorID(String id);
}
