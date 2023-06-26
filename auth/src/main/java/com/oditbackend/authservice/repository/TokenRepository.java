package com.oditbackend.authservice.repository;

import java.util.List;
import java.util.Optional;

import com.oditbackend.authservice.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query(value = """
      select t from Token t inner join User u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.expired = false and t.revoked = false)\s
      """)
    List<Token> findAllValidTokenByUser(@Param("id") Integer id);

    Optional<Token> findByToken(String token);
}