# Nanoflow: GetURLByDeeplink

**Allowed Roles:** DeepLink.Admin, DeepLink.User

## 📥 Inputs

- **$DeepLink** (DeepLink.DeepLink)

## ⚙️ Execution Flow

1. 🔀 **DECISION:** `$DeepLink!=empty`
   ➔ **If [true]:**
      1. **Create Variable **$Path** = `@DeepLink.RequestHandlerName + '/' +$DeepLink/Name`**
      2. **Create Variable **$QueryString** = `''`**
      3. 🔀 **DECISION:** `$DeepLink/UseStringArgument`
         ➔ **If [false]:**
            1. **Create Variable **$ExampleValue** = `''`**
            2. 🔀 **DECISION:** `$DeepLink/ObjectType != empty`
               ➔ **If [true]:**
                  1. **Call Microflow **DeepLink.GetExampleObject** (Result: **$ObjectExampleValueFromDB**)**
                  2. 🔀 **DECISION:** `$ObjectExampleValueFromDB != empty`
                     ➔ **If [true]:**
                        1. **Update Variable **$ExampleValue** = `$ObjectExampleValueFromDB`**
                        2. 🔀 **DECISION:** `$ExampleValue != empty`
                           ➔ **If [true]:**
                              1. **Update Variable **$Path** = `$Path +'/' + $ExampleValue`**
                              2. **Call Microflow **DeepLink.SUB_GetApplicationURL** (Result: **$NewURL**)**
                              3. **Update Variable **$NewURL** = `$NewURL + $Path + $QueryString`**
                              4. **Create **DeepLink.DeepLinkURL** (Result: **$NewDeepLinkURL**)
      - Set **URL** = `$NewURL`
      - Set **Path** = `$Path`
      - Set **QueryString** = `$QueryString`
      - Set **DeepLinkURL_DeepLink** = `$DeepLink`**
                              5. 🏁 **END:** Return `$NewDeepLinkURL`
                           ➔ **If [false]:**
                              1. **Update Variable **$Path** = `$Path`**
                              2. **Call Microflow **DeepLink.SUB_GetApplicationURL** (Result: **$NewURL**)**
                              3. **Update Variable **$NewURL** = `$NewURL + $Path + $QueryString`**
                              4. **Create **DeepLink.DeepLinkURL** (Result: **$NewDeepLinkURL**)
      - Set **URL** = `$NewURL`
      - Set **Path** = `$Path`
      - Set **QueryString** = `$QueryString`
      - Set **DeepLinkURL_DeepLink** = `$DeepLink`**
                              5. 🏁 **END:** Return `$NewDeepLinkURL`
                     ➔ **If [false]:**
                        1. **Update Variable **$ExampleValue** = `'[value]'`**
                        2. 🔀 **DECISION:** `$ExampleValue != empty`
                           ➔ **If [true]:**
                              1. **Update Variable **$Path** = `$Path +'/' + $ExampleValue`**
                              2. **Call Microflow **DeepLink.SUB_GetApplicationURL** (Result: **$NewURL**)**
                              3. **Update Variable **$NewURL** = `$NewURL + $Path + $QueryString`**
                              4. **Create **DeepLink.DeepLinkURL** (Result: **$NewDeepLinkURL**)
      - Set **URL** = `$NewURL`
      - Set **Path** = `$Path`
      - Set **QueryString** = `$QueryString`
      - Set **DeepLinkURL_DeepLink** = `$DeepLink`**
                              5. 🏁 **END:** Return `$NewDeepLinkURL`
                           ➔ **If [false]:**
                              1. **Update Variable **$Path** = `$Path`**
                              2. **Call Microflow **DeepLink.SUB_GetApplicationURL** (Result: **$NewURL**)**
                              3. **Update Variable **$NewURL** = `$NewURL + $Path + $QueryString`**
                              4. **Create **DeepLink.DeepLinkURL** (Result: **$NewDeepLinkURL**)
      - Set **URL** = `$NewURL`
      - Set **Path** = `$Path`
      - Set **QueryString** = `$QueryString`
      - Set **DeepLinkURL_DeepLink** = `$DeepLink`**
                              5. 🏁 **END:** Return `$NewDeepLinkURL`
               ➔ **If [false]:**
                  1. **Update Variable **$Path** = `$Path`**
                  2. **Call Microflow **DeepLink.SUB_GetApplicationURL** (Result: **$NewURL**)**
                  3. **Update Variable **$NewURL** = `$NewURL + $Path + $QueryString`**
                  4. **Create **DeepLink.DeepLinkURL** (Result: **$NewDeepLinkURL**)
      - Set **URL** = `$NewURL`
      - Set **Path** = `$Path`
      - Set **QueryString** = `$QueryString`
      - Set **DeepLinkURL_DeepLink** = `$DeepLink`**
                  5. 🏁 **END:** Return `$NewDeepLinkURL`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$DeepLink/SeparateGetParameters`
               ➔ **If [true]:**
                  1. **Call Microflow **DeepLink.GetMicroflowInputParametersAsQueryString** (Result: **$ParametersAsString**)**
                  2. **Update Variable **$QueryString** = `(if length($ParametersAsString) > 0 then '?' else '' ) + $ParametersAsString`**
                  3. **Call Microflow **DeepLink.SUB_GetApplicationURL** (Result: **$NewURL**)**
                  4. **Update Variable **$NewURL** = `$NewURL + $Path + $QueryString`**
                  5. **Create **DeepLink.DeepLinkURL** (Result: **$NewDeepLinkURL**)
      - Set **URL** = `$NewURL`
      - Set **Path** = `$Path`
      - Set **QueryString** = `$QueryString`
      - Set **DeepLinkURL_DeepLink** = `$DeepLink`**
                  6. 🏁 **END:** Return `$NewDeepLinkURL`
               ➔ **If [false]:**
                  1. **Call Microflow **DeepLink.SUB_GetApplicationURL** (Result: **$NewURL**)**
                  2. **Update Variable **$NewURL** = `$NewURL + $Path + $QueryString`**
                  3. **Create **DeepLink.DeepLinkURL** (Result: **$NewDeepLinkURL**)
      - Set **URL** = `$NewURL`
      - Set **Path** = `$Path`
      - Set **QueryString** = `$QueryString`
      - Set **DeepLinkURL_DeepLink** = `$DeepLink`**
                  4. 🏁 **END:** Return `$NewDeepLinkURL`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `empty`

## ⚠️ Error Handling

- On error in **Call Microflow **DeepLink.GetExampleObject** (Result: **$ObjectExampleValueFromDB**)** → Update Variable **$ExampleValue** = `'<Error>'`

## 🏁 Returns
`Object`
