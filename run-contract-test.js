const BASE_URL = "http://localhost:8080";

function randomPlate() {
    return "TEST" + Math.floor(Math.random() * 100000);
}

function formatBodyForLog(body) {
    if (!body || !body.trim()) {
        return null;
    }

    try {
        return JSON.stringify(JSON.parse(body), null, 2);
    } catch {
        return body;
    }
}

async function login(username, password) {
    const res = await fetch(`${BASE_URL}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
    });

    const body = await res.text();
    const data = body ? JSON.parse(body) : null;

    if (!res.ok) {
        const formattedBody = formatBodyForLog(body);
        throw new Error(`Login failed (${username}) - status ${res.status}${formattedBody ? `\nBody:\n${formattedBody}` : ""}`);
    }

    return data.token;
}

async function request(name, options, expectedStatus) {
    const res = await fetch(options.url, options);
    const body = await res.text();

    const ok = res.status === expectedStatus;

    console.log(`\n[${name}]`);
    console.log(`Status: ${res.status} (expected ${expectedStatus})`);
    const formattedBody = formatBodyForLog(body);
    if (formattedBody) {
        console.log("Body:");
        console.log(formattedBody);
    }
    console.log(ok ? "OK" : "FAIL");

    if (!ok) {
        process.exit(1);
    }

    return { status: res.status, body };
}

async function run() {
    try {
        console.log("=== LOGIN ===");

        const adminToken = await login("admin", "admin123");
        const userToken = await login("user", "user123");

        let vehicleId = null;
        const plate = randomPlate();

        console.log("\n=== VEHICLE TESTS ===");

        // CREATE (ADMIN)
        const createRes = await request(
            "ADMIN cria veículo",
            {
                url: `${BASE_URL}/veiculos`,
                method: "POST",
                headers: {
                    Authorization: `Bearer ${adminToken}`,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    brand: "Toyota",
                    model: "Corolla",
                    year: 2022,
                    color: "Black",
                    plate,
                    priceBrl: 100000
                })
            },
            201
        );

        const createdVehicle = JSON.parse(createRes.body);
        vehicleId = createdVehicle.id;

        // USER NÃO PODE CRIAR
        await request(
            "USER não pode criar (403)",
            {
                url: `${BASE_URL}/veiculos`,
                method: "POST",
                headers: {
                    Authorization: `Bearer ${userToken}`,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    brand: "Honda",
                    model: "Civic",
                    year: 2023,
                    color: "White",
                    plate: randomPlate(),
                    priceBrl: 120000
                })
            },
            403
        );

        // LIST
        await request(
            "USER lista veículos",
            {
                url: `${BASE_URL}/veiculos`,
                method: "GET",
                headers: {
                    Authorization: `Bearer ${userToken}`
                }
            },
            200
        );

        // GET BY ID
        await request(
            "Busca por ID",
            {
                url: `${BASE_URL}/veiculos/${vehicleId}`,
                method: "GET",
                headers: {
                    Authorization: `Bearer ${userToken}`
                }
            },
            200
        );

        // UPDATE (PUT)
        await request(
            "ADMIN atualiza veículo",
            {
                url: `${BASE_URL}/veiculos/${vehicleId}`,
                method: "PUT",
                headers: {
                    Authorization: `Bearer ${adminToken}`,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    brand: "Honda",
                    model: "Civic",
                    year: 2023,
                    color: "White",
                    plate,
                    priceBrl: 120000
                })
            },
            200
        );

        // PATCH
        await request(
            "ADMIN patch veículo",
            {
                url: `${BASE_URL}/veiculos/${vehicleId}`,
                method: "PATCH",
                headers: {
                    Authorization: `Bearer ${adminToken}`,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    color: "Red"
                })
            },
            200
        );

        // DELETE
        await request(
            "ADMIN remove veículo",
            {
                url: `${BASE_URL}/veiculos/${vehicleId}`,
                method: "DELETE",
                headers: {
                    Authorization: `Bearer ${adminToken}`
                }
            },
            204
        );

        // GET AFTER DELETE (deve falhar)
        await request(
            "Busca após delete (404)",
            {
                url: `${BASE_URL}/veiculos/${vehicleId}`,
                method: "GET",
                headers: {
                    Authorization: `Bearer ${userToken}`
                }
            },
            404
        );

        // SEM TOKEN
        await request(
            "Sem token (401)",
            {
                url: `${BASE_URL}/veiculos`,
                method: "GET"
            },
            401
        );

        console.log("\n=== TODOS OS TESTES PASSARAM ===");

    } catch (err) {
        console.error("\nERRO GERAL:", err.message);
        process.exit(1);
    }
}

run();