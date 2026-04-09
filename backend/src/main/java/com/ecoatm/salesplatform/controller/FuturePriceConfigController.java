package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.model.pws.FuturePriceConfig;
import com.ecoatm.salesplatform.service.FuturePriceConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pws/pricing/config")
public class FuturePriceConfigController {

    private final FuturePriceConfigService configService;

    public FuturePriceConfigController(FuturePriceConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getConfig() {
        FuturePriceConfig config = configService.getConfig();
        return ResponseEntity.ok(Map.of(
                "id", config.getId(),
                "futurePriceDate", config.getFuturePriceDate() != null ? config.getFuturePriceDate().toString() : ""
        ));
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateConfig(@RequestBody Map<String, String> body) {
        String dateStr = body.get("futurePriceDate");
        LocalDateTime date = (dateStr != null && !dateStr.isBlank())
                ? LocalDateTime.parse(dateStr)
                : null;
        FuturePriceConfig config = configService.updateFuturePriceDate(date);
        return ResponseEntity.ok(Map.of(
                "id", config.getId(),
                "futurePriceDate", config.getFuturePriceDate() != null ? config.getFuturePriceDate().toString() : ""
        ));
    }
}
