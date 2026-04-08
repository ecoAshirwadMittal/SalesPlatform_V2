# Nanoflow: DS_GetOrSetBuyerCode_SessionAndTabHelper

**Allowed Roles:** EcoATM_Direct_Theme.Administrator, EcoATM_Direct_Theme.User

## ⚙️ Execution Flow

1. **Call JS Action **EcoATM_Direct_Theme.JSA_GetTabIndex** (Result: **$TabId**)**
2. **Call Microflow **EcoATM_Direct_Theme.SUB_GetOrSetBuyerCode_SessionAndTabHelper** (Result: **$BuyerCode_SessionAndTabHelper**)**
3. 🏁 **END:** Return `$BuyerCode_SessionAndTabHelper`

## 🏁 Returns
`Object`
