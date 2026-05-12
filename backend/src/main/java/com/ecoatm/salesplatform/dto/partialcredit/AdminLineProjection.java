package com.ecoatm.salesplatform.dto.partialcredit;

import com.ecoatm.salesplatform.dto.partialcredit.CreditRequestDetail.EncumberedDeviceLineDto;
import com.ecoatm.salesplatform.dto.partialcredit.CreditRequestDetail.MissingDeviceLineDto;
import com.ecoatm.salesplatform.dto.partialcredit.CreditRequestDetail.WrongDeviceLineDto;
import com.ecoatm.salesplatform.model.partialcredit.EncumberedDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.MissingDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.WrongDeviceLine;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.LineKind;

/**
 * Discriminated-union projection of the three line entity types into a
 * single wire shape. Exactly one of the three {@code *Line} fields is
 * non-null per row — {@code kind} disambiguates so the frontend can
 * pattern-match the response without a type sniffer.
 *
 * <p>Reuses the Sprint-2 buyer-side {@code CreditRequestDetail.*Dto}
 * records so the line shapes stay 1:1 between the buyer detail and the
 * admin response. New admin-only fields are surfaced inside those DTOs
 * already (e.g. {@code WrongDeviceLineDto.latestPrice} +
 * {@code actionRecommendation}).
 */
public record AdminLineProjection(
        LineKind kind,
        MissingDeviceLineDto missingLine,
        WrongDeviceLineDto wrongLine,
        EncumberedDeviceLineDto encumberedLine) {

    public static AdminLineProjection of(MissingDeviceLine line) {
        return new AdminLineProjection(LineKind.MISSING,
                MissingDeviceLineDto.from(line), null, null);
    }

    public static AdminLineProjection of(WrongDeviceLine line) {
        return new AdminLineProjection(LineKind.WRONG,
                null, WrongDeviceLineDto.from(line), null);
    }

    public static AdminLineProjection of(EncumberedDeviceLine line) {
        return new AdminLineProjection(LineKind.ENCUMBERED,
                null, null, EncumberedDeviceLineDto.from(line));
    }

    /**
     * Routes by entity type. Used by the controller to project the
     * untyped {@code Object updatedLine} from
     * {@code LineDecisionResult} without sniffing types in the
     * controller body.
     */
    public static AdminLineProjection fromUntyped(Object line) {
        if (line instanceof MissingDeviceLine m) return of(m);
        if (line instanceof WrongDeviceLine w) return of(w);
        if (line instanceof EncumberedDeviceLine e) return of(e);
        throw new IllegalStateException(
                "Unexpected line type: " + (line == null ? "null" : line.getClass()));
    }
}
