# Java Action: PGPEncryptDocument

> Encrypt the FileDocument using PGP encryption. This is allowed to be the same FileDocument instance and the action will just store the encrypted file in the provided entity. The certificate must be a valid public PGP key provided by the external party. This action will either return true or an exception

**Returns:** `Boolean`

## Parameters

| Name | Type | Required |
|---|---|---|
| `ExternalPublicKey` | ConcreteEntity | ✅ |
| `DocumentToEncrypt` | ConcreteEntity | ✅ |
| `OutputDocument` | ConcreteEntity | ✅ |
