package org.yearup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yearup.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>
{
    User findByUsername(String username);

    boolean existsByUsername(String username);
}
