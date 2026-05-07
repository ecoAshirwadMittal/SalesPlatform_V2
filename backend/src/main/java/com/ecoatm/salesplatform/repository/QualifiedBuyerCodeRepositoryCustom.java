package com.ecoatm.salesplatform.repository;

/**
 * Custom-fragment interface composed into {@link QualifiedBuyerCodeRepository}
 * via Spring Data's {@code <RepoName>Impl}-suffix discovery. Holds the
 * R2-init bulk-write methods that need raw JDBC for Postgres {@code bigint[]}
 * parameter binding (Hibernate's default {@code @Query} coercion is not
 * reliable across versions for native array params).
 *
 * <p>See {@link QualifiedBuyerCodeRepositoryImpl} for the implementations.
 */
public interface QualifiedBuyerCodeRepositoryCustom {

    /**
     * Round-agnostic three-set QBC bulk INSERT. Used by both R2 buyer assignment
     * (sub-project 5) and R3 pre-process (sub-project 6). Writes one
     * {@code qualified_buyer_codes} row for every Active Wholesale/Data_Wipe
     * buyer code, classifying by membership in {@code qualifiedIds} or
     * {@code specialIds}:
     *
     * <ul>
     *   <li>code in {@code specialIds} → {@code qualification_type=Qualified},
     *       {@code included=TRUE}, {@code is_special_treatment=TRUE}
     *   <li>code in {@code qualifiedIds} (and not in {@code specialIds}) →
     *       {@code qualification_type=Qualified}, {@code included=TRUE},
     *       {@code is_special_treatment=FALSE}
     *   <li>otherwise → {@code qualification_type=Not_Qualified},
     *       {@code included=FALSE}, {@code is_special_treatment=FALSE}
     * </ul>
     *
     * <p>Caller is expected to {@link QualifiedBuyerCodeRepository#deleteBySchedulingAuctionId}
     * the SA before invoking this method — the {@code uq_qbc_sa_bc} unique
     * constraint (V72) prevents re-insert otherwise.
     *
     * @return number of rows inserted (== count of active DW/WH codes).
     */
    int bulkInsertForRound(Long saId, Long[] qualifiedIds, Long[] specialIds);

    /**
     * Populates legacy {@code qbc_buyer_codes} and {@code qbc_scheduling_auctions}
     * junction tables. <strong>Post-V72 no-op</strong> — the V72 flatten
     * migration dropped both tables and replaced them with direct
     * {@code scheduling_auction_id} / {@code buyer_code_id} FK columns on
     * {@code qualified_buyer_codes} (already populated by
     * {@link #bulkInsertForRound}). Retained for spec parity with sub-project 5
     * design §7.3 step 3.
     *
     * @return always 0 post-V72.
     */
    int bulkInsertJunctions(Long saId);
}
