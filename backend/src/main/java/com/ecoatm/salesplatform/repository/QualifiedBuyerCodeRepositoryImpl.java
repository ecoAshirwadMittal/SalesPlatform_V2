package com.ecoatm.salesplatform.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;

/**
 * Custom-fragment implementation discovered by Spring Data via the
 * {@code <RepoName>Impl} naming convention. Holds the R2-init bulk-write
 * SQL that requires raw JDBC for reliable Postgres {@code bigint[]}
 * parameter binding.
 */
public class QualifiedBuyerCodeRepositoryImpl implements QualifiedBuyerCodeRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    /**
     * Three-set INSERT producing one row per Active Wholesale/Data_Wipe
     * buyer code. {@code GROUP BY bc.id} collapses any M:M fan-out from
     * {@code buyer_code_buyers} where a code is shared across buyers.
     *
     * <p>Uses raw JDBC ({@link Session#doReturningWork}) instead of
     * {@link jakarta.persistence.Query#executeUpdate()} because
     * Hibernate's {@code setParameter} does not coerce a Java
     * {@code Long[]} into a Postgres {@code bigint[]} reliably across
     * versions. {@code Connection#createArrayOf("bigint", ...)} is the
     * supported path.
     */
    private static final String BULK_INSERT_SQL = """
        INSERT INTO buyer_mgmt.qualified_buyer_codes (
            scheduling_auction_id, buyer_code_id, qualification_type,
            included, is_special_treatment, created_date, changed_date
        )
        SELECT ?, bc.id,
               CASE
                 WHEN bc.id = ANY(?) THEN 'Qualified'
                 WHEN bc.id = ANY(?) THEN 'Qualified'
                 ELSE 'Not_Qualified'
               END,
               CASE
                 WHEN bc.id = ANY(?) THEN TRUE
                 WHEN bc.id = ANY(?) THEN TRUE
                 ELSE FALSE
               END,
               (bc.id = ANY(?)),
               NOW(), NOW()
          FROM buyer_mgmt.buyer_codes bc
          JOIN buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_code_id = bc.id
          JOIN buyer_mgmt.buyers b              ON b.id = bcb.buyer_id
         WHERE bc.buyer_code_type IN ('Wholesale','Data_Wipe')
           AND b.status = 'Active'
         GROUP BY bc.id
        """;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int bulkInsertForRound(Long saId, Long[] qualifiedIds, Long[] specialIds) {
        // Defensive defaults — Postgres array_op() with NULL returns NULL,
        // never TRUE. Empty arrays evaluate false on every row, which is
        // the correct semantic for an empty set.
        Long[] safeQualified = qualifiedIds != null ? qualifiedIds : new Long[0];
        Long[] safeSpecial   = specialIds   != null ? specialIds   : new Long[0];

        Session session = em.unwrap(Session.class);
        return session.doReturningWork(connection -> {
            try (PreparedStatement ps = connection.prepareStatement(BULK_INSERT_SQL)) {
                // Reuse the two java.sql.Array objects across 6 placeholders to avoid 4 redundant JNI roundtrips.
                java.sql.Array specialArr   = connection.createArrayOf("bigint", safeSpecial);
                java.sql.Array qualifiedArr = connection.createArrayOf("bigint", safeQualified);
                ps.setLong(1, saId);
                ps.setArray(2, specialArr);    // qualification_type WHEN special
                ps.setArray(3, qualifiedArr);  // qualification_type WHEN qualified
                ps.setArray(4, specialArr);    // included WHEN special
                ps.setArray(5, qualifiedArr);  // included WHEN qualified
                ps.setArray(6, specialArr);    // is_special_treatment
                return ps.executeUpdate();
            }
        });
    }

    /**
     * Post-V72 no-op. {@code qbc_buyer_codes} and
     * {@code qbc_scheduling_auctions} were dropped by V72 — the QBC table
     * holds {@code scheduling_auction_id} and {@code buyer_code_id} as
     * direct FK columns now (populated by
     * {@link #bulkInsertForRound(Long, Long[], Long[])}). Retained for
     * spec parity; always returns 0.
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public int bulkInsertJunctions(Long saId) {
        return 0;
    }
}
