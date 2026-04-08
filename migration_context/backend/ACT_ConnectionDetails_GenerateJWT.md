# Microflow Detailed Specification: ACT_ConnectionDetails_GenerateJWT

### 📥 Inputs (Parameters)
- **$ConnectionDetails** (Type: SnowflakeRESTSQL.ConnectionDetails)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **SnowflakeRESTSQL.JWT_GetCreate** (Result: **$JWT**)**
2. **Call Microflow **SnowflakeRESTSQL.ConnectionDetails_GenerateToken_JWT** (Result: **$Token**)**
3. **Update **$JWT**
      - Set **Token** = `$Token`
      - Set **ExpirationDate** = `addMinutes([%CurrentDateTime%],59)`**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.