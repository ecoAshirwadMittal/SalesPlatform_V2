package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.MasterDataItemDto;
import com.ecoatm.salesplatform.dto.MasterDataItemRequest;
import com.ecoatm.salesplatform.model.mdm.*;
import com.ecoatm.salesplatform.repository.mdm.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Generic CRUD facade over the seven MDM BaseLookup tables
 * (brand, color, category, capacity, carrier, grade, model) for the
 * PWS Data Center admin Master Data screen. Soft-delete flips
 * is_enabled to false rather than removing rows — devices reference
 * these by FK and hard deletes would break historical data.
 */
@Service
public class AdminMasterDataService {

    private final Map<String, LookupHandler<? extends BaseLookup>> handlers;

    public AdminMasterDataService(BrandRepository brand,
                                  ColorRepository color,
                                  CategoryRepository category,
                                  CapacityRepository capacity,
                                  CarrierRepository carrier,
                                  GradeRepository grade,
                                  ModelRepository model) {
        this.handlers = Map.of(
                "brands",     new LookupHandler<>(brand, Brand::new),
                "colors",     new LookupHandler<>(color, Color::new),
                "categories", new LookupHandler<>(category, Category::new),
                "capacities", new LookupHandler<>(capacity, Capacity::new),
                "carriers",   new LookupHandler<>(carrier, Carrier::new),
                "grades",     new LookupHandler<>(grade, Grade::new),
                "models",     new LookupHandler<>(model, Model::new)
        );
    }

    public Page<MasterDataItemDto> list(String type, int page, int size) {
        LookupHandler<? extends BaseLookup> handler = requireHandler(type);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return handler.repository.findAll(pageable).map(MasterDataItemDto::from);
    }

    @Transactional
    public MasterDataItemDto create(String type, MasterDataItemRequest request) {
        validateName(request.name());
        return createInternal(requireHandler(type), request);
    }

    private <T extends BaseLookup> MasterDataItemDto createInternal(LookupHandler<T> handler, MasterDataItemRequest request) {
        T entity = handler.factory.get();
        applyUpdates(entity, request, true);
        T saved = handler.repository.save(entity);
        return MasterDataItemDto.from(saved);
    }

    @Transactional
    public MasterDataItemDto update(String type, Long id, MasterDataItemRequest request) {
        validateName(request.name());
        LookupHandler<? extends BaseLookup> handler = requireHandler(type);
        BaseLookup entity = handler.repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(type + " not found: " + id));
        applyUpdates(entity, request, false);
        return MasterDataItemDto.from(saveCast(handler, entity));
    }

    @Transactional
    public MasterDataItemDto softDelete(String type, Long id) {
        LookupHandler<? extends BaseLookup> handler = requireHandler(type);
        BaseLookup entity = handler.repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(type + " not found: " + id));
        if (Boolean.FALSE.equals(entity.getIsEnabled())) {
            throw new IllegalStateException(type + " " + id + " is already disabled");
        }
        entity.setIsEnabled(false);
        return MasterDataItemDto.from(saveCast(handler, entity));
    }

    public java.util.Set<String> supportedTypes() {
        return handlers.keySet();
    }

    private LookupHandler<? extends BaseLookup> requireHandler(String type) {
        LookupHandler<? extends BaseLookup> handler = handlers.get(type);
        if (handler == null) {
            throw new IllegalArgumentException(
                    "Unknown master data type: " + type + ". Supported: " + handlers.keySet());
        }
        return handler;
    }

    private void applyUpdates(BaseLookup entity, MasterDataItemRequest request, boolean isCreate) {
        entity.setName(request.name());
        entity.setDisplayName(request.displayName());
        if (request.sortRank() != null) entity.setSortRank(request.sortRank());
        if (request.isEnabled() != null) {
            entity.setIsEnabled(request.isEnabled());
        } else if (isCreate) {
            entity.setIsEnabled(true);
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private BaseLookup saveCast(LookupHandler<? extends BaseLookup> handler, BaseLookup entity) {
        JpaRepository repo = handler.repository;
        return (BaseLookup) repo.save(entity);
    }

    private static final class LookupHandler<T extends BaseLookup> {
        final JpaRepository<T, Long> repository;
        final Supplier<T> factory;

        LookupHandler(JpaRepository<T, Long> repository, Supplier<T> factory) {
            this.repository = repository;
            this.factory = factory;
        }
    }
}
