# Microflow Analysis: SUB_HttpResponse_ValidateToken

### Requirements (Inputs):
- **$httpResponse** (A record of type: System.HttpResponse)
- **$validationToken** (A record of type: Object)

### Execution Steps:
1. **Decision:** "Check if "$validationToken" exists"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Log Message**
3. **Update the **$undefined** (Object):
      - Change [System.HttpMessage.Content] to: "urlDecode($validationToken)"**
4. **Create Object
      - Store the result in a new variable called **$NewHttpHeader****
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
