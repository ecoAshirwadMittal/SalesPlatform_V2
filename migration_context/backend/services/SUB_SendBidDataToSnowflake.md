# Microflow Detailed Specification: SUB_SendBidDataToSnowflake

### 📥 Inputs (Parameters)
- **$BidRound** (Type: AuctionUI.BidRound)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'SendBidDataToSnowflake'`**
2. **Create Variable **$Description** = `'Send BidData to Snowflake for BuyerCode: '+$BidRound/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code+' for Round:'+$BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **Retrieve related **BidData_BidRound** via Association from **$BidRound** (Result: **$BidDataList**)**
5. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/BidAmount>0` (Result: **$BidDataList_PositiveBidAmount**)**
6. **ExportXml**
7. **Retrieve related **BidRound_SchedulingAuction** via Association from **$BidRound** (Result: **$SchedulingAuction**)**
8. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction**)**
9. **DB Retrieve **System.UserRole** Filter: `[System.UserRoles=$currentUser]` (Result: **$UserRole**)**
10. **Create Variable **$isBidderBuyer** = `if $UserRole/Name='Bidder' then true else false`**
11. **Create Variable **$jdbc** = `@EcoATM_PO.Snowflake_DBSource + '&user='+@EcoATM_PO.Snowflake_DBUsername+'&password='+ @EcoATM_PO.Snowflake_DBPassword`**
12. **CreateList**
13. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$JSON_INPUT**)
      - Set **ARG_NAME** = `'JSON_INPUT'`
      - Set **AGR_CONTENT** = `$JSON_BidData`**
14. **Add **$$JSON_INPUT** to/from list **$StoreProcedureArgumentList****
15. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$BUYER_CODE**)
      - Set **ARG_NAME** = `'BUYER_CODE'`
      - Set **AGR_CONTENT** = `$BidRound/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code`**
16. **Add **$$BUYER_CODE** to/from list **$StoreProcedureArgumentList****
17. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$WEEK_NUMBER**)
      - Set **ARG_NAME** = `'WEEK_NUMBER'`
      - Set **AGR_CONTENT** = `toString($Auction/AuctionUI.Auction_Week/EcoATM_MDM.Week/WeekNumber)`**
18. **Add **$$WEEK_NUMBER** to/from list **$StoreProcedureArgumentList****
19. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$YEAR_NUMBER**)
      - Set **ARG_NAME** = `'YEAR_NUMBER'`
      - Set **AGR_CONTENT** = `toString($Auction/AuctionUI.Auction_Week/EcoATM_MDM.Week/Year)`**
20. **Add **$$YEAR_NUMBER** to/from list **$StoreProcedureArgumentList****
21. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$ROUND_NUMBER**)
      - Set **ARG_NAME** = `'ROUND_NUMBER'`
      - Set **AGR_CONTENT** = `toString($SchedulingAuction/Round)`**
22. **Add **$$ROUND_NUMBER** to/from list **$StoreProcedureArgumentList****
23. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$SUBMITTED_BY**)
      - Set **ARG_NAME** = `'SUBMITTED_BY'`
      - Set **AGR_CONTENT** = `$currentUser/Name`**
24. **Add **$$SUBMITTED_BY** to/from list **$StoreProcedureArgumentList****
25. **Create **EcoATM_PWSIntegration.StoreProcedureArgument** (Result: **$IS_BIDDER_BUYER**)
      - Set **ARG_NAME** = `'IS_BIDDER_BUYER'`
      - Set **AGR_CONTENT** = `toString($isBidderBuyer)`**
26. **Add **$$IS_BIDDER_BUYER** to/from list **$StoreProcedureArgumentList****
27. **Create Variable **$Storeproc** = `@EcoATM_PWS.SnowflakeEnvironmentDB +'.'+ @AuctionUI.UpsertBidDataStoredProc`**
28. **JavaCallAction**
29. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
30. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.