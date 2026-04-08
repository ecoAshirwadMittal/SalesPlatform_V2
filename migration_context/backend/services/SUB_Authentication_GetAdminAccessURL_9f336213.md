# Microflow Analysis: SUB_Authentication_GetAdminAccessURL

### Requirements (Inputs):
- **$Authentication** (A record of type: MicrosoftGraph.Authentication)

### Execution Steps:
1. **Run another process: "MicrosoftGraph.SUB_Authorization_GetOrCreate"
      - Store the result in a new variable called **$Authorization****
2. **Java Action Call
      - Store the result in a new variable called **$URL****
3. **Create Variable**
4. **Create Variable**
5. **Permanently save **$undefined** to the database.**
6. **Permanently save **$undefined** to the database.**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
