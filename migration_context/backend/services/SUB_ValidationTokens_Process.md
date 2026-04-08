# Microflow Detailed Specification: SUB_ValidationTokens_Process

### 📥 Inputs (Parameters)
- **$ValidationTokens** (Type: MicrosoftGraph.ValidationTokens)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **Retrieve related **NPStringArrayWrapper_NPStringArray** via Association from **$ValidationTokens** (Result: **$NPStringArrayWrapperList**)**
3. **Create Variable **$Valid** = `false`**
4. 🔄 **LOOP:** For each **$IteratorNPStringArrayWrapper** in **$NPStringArrayWrapperList**
   │ 1. **Create Variable **$TokenValid** = `false`**
   │ 2. **Update Variable **$Valid** = `$TokenValid`**
   └─ **End Loop**
5. **LogMessage**
6. 🏁 **END:** Return `$Valid`

**Final Result:** This process concludes by returning a [Boolean] value.