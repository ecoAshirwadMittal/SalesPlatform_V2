# Microflow Detailed Specification: IVK_PerformTests

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **OQL.IVK_CreateContent** (Result: **$Variable**)**
2. **DB Retrieve **OQL.ExamplePerson** Filter: `[Name != empty] [OQL.MarriedTo/OQL.ExamplePerson]` (Result: **$ExamplePerson**)**
3. **JavaCallAction**
4. **JavaCallAction**
5. **JavaCallAction**
6. **JavaCallAction**
7. **JavaCallAction**
8. **JavaCallAction**
9. **JavaCallAction**
10. **JavaCallAction**
11. **Retrieve related **MarriedTo** via Association from **$ExamplePerson** (Result: **$Marriedto**)**
12. **JavaCallAction**
13. **JavaCallAction**
14. **List Operation: **Head** on **$undefined** (Result: **$ExamplePersonResult**)**
15. 🔀 **DECISION:** `$ExamplePersonResult/OQL.ExamplePersonResult_ExamplePerson = $ExamplePerson`
   ➔ **If [true]:**
      1. **Call Microflow **OQL.IVK_TestCount****
      2. **Show Message (Information): `Yay!`**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Error): `Booo!`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.