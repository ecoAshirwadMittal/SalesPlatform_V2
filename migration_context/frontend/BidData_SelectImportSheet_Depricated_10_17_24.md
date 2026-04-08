# Nanoflow: BidData_SelectImportSheet_Depricated_10_17_24

**Allowed Roles:** EcoATM_BidData.User

## ⚙️ Execution Flow

1. **LogMessage (Warning)**
2. **DB Retrieve **EcoATM_BidData.BidData_ImportSettings**  (Result: **$BidData_ImportSettings**)**
3. 🔀 **DECISION:** `$BidData_ImportSettings != empty`
   ➔ **If [true]:**
      1. **Retrieve related **BidData_ImportSettings_DefaultTemplate** via Association from **$BidData_ImportSettings** (Result: **$BidImport_DefaultTemplate**)**
      2. **Create **ExcelImporter.XMLDocumentTemplate** (Result: **$NewXMLDocumentTemplate**)
      - Set **XMLDocumentTemplate_Template** = `$BidImport_DefaultTemplate`**
      3. **Open Page: **EcoATM_BidData.BidData_XMLUpload****
      4. **LogMessage (Warning)**
      5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Error): `An error occurred, please contact your system administrator.`**
      2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
