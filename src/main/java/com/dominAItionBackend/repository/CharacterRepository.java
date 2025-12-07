package com.dominAItionBackend.repository;

import com.dominAItionBackend.models.Character;
import com.dominAItionBackend.models.World;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterRepository extends MongoRepository<Character, String> {
    List<Character> findByCreatorID(String id);

}
