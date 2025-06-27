package com.merveartut.task_manager.repository;

import com.merveartut.task_manager.enums.Role;
import com.merveartut.task_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByName(String name);

    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findByRole(@Param("role")Role role);

    boolean existsByEmail(String email);
}
