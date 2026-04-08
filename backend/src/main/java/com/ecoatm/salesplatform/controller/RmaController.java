package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.service.RmaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pws/rma")
@CrossOrigin(origins = "http://localhost:3000")
public class RmaController {

    private final RmaService rmaService;

    public RmaController(RmaService rmaService) {
        this.rmaService = rmaService;
    }

    /** List RMAs, optionally filtered by buyerCodeId and status group. */
    @GetMapping
    public ResponseEntity<List<RmaResponse>> getRmas(
            @RequestParam(required = false) Long buyerCodeId,
            @RequestParam(required = false) String status) {
        if (buyerCodeId != null) {
            return ResponseEntity.ok(rmaService.getRmasByBuyerCode(buyerCodeId, status));
        }
        return ResponseEntity.ok(rmaService.getAllRmas(status));
    }

    /** Status summary cards (for Sales view). */
    @GetMapping("/summary")
    public ResponseEntity<List<RmaSummaryResponse>> getSummary(
            @RequestParam(required = false) Long buyerCodeId) {
        if (buyerCodeId != null) {
            return ResponseEntity.ok(rmaService.getSummary(buyerCodeId));
        }
        return ResponseEntity.ok(rmaService.getAllSummary());
    }

    /** Get available return reasons for RMA submission. */
    @GetMapping("/reasons")
    public ResponseEntity<List<RmaReasonResponse>> getReasons() {
        return ResponseEntity.ok(rmaService.getReturnReasons());
    }

    /** Download CSV template for RMA submission. */
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        String csv = "IMEI/Serial,Return Reason\n";
        byte[] content = csv.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=RMA_Template.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(content.length)
                .body(content);
    }

    /** Submit an RMA request with a CSV file upload. */
    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RmaSubmitResponse> submitRma(
            @RequestParam Long buyerCodeId,
            @RequestParam Long userId,
            @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(RmaSubmitResponse.failure(List.of("No file uploaded.")));
        }
        RmaSubmitResponse response = rmaService.submitRmaRequest(buyerCodeId, userId, file.getInputStream());
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    /** Get RMA detail with all items. */
    @GetMapping("/{rmaId}")
    public ResponseEntity<RmaDetailResponse> getRmaDetail(@PathVariable Long rmaId) {
        return ResponseEntity.ok(rmaService.getRmaDetail(rmaId));
    }

    /** Approve all items in an RMA. */
    @PutMapping("/{rmaId}/items/approve-all")
    public ResponseEntity<RmaDetailResponse> approveAll(@PathVariable Long rmaId) {
        return ResponseEntity.ok(rmaService.approveAllItems(rmaId));
    }

    /** Decline all items in an RMA. */
    @PutMapping("/{rmaId}/items/decline-all")
    public ResponseEntity<RmaDetailResponse> declineAll(@PathVariable Long rmaId) {
        return ResponseEntity.ok(rmaService.declineAllItems(rmaId));
    }

    /** Update a single item's status (Approve/Decline). */
    @PutMapping("/items/{itemId}/status")
    public ResponseEntity<RmaDetailResponse> updateItemStatus(
            @PathVariable Long itemId,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return ResponseEntity.ok(rmaService.updateItemStatus(itemId, status));
    }

    /** Complete review — finalize the RMA. */
    @PutMapping("/{rmaId}/complete-review")
    public ResponseEntity<RmaDetailResponse> completeReview(
            @PathVariable Long rmaId,
            @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(rmaService.completeReview(rmaId, userId));
    }
}
