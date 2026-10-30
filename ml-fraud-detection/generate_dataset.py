import csv
import random
import uuid

# Configuration
NUM_RECORDS = 2000
FRAUD_RATIO = 0.025 # 2.5% fraud rate (imbalanced as specified)

random.seed(42)

countries = ["FR", "MA", "US", "DE", "ES", "GB", "AE"]
categories = ["GROCERY", "ELECTRONICS", "LUXURY", "TRAVEL", "RESTAURANT", "GAS_STATION", "ONLINE_GAMING"]

fieldnames = [
    "transaction_id",
    "account_id",
    "amount",
    "hour",
    "country",
    "merchant_category",
    "transactions_last_24h",
    "average_amount_last_7d",
    "is_fraud"
]

rows = []
for i in range(NUM_RECORDS):
    is_fraud = 1 if random.random() < FRAUD_RATIO else 0
    tx_id = str(uuid.uuid4())
    acc_id = str(uuid.uuid4())
    
    if is_fraud:
        amount = round(random.uniform(500.0, 3500.0), 2)
        hour = random.choice([0, 1, 2, 3, 4, 23])
        country = random.choice(["US", "AE", "GB"])
        category = random.choice(["ELECTRONICS", "LUXURY", "ONLINE_GAMING"])
        tx_24h = random.randint(8, 25)
        avg_7d = round(random.uniform(30.0, 120.0), 2)
    else:
        amount = round(random.uniform(5.0, 250.0), 2)
        hour = random.randint(7, 22)
        country = random.choice(["FR", "MA", "DE", "ES"])
        category = random.choice(["GROCERY", "RESTAURANT", "GAS_STATION", "TRAVEL"])
        tx_24h = random.randint(1, 5)
        avg_7d = round(random.uniform(20.0, 180.0), 2)

    # Inject realistic small missing values to test Imputer
    if random.random() < 0.01:
        avg_7d = ""
    if random.random() < 0.01:
        country = ""

    rows.append({
        "transaction_id": tx_id,
        "account_id": acc_id,
        "amount": amount,
        "hour": hour,
        "country": country,
        "merchant_category": category,
        "transactions_last_24h": tx_24h,
        "average_amount_last_7d": avg_7d,
        "is_fraud": is_fraud
    })

with open("ml-fraud-detection/bank_transactions.csv", "w", newline="", encoding="utf-8") as f:
    writer = csv.DictWriter(f, fieldnames=fieldnames)
    writer.writeheader()
    writer.writerows(rows)

print(f"Generated {NUM_RECORDS} transactions in ml-fraud-detection/bank_transactions.csv")
