# Page: SavePasswordExample_NewEdit

**Allowed Roles:** Encryption.User

**Layout:** `Encryption.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🔤 **Text**: "Title"
  - 🔤 **Text**: "Username"
  - 🔤 **Text**: "Password"
    ↳ [acti] → **Cancel Changes**
    ↳ [acti] → **Microflow**: `Encryption.MB_SavePassword`
