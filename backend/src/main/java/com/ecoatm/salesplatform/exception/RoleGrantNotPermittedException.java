package com.ecoatm.salesplatform.exception;

/**
 * Thrown when a user-provisioning caller attempts to assign a role that none of
 * the caller's OWN roles is permitted to grant (per {@code
 * identity.grantable_roles}). Maps to HTTP 403 in {@link GlobalExceptionHandler}.
 *
 * <p>The message is deliberately generic and never names which role was
 * disallowed — enumerating it would hand a caller a role-probing oracle
 * (repo Security Rule: generic rejection, no role enumeration).
 */
public class RoleGrantNotPermittedException extends RuntimeException {

    public RoleGrantNotPermittedException() {
        super("Not permitted to grant one or more of the requested roles");
    }
}
