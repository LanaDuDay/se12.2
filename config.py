from binance.client import Client

api_key = 'P3nlErUCDYzxboVzFPdAGUkN9VavD7Mq41TqOguuXAXtQGParQidTGyTH1Oh7xWW'
api_secret = 'A3FkAcHXGDwzDlAefSZZC6eIxBexy6AaCE9HM5ke4aNR8f2CmFfMfXZkYWDKtncr'

client = Client(api_key, api_secret)

# Parameters
symbol = "BTCUSDT"  # Symbol to trade
quantity = 0.01  # Quantity to trade (in BTC)
interval = "1h"  # Interval for retrieving historical price data
mean_reversion_threshold = 0.05  # Threshold for mean reversion strategy
stop_loss_threshold = -0.1  # Stop loss threshold for closing position