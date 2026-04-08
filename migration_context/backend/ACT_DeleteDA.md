# Microflow Detailed Specification: ACT_DeleteDA

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_DA.DAWeek**  (Result: **$DAWeekList**)**
2. **Delete**
3. **Show Message (Information): `'Delete complete'`**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.