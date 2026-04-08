# Microflow Detailed Specification: Act_SaveSaleRep

### 📥 Inputs (Parameters)
- **$SalesRepresentative** (Type: EcoATM_BuyerManagement.SalesRepresentative)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$SalesRepFirstName** = `trim($SalesRepresentative/SalesRepFirstName)`**
2. **Create Variable **$SalesRepLastName** = `trim($SalesRepresentative/SalesRepLastName)`**
3. **DB Retrieve **EcoATM_BuyerManagement.SalesRepresentative** Filter: `[id !=$SalesRepresentative]` (Result: **$SalesRepresentativeList**)**
4. **List Operation: **FindByExpression** on **$undefined** where `toLowerCase($SalesRepFirstName) = toLowerCase($currentObject/SalesRepFirstName) and toLowerCase($SalesRepLastName) = toLowerCase($currentObject/SalesRepLastName)` (Result: **$Existing**)**
5. 🔀 **DECISION:** `$Existing != empty`
   ➔ **If [true]:**
      1. **Show Message (Information): `{1} already Exists`**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$SalesRepresentative** (and Save to DB)
      - Set **SalesRepFirstName** = `$SalesRepFirstName`
      - Set **SalesRepLastName** = `$SalesRepLastName`**
      2. **CreateList**
      3. **Add **$$SalesRepresentative
** to/from list **$UpdateSalesRepresentativeList****
      4. **Call Microflow **EcoATM_MDM.SUB_SendAllSalesRepresentativeToSnowflake****
      5. **Close current page/popup**
      6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.