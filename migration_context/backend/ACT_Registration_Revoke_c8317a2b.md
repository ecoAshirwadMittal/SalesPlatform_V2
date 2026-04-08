# Microflow Analysis: ACT_Registration_Revoke

### Requirements (Inputs):
- **$Configuration** (A record of type: DocumentGeneration.Configuration)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [DocumentGeneration.Configuration.DeploymentType] to: "empty"
      - Change [DocumentGeneration.Configuration.RegistrationStatus] to: "DocumentGeneration.Enum_RegistrationStatus.Unregistered"
      - Change [DocumentGeneration.Configuration.AccessToken] to: "empty"
      - Change [DocumentGeneration.Configuration.RefreshToken] to: "empty"
      - **Save:** This change will be saved to the database immediately.**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
