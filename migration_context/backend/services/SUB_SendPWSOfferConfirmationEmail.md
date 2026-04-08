# Microflow Detailed Specification: SUB_SendPWSOfferConfirmationEmail

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **UserRoles** via Association from **$currentUser** (Result: **$UserRoleList**)**
2. **List Operation: **Find** on **$undefined** where `'Bidder'` (Result: **$Bidder**)**
3. 🔀 **DECISION:** `$Bidder!=empty`
   ➔ **If [true]:**
      1. **Retrieve related **Offer_BuyerCode** via Association from **$Offer** (Result: **$BuyerCode**)**
      2. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      3. **DB Retrieve **Email_Connector.EmailTemplate** Filter: `[TemplateName='PWSOfferConfirmation']` (Result: **$EmailTemplate**)**
      4. 🔀 **DECISION:** `$EmailTemplate!=empty`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Error****
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
            2. **Create Variable **$HtmlTable** = `'<table style="border-collapse: collapse; width: 100%;"> <tr style="border-bottom: 1px solid #B7B5B5"> <th style="text-align: left; width: 20%; font-weight: 400; font-size: 12px; color: #6F6F6F; padding: 8px; justify-content: center; align-items: center; font-family: Trebuchet MS, Helvetica, sans-serif;">SKU</th> <th style="text-align: left; width: 41%; font-weight: 400; font-size: 12px; color: #6F6F6F; padding: 8px; justify-content: center; align-items: center; font-family: Trebuchet MS, Helvetica, sans-serif;">Description</th> <th style="text-align: center; width: 13%; font-weight: 400; font-size: 12px; color: #6F6F6F; padding: 8px; justify-content: center; align-items: center; font-family: Trebuchet MS, Helvetica, sans-serif;">Ordered Qty</th> <th style="text-align: right; width: 13%; font-weight: 400; font-size: 12px; color: #6F6F6F; padding: 8px; justify-content: center; align-items: center; font-family: Trebuchet MS, Helvetica, sans-serif;">Unit Price</th> <th style="text-align: right; width: 13%; font-weight: 400; font-size: 12px; color: #6F6F6F; padding: 8px; justify-content: center; align-items: center; font-family: Trebuchet MS, Helvetica, sans-serif;">Total Price</th> </tr>'`**
            3. **Create Variable **$FinalTotal** = `0`**
            4. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList**
               │ 1. **Retrieve related **OfferItem_Device** via Association from **$IteratorOfferItem** (Result: **$Device**)**
               │ 2. **Create Variable **$Description** = `$Device/EcoATM_PWSMDM.Device_Model/EcoATM_PWSMDM.Model/DisplayName+ ', ' + $Device/EcoATM_PWSMDM.Device_Carrier/EcoATM_PWSMDM.Carrier/DisplayName+ ', ' + $Device/EcoATM_PWSMDM.Device_Capacity/EcoATM_PWSMDM.Capacity/DisplayName+ ', ' + $Device/EcoATM_PWSMDM.Device_Color/EcoATM_PWSMDM.Color/DisplayName`**
               │ 3. **Update Variable **$HtmlTable** = `$HtmlTable + ' <tr style="border-bottom: 1px solid #B7B5B5"> <td style="text-align: left; color: #514F4E; padding: 8px; align-items: center; text-overflow: ellipsis; font-size: 14px; font-style: normal; font-weight: 400; line-height: 22px; font-family: Trebuchet MS, Helvetica, sans-serif;">' + $Device/SKU + '</th> <td style="text-align: left; color: #514F4E; text-align: left; padding: 8px; align-items: center; text-overflow: ellipsis; font-size: 14px; font-style: normal; font-weight: 700; font-family: Trebuchet MS, Helvetica, sans-serif;">' + $Description + '</th> <td style="text-align: center; color: #514F4E; padding: 8px; align-items: center; text-overflow: ellipsis; font-size: 14px; font-style: normal; font-weight: 400; line-height: 22px; font-family: Trebuchet MS, Helvetica, sans-serif;">' + $IteratorOfferItem/OfferQuantity + '</th> <td style="text-align: right; color: #514F4E; padding: 8px; align-items: center; text-overflow: ellipsis; font-size: 14px; font-style: normal; font-weight: 400; line-height: 22px; font-family: Trebuchet MS, Helvetica, sans-serif;">$' + formatDecimal($IteratorOfferItem/OfferPrice, '#,##0')+ '</th> <td style="text-align: right; color: #514F4E; padding: 8px; align-items: center; text-overflow: ellipsis; font-size: 14px; font-style: normal; font-weight: 400; line-height: 22px; font-family: Trebuchet MS, Helvetica, sans-serif;">$' + formatDecimal($IteratorOfferItem/OfferTotalPrice, '#,##0')+ '</th> </tr>'`**
               │ 4. **Update Variable **$FinalTotal** = `$FinalTotal+$IteratorOfferItem/OfferTotalPrice`**
               └─ **End Loop**
            5. **Update Variable **$HtmlTable** = `$HtmlTable + '</table>'`**
            6. **Create Variable **$TotalHTML** = `'<table width="100%" cellpadding="0" cellspacing="0" style="padding-top: 24px;"> <tr> <td align="right" style="padding: 8px 0; font-size: 16px; color: #514F4E; white-space: nowrap;"> <span style="font-weight: 400; margin-right: 8px; font-family: Trebuchet MS, Helvetica, sans-serif;">Total:</span> <span style="font-weight: 700; font-family: Trebuchet MS, Helvetica, sans-serif;">$' + formatDecimal($Offer/OfferTotalPrice, '#,##0')+ '</span> </td> </tr> </table>'`**
            7. **Create Variable **$FooterHTML** = `'<div style="display: flex; padding: 16px 0px; flex-direction: column; align-items: center; gap: 16px; align-self: stretch;"> <span style="color: #000; text-align: center; font-family: Trebuchet MS, Helvetica, sans-serif; font-size: 12px; font-style: normal; font-weight: 400; line-height: 140%;">This email is sent by ecoATM, LLC, 10121 Barnes Canyon Rd, San Diego, CA 92121. This is an automated message, please do not reply. Copyright 2025 – ecoATM, LLC. All Rights Reserved.</span></div>'`**
            8. **Create **EcoATM_PWS.PWSEmailNotification** (Result: **$NewPWSEmailNotification**)
      - Set **BuyerName** = `$Offer/EcoATM_PWS.OfferSubmittedBy_Account/Administration.Account/FullName`
      - Set **OfferID** = `$Offer/OfferID`
      - Set **TableHTML** = `$HtmlTable`
      - Set **Total** = `$Offer/OfferTotalPrice`
      - Set **TotalHTML** = `$TotalHTML`
      - Set **FooterHTML** = `$FooterHTML`
      - Set **CompanyName** = `$BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/CompanyName`**
            9. **Update **$EmailTemplate**
      - Set **To** = `$Offer/EcoATM_PWS.OfferSubmittedBy_Account/EcoATM_UserManagement.EcoATMDirectUser/Name`**
            10. **DB Retrieve **Email_Connector.EmailAccount**  (Result: **$EmailAccount**)**
            11. **JavaCallAction**
            12. **Delete**
            13. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            14. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.