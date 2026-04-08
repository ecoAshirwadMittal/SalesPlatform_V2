# Nanoflow: OCH_Authentication_SelectedResponseType

**Allowed Roles:** MicrosoftGraph.Administrator

## 📥 Inputs

- **$Authentication** (MicrosoftGraph.Authentication)

## ⚙️ Execution Flow

1. **Retrieve related **SelectedResponseType** via Association from **$Authentication** (Result: **$StringArrayWrapper_SelectedResponseType**)**
2. 🔀 **DECISION:** `contains($StringArrayWrapper_SelectedResponseType/Value,'id_token')`
   ➔ **If [true]:**
      1. **Retrieve related **Response_modes_supported** via Association from **$Authentication** (Result: **$StringArrayWrapper_ResponseModesSupported**)**
      2. **Retrieve related **StringArrayWrapper_StringArray** via Association from **$StringArrayWrapper_ResponseModesSupported** (Result: **$StringArrayWrapperList**)**
      3. **List Operation: **Find** on **$StringArrayWrapperList** where `'form_post'` (Result: **$SelectedResponseMode**)**
      4. **Update **$Authentication**
      - Set **SelectedResponseMode** = `$SelectedResponseMode`**
      5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$StringArrayWrapper_SelectedResponseType/Value = 'code'`
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Retrieve related **Response_modes_supported** via Association from **$Authentication** (Result: **$StringArrayWrapper_ResponseModesSupported_1**)**
            2. **Retrieve related **StringArrayWrapper_StringArray** via Association from **$StringArrayWrapper_ResponseModesSupported_1** (Result: **$StringArrayWrapperList_1**)**
            3. **List Operation: **Find** on **$StringArrayWrapperList_1** where `'query'` (Result: **$SelectedResponseMode_Query**)**
            4. **Update **$Authentication**
      - Set **SelectedResponseMode** = `$SelectedResponseMode_Query`**
            5. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
