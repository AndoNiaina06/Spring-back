package com.starter.starter.repository;

import com.starter.starter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long>{
    User findByEmail(@Param("email")String email);
}
