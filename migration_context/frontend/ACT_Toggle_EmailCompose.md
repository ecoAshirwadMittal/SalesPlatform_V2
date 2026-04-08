# Nanoflow: ACT_Toggle_EmailCompose

**Allowed Roles:** Email_Connector.EmailConnectorAdmin

## 📥 Inputs

- **$EmailAccount** (Email_Connector.EmailAccount)

## ⚙️ Execution Flow

1. 🔀 **DECISION:** `$EmailAccount/isOutgoingEmailConfigured`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$EmailAccount/ComposeEmail`
         ➔ **If [true]:**
            1. **Update **$EmailAccount**
      - Set **ComposeEmail** = `false`**
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update **$EmailAccount**
      - Set **ComposeEmail** = `true`**
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Warning): `Outgoing email configuration not found for email '{1}'`**
      2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
