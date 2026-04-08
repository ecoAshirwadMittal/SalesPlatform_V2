# Microflow Analysis: ACT_Emailtemplate_CreateToken

### Requirements (Inputs):
- **$EmailTemplate** (A record of type: Email_Connector.EmailTemplate)

### Execution Steps:
1. **Decision:** "MxObject selected?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Retrieve
      - Store the result in a new variable called **$TokenList****
3. **Create Variable**
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Decision:** "Check iterator variable"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
6. **Create Object
      - Store the result in a new variable called **$NewToken****
7. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailTemplate_Token] to: "$NewToken"**
8. **Show Page**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
