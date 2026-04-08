# Page: Site_Details

**Layout:** `Sharepoint.DefaultLayout`

## Widget Tree

- 📦 **DataView** [Context]
    ↳ [acti] → **OpenLink**
  - 📑 **TabContainer**
    - 📑 **Tab**: "Sharepoint Lists"
      - 📦 **DataGrid** [MF: Sharepoint.DS_GetLists]
          ↳ [acti] → **Microflow**: `Sharepoint.ACT_OpenList`
        - 📊 **Column**: id [Width: 25]
        - 📊 **Column**: Name [Width: 25]
        - 📊 **Column**: Created date time [Width: 25]
        - 📊 **Column**: Last modified date time [Width: 25]
    - 📑 **Tab**: "Drives"
      - 📦 **DataGrid** [MF: Sharepoint.DS_GetDrives]
          ↳ [acti] → **Microflow**: `Sharepoint.ACT_OpenDrive`
        - 📊 **Column**: id [Width: 20]
        - 📊 **Column**: Name [Width: 20]
        - 📊 **Column**: Drive type [Width: 20]
        - 📊 **Column**: Path [Width: 20]
        - 📊 **Column**: Description [Width: 20]
