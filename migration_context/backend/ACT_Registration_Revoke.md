# Microflow Detailed Specification: ACT_Registration_Revoke

### 📥 Inputs (Parameters)
- **$Configuration** (Type: DocumentGeneration.Configuration)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$Configuration** (and Save to DB)
      - Set **DeploymentType** = `empty`
      - Set **RegistrationStatus** = `DocumentGeneration.Enum_RegistrationStatus.Unregistered`
      - Set **AccessToken** = `empty`
      - Set **RefreshToken** = `empty`**
2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.