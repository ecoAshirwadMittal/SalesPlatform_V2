# Microflow Detailed Specification: SUB_SendUserLoginToSnowflake

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'SendUserLoginToSnowflake'`**
2. **Create Variable **$Description** = `'Send User Login to Snowflake'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **Create Variable **$jdbc** = `@EcoATM_PO.Snowflake_DBSource + '&user='+@EcoATM_PO.Snowflake_DBUsername+'&password='+ @EcoATM_PO.Snowflake_DBPassword`**
5. **Create Variable **$Email** = `if $EcoATMDirectUser/Email!=empty then $EcoATMDirectUser/Email else 'System'`**
6. **Create Variable **$Date** = `formatDateTime([%CurrentDateTime%], 'yyyy-MM-dd HH:mm:ss')`**
7. **Create Variable **$Query** = `'INSERT INTO '+@EcoATM_PWS.SnowflakeEnvironmentDB+'.AUCTIONS.USERLOGIN (EMAIL, LOGIN_TIME) VALUES ('''+$Email+''','''+$Date+''')'`**
8. **JavaCallAction**
9. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
10. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.