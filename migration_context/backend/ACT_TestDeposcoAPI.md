# Microflow Detailed Specification: ACT_TestDeposcoAPI

### 📥 Inputs (Parameters)
- **$DeposcoConfig** (Type: EcoATM_PWSIntegration.DeposcoConfig)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_PWSIntegration.SUB_GenerateDeposcoPassword** (Result: **$EncodedAuth**)**
2. **Create Variable **$Count** = `1`**
3. 🔄 **LOOP:** For each **$undefined** in **$undefined**
   │ 1. **RestCall**
   │ 2. **Update Variable **$Count** = `$Count-1`**
   │ 3. **Show Message (Information): `{1}`**
   └─ **End Loop**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.