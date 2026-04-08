# Microflow Detailed Specification: SUB_CalculatePurcentage

### 📥 Inputs (Parameters)
- **$Numerator** (Type: Variable)
- **$Divisor** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Divisor != empty and $Divisor>0`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `($Numerator div $Divisor) * 100`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `0.0`

**Final Result:** This process concludes by returning a [Decimal] value.