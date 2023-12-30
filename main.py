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

@app.route('/place_order', methods=['POST'])
def place_order():
    try:
        # Nhận dữ liệu từ Android
        data = request.form
        print(data)
        symbol = data.get('symbol')
        quantity = float(data.get('quantity'))
        price = float(data.get('price'))
        print(data)

        # Thực hiện order Buy
        order_result = place_buy_order(symbol, quantity, price)
        print(order_result);
        return jsonify({"result": order_result})

    except Exception as e:
        return jsonify({"error": str(e)}), 500

# Hàm thực hiện order Buy
def place_buy_order(symbol, quantity, price):
    try:
        order = client.create_order(
            symbol=symbol,
            side=Client.SIDE_BUY,
            type=Client.ORDER_TYPE_LIMIT,
            timeInForce=Client.TIME_IN_FORCE_GTC,
            quantity=quantity,
            price=price
        )
        return order
    except Exception as e:
        return str(e)


    
@app.route('/open_orders_json', methods=['POST'])
def open_orders_json():
    # Lấy danh sách tất cả các symbols
    data = request.form
    print(data)
    exchange_info = client.get_exchange_info()
    symbols = [symbol_info['symbol'] for symbol_info in exchange_info['symbols']]

    # Lấy danh sách các lệnh giới hạn đang chờ khớp cho mỗi symbol
    open_orders_by_symbol = {}
    for symbol in symbols:
        open_orders = client.get_open_orders(symbol=symbol)
        if open_orders:
            open_orders_by_symbol[symbol] = open_orders
    print(open_orders_by_symbol)
    print("xin chao")
    # Trả về dữ liệu JSON
    return jsonify(open_orders_by_symbol)

@app.route('/buy_market', methods=['POST'])
def buy_market():
    data = request.form
    symbol = data.get('symbol')
    quantity = float(data.get('quantity'))

    try:
        order = client.create_order(
                symbol=symbol,
                side=Client.SIDE_BUY,
                type=Client.ORDER_TYPE_MARKET,
                quantity=quantity,
                recvWindow=50000  # Đặt giá trị recvWindow là 5000 mili giây (5 giây), ví dụ.
            )
        return jsonify(order)
    except Exception as e:
        return jsonify({'error': str(e)}), 400
    
def place_sell_order(symbol,quantity,price):
    try:
        order = client.create_order(
            symbol=symbol,
            side=Client.SIDE_SELL,
            type=Client.ORDER_TYPE_LIMIT,
            timeInForce=Client.TIME_IN_FORCE_GTC,
            quantity=quantity,
            price=price
        )
        return order
    except Exception as e:
        return str(e)

# Route cho gửi đơn giới hạn
@app.route('/send_limit', methods=['POST'])
def send_limit():
    try:
        # Nhận dữ liệu từ Android
        data = request.form
        print(data)
        symbol = data.get('symbol')
        quantity = float(data.get('quantity'))
        price = float(data.get('price'))
        print(data)

        # Thực hiện order Buy
        order_result = place_sell_order(symbol, quantity, price)
        print(order_result);
        return jsonify({"result": order_result})

    except Exception as e:
        return jsonify({"error": str(e)}), 500


# Route cho gửi đơn thị trường
@app.route('/send_market', methods=['POST'])
def send_market():
    data = request.form
    symbol = data.get('symbol')
    quantity = float(data.get('quantity'))

    try:
        order = client.create_order(
                symbol=symbol,
                side=Client.SIDE_SELL,
                type=Client.ORDER_TYPE_MARKET,
                quantity=quantity,
                recvWindow=50000  # Đặt giá trị recvWindow là 5000 mili giây (5 giây), ví dụ.
            )
        return jsonify(order)
    except Exception as e:
        return jsonify({'error': str(e)}), 400

if __name__ == '__main__':
    app.run(host = "0.0.0.0", debug=True)
# Main loop

