# Page: RedirectPage

**Allowed Roles:** ForgotPassword.Administrator, ForgotPassword.Guest_ResetPassword, ForgotPassword.Guest_SignUp

**Layout:** `ForgotPassword.xAnonymousUserLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - ⚡ **Button**: Login Now [Style: Default] [Style: `padding-right:15px;`]
    ↳ [acti] → **Nanoflow**: `ForgotPassword.RefreshToSigninURL`
  - 🧩 **HTMLSnippet** (ID: `HTMLSnippet.widget.HTMLSnippet`)
      - contenttype: jsjQuery
      - contents: function doReload() { setTimeout(function(){ $(".mx-name-loginbtn").click(); }, 800); } doReload();
