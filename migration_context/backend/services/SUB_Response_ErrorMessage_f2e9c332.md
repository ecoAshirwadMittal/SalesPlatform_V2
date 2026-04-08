# Microflow Analysis: SUB_Response_ErrorMessage

### Requirements (Inputs):
- **$HttpResponse** (A record of type: System.HttpResponse)
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Import Xml**
2. **Retrieve
      - Store the result in a new variable called **$Error****
3. **Update the **$undefined** (Object):
      - Change [MicrosoftGraph.Error_Authorization] to: "$Authorization"**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
