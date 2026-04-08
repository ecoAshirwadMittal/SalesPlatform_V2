# Nanoflow: OCh_RegistrationWizard_ApplicationUrl

**Allowed Roles:** DocumentGeneration.Administrator

## 📥 Inputs

- **$RegistrationWizard** (DocumentGeneration.RegistrationWizard)

## ⚙️ Execution Flow

1. 🔀 **DECISION:** `$RegistrationWizard/ApplicationUrl != empty`
   ➔ **If [true]:**
      1. **Create Variable **$TrimmedApplicationUrl** = `trim($RegistrationWizard/ApplicationUrl)`**
      2. **Create Variable **$UrlWithoutTrailingSlash** = `if endsWith($TrimmedApplicationUrl, '/') then substring($TrimmedApplicationUrl, 0, length($TrimmedApplicationUrl) - 1) else $TrimmedApplicationUrl`**
      3. **Update **$RegistrationWizard**
      - Set **ApplicationUrl** = `$UrlWithoutTrailingSlash`**
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
