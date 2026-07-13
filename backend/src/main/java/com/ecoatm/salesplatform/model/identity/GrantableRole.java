package com.ecoatm.salesplatform.model.identity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;

/**
 * Maps {@code identity.grantable_roles} — the {@code (grantor_role_id,
 * grantee_role_id)} matrix that defines which roles a caller's OWN roles are
 * permitted to assign (Mendix {@code system$grantableroles}).
 *
 * <p>Read-only JPA anchor for {@link
 * com.ecoatm.salesplatform.repository.GrantableRoleRepository}; the
 * grant-authorization queries are native reads and never mutate this table.
 */
@Entity
@Table(name = "grantable_roles", schema = "identity")
@IdClass(GrantableRole.GrantableRoleId.class)
public class GrantableRole {

    @Id
    @Column(name = "grantor_role_id")
    private Long grantorRoleId;

    @Id
    @Column(name = "grantee_role_id")
    private Long granteeRoleId;

    protected GrantableRole() {
    }

    public Long getGrantorRoleId() {
        return grantorRoleId;
    }

    public Long getGranteeRoleId() {
        return granteeRoleId;
    }

    /** Composite primary key for {@link GrantableRole}. */
    public static class GrantableRoleId implements Serializable {

        private Long grantorRoleId;
        private Long granteeRoleId;

        public GrantableRoleId() {
        }

        public GrantableRoleId(Long grantorRoleId, Long granteeRoleId) {
            this.grantorRoleId = grantorRoleId;
            this.granteeRoleId = granteeRoleId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof GrantableRoleId that)) {
                return false;
            }
            return Objects.equals(grantorRoleId, that.grantorRoleId)
                    && Objects.equals(granteeRoleId, that.granteeRoleId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(grantorRoleId, granteeRoleId);
        }
    }
}
