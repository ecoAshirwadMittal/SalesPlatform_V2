# Microflow Detailed Specification: ACT_Order_ReSubmitToOracle

### 📥 Inputs (Parameters)
- **$Order** (Type: EcoATM_PWS.Order)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Retrieve related **Offer_Order** via Association from **$Order** (Result: **$Offer**)**
3. **Call Microflow **EcoATM_PWS.SUB_Order_SendOrderToOracle** (Result: **$CreateOrderResponse**)**
4. **Call Microflow **EcoATM_PWS.SUB_CreateOrderResponse_ManageResult****
5. **Create **EcoATM_PWS.UserMessage** (Result: **$UserMessage**)
      - Set **Title** = `'Offer response resubmitted'`
      - Set **Message** = `'You will receive details about your order shortly.'`
      - Set **CSSClass** = `'pws-file-upload-success'`
      - Set **IsSuccess** = `true`**
6. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
7. **Call Microflow **Custom_Logging.SUB_Log_Info****
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.