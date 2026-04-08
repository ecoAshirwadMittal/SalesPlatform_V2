# Microflow Detailed Specification: DS_CreateRMAMasterHelper

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **RMAMasterHelper_Session** via Association from **$currentSession** (Result: **$RMAMasterHelperList**)**
2. 🔀 **DECISION:** `$RMAMasterHelperList!=empty`
   ➔ **If [true]:**
      1. **List Operation: **Head** on **$undefined** (Result: **$RMAMasterHelper**)**
      2. 🏁 **END:** Return `$RMAMasterHelper`
   ➔ **If [false]:**
      1. **Create **EcoATM_RMA.RMAMasterHelper** (Result: **$NewRMAMasterHelper**)
      - Set **RMAMasterHelper_Session** = `$currentSession`**
      2. **DB Retrieve **EcoATM_RMA.RMAStatus**  (Result: **$RMAStatusList**)**
      3. **CreateList**
      4. **Create **EcoATM_RMA.RMAUiHelper** (Result: **$NewRMAUiHelper_PendingApproval**)
      - Set **HeaderLabel** = `EcoATM_RMA.ENUM_StatusGroup.Pending_Approval`
      - Set **RMAUiHelper_RMAMasterHelper** = `$NewRMAMasterHelper`
      - Set **RMAUiHelper_RMAMasterHelper_Selected** = `$NewRMAMasterHelper`
      - Set **SortOrder** = `1`
      - Set **HeaderCSSClass** = `getKey(EcoATM_Direct_Theme.ENUM_PWSBgColor.pws_bgcolor_brightyellow)`
      - Set **DataGridHoverCSSClass** = `getKey(EcoATM_Direct_Theme.ENUM_PWSBgColor.pws_bgcolor_brandsecondarylight)`**
      5. **Add **$$NewRMAUiHelper_PendingApproval** to/from list **$RMAUiHelperList****
      6. **Create **EcoATM_RMA.RMAUiHelper** (Result: **$NewRMAUiHelper_Open**)
      - Set **HeaderLabel** = `EcoATM_RMA.ENUM_StatusGroup.Open`
      - Set **RMAUiHelper_RMAMasterHelper** = `$NewRMAMasterHelper`
      - Set **SortOrder** = `2`
      - Set **HeaderCSSClass** = `getKey(EcoATM_Direct_Theme.ENUM_PWSBgColor.pws_bgcolor_lightorange)`
      - Set **DataGridHoverCSSClass** = `getKey(EcoATM_Direct_Theme.ENUM_PWSBgColor.pws_bgcolor_orangehighlight)`**
      7. **Add **$$NewRMAUiHelper_Open** to/from list **$RMAUiHelperList****
      8. **Create **EcoATM_RMA.RMAUiHelper** (Result: **$NewRMAUiHelper_Closed**)
      - Set **HeaderLabel** = `EcoATM_RMA.ENUM_StatusGroup.Closed`
      - Set **RMAUiHelper_RMAMasterHelper** = `$NewRMAMasterHelper`
      - Set **SortOrder** = `3`
      - Set **HeaderCSSClass** = `getKey(EcoATM_Direct_Theme.ENUM_PWSBgColor.pws_bgcolor_lightgreen)`
      - Set **DataGridHoverCSSClass** = `getKey(EcoATM_Direct_Theme.ENUM_PWSBgColor.pws_bgcolor_hothighlight)`**
      9. **Add **$$NewRMAUiHelper_Closed** to/from list **$RMAUiHelperList****
      10. **Create **EcoATM_RMA.RMAUiHelper** (Result: **$NewRMAUiHelper_Declined**)
      - Set **HeaderLabel** = `EcoATM_RMA.ENUM_StatusGroup.Declined`
      - Set **RMAUiHelper_RMAMasterHelper** = `$NewRMAMasterHelper`
      - Set **SortOrder** = `4`
      - Set **HeaderCSSClass** = `getKey(EcoATM_Direct_Theme.ENUM_PWSBgColor.pws_bgcolor_peach)`
      - Set **DataGridHoverCSSClass** = `getKey(EcoATM_Direct_Theme.ENUM_PWSBgColor.pws_bgcolor_lightorange)`**
      11. **Add **$$NewRMAUiHelper_Declined** to/from list **$RMAUiHelperList****
      12. **Create **EcoATM_RMA.RMAUiHelper** (Result: **$NewRMAUiHelper_Total**)
      - Set **HeaderLabel** = `empty`
      - Set **RMASystemStatus** = `'Total'`
      - Set **SortOrder** = `999`
      - Set **RMAUiHelper_RMAMasterHelper** = `$NewRMAMasterHelper`**
      13. **Add **$$NewRMAUiHelper_Total** to/from list **$RMAUiHelperList****
      14. 🔄 **LOOP:** For each **$IteratorRMAUiHelper** in **$RMAUiHelperList**
         │ 1. 🔀 **DECISION:** `$IteratorRMAUiHelper/HeaderLabel != empty`
         │    ➔ **If [true]:**
         │       1. **List Operation: **Filter** on **$undefined** where `$IteratorRMAUiHelper/HeaderLabel` (Result: **$RMAStatusList_Iterator**)**
         │       2. **Update **$IteratorRMAUiHelper**
      - Set **RMAUiHelper_RMAStatus** = `$RMAStatusList_Iterator`**
         │    ➔ **If [false]:**
         └─ **End Loop**
      15. **Commit/Save **$RMAUiHelperList** to Database**
      16. 🏁 **END:** Return `$NewRMAMasterHelper`

**Final Result:** This process concludes by returning a [Object] value.