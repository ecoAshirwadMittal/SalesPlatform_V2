# Microflow Analysis: DS_GetPWSNavigationMenuItems

### Requirements (Inputs):
- **$ENUM_CurrentNavigationView** (A record of type: Object)

### Execution Steps:
1. **Search the Database for **EcoATM_PWS.NavigationMenu** using filter: { [
  (
    IsActive = true()
    and UserGroup = $ENUM_CurrentNavigationView
  )
] } (Call this list **$NavigationMenuList**)**
2. **Take the list **$NavigationMenuList**, perform a [Sort], and call the result **$NavigationMenuListSorted****
3. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
