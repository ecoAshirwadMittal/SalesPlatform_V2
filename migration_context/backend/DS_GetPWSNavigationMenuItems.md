# Microflow Detailed Specification: DS_GetPWSNavigationMenuItems

### 📥 Inputs (Parameters)
- **$ENUM_CurrentNavigationView** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.NavigationMenu** Filter: `[ ( IsActive = true() and UserGroup = $ENUM_CurrentNavigationView ) ]` (Result: **$NavigationMenuList**)**
2. **List Operation: **Sort** on **$undefined** sorted by: Order (Ascending) (Result: **$NavigationMenuListSorted**)**
3. 🏁 **END:** Return `$NavigationMenuListSorted`

**Final Result:** This process concludes by returning a [List] value.