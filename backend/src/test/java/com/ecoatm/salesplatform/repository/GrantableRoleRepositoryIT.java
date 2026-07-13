package com.ecoatm.salesplatform.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

/**
 * Repository IT for {@link GrantableRoleRepository}, driven against the live
 * Postgres seed (V16 {@code user_roles} + {@code grantable_roles}) — same
 * {@code @DataJpaTest} + {@code replace = NONE} pattern as
 * {@link BuyerCodeRepositoryIT}.
 *
 * <p>Proves the grant-authorization queries resolve correctly against the REAL
 * seeded matrix, including the load-bearing case: a seeded {@code Administrator}
 * caller may grant every role (grantor id 1 → all 11 grantee ids). This is the
 * defense-in-depth guarantee that the {@code DirectUserService} guard never
 * rejects a real Administrator provisioning a user.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class GrantableRoleRepositoryIT {

    @Autowired
    GrantableRoleRepository grantableRoleRepository;

    @Test
    @DisplayName("Administrator role name resolves to grantor id 1 (aligned across V15/V16/V17 seeds)")
    void findRoleIdsByNames_administrator_resolvesToGrantorIdOne() {
        List<Long> ids = grantableRoleRepository.findRoleIdsByNames(List.of("Administrator"));

        // V16 DELETEs the V15 dev role rows (ids 1001..) and re-seeds Administrator=1;
        // resolving by NAME therefore always lands on the grantor row that owns the
        // grantable matrix, regardless of the transient V15 id space.
        assertThat(ids).containsExactly(1L);
    }

    @Test
    @DisplayName("Administrator (grantor 1) may grant all 11 seeded roles")
    void findGranteeRoleIds_administratorGrantsEveryRole() {
        List<Long> grantorRoleIds = grantableRoleRepository.findRoleIdsByNames(List.of("Administrator"));
        List<Long> granteeIds = grantableRoleRepository.findGranteeRoleIds(grantorRoleIds);

        assertThat(granteeIds)
                .containsExactlyInAnyOrder(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L);
    }

    @Test
    @DisplayName("SalesRep (grantor 9) has no grantable entry — grants nothing")
    void findGranteeRoleIds_salesRepGrantsNothing() {
        // SalesRep=9 in V16; it has no grantable_roles row, so a SalesRep caller's
        // requested roleIds can never self-authorize.
        List<Long> granteeIds = grantableRoleRepository.findGranteeRoleIds(List.of(9L));

        assertThat(granteeIds).isEmpty();
    }

    @Test
    @DisplayName("Anonymous (grantor 3) may grant only User (2) per the seeded matrix")
    void findGranteeRoleIds_anonymousGrantsOnlyUser() {
        List<Long> granteeIds = grantableRoleRepository.findGranteeRoleIds(List.of(3L));

        assertThat(granteeIds).containsExactly(2L);
    }
}
