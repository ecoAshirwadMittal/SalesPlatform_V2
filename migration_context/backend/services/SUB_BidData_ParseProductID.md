# Microflow Detailed Specification: SUB_BidData_ParseProductID

### 📥 Inputs (Parameters)
- **$ProductID** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$ParseProductIDDecimal** = `if (trim($ProductID) != '') then parseDecimal($ProductID) else empty`**
2. **Create Variable **$ParseProductIDInteger** = `empty`**
3. 🔀 **DECISION:** `$ParseProductIDDecimal != empty`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$ParseProductIDInteger`
   ➔ **If [true]:**
      1. **Update Variable **$ParseProductIDInteger** = `floor($ParseProductIDDecimal)`**
      2. 🏁 **END:** Return `$ParseProductIDInteger`

**Final Result:** This process concludes by returning a [Integer] value.