# Microflow Analysis: ACT_EmailAccount_SaveEmailSecurityConfiguration

### Requirements (Inputs):
- **$EmailAccount** (A record of type: Email_Connector.EmailAccount)

### Execution Steps:
1. **Decision:** "isP12Configured?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **CertificateProvided?**
2. **Update the **$undefined** (Object):
      - Change [Email_Connector.Pk12Certificate_EmailAccount] to: "empty"**
3. **Decision:** "LDAPConigured?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Retrieve
      - Store the result in a new variable called **$LDAPConfiguration****
5. **Decision:** "BaseDNEmpty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Validation Feedback**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
