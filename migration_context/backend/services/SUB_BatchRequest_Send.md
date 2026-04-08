# Microflow Detailed Specification: SUB_BatchRequest_Send

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)
- **$BatchRequest** (Type: MicrosoftGraph.Batch)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. 🔀 **DECISION:** `$Authorization != empty`
   ➔ **If [true]:**
      1. **Create Variable **$Location** = `@MicrosoftGraph.GraphLocation+'/$batch'`**
      2. **ExportXml**
      3. **Call Microflow **MicrosoftGraph.POST** (Result: **$Response**)**
      4. 🔀 **DECISION:** `Successful?`
         ➔ **If [true]:**
            1. **ImportXml**
            2. **Retrieve related **Responses_Batch** via Association from **$Batch** (Result: **$Responses**)**
            3. **Retrieve related **BatchResponse_Responses** via Association from **$Responses** (Result: **$BatchResponseList**)**
            4. **LogMessage**
            5. 🏁 **END:** Return `$BatchResponseList`
         ➔ **If [false]:**
            1. **Call Microflow **MicrosoftGraph.SUB_Response_ErrorMessage** (Result: **$ErrorMessage**)**
            2. **LogMessage**
            3. 🏁 **END:** Return `empty`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [List] value.