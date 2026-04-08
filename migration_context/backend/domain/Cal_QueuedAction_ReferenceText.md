# Microflow Detailed Specification: Cal_QueuedAction_ReferenceText

### 📥 Inputs (Parameters)
- **$MicroflowName** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$ReferenceText** = `'Batch'`**
2. 🔀 **DECISION:** `$MicroflowName!=empty and trim($MicroflowName)!=''`
   ➔ **If [true]:**
      1. **Create Variable **$Len** = `length($MicroflowName)`**
      2. **Create Variable **$Pos** = `find($MicroflowName,'.')`**
      3. **Update Variable **$Pos** = `if $Pos>=0 then $Pos+5 else 5`**
      4. **Update Variable **$ReferenceText** = `substring($MicroflowName,$Pos,$Len-$Pos)`**
      5. 🏁 **END:** Return `$ReferenceText`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$ReferenceText`

**Final Result:** This process concludes by returning a [String] value.