# Utility: deposco_test_data.json

- **Path**: `src\utils\resources\deposco_test_data.json`
- **Category**: Utility
- **Lines**: 46
- **Size**: 1,206 bytes

## Source Code

```json
{
    "deposco": {
        "url": "https://ecoatm-ua.deposco.com/deposco/login/home",
        "credentials": {
            "default": {
                "username": "TASNEEM.AMINA",
                "password": "Eco10Feb2025!"
            }
        }
    },
    "testScenarios": {
        "integration": {
            "description": "Basic integration test - navigate to order details",
            "orderNumbers": [
                "4500368",
                "4500369",
                "4500370"
            ]
        },
        "shipOrder": {
            "description": "Ship order workflow - Post Order Import and Build Release Wave",
            "orderNumbers": [
                "5008556",
                "5008557",
                "5008558"
            ]
        },
        "smoke": {
            "description": "Quick smoke test with single order",
            "orderNumbers": [
                "5008556"
            ]
        }
    },
    "orderNumbers": {
        "integration_single": "4500368",
        "shipOrder_single": "5008556",
        "batch_test_orders": [
            "4500368",
            "4500369",
            "4500370",
            "5008556",
            "5008557"
        ]
    }
}
```
