package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.SalesRepCreateRequest;
import com.ecoatm.salesplatform.dto.SalesRepResponse;
import com.ecoatm.salesplatform.dto.SalesRepUpdateRequest;
import com.ecoatm.salesplatform.exception.DuplicateSalesRepException;
import com.ecoatm.salesplatform.exception.SalesRepHasOffersException;
import com.ecoatm.salesplatform.model.buyermgmt.SalesRepresentative;
import com.ecoatm.salesplatform.repository.SalesRepresentativeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Write CRUD for internal sales representatives (gap-analysis 2.4, sub-feature
 * 1). Ports the legacy Mendix {@code Act_SaveSaleRep} (trim + case-insensitive
 * duplicate-name guard) and {@code ACT_DeleteSalesRep} (offer-reference guard)
 * microflows.
 *
 * <p>Identity (owner on create, changer on update) is passed in as a
 * JWT-derived caller id — never taken from a request field. No live Snowflake
 * push: the app is moving to a scheduled batch sync (separate effort).
 */
@Service
public class SalesRepService {

    private final SalesRepresentativeRepository repository;

    public SalesRepService(SalesRepresentativeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<SalesRepResponse> list() {
        return repository.findAllByOrderByFirstNameAscLastNameAsc().stream()
                .map(SalesRepResponse::from)
                .toList();
    }

    @Transactional
    public SalesRepResponse create(SalesRepCreateRequest req, Long callerUserId) {
        String firstName = req.firstName().trim();
        String lastName = req.lastName().trim();

        if (repository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName)) {
            throw new DuplicateSalesRepException(firstName, lastName);
        }

        LocalDateTime now = LocalDateTime.now();
        SalesRepresentative rep = new SalesRepresentative();
        rep.setId(repository.nextId());
        rep.setFirstName(firstName);
        rep.setLastName(lastName);
        rep.setActive(req.active() == null || req.active());
        rep.setOwnerId(callerUserId);
        rep.setChangedById(callerUserId);
        rep.setCreatedDate(now);
        rep.setChangedDate(now);

        return SalesRepResponse.from(repository.save(rep));
    }

    @Transactional
    public SalesRepResponse update(Long id, SalesRepUpdateRequest req, Long callerUserId) {
        SalesRepresentative rep = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sales representative not found: id=" + id));

        String firstName = req.firstName().trim();
        String lastName = req.lastName().trim();

        if (repository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndIdNot(firstName, lastName, id)) {
            throw new DuplicateSalesRepException(firstName, lastName);
        }

        rep.setFirstName(firstName);
        rep.setLastName(lastName);
        rep.setActive(req.active() == null || req.active());
        rep.setChangedById(callerUserId);
        rep.setChangedDate(LocalDateTime.now());

        return SalesRepResponse.from(repository.save(rep));
    }

    @Transactional
    public void delete(Long id) {
        SalesRepresentative rep = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sales representative not found: id=" + id));

        if (repository.countOffersReferencing(id) > 0) {
            throw new SalesRepHasOffersException();
        }

        repository.delete(rep);
    }
}
