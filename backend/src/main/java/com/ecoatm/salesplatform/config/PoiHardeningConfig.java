package com.ecoatm.salesplatform.config;

import jakarta.annotation.PostConstruct;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Global Apache POI hardening against decompression ("zip") bombs.
 *
 * <p>A crafted xlsx/docx is a zip that can expand to gigabytes in memory from a
 * few KB on disk. These limits apply process-wide to every POI reader — PO /
 * reserve-bid / bid-import upload parsers, the partial-credit file-drop parser,
 * and the xlsx exporters — so no single upload endpoint can exhaust the heap
 * (security review 2026-07-10, H-9). Tunable via config for large legitimate files.
 */
@Configuration
public class PoiHardeningConfig {

    /** Reject a zip entry whose uncompressed:compressed ratio exceeds 1 / this. */
    @Value("${poi.min-inflate-ratio:0.01}")
    private double minInflateRatio;

    /** Cap any single in-memory byte[] POI allocates while reading a document. */
    @Value("${poi.max-byte-array-bytes:104857600}") // 100 MB
    private int maxByteArrayBytes;

    @PostConstruct
    void hardenPoi() {
        ZipSecureFile.setMinInflateRatio(minInflateRatio);
        IOUtils.setByteArrayMaxOverride(maxByteArrayBytes);
    }
}
