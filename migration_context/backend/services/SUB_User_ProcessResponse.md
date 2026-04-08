# Microflow Detailed Specification: SUB_User_ProcessResponse

### 📥 Inputs (Parameters)
- **$Request** (Type: MicrosoftGraph.Response)
- **$UserList** (Type: MicrosoftGraph.User)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Value_Response** via Association from **$Request** (Result: **$Value**)**
2. **Retrieve related **User_Value** via Association from **$Value** (Result: **$UserList_Result**)**
3. **Add **$$UserList_Result** to/from list **$UserList****
4. **AggregateList**
5. **LogMessage**
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.