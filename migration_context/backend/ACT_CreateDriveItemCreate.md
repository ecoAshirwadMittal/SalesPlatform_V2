# Microflow Detailed Specification: ACT_CreateDriveItemCreate

### 📥 Inputs (Parameters)
- **$FileDocument** (Type: System.FileDocument)
- **$Auction** (Type: AuctionUI.Auction)
- **$AuctionSubFolderName** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. 🔀 **DECISION:** `@EcoATM_Direct_Sharepoint.SharepointBidRoundEnabled = true`
   ➔ **If [true]:**
      1. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
      2. **Create Variable **$FolderDatePart** = `formatDateTime($Week/WeekStartDateTime,'YYYY-MM-dd')`**
      3. **Create Variable **$AuctionFolderName** = `'auction_' + $FolderDatePart`**
      4. **Create Variable **$SharepointBidSite** = `@EcoATM_Direct_Sharepoint.SharepointSiteId`**
      5. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_GetAuthorization** (Result: **$Authorization**)**
      6. **Create Variable **$ParentFolder** = `'root:/'+ @EcoATM_Direct_Sharepoint.SharepointRoundBidsRootFolderName + '/' +$AuctionFolderName + $AuctionSubFolderName + $FileDocument/Name`**
      7. **Call Microflow **Sharepoint.GetDrives** (Result: **$SiteDrives**)**
      8. **List Operation: **Find** on **$undefined** where `'Documents'` (Result: **$Drive**)**
      9. 🔀 **DECISION:** `$Drive != empty`
         ➔ **If [true]:**
            1. **Call Microflow **Sharepoint.CreateDriveItem** (Result: **$DriveItemId**)**
            2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            3. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Error****
            2. 🏁 **END:** Return `false`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.