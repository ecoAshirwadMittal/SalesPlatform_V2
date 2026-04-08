# Microflow Detailed Specification: ACT_Registration_Start

### 📥 Inputs (Parameters)
- **$Configuration** (Type: DocumentGeneration.Configuration)
- **$RegistrationWizard** (Type: DocumentGeneration.RegistrationWizard)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$RegistrationWizard/DeploymentType`
   ➔ **If [MendixPrivateCloud]:**
      1. **Call Microflow **DocumentGeneration.SUB_Registration_Process****
      2. 🏁 **END:** Return empty
   ➔ **If [MendixPublicCloud]:**
      1. **Call Microflow **DocumentGeneration.SUB_Registration_Process****
      2. 🏁 **END:** Return empty
   ➔ **If [MendixCloudDedicated]:**
      1. **Call Microflow **DocumentGeneration.SUB_Registration_Process****
      2. 🏁 **END:** Return empty
   ➔ **If [(empty)]:**
      1. **ValidationFeedback**
      2. 🏁 **END:** Return empty
   ➔ **If [Other]:**
      1. **Call Microflow **DocumentGeneration.SUB_ManualRegistration_Process****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.