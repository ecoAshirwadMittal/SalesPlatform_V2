package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.dto.PODetailListResponse;
import com.ecoatm.salesplatform.dto.PODetailUploadResult;
import com.ecoatm.salesplatform.dto.PurchaseOrderListResponse;
import com.ecoatm.salesplatform.dto.PurchaseOrderRequest;
import com.ecoatm.salesplatform.dto.PurchaseOrderRow;
import com.ecoatm.salesplatform.service.auctions.purchaseorder.PODetailService;
import com.ecoatm.salesplatform.service.auctions.purchaseorder.POExcelBuilder;
import com.ecoatm.salesplatform.service.auctions.purchaseorder.PurchaseOrderException;
import com.ecoatm.salesplatform.service.auctions.purchaseorder.PurchaseOrderService;
import com.ecoatm.salesplatform.service.auctions.purchaseorder.PurchaseOrderValidationException;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Admin REST surface for the Purchase Order module (sub-project 4B).
 *
 * <p>Class-level {@code @PreAuthorize} gates all 8 endpoints to
 * {@code Administrator} or {@code SalesOps} per design §3 #16. The matching
 * URL pattern in {@code SecurityConfig} is the load-bearing fence; this
 * annotation is defense-in-depth against the matcher being rearranged.
 *
 * <p>The {@link PurchaseOrderException} handler maps the small set of domain
 * codes the service throws ({@code PURCHASE_ORDER_NOT_FOUND},
 * {@code DUPLICATE_PO_DETAIL}, plus validation codes wrapped by
 * {@link PurchaseOrderValidationException}) onto HTTP status codes; everything
 * else falls through to 400 because validation failures are by far the most
 * common non-OK response on this surface.
 */
@RestController
@RequestMapping("/api/v1/admin/purchase-orders")
@PreAuthorize("hasAnyRole('Administrator','SalesOps')")
public class PurchaseOrderController {

    private final PurchaseOrderService poService;
    private final PODetailService detailService;
    private final POExcelBuilder excelBuilder;

    public PurchaseOrderController(PurchaseOrderService poService,
                                   PODetailService detailService,
                                   POExcelBuilder excelBuilder) {
        this.poService = poService;
        this.detailService = detailService;
        this.excelBuilder = excelBuilder;
    }

    @GetMapping
    public PurchaseOrderListResponse list(
            @RequestParam(required = false) Long weekFromId,
            @RequestParam(required = false) Long weekToId,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            Pageable pageable) {
        return poService.list(weekFromId, weekToId, yearFrom, yearTo, pageable);
    }

    @GetMapping("/{id}")
    public PurchaseOrderRow get(@PathVariable long id) {
        return poService.findById(id);
    }

    @PostMapping
    public ResponseEntity<PurchaseOrderRow> create(@Valid @RequestBody PurchaseOrderRequest req) {
        PurchaseOrderRow row = poService.create(req);
        return ResponseEntity.status(201).body(row);
    }

    @PutMapping("/{id}")
    public PurchaseOrderRow update(@PathVariable long id,
                                   @Valid @RequestBody PurchaseOrderRequest req) {
        return poService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        poService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/details")
    public PODetailListResponse listDetails(@PathVariable long id, Pageable pageable) {
        return detailService.list(id, pageable);
    }

    @PostMapping(path = "/{id}/details/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PODetailUploadResult upload(@PathVariable long id,
                                       @RequestParam("file") MultipartFile file) throws IOException {
        return detailService.upload(id, file.getInputStream());
    }

    @GetMapping(path = "/{id}/details/download",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<InputStreamResource> download(@PathVariable long id) {
        var rows = detailService.listAllForExport(id);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        excelBuilder.write(rows, out);
        byte[] bytes = out.toByteArray();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"po-" + id + ".xlsx\"")
                .contentLength(bytes.length)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(new ByteArrayInputStream(bytes)));
    }

    @ExceptionHandler(PurchaseOrderException.class)
    public ResponseEntity<ErrorBody> handleDomain(PurchaseOrderException ex) {
        int status = switch (ex.getCode()) {
            case "PURCHASE_ORDER_NOT_FOUND" -> 404;
            case "DUPLICATE_PO_DETAIL" -> 409;
            default -> 400;
        };
        List<String> details = ex instanceof PurchaseOrderValidationException v
                ? v.getDetails() : List.of();
        return ResponseEntity.status(status)
                .body(new ErrorBody(ex.getCode(), ex.getMessage(), details));
    }

    public record ErrorBody(String code, String message, List<String> details) {}
}
