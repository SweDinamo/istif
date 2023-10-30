package com.istif_backend.repository;

import com.istif_backend.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User,Long> {
    List<User> findAll();
    User findByUsername(String username);
    User findByEmail(String email);
}
