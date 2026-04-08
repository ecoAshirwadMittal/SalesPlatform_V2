# Microflow Detailed Specification: BCo_Column

### 📥 Inputs (Parameters)
- **$pColumn** (Type: ExcelImporter.Column)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **ExcelImporter.Column_SetDetails****
2. **Update **$pColumn**
      - Set **IsReferenceKey** = `if( $pColumn/MappingType = ExcelImporter.MappingType.Attribute ) then if( $pColumn/IsKey = ExcelImporter.YesNo.Yes ) then ExcelImporter.ReferenceKeyType.YesOnlyMainObject else ExcelImporter.ReferenceKeyType.NoKey else $pColumn/IsReferenceKey`**
3. **Retrieve related **Column_Template** via Association from **$pColumn** (Result: **$Template**)**
4. 🔀 **DECISION:** `VALID?`
   ➔ **If [ValidAttribute]:**
      1. **DB Retrieve **ExcelImporter.Column** Filter: `[ExcelImporter.Column_Template=$pColumn/ExcelImporter.Column_Template] [MappingType='Attribute'] [ExcelImporter.Column_MxObjectMember=$pColumn/ExcelImporter.Column_MxObjectMember] [id!=$pColumn]` (Result: **$DuplicateMapping**)**
      2. 🔀 **DECISION:** `$DuplicateMapping != empty`
         ➔ **If [false]:**
            1. **Call Microflow **ExcelImporter.prepareReferenceHandling****
            2. 🔀 **DECISION:** empty
               ➔ **If [Valid]:**
                  1. **Update **$pColumn**
      - Set **Status** = `ExcelImporter.Status.VALID`**
                  2. 🏁 **END:** Return `true`
               ➔ **If [NoInputParams]:**
                  1. **ValidationFeedback**
                  2. 🏁 **END:** Return `false`
               ➔ **If [WrongNrOfInputParams]:**
                  1. **ValidationFeedback**
                  2. 🏁 **END:** Return `false`
               ➔ **If [WrongReturnType]:**
                  1. **ValidationFeedback**
                  2. 🏁 **END:** Return `false`
               ➔ **If [(empty)]:**
                  1. 🏁 **END:** Return `true`
         ➔ **If [true]:**
            1. **Show Message (Warning): `This excel template already contains a mapping for this attribute. The other column is: {1}`**
            2. 🏁 **END:** Return `false`
   ➔ **If [NoReferenceSelected]:**
      1. **Show Message (Warning): `The reference field is mandatory when mapping type 'Reference' is selected`**
      2. 🏁 **END:** Return `false`
   ➔ **If [NoReferencedObjectSelected]:**
      1. **Show Message (Warning): `An objecttype must be selected when the mapping type 'Reference' is selected`**
      2. 🏁 **END:** Return `false`
   ➔ **If [ValidReference]:**
      1. **Call Microflow **ExcelImporter.prepareReferenceHandling****
      2. 🔀 **DECISION:** empty
         ➔ **If [Valid]:**
            1. **Update **$pColumn**
      - Set **Status** = `ExcelImporter.Status.VALID`**
            2. 🏁 **END:** Return `true`
         ➔ **If [NoInputParams]:**
            1. **ValidationFeedback**
            2. 🏁 **END:** Return `false`
         ➔ **If [WrongNrOfInputParams]:**
            1. **ValidationFeedback**
            2. 🏁 **END:** Return `false`
         ➔ **If [WrongReturnType]:**
            1. **ValidationFeedback**
            2. 🏁 **END:** Return `false`
         ➔ **If [(empty)]:**
            1. 🏁 **END:** Return `true`
   ➔ **If [NoAttributeSelected]:**
      1. **ValidationFeedback**
      2. 🏁 **END:** Return `false`
   ➔ **If [UnUsed]:**
      1. **Update **$pColumn**
      - Set **Status** = `ExcelImporter.Status.INFO`**
      2. **Call Microflow **ExcelImporter.prepareReferenceHandling****
      3. **Update **$pColumn**
      - Set **FindAttribute** = `empty`
      - Set **FindReference** = `empty`
      - Set **FindObjectType** = `empty`
      - Set **FindMicroflow** = `empty`
      - Set **Column_MxObjectType_Reference** = `empty`
      - Set **Column_MxObjectMember** = `empty`
      - Set **Column_MxObjectMember_Reference** = `empty`
      - Set **Column_MxObjectReference** = `empty`
      - Set **Column_Microflows** = `empty`
      - Set **IsKey** = `ExcelImporter.YesNo.No`
      - Set **IsReferenceKey** = `ExcelImporter.ReferenceKeyType.NoKey`
      - Set **Details** = `empty`**
      4. 🏁 **END:** Return `true`
   ➔ **If [InvalidReference]:**
      1. **Show Message (Warning): `An invalid reference was selected. The selected reference belongs to a different objectType`**
      2. 🏁 **END:** Return `false`
   ➔ **If [InvalidAttribute]:**
      1. **Show Message (Warning): `An invalid attribute was selected. The selected attribute belongs to a different objectType`**
      2. 🏁 **END:** Return `false`
   ➔ **If [InvalidReferencedObject]:**
      1. **Show Message (Warning): `An invalid objectType was selected. The selected objectType is not part of this reference`**
      2. 🏁 **END:** Return `false`
   ➔ **If [(empty)]:**
      1. **Show Message (Warning): `A mapping type must be selected before you can save this column`**
      2. 🏁 **END:** Return `false`
   ➔ **If [NoAssociationKeys]:**
      1. **Show Message (Warning): `So far there are no columns defined as key for this association. At least 1 column must be defined as a key.`**
      2. 🏁 **END:** Return `false`
   ➔ **If [InvalidAutoNumberSelection]:**
      1. **Show Message (Warning): `An 'AutoNumber' field can only be used as a key to search on, it cannot be set by the mapping`**
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.