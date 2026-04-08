# Java Action: XSSSanitize

> Removes all potential dangerous HTML from a string so that it can be safely displayed in a browser. This function should be applied to all HTML which is displayed in the browser, and can be entered by (untrusted) users. - HTML: The html to sanitize - policy1... policy6: one or more values of SanitizerPolicy. You may leave these policy parameters empty if you don't want to allow additional elements. BLOCKS: Allows common block elements including <p>, <h1>, etc. FORMATTING: Allows common formatting elements including <b>, <i>, etc. IMAGES: Allows <img> elements from HTTP, HTTPS, and relative sources. LINKS: Allows HTTP, HTTPS, MAILTO and relative links STYLES: Allows certain safe CSS properties in style="..." attributes. TABLES: Allows commons table elements. For more information, visit: http://javadoc.io/doc/com.googlecode.owasp-java-html-sanitizer/owasp-java-html-sanitizer/20180219.1

**Returns:** `String`

## Parameters

| Name | Type | Required |
|---|---|---|
| `html` | String | ✅ |
| `policy1` | Enumeration | ✅ |
| `policy2` | Enumeration | ✅ |
| `policy3` | Enumeration | ✅ |
| `policy4` | Enumeration | ✅ |
| `policy5` | Enumeration | ✅ |
| `policy6` | Enumeration | ✅ |
