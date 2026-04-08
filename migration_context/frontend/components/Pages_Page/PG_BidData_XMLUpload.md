# Page: PG_BidData_XMLUpload

**Allowed Roles:** EcoATM_BidData.User

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - ⚡ **Button**: Import [Style: Success]
    ↳ [acti] → **Microflow**: `EcoATM_BidData.BidData_ImportFromXml`
    ↳ [acti] → **Cancel Changes**
