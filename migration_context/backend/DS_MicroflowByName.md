# Microflow Detailed Specification: DS_MicroflowByName

### 📥 Inputs (Parameters)
- **$CompleteName** (Type: Variable)
- **$ParametersAllowed** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. **AggregateList**
3. 🔀 **DECISION:** `$Count=1`
   ➔ **If [true]:**
      1. **List Operation: **Head** on **$undefined** (Result: **$Microflow**)**
      2. 🏁 **END:** Return `$Microflow`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$Count=0`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `empty`
         ➔ **If [false]:**
            1. **List Operation: **Find** on **$undefined** where `$CompleteName` (Result: **$UniqueMicroflow**)**
            2. 🏁 **END:** Return `$UniqueMicroflow`

**Final Result:** This process concludes by returning a [Object] value.