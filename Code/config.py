from binance.client import Client

api_key = 'eifB4BAvzSGJYjZwUe0cwrxwCDoiesdnxOnGsO7MSCiOBxcpO8Ferl25kVLVLrcS'
api_secret = 'BrJsu91t9DCi6z3Dmab8x4M7bbQ5T5Cu9mNyDvu5yg0a7uGOWviiHP71f6eGXcti'
client = Client(api_key, api_secret, testnet=True)

# Parameters
symbol = "BTCUSDT"  # Symbol to trade
quantity = 0.001  # Quantity to trade (in BTC)
interval = "1h"  # Interval for retrieving historical price data
mean_reversion_threshold = 0.05  # Threshold for mean reversion strategy
stop_loss_threshold = -0.1  # Stop loss threshold for closing position