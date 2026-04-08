# Microflow Detailed Specification: ACT_Lock_Release

### 📥 Inputs (Parameters)
- **$Lock** (Type: EcoATM_Lock.Lock)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$Lock** (and Save to DB)
      - Set **Active** = `false`**
2. **Show Message (Information): `Lock has been deactivated.`**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.