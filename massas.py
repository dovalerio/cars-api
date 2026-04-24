import requests
import random
import string

URL = "http://localhost:8080/veiculos"

brands = ["Toyota", "Honda", "Ford", "Chevrolet", "BMW"]
models = ["Corolla", "Civic", "Focus", "Onix", "X1"]
colors = ["Black", "White", "Silver", "Blue", "Red"]

def generate_plate():
    letters = ''.join(random.choices(string.ascii_uppercase, k=3))
    numbers = ''.join(random.choices(string.digits, k=4))
    return f"{letters}{numbers}"

def generate_vehicle():
    return {
        "brand": random.choice(brands),
        "model": random.choice(models),
        "year": random.randint(2015, 2024),
        "color": random.choice(colors),
        "plate": generate_plate(),
        "priceBrl": random.randint(20000, 150000)
    }

def create_vehicle(vehicle):
    response = requests.post(URL, json=vehicle)

    if response.status_code == 201:
        print(f"✔ Criado: {vehicle['plate']}")
    else:
        print(f"✘ Erro ({response.status_code}): {response.text}")

def main():
    total = 20

    for _ in range(total):
        vehicle = generate_vehicle()
        create_vehicle(vehicle)

if __name__ == "__main__":
    main()