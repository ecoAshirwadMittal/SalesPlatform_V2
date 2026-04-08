# Microflow Detailed Specification: ACT_SoftDelete_BuyerCode

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$Buyer** (Type: EcoATM_BuyerManagement.Buyer)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$BuyerCode**
      - Set **softDelete** = `true`**
2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.