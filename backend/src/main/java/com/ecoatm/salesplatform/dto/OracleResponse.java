package com.ecoatm.salesplatform.dto;

/**
 * Represents the response from Oracle ERP order creation.
 * Maps to Mendix EcoATM_PWSIntegration.OracleResponse entity:
 *   ReturnCode, ReturnMessage, OrderId, OrderNumber, HTTPCode, JSONResponse
 *
 * When Oracle integration is implemented, this will be populated from the actual HTTP response.
 * For now, simulateSuccess() provides a local stand-in so the full flow can execute end-to-end.
 */
public class OracleResponse {

    private String returnCode;
    private String returnMessage;
    private String orderId;
    private String orderNumber;
    private Integer httpCode;
    private String jsonResponse;

    /**
     * Simulate a successful Oracle response for local development.
     * Generates a pseudo order number using current timestamp.
     * Replace with actual Oracle REST API call in production.
     */
    public static OracleResponse simulateSuccess() {
        OracleResponse r = new OracleResponse();
        r.returnCode = "00";
        r.returnMessage = "Order Created Successfully";
        r.orderNumber = "ORD-" + System.currentTimeMillis() % 1000000;
        r.orderId = String.valueOf(System.currentTimeMillis() % 100000);
        r.httpCode = 200;
        r.jsonResponse = "{\"status\":\"simulated\"}";
        return r;
    }

    // Getters and Setters
    public String getReturnCode() { return returnCode; }
    public void setReturnCode(String returnCode) { this.returnCode = returnCode; }

    public String getReturnMessage() { return returnMessage; }
    public void setReturnMessage(String returnMessage) { this.returnMessage = returnMessage; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public Integer getHttpCode() { return httpCode; }
    public void setHttpCode(Integer httpCode) { this.httpCode = httpCode; }

    public String getJsonResponse() { return jsonResponse; }
    public void setJsonResponse(String jsonResponse) { this.jsonResponse = jsonResponse; }
}
