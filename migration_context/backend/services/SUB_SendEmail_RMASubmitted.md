# Microflow Detailed Specification: SUB_SendEmail_RMASubmitted

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **Email_Connector.EmailTemplate** Filter: `[TemplateName='PWSRMASubmissionEmail']` (Result: **$EmailTemplate**)**
3. 🔀 **DECISION:** `$EmailTemplate!=empty`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Error****
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **DB Retrieve **Email_Connector.EmailAccount**  (Result: **$EmailAccount**)**
      2. **Retrieve related **RMAItem_RMA** via Association from **$RMA** (Result: **$RMAItemList**)**
      3. **Create Variable **$HtmlTable** = `'<table style="border-collapse: collapse; width: 100%;"> <tr style="border-bottom: 1px solid #B7B5B5;"> <th style="text-align: left; width: 20%; font-weight: 400; font-size: 12px; color: #6F6F6F; padding: 8px; justify-content: center; align-items: center; font-family: Trebuchet MS, Helvetica, sans-serif;">IMEI</th> <th style="text-align: left; width: 80%; font-weight: 400; font-size: 12px; color: #6F6F6F; padding: 8px; justify-content: center; align-items: center; font-family: Trebuchet MS, Helvetica, sans-serif;">Description</th> </tr>'`**
      4. 🔄 **LOOP:** For each **$IteratorRMAItem** in **$RMAItemList**
         │ 1. **Retrieve related **RMAItem_Device** via Association from **$IteratorRMAItem** (Result: **$Device**)**
         │ 2. **Create Variable **$Description** = `$Device/EcoATM_PWSMDM.Device_Model/EcoATM_PWSMDM.Model/DisplayName+ ', ' + $Device/EcoATM_PWSMDM.Device_Carrier/EcoATM_PWSMDM.Carrier/DisplayName+ ', ' + $Device/EcoATM_PWSMDM.Device_Capacity/EcoATM_PWSMDM.Capacity/DisplayName+ ', ' + $Device/EcoATM_PWSMDM.Device_Color/EcoATM_PWSMDM.Color/DisplayName`**
         │ 3. **Update Variable **$HtmlTable** = `$HtmlTable + ' <tr style="border-bottom: 1px solid #B7B5B5;"> <td style="text-align: left; color: #514F4E; padding: 8px; align-items: center; text-overflow: ellipsis; font-size: 14px; font-style: normal; font-weight: 400; line-height: 22px; font-family: Trebuchet MS, Helvetica, sans-serif;">' + $IteratorRMAItem/IMEI + '</th> <td style="text-align: left; color: #514F4E; text-align: left; padding: 8px; align-items: center; text-overflow: ellipsis; font-size: 14px; font-style: normal; font-weight: 700; font-family: Trebuchet MS, Helvetica, sans-serif;">' + $Description+ '</th> </tr>'`**
         └─ **End Loop**
      5. **Update Variable **$HtmlTable** = `$HtmlTable + '</table>'`**
      6. **Create Variable **$TotalHTML** = `'<table width="100%" cellpadding="0" cellspacing="0" style="padding-top: 24px;"> <tr> <td align="right" style="padding: 8px 0; font-size: 16px; color: #514F4E; white-space: nowrap;"> <span style="font-weight: 400; margin-right: 8px; font-family: Trebuchet MS, Helvetica, sans-serif;">Total Devices</span> <span style="font-weight: 700; font-family: Trebuchet MS, Helvetica, sans-serif;">' + $RMA/RequestQty + '</span> </td> </tr> </table>'`**
      7. **Create Variable **$FooterHTML** = `'<div style="display: flex; padding: 16px 0px; flex-direction: column; align-items: center; gap: 16px; align-self: stretch;"> <span style="color: #000; text-align: center; font-size: 12px; font-style: normal; font-weight: 400; line-height: 140%; font-family: Trebuchet MS, Helvetica, sans-serif;">This email is sent by ecoATM, LLC, 10121 Barnes Canyon Rd, San Diego, CA 92121. This is an automated message, please do not reply. Copyright 2025 – ecoATM, LLC. All Rights Reserved.</span></div>'`**
      8. **Retrieve related **RMA_BuyerCode** via Association from **$RMA** (Result: **$BuyerCode**)**
      9. **Retrieve related **BuyerCode_Buyer** via Association from **$BuyerCode** (Result: **$Buyer**)**
      10. **Retrieve related **EcoATMDirectUser_Buyer** via Association from **$Buyer** (Result: **$EcoATMDirectUserList**)**
      11. **CreateList**
      12. 🔄 **LOOP:** For each **$IteratorEcoATMDirectUser** in **$EcoATMDirectUserList**
         │ 1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
         │ 2. **Create **EcoATM_PWS.PWSEmailNotification** (Result: **$NewPWSEmailNotification**)
      - Set **BuyerName** = `$IteratorEcoATMDirectUser/FullName`
      - Set **TableHTML** = `$HtmlTable`
      - Set **TotalHTML** = `$TotalHTML`
      - Set **FooterHTML** = `$FooterHTML`**
         │ 3. **Update **$EmailTemplate**
      - Set **To** = `$IteratorEcoATMDirectUser/Name`**
         │ 4. **JavaCallAction**
         │ 5. **Add **$$NewPWSEmailNotification** to/from list **$PWSEmailNotificationList****
         │ 6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
         └─ **End Loop**
      13. **Delete**
      14. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      15. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.