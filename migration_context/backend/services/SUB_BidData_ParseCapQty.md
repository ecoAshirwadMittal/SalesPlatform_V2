# Microflow Detailed Specification: SUB_BidData_ParseCapQty

### 📥 Inputs (Parameters)
- **$CapQty** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$ParseCapQtyDecimal** = `if (trim($CapQty) != '') then parseDecimal($CapQty) else empty`**
2. **Create Variable **$ParseCapQty** = `empty`**
3. 🔀 **DECISION:** `$ParseCapQtyDecimal != empty`
   ➔ **If [true]:**
      1. **Update Variable **$ParseCapQty** = `floor($ParseCapQtyDecimal)`**
      2. 🏁 **END:** Return `$ParseCapQty`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$ParseCapQty`

**Final Result:** This process concludes by returning a [Integer] value.