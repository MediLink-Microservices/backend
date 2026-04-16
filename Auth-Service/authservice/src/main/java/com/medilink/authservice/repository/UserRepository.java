package com.medilink.authservice.repository;

import com.medilink.authservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByEmailAndIsActiveTrue(String email);
    long countByRole(String role);
    java.util.List<User> findByRole(String role);
    long countByRoleAndIsApprovedFalse(String role);
}