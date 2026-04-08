# Nanoflow: OCH_DevicePrice

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.SalesLeader

## 📥 Inputs

- **$Device** (EcoATM_PWSMDM.Device)

## ⚙️ Execution Flow

1. **Retrieve related **Price_Device_Current** via Association from **$Device** (Result: **$Price_Current**)**
2. **Retrieve related **Price_Device_Future** via Association from **$Device** (Result: **$Price_Future**)**
3. **Call Microflow **EcoATM_PWS.SUB_Notes_RetrieveOrCreate** (Result: **$DeviceNote**)**
4. **Create Variable **$Valid** = `true`**
5. **Create Variable **$Notes** = `''`**
6. 🔀 **DECISION:** `$Price_Current/ListPrice != empty and $Price_Current/ListPrice > 0`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$Price_Current/MinPrice != empty and $Price_Current/MinPrice > 0`
         ➔ **If [true]:**
            1. **Update **$DeviceNote**
      - Set **Notes** = `$Notes`**
            2. **Update **$Device**
      - Set **IsChanged** = `true`
      - Set **Device_Session** = `$CurrentSession`**
            3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update Variable **$Valid** = `false`**
            2. **Update Variable **$Notes** = `if $Notes = empty or trim($Notes) = '' then 'Min Price is Missing' else $Notes + ', Min Price is Missing'`**
            3. **Update **$DeviceNote**
      - Set **Notes** = `$Notes`**
            4. **Update **$Device**
      - Set **IsChanged** = `true`
      - Set **Device_Session** = `$CurrentSession`**
            5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update Variable **$Valid** = `false`**
      2. **Update Variable **$Notes** = `'List Price is Missing'`**
      3. 🔀 **DECISION:** `$Price_Current/MinPrice != empty and $Price_Current/MinPrice > 0`
         ➔ **If [true]:**
            1. **Update **$DeviceNote**
      - Set **Notes** = `$Notes`**
            2. **Update **$Device**
      - Set **IsChanged** = `true`
      - Set **Device_Session** = `$CurrentSession`**
            3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update Variable **$Valid** = `false`**
            2. **Update Variable **$Notes** = `if $Notes = empty or trim($Notes) = '' then 'Min Price is Missing' else $Notes + ', Min Price is Missing'`**
            3. **Update **$DeviceNote**
      - Set **Notes** = `$Notes`**
            4. **Update **$Device**
      - Set **IsChanged** = `true`
      - Set **Device_Session** = `$CurrentSession`**
            5. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
