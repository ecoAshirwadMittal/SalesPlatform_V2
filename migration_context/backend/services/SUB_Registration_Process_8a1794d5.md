# Microflow Analysis: SUB_Registration_Process

### Requirements (Inputs):
- **$Configuration** (A record of type: DocumentGeneration.Configuration)
- **$RegistrationWizard** (A record of type: DocumentGeneration.RegistrationWizard)

### Execution Steps:
1. **Run another process: "DocumentGeneration.VAL_Registration_Input"
      - Store the result in a new variable called **$IsValid****
2. **Decision:** "IsValid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Java Action Call
      - Store the result in a new variable called **$TokenResult**** ⚠️ *(This step has a safety catch if it fails)*
4. **Decision:** "Success?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Show message**
5. **Update the **$undefined** (Object):
      - Change [DocumentGeneration.Configuration.DeploymentType] to: "$RegistrationWizard/DeploymentType"
      - Change [DocumentGeneration.Configuration.RegistrationStatus] to: "DocumentGeneration.Enum_RegistrationStatus.Registered"
      - **Save:** This change will be saved to the database immediately.**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
