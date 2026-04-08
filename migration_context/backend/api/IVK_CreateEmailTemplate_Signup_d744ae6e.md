# Microflow Analysis: IVK_CreateEmailTemplate_Signup

### Requirements (Inputs):
- **$Configuration** (A record of type: ForgotPassword.Configuration)

### Execution Steps:
1. **Decision:** "already has email template"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
2. **Search the Database for **MxModelReflection.Module** using filter: { [ModuleName='ForgotPassword'] } (Call this list **$Module**)**
3. **Decision:** "Module enabled?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Java Action Call
      - Store the result in a new variable called **$none****
5. **Search the Database for **MxModelReflection.MxObjectType** using filter: { [Module='ForgotPassword']
[Name='ForgotPassword'] } (Call this list **$MxObject**)**
6. **Search the Database for **MxModelReflection.MxObjectMember** using filter: { [MxModelReflection.MxObjectMember_MxObjectType = $MxObject]
[AttributeName='EmailAddress'] } (Call this list **$MxObjectMember_Email**)**
7. **Create Object
      - Store the result in a new variable called **$NewToken_Email****
8. **Search the Database for **MxModelReflection.MxObjectMember** using filter: { [MxModelReflection.MxObjectMember_MxObjectType = $MxObject]
[AttributeName='ForgotPasswordURL'] } (Call this list **$MxObjectMember_ForgotPasswordURL**)**
9. **Create Object
      - Store the result in a new variable called **$NewToken_ResetURL****
10. **Search the Database for **MxModelReflection.MxObjectMember** using filter: { [MxModelReflection.MxObjectMember_MxObjectType = $MxObject]
[AttributeName='UserFullname'] } (Call this list **$MxObjectMember_UserFullname**)**
11. **Create Object
      - Store the result in a new variable called **$NewToken_FullName****
12. **Create Object
      - Store the result in a new variable called **$NewEmailTemplate****
13. **Update the **$undefined** (Object):
      - Change [ForgotPassword.Configuration_EmailTemplate_Signup] to: "$NewEmailTemplate"
      - **Save:** This change will be saved to the database immediately.**
14. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
