# Microflow Detailed Specification: ACT_UploadBidRoundFileToSharepoint

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_GetAuthorization** (Result: **$Authorization**)**
2. **Create Variable **$SharepointBidSite** = `@EcoATM_Direct_Sharepoint.SharepointSiteId`**
3. **Call Microflow **Sharepoint.GetSite** (Result: **$Site**)**
4. **Call Microflow **Sharepoint.GetLists** (Result: **$Result**)**
5. **List Operation: **Find** on **$undefined** (Result: **$NewList**)**
6. **Call Microflow **Sharepoint.ACT_CreateDriveItem****
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.