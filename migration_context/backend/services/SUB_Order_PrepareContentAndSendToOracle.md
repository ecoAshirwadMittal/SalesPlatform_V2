# Microflow Detailed Specification: SUB_Order_PrepareContentAndSendToOracle

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)
- **$Order** (Type: EcoATM_PWS.Order)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Description** = `'Prepare data and submit to Oracle Offer [OfferId;'+$Offer/OfferID+']'`**
2. **Call Microflow **Custom_Logging.SUB_Log_Info****
3. **Call Microflow **EcoATM_PWS.SUB_Offer_PrepareOraclePayload** (Result: **$JSONContent**)**
4. 🔀 **DECISION:** `$JSONContent!=empty`
   ➔ **If [true]:**
      1. **Update **$Order**
      - Set **JSONContent** = `$JSONContent`**
      2. **Call Microflow **EcoATM_PWS.SUB_Order_SendOrderToOracle** (Result: **$CreateOrderResponse**)**
      3. **DB Retrieve **Administration.Account** Filter: `[id=$currentUser]` (Result: **$CurrentLoggedInUser**)**
      4. **Call Microflow **Custom_Logging.SUB_Log_Info****
      5. 🏁 **END:** Return `$CreateOrderResponse`
   ➔ **If [false]:**
      1. **Create **EcoATM_PWSIntegration.OracleResponse** (Result: **$GeneralErrorCreateOrderResponse**)
      - Set **ReturnMessage** = `'JSON Content Generation Failed'`**
      2. **Call Microflow **Custom_Logging.SUB_Log_Error****
      3. 🏁 **END:** Return `$GeneralErrorCreateOrderResponse`

**Final Result:** This process concludes by returning a [Object] value.