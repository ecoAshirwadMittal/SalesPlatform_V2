# Microflow Analysis: ACT_EmailTemplate_Save

### Requirements (Inputs):
- **$EmailTemplate** (A record of type: Email_Connector.EmailTemplate)

### Execution Steps:
1. **Run another process: "Email_Connector.VAL_EmailTemplateRecipients"
      - Store the result in a new variable called **$Valid****
2. **Decision:** "TemplateName filled?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Decision:** "UseOnlyPlainText?"
   - If [false] -> Move to: **HTML Body Filled
**
   - If [true] -> Move to: **Finish**
4. **Decision:** "HTML Body Filled
"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
5. **Change Variable**
6. **Show Message**
7. **Validation Feedback**
8. **Decision:** "Plain Body filled in"
   - If [false] -> Move to: **PlainText?**
   - If [true] -> Move to: **Finish**
9. **Decision:** "PlainText?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
10. **Change Variable**
11. **Show Message**
12. **Validation Feedback**
13. **Java Action Call
      - Store the result in a new variable called **$ParsedPlainString****
14. **Decision:** "PlainBodyOnlySpaces"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **PlainText?**
15. **Decision:** "Email template valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
16. **Retrieve
      - Store the result in a new variable called **$TokenList****
17. **Permanently save **$undefined** to the database.**
18. **Retrieve
      - Store the result in a new variable called **$AttachmentList****
19. **Permanently save **$undefined** to the database.**
20. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailTemplate.hasAttachment] to: "$AttachmentList!=empty"**
21. **Permanently save **$undefined** to the database.**
22. **Close Form**
23. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
