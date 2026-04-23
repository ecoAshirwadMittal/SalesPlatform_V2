package com.ecoatm.salesplatform.repository;

import com.ecoatm.salesplatform.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Find a non-consumed, non-expired token by its SHA-256 hash.
     * The caller must still verify {@code expiresAt} is after now() as a
     * double-check — the query filter is the primary guard.
     */
    @Query("""
            SELECT t FROM PasswordResetToken t
            WHERE t.tokenHash = :hash
              AND t.consumedAt IS NULL
              AND t.expiresAt > :now
            """)
    Optional<PasswordResetToken> findValidByHash(@Param("hash") String hash,
                                                  @Param("now") Instant now);

    /**
     * Purge all unconsumed tokens for a user before issuing a new one.
     * Prevents token accumulation when a user requests multiple resets.
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.userId = :userId AND t.consumedAt IS NULL")
    void deleteActiveTokensForUser(@Param("userId") Long userId);
}
