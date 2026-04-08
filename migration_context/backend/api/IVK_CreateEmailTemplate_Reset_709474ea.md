# Microflow Analysis: IVK_CreateEmailTemplate_Reset

### Execution Steps:
1. **Search the Database for **MxModelReflection.Module** using filter: { [ModuleName='ForgotPassword'] } (Call this list **$Module**)**
2. **Decision:** "Module enabled?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Java Action Call
      - Store the result in a new variable called **$none****
4. **Search the Database for **MxModelReflection.MxObjectType** using filter: { [Module='ForgotPassword']
[Name='ForgotPassword'] } (Call this list **$MxObject**)**
5. **Search the Database for **MxModelReflection.MxObjectMember** using filter: { [MxModelReflection.MxObjectMember_MxObjectType = $MxObject]
[AttributeName='EmailAddress'] } (Call this list **$MxObjectMember_Email**)**
6. **Create Object
      - Store the result in a new variable called **$NewToken_Email****
7. **Search the Database for **MxModelReflection.MxObjectMember** using filter: { [MxModelReflection.MxObjectMember_MxObjectType = $MxObject]
[AttributeName='ForgotPasswordURL'] } (Call this list **$MxObjectMember_ForgotPasswordURL**)**
8. **Create Object
      - Store the result in a new variable called **$NewToken_ResetURL****
9. **Search the Database for **MxModelReflection.MxObjectMember** using filter: { [MxModelReflection.MxObjectMember_MxObjectType = $MxObject]
[AttributeName='UserFullname'] } (Call this list **$MxObjectMember_UserFullname**)**
10. **Create Object
      - Store the result in a new variable called **$NewToken_FullName****
11. **Create Object
      - Store the result in a new variable called **$NewEmailTemplate****
12. **Show Page**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
