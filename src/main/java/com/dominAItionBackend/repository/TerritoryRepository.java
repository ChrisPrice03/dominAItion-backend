package com.dominAItionBackend.repository;

import com.dominAItionBackend.models.Territory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TerritoryRepository extends MongoRepository<Territory, String> {
    List<Territory> findByGameIdAndOwnerId(String gameId, String ownerId);
}
