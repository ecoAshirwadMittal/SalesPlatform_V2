# Microflow Detailed Specification: IVK_OpenReferencedMendixObject

### 📥 Inputs (Parameters)
- **$Selection** (Type: MxModelReflection.MxObjectReference)
- **$MxObjectType** (Type: MxModelReflection.MxObjectType)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxObjectReference_MxObjectType** via Association from **$Selection** (Result: **$MendixObject**)**
2. 🔄 **LOOP:** For each **$Iterator** in **$MendixObject**
   │ 1. 🔀 **DECISION:** `$Iterator != $MxObjectType`
   │    ➔ **If [true]:**
   │       1. **Maps to Page: **MxModelReflection.MxObject_Details****
   │    ➔ **If [false]:**
   └─ **End Loop**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.