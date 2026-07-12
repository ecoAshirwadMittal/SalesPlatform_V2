package com.ecoatm.salesplatform.service.rma;

/**
 * Result of a Deposco reverse-logistics status poll for a single RMA.
 *
 * <p>Carries the raw order-status string Deposco reports for the RMA — the
 * modern equivalent of the legacy {@code SUB_SyncRMAStatus}
 * {@code $RMAResponse/OrderStatus}. The sync service (not this type) decides
 * whether the reported status means the RMA has been received, so a real HTTP
 * client can drop in and report any status verbatim without baking the
 * "Received" rule into the DTO.
 *
 * @param reportedStatus the RMA status Deposco reports (e.g. {@code "Received"})
 */
public record DeposcoRmaStatus(String reportedStatus) {
}
