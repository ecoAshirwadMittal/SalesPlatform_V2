# Microflow Analysis: ACT_RMADetails_CompleteReview

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Run another process: "EcoATM_RMA.SUB_CalculateApprovedRMAValues"
      - Store the result in a new variable called **$Status****
5. **Retrieve
      - Store the result in a new variable called **$RMAItemList****
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Permanently save **$undefined** to the database.**
8. **Take the list **$RMAItemList**, perform a [Filter] where: { EcoATM_RMA.ENUM_RMAItemStatus.Approve }, and call the result **$RMAItemList_Approved****
9. **Aggregate List
      - Store the result in a new variable called **$Count_Total****
10. **Aggregate List
      - Store the result in a new variable called **$Count_Approved****
11. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMA.ApprovedCount] to: "$Count_Approved"
      - Change [EcoATM_RMA.RMA.DeclinedCount] to: "$Count_Total-$Count_Approved"**
12. **Search the Database for **EcoATM_UserManagement.EcoATMDirectUser** using filter: { [id = $currentUser] } (Call this list **$SalesPerson**)**
13. **Decision:** "status?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **declined?**
14. **Run another process: "EcoATM_RMA.SUB_RMA_PrepareContentAndSendToOracle"
      - Store the result in a new variable called **$CreateOrderResponse****
15. **Decision:** "Response exist?"
   - If [true] -> Move to: **Is success?**
   - If [false] -> Move to: **Activity**
16. **Decision:** "Is success?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
17. **Search the Database for **EcoATM_RMA.RMAStatus** using filter: { [
  (
    SystemStatus = 'Approved'
  )
] } (Call this list **$RMAStatus_Approved**)**
18. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMA.ApprovalDate] to: "[%CurrentDateTime%]"
      - Change [EcoATM_RMA.RMA_EcoATMDirectUser_ReviewedBy] to: "$SalesPerson"
      - Change [EcoATM_RMA.RMA_RMAStatus] to: "$RMAStatus_Approved"
      - Change [EcoATM_RMA.RMA.OracleRMAStatus] to: "$CreateOrderResponse/ReturnMessage"
      - Change [EcoATM_RMA.RMA.OracleJSONResponse] to: "$CreateOrderResponse/JSONResponse"
      - Change [EcoATM_RMA.RMA.OracleHTTPCode] to: "$CreateOrderResponse/HTTPCode"
      - Change [EcoATM_RMA.RMA.OracleNumber] to: "$CreateOrderResponse/OrderNumber"
      - Change [EcoATM_RMA.RMA.OracleId] to: "$CreateOrderResponse/OrderId"
      - Change [EcoATM_RMA.RMA.SystemStatus] to: "$RMAStatus_Approved/SystemStatus"
      - **Save:** This change will be saved to the database immediately.**
19. **Create Object
      - Store the result in a new variable called **$UserMessage_Submit****
20. **Show Page**
21. **Show Page**
22. **Run another process: "EcoATM_RMA.SUB_SendEmail_RMAApproved"**
23. **Run another process: "EcoATM_RMA.SUB_SendRMADetailsToSnowflake"**
24. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
25. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
