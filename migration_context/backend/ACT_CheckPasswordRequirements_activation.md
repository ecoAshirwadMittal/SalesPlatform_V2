# Microflow Detailed Specification: ACT_CheckPasswordRequirements_activation

### 📥 Inputs (Parameters)
- **$ActivateUser** (Type: EcoATM_UserManagement.ActivateUser)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$upper** = `isMatch($ActivateUser/Password, '.*(?=.*[A-Z]).*')`**
2. **Create Variable **$length** = `isMatch($ActivateUser/Password, '^.{8,}$')`**
3. **Create Variable **$special** = `isMatch($ActivateUser/Password, '.*([!@#\$%^&*()<>])*')`**
4. **Update **$ActivateUser** (and Save to DB)
      - Set **IsLengthValid** = `$length`
      - Set **HasUpperCaseLetter** = `$upper`
      - Set **HasSpecialCharacter** = `$special`**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.