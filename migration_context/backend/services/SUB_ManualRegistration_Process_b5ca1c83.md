# Microflow Analysis: SUB_ManualRegistration_Process

### Requirements (Inputs):
- **$Configuration** (A record of type: DocumentGeneration.Configuration)
- **$RegistrationWizard** (A record of type: DocumentGeneration.RegistrationWizard)

### Execution Steps:
1. **Run another process: "DocumentGeneration.VAL_ManualRegistration_Input"
      - Store the result in a new variable called **$IsValid****
2. **Decision:** "IsValid?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
