# Microflow Analysis: ACT_ConnectionDetails_GenerateJWT

### Requirements (Inputs):
- **$ConnectionDetails** (A record of type: SnowflakeRESTSQL.ConnectionDetails)

### Execution Steps:
1. **Run another process: "SnowflakeRESTSQL.JWT_GetCreate"
      - Store the result in a new variable called **$JWT****
2. **Run another process: "SnowflakeRESTSQL.ConnectionDetails_GenerateToken_JWT"
      - Store the result in a new variable called **$Token****
3. **Update the **$undefined** (Object):
      - Change [SnowflakeRESTSQL.JWT.Token] to: "$Token"
      - Change [SnowflakeRESTSQL.JWT.ExpirationDate] to: "addMinutes([%CurrentDateTime%],59)"**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
