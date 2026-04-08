# Microflow Detailed Specification: SUB_HttpMessage_ParseFormData

### 📥 Inputs (Parameters)
- **$Key** (Type: Variable)
- **$HttpRequest** (Type: System.HttpRequest)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. **List Operation: **FindByExpression** on **$undefined** where `startsWith($currentObject/Value,$Key)` (Result: **$NewSplitItem**)**
3. **Create Variable **$Value** = `empty`**
4. **JavaCallAction**
5. 🔄 **LOOP:** For each **$IteratorSplitItem_KeyValue** in **$ParametersList_KeyValue**
   │ 1. 🔀 **DECISION:** `$IteratorSplitItem_KeyValue/Index = 1`
   │    ➔ **If [true]:**
   │       1. **Update Variable **$Value** = `$IteratorSplitItem_KeyValue/Value`**
   │    ➔ **If [false]:**
   └─ **End Loop**
6. 🔀 **DECISION:** `contains($HttpRequest/Content,$Key+'=')`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$Value`

**Final Result:** This process concludes by returning a [String] value.