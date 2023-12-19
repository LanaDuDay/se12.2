from config import *
from functions import *
import math

# Main loop
while True:
    try:
        # Retrieve historical price data
        historical_data = get_historical_data()

        # Calculate mean and standard deviation
        mean, stdev = calculate_statistics(historical_data)

        # Get current price
        price = get_current_price()

        print(is_below_mean(price, mean, stdev, mean_reversion_threshold))
        print(is_above_mean(price, mean, stdev, mean_reversion_threshold))
        
        # Check if price is below mean by threshold
        if is_below_mean(price, mean, stdev, mean_reversion_threshold):
            # Place buy order
            account_info = client.get_account(recvWindow=50000)
            balances = account_info['balances']

            # Print the quantity of each coin
            for balance in balances:
                asset = balance['asset']
                free = float(balance['free'])
                locked = float(balance['locked'])
                total = free + locked

                # Print the amount of BTC, MLN, ETH, USDT
                if asset in ['BTC', 'MLN', 'ETH', 'USDT'] and total > 0:
                    print(f"{asset}: {total}")

            print(f"Placing buy order at {price:.2f}")
            order = client.create_order(
                symbol=symbol,
                side=Client.SIDE_BUY,
                type=Client.ORDER_TYPE_MARKET,
                quantity=quantity,
                recvWindow=50000  # Đặt giá trị recvWindow là 5000 mili giây (5 giây), ví dụ.
            )
            print(order)

            # Wait for order to fill
            while True:
                order_status = client.get_order(
                    symbol=symbol,
                    orderId=order["orderId"],
                    recvWindow=50000 
                )

                if order_status["status"] == "FILLED":
                    print(f"Buy order filled at {order_status['cummulativeQuoteQty']}")
                    break
                else:
                    print("Waiting for order to fill...")
                    time.sleep(1)

            # Place stop loss order
            stop_loss_price = round(float(price) * (1 + stop_loss_threshold), 2)
            
            order = client.create_order(
                symbol=symbol,
                side='SELL',  # Đặt lệnh bán để thiết lập stop-loss
                type='STOP_LOSS_LIMIT',
                timeInForce='GTC',  # Good 'til Cancel
                quantity=0.001,  # Số lượng cần bán
                price=stop_loss_price,  # Giá stop-loss
                stopPrice=round(stop_loss_price * 0.98,2),  # Giá stop-loss
                recvWindow=50000,
            )

            print(f"Stop loss order placed at {stop_loss_price}")

        # Check if price is above mean by threshold
        elif is_above_mean(price, mean, stdev, mean_reversion_threshold):
            # Place sell order
            print(f"Placing sell order at {price:.2f}")

            account_info = client.get_account(recvWindow=50000)
            balances = account_info['balances']
            for balance in balances:
                asset = balance['asset']
                free = float(balance['free'])
                locked = float(balance['locked'])
                total = free + locked

                # Print the amount of BTC, MLN, ETH, USDT
                if asset in ['BTC', 'MLN', 'ETH', 'USDT'] and total > 0:
                    print(f"{asset}: {total}")
            order = client.create_order(
                symbol=symbol,
                side=Client.SIDE_SELL,
                type=Client.ORDER_TYPE_MARKET,
                quantity=quantity,
                recvWindow=50000  # Đặt giá trị recvWindow là 5000 mili giây (5 giây), ví dụ.
            )
            print(order)

            # Wait for order to fill
            while True:
                order_status = client.get_order(
                    symbol=symbol,
                    orderId=order["orderId"],
                    recvWindow=50000
                )

                if order_status["status"] == "FILLED":
                    print(f"Sell order filled at {order_status['price']}")
                    break
                else:
                    print("Waiting for order to fill...")
                    time.sleep(1)

            stop_loss_price = round(float(price) * (1 + stop_loss_threshold), 2)

            # Tạo đơn đặt hàng với giá mới
            order = client.create_order(
                symbol=symbol,
                side='SELL',  # Đặt lệnh bán để thiết lập stop-loss
                type='STOP_LOSS_LIMIT',
                timeInForce='GTC',  # Good 'til Cancel
                quantity=0.001,  # Số lượng cần bán
                price=stop_loss_price,  # Giá stop-loss
                stopPrice=round(stop_loss_price * 0.98,2),  # Giá stop-loss
                recvWindow=50000,
            )

            print(f"Stop loss order placed at {stop_loss_price}")
        else:
            print("Không thực hiện mua bán trong interval này")
        # Wait for the next interval
        time.sleep(3600)

    except binance.exceptions.BinanceAPIException as e:
        if e.code == -1021:
            print("Timestamp error. Retrying...")
            time.sleep(10)
        else:
            print(f"An error occurred: {e}")
            time.sleep(60)
    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        time.sleep(60)
