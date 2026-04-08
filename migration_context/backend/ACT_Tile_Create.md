# Microflow Detailed Specification: ACT_Tile_Create

### 📥 Inputs (Parameters)
- **$Tile** (Type: Eco_Core.Tile)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Tile/HasContents = true`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$Tile/LinkType`
         ➔ **If [Deeplink]:**
            1. **JavaCallAction**
            2. **Create Variable **$Variable** = `$ReturnValueName + '/p/' + $Tile/URL`**
            3. **Update **$Tile**
      - Set **URL** = `$Variable`**
            4. **Commit/Save **$Tile** to Database**
            5. **Maps to Page: **Eco_Core.Settings_View****
            6. 🏁 **END:** Return empty
         ➔ **If [(empty)]:**
            1. 🏁 **END:** Return empty
         ➔ **If [URL]:**
            1. **Commit/Save **$Tile** to Database**
            2. **Maps to Page: **Eco_Core.Settings_View****
            3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Information): `Image is required`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.