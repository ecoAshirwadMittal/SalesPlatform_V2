package com.ecoatm.salesplatform.repository.email;

import com.ecoatm.salesplatform.model.email.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The bean name is pinned explicitly because
 * {@link com.ecoatm.salesplatform.repository.partialcredit.EmailTemplateRepository}
 * already exists with the identical simple name — Spring Data derives the
 * default repository bean name from the unqualified interface name, so two
 * same-named interfaces in different packages collide
 * ({@code BeanDefinitionOverrideException}) unless one is disambiguated.
 * Callers still {@code @Autowired} by type ({@code EmailTemplateRepository}
 * imported from this package), so this has no effect on wiring.
 */
@Repository("emailManagementTemplateRepository")
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {

    Optional<EmailTemplate> findByTemplateKey(String templateKey);
}
