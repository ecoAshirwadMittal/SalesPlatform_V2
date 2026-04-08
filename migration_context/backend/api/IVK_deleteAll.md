# Microflow Detailed Specification: IVK_deleteAll

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **MxModelReflection.MxObjectType**  (Result: **$allObjTypes**)**
2. **Delete**
3. **DB Retrieve **MxModelReflection.Microflows**  (Result: **$allMicroflows**)**
4. **Delete**
5. **DB Retrieve **MxModelReflection.ValueType**  (Result: **$allValueTypes**)**
6. **Delete**
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.