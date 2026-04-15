package com.ecoatm.salesplatform.service;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Shared cross-schema lookups for buyer_mgmt tables.
 * Centralizes native queries that were previously duplicated across
 * RmaService, OfferService, OfferReviewService, and CounterOfferService.
 */
@Service
public class BuyerCodeLookupService {

    private final EntityManager em;

    public BuyerCodeLookupService(EntityManager em) {
        this.em = em;
    }

    /** Buyer code ID → code string (e.g., "BC001"). */
    @Transactional(readOnly = true)
    public String findCodeById(Long buyerCodeId) {
        @SuppressWarnings("unchecked")
        List<String> results = em.createNativeQuery(
                "SELECT bc.code FROM buyer_mgmt.buyer_codes bc WHERE bc.id = :id")
                .setParameter("id", buyerCodeId)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    /** Batch lookup: buyer code IDs → Map of id to code string. */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Map<Long, String> findCodesByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return Map.of();
        List<Object[]> rows = em.createNativeQuery(
                "SELECT bc.id, bc.code FROM buyer_mgmt.buyer_codes bc WHERE bc.id IN :ids")
                .setParameter("ids", ids)
                .getResultList();
        Map<Long, String> map = new HashMap<>();
        for (Object[] row : rows) {
            map.put(((Number) row[0]).longValue(), (String) row[1]);
        }
        return map;
    }

    /** Batch lookup: buyer code IDs → Map of id to [code, companyName]. */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Map<Long, BuyerCodeInfo> findCodeAndCompanyByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return Map.of();
        List<Object[]> rows = em.createNativeQuery(
                "SELECT bc.id, bc.code, b.company_name " +
                "FROM buyer_mgmt.buyer_codes bc " +
                "LEFT JOIN buyer_mgmt.buyer_code_buyers bcb ON bc.id = bcb.buyer_code_id " +
                "LEFT JOIN buyer_mgmt.buyers b ON bcb.buyer_id = b.id " +
                "WHERE bc.id IN :ids")
                .setParameter("ids", ids)
                .getResultList();
        Map<Long, BuyerCodeInfo> map = new HashMap<>();
        for (Object[] row : rows) {
            Long id = ((Number) row[0]).longValue();
            map.put(id, new BuyerCodeInfo((String) row[1], (String) row[2]));
        }
        return map;
    }

    /** Batch lookup: buyer code IDs → Map of id to company name. */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Map<Long, String> findCompanyNamesByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return Map.of();
        List<Object[]> rows = em.createNativeQuery("""
                SELECT bcb.buyer_code_id, b.company_name
                FROM buyer_mgmt.buyer_code_buyers bcb
                JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
                WHERE bcb.buyer_code_id IN :ids
                """)
                .setParameter("ids", ids)
                .getResultList();
        Map<Long, String> map = new HashMap<>();
        for (Object[] row : rows) {
            map.put(((Number) row[0]).longValue(), (String) row[1]);
        }
        return map;
    }

    /** Batch lookup: buyer code IDs → Map of id to sales rep full name. */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Map<Long, String> findSalesRepsByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return Map.of();
        List<Object[]> rows = em.createNativeQuery("""
                SELECT bcb.buyer_code_id, sr.first_name, sr.last_name
                FROM buyer_mgmt.buyer_code_buyers bcb
                JOIN buyer_mgmt.buyer_sales_reps bsr ON bsr.buyer_id = bcb.buyer_id
                JOIN buyer_mgmt.sales_representatives sr ON sr.id = bsr.sales_rep_id
                WHERE bcb.buyer_code_id IN :ids
                """)
                .setParameter("ids", ids)
                .getResultList();
        Map<Long, String> map = new HashMap<>();
        for (Object[] row : rows) {
            String name = (row[1] != null ? row[1] : "") + " " + (row[2] != null ? row[2] : "");
            map.put(((Number) row[0]).longValue(), name.trim());
        }
        return map;
    }

    /** Look up buyer code ID from code string. Returns null if not found. */
    @Transactional(readOnly = true)
    public Long findActiveIdByCode(String code) {
        @SuppressWarnings("unchecked")
        List<Long> ids = em.createNativeQuery(
                "SELECT id FROM buyer_mgmt.buyer_codes WHERE code = :code AND status = 'Active' AND soft_delete = false",
                Long.class)
                .setParameter("code", code)
                .getResultList();
        return ids.isEmpty() ? null : ids.get(0);
    }

    /** Typed return for code + company name lookups. */
    public record BuyerCodeInfo(String code, String companyName) {}
}
