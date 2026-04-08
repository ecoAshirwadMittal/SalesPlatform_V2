# Microflow Analysis: MB_ChangePasswordSave

### Requirements (Inputs):
- **$PasswordData** (A record of type: Encryption.PasswordData)

### Execution Steps:
1. **Decision:** "Password given?"
   - If [true] -> Move to: **Passwords match?**
   - If [false] -> Move to: **Activity**
2. **Decision:** "Passwords match?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Run another process: "Encryption.Encrypt"
      - Store the result in a new variable called **$EncryptedPassword****
4. **Retrieve
      - Store the result in a new variable called **$ExampleConfiguration****
5. **Update the **$undefined** (Object):
      - Change [Encryption.ExampleConfiguration.Password] to: "$EncryptedPassword"
      - **Save:** This change will be saved to the database immediately.**
6. **Delete**
7. **Close Form**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
