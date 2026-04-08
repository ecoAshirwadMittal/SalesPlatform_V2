# Microflow Analysis: ACT_Order_ReSubmitToOracle

### Requirements (Inputs):
- **$Order** (A record of type: EcoATM_PWS.Order)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Retrieve
      - Store the result in a new variable called **$Offer****
3. **Run another process: "EcoATM_PWS.SUB_Order_SendOrderToOracle"
      - Store the result in a new variable called **$CreateOrderResponse****
4. **Run another process: "EcoATM_PWS.SUB_CreateOrderResponse_ManageResult"**
5. **Create Object
      - Store the result in a new variable called **$UserMessage****
6. **Show Page**
7. **Run another process: "Custom_Logging.SUB_Log_Info"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
