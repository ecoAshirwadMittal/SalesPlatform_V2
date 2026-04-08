# Microflow Detailed Specification: SUB_RegistrationWizard_FindOrCreate

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **DocumentGeneration.SUB_Configuration_FindOrCreate** (Result: **$Configuration**)**
2. **JavaCallAction**
3. **JavaCallAction**
4. **Create **DocumentGeneration.RegistrationWizard** (Result: **$NewRegistrationWizard**)
      - Set **RegistrationWizard_Configuration** = `$Configuration`
      - Set **ServiceType** = `$ServiceType`
      - Set **ApplicationUrl** = `$ApplicationURL`**
5. 🏁 **END:** Return `$NewRegistrationWizard`

**Final Result:** This process concludes by returning a [Object] value.