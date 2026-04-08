# Microflow Detailed Specification: ACT_ConnectionDetails_ValidateConnectionWithJWT

### 📥 Inputs (Parameters)
- **$ConnectionDetails** (Type: SnowflakeRESTSQL.ConnectionDetails)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **SnowflakeRESTSQL.Statement** (Result: **$NewStatement**)
      - Set **SQLStatement** = `'SELECT FLOOR(2.3)'`
      - Set **Role** = `'PUBLIC'`**
2. **Call Microflow **SnowflakeRESTSQL.ConnectionDetails_GenerateToken_JWT** (Result: **$Token**)**
3. **Call Microflow **SnowflakeRESTSQL.POST_v1_ExecuteStatement** (Result: **$HttpResponseList**)**
4. 🔀 **DECISION:** `$HttpResponseList!=empty`
   ➔ **If [true]:**
      1. **Show Message (Information): `Connection to snowflake was successful`**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.