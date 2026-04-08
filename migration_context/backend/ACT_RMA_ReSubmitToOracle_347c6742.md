# Microflow Analysis: ACT_RMA_ReSubmitToOracle

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Run another process: "EcoATM_RMA.SUB_RMA_SendRMAToOracle"
      - Store the result in a new variable called **$CreateOrderResponse****
5. **Search the Database for **EcoATM_RMA.RMAStatus** using filter: { [
  (
    SystemStatus = 'Open'
  )
] } (Call this list **$RMAStatus_Open**)**
6. **Search the Database for **EcoATM_UserManagement.EcoATMDirectUser** using filter: { [id = $currentUser] } (Call this list **$SalesPerson**)**
7. **Decision:** "Response exist?"
   - If [true] -> Move to: **Is success?**
   - If [false] -> Move to: **Activity**
8. **Decision:** "Is success?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
9. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMA_EcoATMDirectUser_ReviewedBy] to: "$SalesPerson"
      - Change [EcoATM_RMA.RMA.IsSuccessful] to: "false"
      - Change [EcoATM_RMA.RMA.OracleRMAStatus] to: "$CreateOrderResponse/ReturnMessage"
      - Change [EcoATM_RMA.RMA.OracleHTTPCode] to: "$CreateOrderResponse/HTTPCode"
      - Change [EcoATM_RMA.RMA.OracleJSONResponse] to: "$CreateOrderResponse/JSONResponse"
      - **Save:** This change will be saved to the database immediately.**
10. **Run another process: "EcoATM_PWSIntegration.SUB_PWSErrorCode_GetMessage"
      - Store the result in a new variable called **$PWSResponseConfig_1****
11. **Create Object
      - Store the result in a new variable called **$UserMessage_Submit_1_1_1****
12. **Show Page**
13. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
14. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
