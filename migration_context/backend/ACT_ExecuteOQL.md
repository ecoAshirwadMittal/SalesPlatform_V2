# Microflow Detailed Specification: ACT_ExecuteOQL

### 📥 Inputs (Parameters)
- **$Query** (Type: OQL.Query)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. 🔀 **DECISION:** `$Query/ShowAs`
   ➔ **If [DOWNLOAD]:**
      1. **Update **$CSVDownload** (and Save to DB)
      - Set **DeleteAfterDownload** = `true`**
      2. **DownloadFile**
      3. 🏁 **END:** Return empty
   ➔ **If [TABLE]:**
      1. **JavaCallAction**
      2. **Update **$Query**
      - Set **CSV** = `$CSVContents`**
      3. **Delete**
      4. 🏁 **END:** Return empty
   ➔ **If [(empty)]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.