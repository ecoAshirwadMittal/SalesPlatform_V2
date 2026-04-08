# Microflow Analysis: ACT_Registration_Start

### Requirements (Inputs):
- **$Configuration** (A record of type: DocumentGeneration.Configuration)
- **$RegistrationWizard** (A record of type: DocumentGeneration.RegistrationWizard)

### Execution Steps:
1. **Decision:** "Deployment
type"
   - If [MendixPrivateCloud] -> Move to: **Finish**
   - If [MendixPublicCloud] -> Move to: **Finish**
   - If [MendixCloudDedicated] -> Move to: **Finish**
   - If [(empty)] -> Move to: **Activity**
   - If [Other] -> Move to: **Activity**
2. **Run another process: "DocumentGeneration.SUB_Registration_Process"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
