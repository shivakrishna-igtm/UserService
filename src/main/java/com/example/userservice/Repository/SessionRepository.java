package com.example.userservice.Repository;

import com.example.userservice.Models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface SessionRepository extends JpaRepository<Session,Long> {

    Optional<Session> findByTokenAndUser_Id(String token, Long userId);
}
