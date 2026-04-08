# Microflow Analysis: DS_GetParametersFromMicroflow

### Requirements (Inputs):
- **$Microflow** (A record of type: Object)
- **$Type** (A record of type: Object)

### Execution Steps:
1. **Java Action Call
      - Store the result in a new variable called **$ReturnValueName****
2. **Take the list **$ReturnValueName**, perform a [Filter] where: { $Type }, and call the result **$StringParameterList****
3. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
