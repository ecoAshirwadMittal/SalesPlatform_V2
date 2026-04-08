# Microflow Detailed Specification: SUB_BidData_ParseAvailQty

### 📥 Inputs (Parameters)
- **$AvailQty** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$ParseAvailableQtyDecimal** = `if (trim($AvailQty) != '') then parseDecimal($AvailQty) else empty`**
2. **Create Variable **$ParseAvailableQtyInteger** = `empty`**
3. 🔀 **DECISION:** `$ParseAvailableQtyDecimal != empty`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$ParseAvailableQtyInteger`
   ➔ **If [true]:**
      1. **Update Variable **$ParseAvailableQtyInteger** = `floor($ParseAvailableQtyDecimal)`**
      2. 🏁 **END:** Return `$ParseAvailableQtyInteger`

**Final Result:** This process concludes by returning a [Integer] value.