# Microflow Detailed Specification: SUB_Registration_Process

### 📥 Inputs (Parameters)
- **$Configuration** (Type: DocumentGeneration.Configuration)
- **$RegistrationWizard** (Type: DocumentGeneration.RegistrationWizard)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **DocumentGeneration.VAL_Registration_Input** (Result: **$IsValid**)**
2. 🔀 **DECISION:** `$IsValid`
   ➔ **If [true]:**
      1. **JavaCallAction**
      2. 🔀 **DECISION:** `$TokenResult != empty and $TokenResult/Success`
         ➔ **If [true]:**
            1. **Update **$Configuration** (and Save to DB)
      - Set **DeploymentType** = `$RegistrationWizard/DeploymentType`
      - Set **RegistrationStatus** = `DocumentGeneration.Enum_RegistrationStatus.Registered`**
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Show Message (Warning): `Failed to register application: {1}. See the troubleshooting section in the module documentation for more details.`**
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.