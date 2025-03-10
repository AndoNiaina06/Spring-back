package com.starter.starter.repository;

import com.starter.starter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Long>{
    User findByEmail(@Param("email")String email);

    Long countByType(String type);

    @Query("SELECT TO_CHAR(u.createdAt, 'MM') AS month, COUNT(u) " +
            "FROM User u GROUP BY month ORDER BY month")
    List<Object[]> countUsersByMonth();
}
