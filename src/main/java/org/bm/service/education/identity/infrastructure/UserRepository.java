package org.bm.service.education.identity.infrastructure;

import org.bm.service.education.identity.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPersonnelNumber(String personnelNumber);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPersonnelNumber(String personnelNumber);
}
