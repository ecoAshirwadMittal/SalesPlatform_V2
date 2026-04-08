# Microflow Detailed Specification: ACT_MicroflowSelect

### 📥 Inputs (Parameters)
- **$Microflow** (Type: DeepLink.Microflow)
- **$DeepLink** (Type: DeepLink.DeepLink)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **DeepLink.UpdateMicroflowMetaData****
2. 🔀 **DECISION:** `$Microflow = empty`
   ➔ **If [true]:**
      1. **Update **$DeepLink**
      - Set **UseStringArgument** = `false`**
      2. 🔀 **DECISION:** `$Microflow/UseStringArg`
         ➔ **If [false]:**
            1. **JavaCallAction**
            2. **AggregateList**
            3. 🔀 **DECISION:** `$Count=1`
               ➔ **If [true]:**
                  1. **List Operation: **Head** on **$undefined** (Result: **$ObjectParam**)**
                  2. **Update **$DeepLink**
      - Set **ObjectType** = `$ObjectParam/Key`**
                  3. **Update **$DeepLink****
                  4. **Close current page/popup**
                  5. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Update **$DeepLink****
                  2. **Close current page/popup**
                  3. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Call Microflow **DeepLink.DS_GetParametersFromMicroflow** (Result: **$StringParameterList**)**
            2. **AggregateList**
            3. **Update **$DeepLink**
      - Set **SeparateGetParameters** = `$CountStringParameters > 0`**
            4. **Update **$DeepLink****
            5. **Close current page/popup**
            6. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$DeepLink**
      - Set **UseStringArgument** = `$Microflow/UseStringArg`
      - Set **Microflow** = `$Microflow/Name`
      - Set **UseObjectArgument** = `$Microflow/UseObjectArgument`**
      2. 🔀 **DECISION:** `$Microflow/UseStringArg`
         ➔ **If [false]:**
            1. **JavaCallAction**
            2. **AggregateList**
            3. 🔀 **DECISION:** `$Count=1`
               ➔ **If [true]:**
                  1. **List Operation: **Head** on **$undefined** (Result: **$ObjectParam**)**
                  2. **Update **$DeepLink**
      - Set **ObjectType** = `$ObjectParam/Key`**
                  3. **Update **$DeepLink****
                  4. **Close current page/popup**
                  5. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Update **$DeepLink****
                  2. **Close current page/popup**
                  3. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Call Microflow **DeepLink.DS_GetParametersFromMicroflow** (Result: **$StringParameterList**)**
            2. **AggregateList**
            3. **Update **$DeepLink**
      - Set **SeparateGetParameters** = `$CountStringParameters > 0`**
            4. **Update **$DeepLink****
            5. **Close current page/popup**
            6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.