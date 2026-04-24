import requests
import copy
import random
import string

BASE_URL = "http://localhost:8080/veiculos"

# ------------------------
# Utils
# ------------------------

def assert_true(condition, message, response=None):
    if not condition:
        if response is not None:
            print("DEBUG RESPONSE:", response.status_code, response.text)
        raise Exception(f"❌ TEST FAILED: {message}")
    print(f"✔ {message}")

def generate_plate():
    return ''.join(random.choices(string.ascii_uppercase, k=3)) + \
           ''.join(random.choices(string.digits, k=4))

def generate_vehicle_payload():
    return {
        "brand": "Toyota",
        "model": "Corolla",
        "year": 2022,
        "color": "Black",
        "plate": generate_plate(),
        "priceBrl": random.randint(20000, 100000)
    }

# ------------------------
# Validators
# ------------------------

def validate_vehicle_response(data):
    required_fields = [
        "id", "brand", "model", "year",
        "color", "plate", "priceUsd"
    ]

    for field in required_fields:
        assert_true(field in data, f"Campo '{field}' presente")

def validate_error_response(data, expected_status):
    required_fields = ["timestamp", "status", "error", "message", "path"]

    for field in required_fields:
        assert_true(field in data, f"Erro contém '{field}'")

    assert_true(data["status"] == expected_status, "Status no body correto")

# ------------------------
# Tests
# ------------------------

def test_create_vehicle():
    payload = generate_vehicle_payload()

    response = requests.post(BASE_URL, json=payload)

    assert_true(response.status_code == 201, "POST retorna 201", response)

    data = response.json()
    validate_vehicle_response(data)

    return payload, data

def test_get_all():
    response = requests.get(BASE_URL)

    assert_true(response.status_code == 200, "GET retorna 200", response)

    data = response.json()

    assert_true("content" in data, "Response contém content")

    if data["content"]:
        validate_vehicle_response(data["content"][0])

def test_get_by_id(vehicle):
    response = requests.get(f"{BASE_URL}/{vehicle['id']}")

    assert_true(response.status_code == 200, "GET por ID ok", response)

    data = response.json()
    validate_vehicle_response(data)

    assert_true(data["id"] == vehicle["id"], "ID correto")

def test_duplicate_plate():
    payload = generate_vehicle_payload()

    r1 = requests.post(BASE_URL, json=payload)
    r2 = requests.post(BASE_URL, json=copy.deepcopy(payload))

    assert_true(r1.status_code == 201, "Primeiro POST ok", r1)

    validate_vehicle_response(r1.json())

    assert_true(r2.status_code == 409, "Duplicidade retorna 409", r2)

    error = r2.json()
    validate_error_response(error, 409)

    assert_true(
        "Plate" in error["message"],
        "Mensagem de duplicidade coerente"
    )

def test_validation_error():
    payload = {
        "brand": "",
        "model": "Corolla"
    }

    response = requests.post(BASE_URL, json=payload)

    assert_true(response.status_code == 400, "Validação retorna 400", response)

    error = response.json()
    validate_error_response(error, 400)

def test_not_found():
    fake_id = "00000000-0000-0000-0000-000000000000"

    response = requests.get(f"{BASE_URL}/{fake_id}")

    assert_true(response.status_code == 404, "Not found retorna 404", response)

    error = response.json()
    validate_error_response(error, 404)

# ------------------------
# Runner
# ------------------------

def main():
    print("\n🚀 INICIANDO TESTES...\n")

    payload, vehicle = test_create_vehicle()

    test_get_all()
    test_get_by_id(vehicle)
    test_duplicate_plate()
    test_validation_error()
    test_not_found()

    print("\n🎉 TODOS OS TESTES PASSARAM")

if __name__ == "__main__":
    main()