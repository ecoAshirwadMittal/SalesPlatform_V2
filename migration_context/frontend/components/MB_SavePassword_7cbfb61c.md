# Microflow Analysis: MB_SavePassword

### Requirements (Inputs):
- **$ExampleConfiguration** (A record of type: Encryption.ExampleConfiguration)

### Execution Steps:
1. **Run another process: "Encryption.Encrypt"
      - Store the result in a new variable called **$Encrypted****
2. **Update the **$undefined** (Object):
      - Change [Encryption.ExampleConfiguration.Password] to: "$Encrypted"
      - **Save:** This change will be saved to the database immediately.**
3. **Close Form**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
