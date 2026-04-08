# Java Action: PGPDecryptDocument

> Encrypt the FileDocument using PGP encryption. This is allowed to be the same FileDocument instance and the action will just store the decrypted file in the entity. The certificate must be a File containing a valid PGP key ring (matching the document) and the certificate must have a passphrase entered in the attribute This action will either return true or an exception

**Returns:** `Boolean`

## Parameters

| Name | Type | Required |
|---|---|---|
| `PrivateDecryptionKey` | ConcreteEntity | ✅ |
| `DocumentToDecrypt` | ConcreteEntity | ✅ |
| `OutputDocument` | ConcreteEntity | ✅ |
