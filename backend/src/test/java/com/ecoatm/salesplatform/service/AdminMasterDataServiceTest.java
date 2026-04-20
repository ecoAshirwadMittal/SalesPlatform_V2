package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.MasterDataItemDto;
import com.ecoatm.salesplatform.dto.MasterDataItemRequest;
import com.ecoatm.salesplatform.model.mdm.*;
import com.ecoatm.salesplatform.repository.mdm.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminMasterDataServiceTest {

    @Mock private BrandRepository brandRepository;
    @Mock private ColorRepository colorRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private CapacityRepository capacityRepository;
    @Mock private CarrierRepository carrierRepository;
    @Mock private GradeRepository gradeRepository;
    @Mock private ModelRepository modelRepository;

    private AdminMasterDataService service;

    @BeforeEach
    void setUp() {
        service = new AdminMasterDataService(
                brandRepository, colorRepository, categoryRepository,
                capacityRepository, carrierRepository, gradeRepository, modelRepository);
    }

    private Brand makeBrand(Long id, String name, boolean enabled) {
        Brand b = new Brand();
        b.setId(id);
        b.setName(name);
        b.setDisplayName(name);
        b.setIsEnabled(enabled);
        b.setSortRank(1);
        return b;
    }

    @Test
    @DisplayName("list returns mapped page for known type")
    void list_brands_returnsPage() {
        Brand b = makeBrand(1L, "Apple", true);
        when(brandRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(b)));

        Page<MasterDataItemDto> result = service.list("brands", 0, 50);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Apple");
    }

    @Test
    @DisplayName("list throws for unknown type")
    void list_unknownType_throws() {
        assertThatThrownBy(() -> service.list("widgets", 0, 50))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown master data type");
    }

    @Test
    @DisplayName("create persists entity and returns DTO with defaults")
    void create_brand_persistsEntity() {
        when(brandRepository.save(any(Brand.class))).thenAnswer(inv -> {
            Brand arg = inv.getArgument(0);
            arg.setId(99L);
            return arg;
        });

        MasterDataItemDto result = service.create("brands",
                new MasterDataItemRequest("Samsung", "Samsung Inc", null, 5));

        assertThat(result.id()).isEqualTo(99L);
        assertThat(result.name()).isEqualTo("Samsung");
        assertThat(result.displayName()).isEqualTo("Samsung Inc");
        assertThat(result.isEnabled()).isTrue();
        assertThat(result.sortRank()).isEqualTo(5);
        verify(brandRepository).save(any(Brand.class));
    }

    @Test
    @DisplayName("create rejects blank name")
    void create_blankName_throws() {
        assertThatThrownBy(() -> service.create("brands",
                new MasterDataItemRequest("  ", null, null, null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name is required");
        verify(brandRepository, never()).save(any());
    }

    @Test
    @DisplayName("update modifies existing entity")
    void update_existingBrand_updatesFields() {
        Brand existing = makeBrand(5L, "OldName", true);
        when(brandRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(brandRepository.save(any(Brand.class))).thenAnswer(inv -> inv.getArgument(0));

        MasterDataItemDto result = service.update("brands", 5L,
                new MasterDataItemRequest("NewName", "New Display", false, 10));

        assertThat(result.name()).isEqualTo("NewName");
        assertThat(result.displayName()).isEqualTo("New Display");
        assertThat(result.isEnabled()).isFalse();
        assertThat(result.sortRank()).isEqualTo(10);
    }

    @Test
    @DisplayName("update throws when entity not found")
    void update_missingBrand_throws() {
        when(brandRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update("brands", 999L,
                new MasterDataItemRequest("X", null, null, null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("brands not found");
    }

    @Test
    @DisplayName("softDelete flips isEnabled to false")
    void softDelete_enabledBrand_disables() {
        Brand existing = makeBrand(3L, "Foo", true);
        when(brandRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(brandRepository.save(any(Brand.class))).thenAnswer(inv -> inv.getArgument(0));

        MasterDataItemDto result = service.softDelete("brands", 3L);

        assertThat(result.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("softDelete rejects already-disabled entity")
    void softDelete_alreadyDisabled_throws() {
        Brand existing = makeBrand(4L, "Bar", false);
        when(brandRepository.findById(4L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.softDelete("brands", 4L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already disabled");
        verify(brandRepository, never()).save(any());
    }

    @Test
    @DisplayName("supportedTypes exposes all seven lookup tables")
    void supportedTypes_containsAllSeven() {
        assertThat(service.supportedTypes())
                .containsExactlyInAnyOrder("brands", "colors", "categories",
                        "capacities", "carriers", "grades", "models");
    }
}
