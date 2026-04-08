# Microflow Analysis: SUB_Authorization_GetFromForm

### Requirements (Inputs):
- **$httpRequest** (A record of type: System.HttpRequest)
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Run another process: "MicrosoftGraph.SUB_HttpMessage_ParseFormData"
      - Store the result in a new variable called **$Access_Token****
2. **Run another process: "MicrosoftGraph.SUB_HttpMessage_ParseFormData"
      - Store the result in a new variable called **$Id_Token****
3. **Decision:** "Check if "Access_Token" exists"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Run another process: "MicrosoftGraph.SUB_HttpMessage_ParseFormData"
      - Store the result in a new variable called **$Token_Type****
5. **Run another process: "MicrosoftGraph.SUB_HttpMessage_ParseFormData"
      - Store the result in a new variable called **$Expires_In****
6. **Run another process: "MicrosoftGraph.SUB_HttpMessage_ParseFormData"
      - Store the result in a new variable called **$Scope****
7. **Update the **$undefined** (Object):
      - Change [MicrosoftGraph.Authorization.Access_Token] to: "$Access_Token"
      - Change [MicrosoftGraph.Authorization.Scope] to: "$Scope"
      - Change [MicrosoftGraph.Authorization.Expires_In] to: "parseInteger($Expires_In)"
      - Change [MicrosoftGraph.Authorization.Token_type] to: "$Token_Type"
      - Change [MicrosoftGraph.Authorization.Id_Token] to: "$Id_Token"
      - Change [MicrosoftGraph.Authorization.Successful] to: "true"
      - Change [MicrosoftGraph.Authorization.Ext_Expires_In] to: "parseInteger($Expires_In)"
      - **Save:** This change will be saved to the database immediately.**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
