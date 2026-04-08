# Microflow Detailed Specification: SUB_EmailAccount_Delete

### 📥 Inputs (Parameters)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. **Delete**
2. **DB Retrieve **Email_Connector.EmailAccount**  (Result: **$EmailAccount_First**)**
3. **Maps to Page: **Email_Connector.EmailConnector_Overview****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.