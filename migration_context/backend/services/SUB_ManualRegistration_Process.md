# Microflow Detailed Specification: SUB_ManualRegistration_Process

### 📥 Inputs (Parameters)
- **$Configuration** (Type: DocumentGeneration.Configuration)
- **$RegistrationWizard** (Type: DocumentGeneration.RegistrationWizard)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **DocumentGeneration.VAL_ManualRegistration_Input** (Result: **$IsValid**)**
2. 🔀 **DECISION:** `$IsValid`
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **LogMessage**
      2. **JavaCallAction**
      3. **Update **$Configuration** (and Save to DB)
      - Set **RegistrationStatus** = `DocumentGeneration.Enum_RegistrationStatus.Registered`**
      4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.