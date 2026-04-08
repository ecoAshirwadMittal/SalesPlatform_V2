# Microflow Detailed Specification: SUB_SendEmail_RMAApproved

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **Email_Connector.EmailTemplate** Filter: `[TemplateName='PWSRMAApprovalEmail']` (Result: **$EmailTemplate**)**
3. 🔀 **DECISION:** `$EmailTemplate!=empty`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Error****
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **DB Retrieve **Email_Connector.EmailAccount**  (Result: **$EmailAccount**)**
      2. **Retrieve related **RMAItem_RMA** via Association from **$RMA** (Result: **$RMAItemList**)**
      3. **List Operation: **Filter** on **$undefined** where `EcoATM_RMA.ENUM_RMAItemStatus.Approve` (Result: **$RMAItemList_Approved**)**
      4. **Create Variable **$HtmlTable** = `'<table style="border-collapse: collapse; width: 100%;"> <tr style="border-bottom: 1px solid #B7B5B5;"> <th style="text-align: left; width: 20%; font-weight: 400; font-size: 12px; color: #6F6F6F; padding: 8px; justify-content: center; align-items: center; font-family: Trebuchet MS, Helvetica, sans-serif;">IMEI</th> <th style="text-align: left; width: 80%; font-weight: 400; font-size: 12px; color: #6F6F6F; padding: 8px; justify-content: center; align-items: center; font-family: Trebuchet MS, Helvetica, sans-serif;">Description</th> </tr>'`**
      5. 🔄 **LOOP:** For each **$IteratorRMAItem** in **$RMAItemList_Approved**
         │ 1. **Retrieve related **RMAItem_Device** via Association from **$IteratorRMAItem** (Result: **$Device**)**
         │ 2. **Create Variable **$Description** = `$Device/EcoATM_PWSMDM.Device_Model/EcoATM_PWSMDM.Model/DisplayName+ ', ' + $Device/EcoATM_PWSMDM.Device_Carrier/EcoATM_PWSMDM.Carrier/DisplayName+ ', ' + $Device/EcoATM_PWSMDM.Device_Capacity/EcoATM_PWSMDM.Capacity/DisplayName+ ', ' + $Device/EcoATM_PWSMDM.Device_Color/EcoATM_PWSMDM.Color/DisplayName`**
         │ 3. **Update Variable **$HtmlTable** = `$HtmlTable + ' <tr style="border-bottom: 1px solid #B7B5B5;"> <td style="text-align: left; color: #514F4E; padding: 8px; align-items: center; text-overflow: ellipsis; font-size: 14px; font-style: normal; font-weight: 400; line-height: 22px; font-family: Trebuchet MS, Helvetica, sans-serif;">' + $IteratorRMAItem/IMEI + '</th> <td style="text-align: left; color: #514F4E; text-align: left; padding: 8px; align-items: center; text-overflow: ellipsis; font-size: 14px; font-style: normal; font-weight: 700; font-family: Trebuchet MS, Helvetica, sans-serif;">' + $Description+ '</th> </tr>'`**
         └─ **End Loop**
      6. **Update Variable **$HtmlTable** = `$HtmlTable + '</table>'`**
      7. **Create Variable **$RMAButtonHTML** = `'<div style="text-align: center;"> <a href="'+@EcoATM_RMA.RMADetailsURL+$RMA/Number+'" style="background-color: #2CB34A; color: #ffffff; padding: 10px 16px; text-decoration: none; border-radius: 4px; display: ruby; font-size: 16px; font-family: Trebuchet MS, Helvetica, sans-serif; font-weight: bold;"> See Approved / Declined Device Details </a> </div>'`**
      8. **Create Variable **$TotalHTML** = `'<table width="100%" cellpadding="0" cellspacing="0" style="padding-top: 24px;"> <tr> <td align="right" style="padding: 8px 0; font-size: 16px; color: #514F4E; white-space: nowrap;"> <span style="font-weight: 400; margin-right: 8px; font-family: Trebuchet MS, Helvetica, sans-serif;">Total Devices</span> <span style="font-weight: 700; font-family: Trebuchet MS, Helvetica, sans-serif;">' + $RMA/ApprovedQty+ '</span> </td> </tr> </table>'`**
      9. **Create Variable **$FooterHTML** = `'<div style="display: flex; padding: 16px 0px; flex-direction: column; align-items: center; gap: 16px; align-self: stretch;"> <span style="color: #000; text-align: center; font-size: 12px; font-style: normal; font-weight: 400; line-height: 140%; font-family: Trebuchet MS, Helvetica, sans-serif;">This email is sent by ecoATM, LLC, 10121 Barnes Canyon Rd, San Diego, CA 92121. This is an automated message, please do not reply. Copyright 2025 – ecoATM, LLC. All Rights Reserved.</span></div>'`**
      10. **Retrieve related **RMA_BuyerCode** via Association from **$RMA** (Result: **$BuyerCode**)**
      11. **Retrieve related **BuyerCode_Buyer** via Association from **$BuyerCode** (Result: **$Buyer**)**
      12. **Retrieve related **EcoATMDirectUser_Buyer** via Association from **$Buyer** (Result: **$EcoATMDirectUserList**)**
      13. **CreateList**
      14. 🔄 **LOOP:** For each **$IteratorEcoATMDirectUser** in **$EcoATMDirectUserList**
         │ 1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
         │ 2. **Create **EcoATM_PWS.PWSEmailNotification** (Result: **$NewPWSEmailNotification**)
      - Set **RMANumber** = `$RMA/Number`
      - Set **BuyerName** = `$IteratorEcoATMDirectUser/FullName`
      - Set **TableHTML** = `$HtmlTable`
      - Set **TotalHTML** = `$TotalHTML`
      - Set **FooterHTML** = `$FooterHTML`
      - Set **RMAButtonHTML** = `$RMAButtonHTML`**
         │ 3. **Update **$EmailTemplate**
      - Set **To** = `$IteratorEcoATMDirectUser/Name`**
         │ 4. **JavaCallAction**
         │ 5. **Add **$$NewPWSEmailNotification** to/from list **$PWSEmailNotificationList****
         │ 6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
         └─ **End Loop**
      15. **Delete**
      16. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      17. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.