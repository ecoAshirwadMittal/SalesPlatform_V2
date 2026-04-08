# Page: List_Details

**Allowed Roles:** Sharepoint.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📦 **DataView** [Context]
  - 📑 **TabContainer**
    - 📑 **Tab**: "Items"
      - 📦 **DataGrid** [MF: Sharepoint.DS_GetListItems]
          ↳ [acti] → **Microflow**: `Sharepoint.ACT_OpenListItem`
          ↳ [acti] → **Microflow**: `Sharepoint.ACT_CreateDriveItem`
          ↳ [acti] → **Microflow**: `Sharepoint.ACT_CreateListItem`
        - 📊 **Column**: id [Width: 3]
        - 📊 **Column**: Display name [Width: 21]
        - 📊 **Column**: Created date time [Width: 13]
        - 📊 **Column**: Last modified date time [Width: 11]
        - 📊 **Column**: Created by email [Width: 22]
        - 📊 **Column**: Last modified by email [Width: 23]
        - 📊 **Column**: Content type name [Width: 7]
    - 📑 **Tab**: "Drive" 👁️ (If: `$currentObject/Sharepoint.List_Drive != empty`)
      - 📦 **DataView** [Context]
    ↳ [acti] → **Close Page**
