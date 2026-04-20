package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.BuyerCodeDetail;
import com.ecoatm.salesplatform.dto.BuyerCodeUpsertRequest;
import com.ecoatm.salesplatform.dto.BuyerDetailResponse;
import com.ecoatm.salesplatform.dto.BuyerPermissions;
import com.ecoatm.salesplatform.dto.BuyerUpsertRequest;
import com.ecoatm.salesplatform.dto.SalesRepSummary;
import com.ecoatm.salesplatform.model.buyermgmt.Buyer;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerStatus;
import com.ecoatm.salesplatform.model.buyermgmt.SalesRepresentative;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.BuyerRepository;
import com.ecoatm.salesplatform.repository.SalesRepresentativeRepository;
import com.ecoatm.salesplatform.service.snowflake.BuyerSnowflakeEvent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BuyerEditService {

    private final BuyerRepository buyerRepository;
    private final BuyerCodeRepository buyerCodeRepository;
    private final SalesRepresentativeRepository salesRepRepository;
    private final EntityManager em;
    private final ApplicationEventPublisher eventPublisher;

    public BuyerEditService(BuyerRepository buyerRepository,
                            BuyerCodeRepository buyerCodeRepository,
                            SalesRepresentativeRepository salesRepRepository,
                            EntityManager em,
                            ApplicationEventPublisher eventPublisher) {
        this.buyerRepository = buyerRepository;
        this.buyerCodeRepository = buyerCodeRepository;
        this.salesRepRepository = salesRepRepository;
        this.em = em;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public BuyerDetailResponse get(Long id, Authentication auth) {
        Buyer buyer = buyerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Buyer not found: id=" + id));

        List<SalesRepresentative> salesReps = salesRepRepository.findByBuyerId(id);
        List<BuyerCode> buyerCodes = buyerCodeRepository.findByBuyerId(id);

        BuyerPermissions permissions = isAdmin(auth)
                ? BuyerPermissions.forAdmin()
                : BuyerPermissions.forCompliance();

        return toDetailResponse(buyer, salesReps, buyerCodes, permissions);
    }

    @Transactional
    public BuyerDetailResponse create(BuyerUpsertRequest req, Authentication auth) {
        LocalDateTime now = LocalDateTime.now();

        Buyer buyer = new Buyer();
        buyer.setCompanyName(req.companyName().trim());
        buyer.setStatus(BuyerStatus.Active);
        buyer.setSpecialBuyer(req.isSpecialBuyer());
        buyer.setCreatedDate(now);
        buyer.setChangedDate(now);
        Buyer saved = buyerRepository.save(buyer);

        if (req.salesRepIds() != null) {
            updateSalesReps(saved.getId(), req.salesRepIds());
        }

        if (req.buyerCodes() != null) {
            for (BuyerCodeUpsertRequest codeReq : req.buyerCodes()) {
                BuyerCode bc = new BuyerCode();
                bc.setCode(codeReq.code().trim());
                bc.setBuyerCodeType(codeReq.buyerCodeType());
                bc.setBudget(codeReq.budget());
                bc.setStatus("Active");
                bc.setSoftDelete(false);
                bc.setCreatedDate(now);
                bc.setChangedDate(now);
                BuyerCode savedCode = buyerCodeRepository.save(bc);

                em.createNativeQuery("""
                    INSERT INTO buyer_mgmt.buyer_code_buyers (buyer_code_id, buyer_id) VALUES (:codeId, :buyerId)
                """)
                        .setParameter("codeId", savedCode.getId())
                        .setParameter("buyerId", saved.getId())
                        .executeUpdate();
            }
        }

        List<SalesRepresentative> salesReps = salesRepRepository.findByBuyerId(saved.getId());
        List<BuyerCode> buyerCodes = buyerCodeRepository.findByBuyerId(saved.getId());

        eventPublisher.publishEvent(new BuyerSnowflakeEvent.BuyerSaved(saved.getId()));

        return toDetailResponse(saved, salesReps, buyerCodes, BuyerPermissions.forAdmin());
    }

    @Transactional
    public BuyerDetailResponse toggleStatus(Long id, Authentication auth) {
        Buyer buyer = buyerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Buyer not found: id=" + id));

        BuyerStatus newStatus = buyer.getStatus() == BuyerStatus.Active
                ? BuyerStatus.Disabled
                : BuyerStatus.Active;

        if (newStatus == BuyerStatus.Disabled) {
            validateDisable(id);
        }

        buyer.setStatus(newStatus);
        buyer.setChangedDate(LocalDateTime.now());
        buyerRepository.save(buyer);

        List<SalesRepresentative> salesReps = salesRepRepository.findByBuyerId(id);
        List<BuyerCode> buyerCodes = buyerCodeRepository.findByBuyerId(id);
        BuyerPermissions permissions = isAdmin(auth)
                ? BuyerPermissions.forAdmin()
                : BuyerPermissions.forCompliance();

        eventPublisher.publishEvent(new BuyerSnowflakeEvent.BuyerSaved(id));

        return toDetailResponse(buyer, salesReps, buyerCodes, permissions);
    }

    @Transactional
    public BuyerDetailResponse update(Long id, BuyerUpsertRequest req, Authentication auth) {
        Buyer buyer = buyerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Buyer not found: id=" + id));

        boolean admin = isAdmin(auth);
        LocalDateTime now = LocalDateTime.now();

        buyer.setCompanyName(req.companyName().trim());
        buyer.setSpecialBuyer(req.isSpecialBuyer());
        buyer.setChangedDate(now);

        if (admin && req.status() != null) {
            if (req.status() == BuyerStatus.Disabled && buyer.getStatus() != BuyerStatus.Disabled) {
                validateDisable(id);
            }
            buyer.setStatus(req.status());
        }

        buyerRepository.save(buyer);

        if (admin && req.salesRepIds() != null) {
            updateSalesReps(id, req.salesRepIds());
        }

        if (req.buyerCodes() != null) {
            updateBuyerCodes(id, req.buyerCodes(), admin, now);
        }

        List<SalesRepresentative> salesReps = salesRepRepository.findByBuyerId(id);
        List<BuyerCode> buyerCodes = buyerCodeRepository.findByBuyerId(id);
        BuyerPermissions permissions = admin
                ? BuyerPermissions.forAdmin()
                : BuyerPermissions.forCompliance();

        eventPublisher.publishEvent(new BuyerSnowflakeEvent.BuyerSaved(id));

        return toDetailResponse(buyer, salesReps, buyerCodes, permissions);
    }

    private void validateDisable(Long buyerId) {
        Long activeUserCount = (Long) em.createNativeQuery("""
            SELECT COUNT(*)
            FROM user_mgmt.user_buyers ub
            JOIN user_mgmt.ecoatm_direct_users edu ON edu.user_id = ub.user_id
            WHERE ub.buyer_id = :buyerId
              AND edu.overall_user_status = 'Active'
        """).setParameter("buyerId", buyerId).getSingleResult();

        if (activeUserCount > 0) {
            throw new BuyerDisableException(
                    "Cannot disable buyer: " + activeUserCount +
                    " active user(s) are still assigned. Remove user assignments before disabling.");
        }
    }

    private void updateSalesReps(Long buyerId, List<Long> salesRepIds) {
        em.createNativeQuery("DELETE FROM buyer_mgmt.buyer_sales_reps WHERE buyer_id = :buyerId")
                .setParameter("buyerId", buyerId)
                .executeUpdate();

        for (Long repId : salesRepIds) {
            em.createNativeQuery("""
                INSERT INTO buyer_mgmt.buyer_sales_reps (buyer_id, sales_rep_id) VALUES (:buyerId, :repId)
            """)
                    .setParameter("buyerId", buyerId)
                    .setParameter("repId", repId)
                    .executeUpdate();
        }
    }

    private void updateBuyerCodes(Long buyerId, List<BuyerCodeUpsertRequest> codeRequests,
                                   boolean admin, LocalDateTime now) {
        List<BuyerCode> existing = buyerCodeRepository.findByBuyerId(buyerId);
        Map<Long, BuyerCode> existingById = existing.stream()
                .filter(bc -> bc.getId() != null)
                .collect(Collectors.toMap(BuyerCode::getId, Function.identity()));

        Set<Long> processedIds = new HashSet<>();

        for (BuyerCodeUpsertRequest codeReq : codeRequests) {
            if (codeReq.id() != null && existingById.containsKey(codeReq.id())) {
                BuyerCode bc = existingById.get(codeReq.id());
                processedIds.add(bc.getId());

                bc.setCode(codeReq.code().trim());
                bc.setBudget(codeReq.budget());
                bc.setSoftDelete(codeReq.softDelete());
                bc.setChangedDate(now);

                if (admin && codeReq.buyerCodeType() != null) {
                    bc.setBuyerCodeType(codeReq.buyerCodeType());
                }

                buyerCodeRepository.save(bc);
            } else if (codeReq.id() == null) {
                BuyerCode bc = new BuyerCode();
                bc.setCode(codeReq.code().trim());
                bc.setBuyerCodeType(codeReq.buyerCodeType());
                bc.setBudget(codeReq.budget());
                bc.setStatus("Active");
                bc.setSoftDelete(codeReq.softDelete());
                bc.setCreatedDate(now);
                bc.setChangedDate(now);
                BuyerCode saved = buyerCodeRepository.save(bc);

                em.createNativeQuery("""
                    INSERT INTO buyer_mgmt.buyer_code_buyers (buyer_code_id, buyer_id) VALUES (:codeId, :buyerId)
                """)
                        .setParameter("codeId", saved.getId())
                        .setParameter("buyerId", buyerId)
                        .executeUpdate();
            }
        }

        for (BuyerCode bc : existing) {
            if (!processedIds.contains(bc.getId())) {
                bc.setSoftDelete(true);
                bc.setChangedDate(now);
                buyerCodeRepository.save(bc);
            }
        }
    }

    private BuyerDetailResponse toDetailResponse(Buyer buyer,
                                                   List<SalesRepresentative> salesReps,
                                                   List<BuyerCode> buyerCodes,
                                                   BuyerPermissions permissions) {
        BuyerStatus status = buyer.getStatus() == null ? BuyerStatus.Disabled : buyer.getStatus();

        List<SalesRepSummary> repSummaries = salesReps.stream()
                .map(sr -> new SalesRepSummary(sr.getId(), sr.getFirstName(), sr.getLastName()))
                .toList();

        List<BuyerCodeDetail> codeDetails = buyerCodes.stream()
                .map(bc -> new BuyerCodeDetail(
                        bc.getId(),
                        bc.getCode(),
                        bc.getBuyerCodeType(),
                        bc.getBudget(),
                        bc.isSoftDelete(),
                        !buyerCodeRepository.existsByCodeIgnoreCaseAndNotSoftDeleted(
                                bc.getCode(), bc.getId())
                ))
                .toList();

        return new BuyerDetailResponse(
                buyer.getId(),
                buyer.getCompanyName(),
                status,
                buyer.isSpecialBuyer(),
                repSummaries,
                codeDetails,
                permissions
        );
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_Administrator"));
    }

    public static class BuyerDisableException extends RuntimeException {
        public BuyerDisableException(String message) {
            super(message);
        }
    }
}
