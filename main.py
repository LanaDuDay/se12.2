from flask import Flask, jsonify, request
from config import *
from functions import *
import math

app = Flask(__name__)
@app.route('/start_auto_trade', methods=['POST'])
def start_auto_trade():
    try:
        data = request.form
        print(data)
        # Retrieve historical price data
        historical_data = get_historical_data()

        # Calculate mean and standard deviation
        mean, stdev = calculate_statistics(historical_data)

        # Get current price
        price = get_current_price()

        # Check if price is below mean by threshold
        if is_below_mean(price, mean, stdev, mean_reversion_threshold):
            # Place buy order
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
            print(order)

            print(f"Stop loss order placed at {stop_loss_price}")

        # Check if price is above mean by threshold
        elif is_above_mean(price, mean, stdev, mean_reversion_threshold):
            # Place sell order
            print(f"Placing sell order at {price:.2f}")

            
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
                    print(f"Sell order filled at {order_status['price']}")
                    break
                else:
                    print("Waiting for order to fill...")
                    time.sleep(1)

            # Tính toán giá bán mới
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
            print(order)

            print(f"Stop loss order placed at {stop_loss_price}")
        else:
            print("Không thực hiện mua bán trong interval này")
        return jsonify({'status': 'success', 'order': order, 'message': 'Trade executed successfully'})

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
        return jsonify({"message": "Chu kỳ giao dịch tự động đã bắt đầu"}), 200
    except Exception as e:
        return jsonify({"error": f"An error occurred: {str(e)}"}), 500

@app.route('/get_account_info', methods=['GET'])
def get_account_info():
    try:
        # Thực hiện logic lấy thông tin tài khoản ở đây
        # Ví dụ trả về một JSON đơn giản
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

        return jsonify(account_info), 200
    except Exception as e:
        return jsonify({"error": f"An error occurred: {str(e)}"}), 500
    
@app.route('/get_prices', methods=['GET'])
def get_prices():
    symbol_list = ['BTCUSDT', 'BNBUSDT', 'ETHUSDT', 'ADAUSDT', 'XRPUSDT', 'SOLUSDT', 'DOTUSDT', 'DOGEUSDT', 'AVAXUSDT', 'LUNAUSDT']
    prices = []
    for symbol in symbol_list:
        ticker = client.get_ticker(symbol=symbol)
        price = float(ticker['lastPrice'])
        prices.append({"token": symbol, "price": price})

    return jsonify({"PriceCoin": prices})

@app.route('/trade_history', methods=['POST'])
def trade_history():
    try:
        # Lấy dữ liệu từ request JSON
        symbol = request.form.get('symbol', 'BTCUSDT')
        trades = client.get_my_trades(symbol=symbol)

        # Chuẩn bị dữ liệu để in ra
        history_data = []
        for trade in trades:
            history_data.append({
                'symbol': trade['symbol'],
                'orderId': trade['orderId'],
                'price': float(trade['price']),
                'quantity': float(trade['qty']),
                'commission': float(trade['commission']),
                'time': trade['time'],
                'isBuyer': trade['isBuyer']
            })

        return jsonify({'status': 'success', 'trade_history': history_data})
    except Exception as e:
        return jsonify({'status': 'error', 'message': f'Error fetching trade history: {str(e)}'})


if __name__ == '__main__':
    app.run(host = "0.0.0.0", debug=True)
# Main loop

