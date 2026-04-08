# Microflow Detailed Specification: SUB_Device_CreateFromDeviceTemp

### 📥 Inputs (Parameters)
- **$DeviceTemp** (Type: EcoATM_PWSMDM.DeviceTemp)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_PWS.SUB_Color_GetOrCreate** (Result: **$Color**)**
2. **Call Microflow **EcoATM_PWS.SUB_Grade_GetOrCreate** (Result: **$Grade**)**
3. **Call Microflow **EcoATM_PWS.SUB_Category_GetOrCreate** (Result: **$Category**)**
4. **Call Microflow **EcoATM_PWS.SUB_Condition_GetOrCreate** (Result: **$Condition**)**
5. **Call Microflow **EcoATM_PWS.SUB_Model_GetOrCreate** (Result: **$Model**)**
6. **Call Microflow **EcoATM_PWS.SUB_Brand_GetOrCreate** (Result: **$Brand**)**
7. **Call Microflow **EcoATM_PWS.SUB_Capacity_GetOrCreate** (Result: **$Capacity**)**
8. **Call Microflow **EcoATM_PWS.SUB_Carrier_GetOrCreate** (Result: **$Carrier**)**
9. **Create **EcoATM_PWSMDM.Device** (Result: **$NewDevice**)
      - Set **SKU** = `$DeviceTemp/SKU`
      - Set **DeviceCode** = `$DeviceTemp/DeviceCode+''`
      - Set **DeviceDescription** = `$DeviceTemp/DeviceDescription`
      - Set **AvailableQty** = `$DeviceTemp/AvailableQty`
      - Set **SearchAttr** = `$DeviceTemp/SearchAttr`
      - Set **LastUpdateDate** = `[%CurrentDateTime%]`
      - Set **Device_Brand** = `$Brand`
      - Set **Device_Capacity** = `$Capacity`
      - Set **Device_Carrier** = `$Carrier`
      - Set **Device_Category** = `$Category`
      - Set **Device_Color** = `$Color`
      - Set **Device_Condition** = `$Condition`
      - Set **Device_Grade** = `$Grade`
      - Set **Device_Model** = `$Model`
      - Set **ATPQty** = `$DeviceTemp/AvailableQty`**
10. 🏁 **END:** Return `$NewDevice`

**Final Result:** This process concludes by returning a [Object] value.