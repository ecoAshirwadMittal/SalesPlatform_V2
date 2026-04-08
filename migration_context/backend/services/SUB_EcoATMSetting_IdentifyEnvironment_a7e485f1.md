# Microflow Analysis: SUB_EcoATMSetting_IdentifyEnvironment

### Requirements (Inputs):
- **$EcoATMSetting** (A record of type: Custom_Logging.EcoATMSetting)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$EnvironmentList****
2. **Java Action Call
      - Store the result in a new variable called **$ApplicationURL**** ⚠️ *(This step has a safety catch if it fails)*
3. **Take the list **$EnvironmentList**, perform a [FindByExpression] where: { contains($currentObject/Url,$ApplicationURL) }, and call the result **$CurrentEnvironment****
4. **Decision:** "found?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
5. **Run another process: "Custom_Logging.SUB_Log_Info"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
