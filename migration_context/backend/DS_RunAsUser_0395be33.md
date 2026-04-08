# Microflow Analysis: DS_RunAsUser

### Requirements (Inputs):
- **$UserName** (A record of type: Object)

### Execution Steps:
1. **Search the Database for **System.User** using filter: { [Name=$UserName] } (Call this list **$User**)**
2. **Decision:** "User?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
3. **Java Action Call
      - Store the result in a new variable called **$Password****
4. **Create Variable**
5. **Search the Database for **System.UserRole** using filter: { [Name=$RunAsUserRole] } (Call this list **$UserRoleList**)**
6. **Create Object
      - Store the result in a new variable called **$NewUser****
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
