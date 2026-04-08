# Microflow Analysis: SUB_Authorization_ProcessSuccessfulResponse

### Requirements (Inputs):
- **$Response** (A record of type: System.HttpResponse)
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Import Xml**
2. **Java Action Call**
3. **Update the **$undefined** (Object):
      - Change [MicrosoftGraph.Authorization.Successful] to: "true"
      - **Save:** This change will be saved to the database immediately.**
4. **Log Message**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
