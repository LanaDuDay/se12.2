from flask import Flask, jsonify, request
from config import *
from functions import *
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_squared_error
from binance.client import Client
from datetime import datetime, timedelta
from sklearn.impute import SimpleImputer
from scipy.interpolate import lagrange
from io import BytesIO, StringIO
import base64

import time

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
            return jsonify({'status': 'success', 'order': order, 'message': 'Không thực hiện mua bán trong interval này'})
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

        # Danh sách cặp giao dịch bạn quan tâm
        target_assets = ['BTC', 'BNB', 'ETH', 'ADAU', 'XRP', 'SOLU', 'DOT', 'DOGE', 'AVAX', 'LUNA']

        # Tạo một danh sách để lưu thông tin về các cặp giao dịch
        result = []

        for balance in balances:
            asset = balance['asset']
            
            # Kiểm tra xem asset có trong danh sách quan tâm không
            if asset in target_assets:
                free = float(balance['free'])
                locked = float(balance['locked'])
                
                # Tạo symbol từ asset
                symbol = asset + 'USDT'
                
                # Lấy thông tin giá của cặp giao dịch
                ticker = client.get_ticker(symbol=symbol)
                price = float(ticker['lastPrice'])
                
                # Tạo đối tượng JSON cho mỗi cặp giao dịch
                asset_info = {
                    'symbol': str(asset),
                    'quantity': free + locked,
                    'price': price
                }
                
                # Thêm vào danh sách kết quả
                result.append(asset_info)

        # Trả về JSON response
        return jsonify({"PriceCoin":result}), 200

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
    # Danh sách 10 symbols cần lấy
    selected_symbols = ['BTCUSDT', 'BNBUSDT', 'ETHUSDT', 'ADAUSDT', 'XRPUSDT', 'SOLUSDT', 'DOTUSDT', 'DOGEUSDT', 'AVAXUSDT', 'LUNAUSDT']

    # Lấy danh sách các lệnh giới hạn đang chờ khớp cho mỗi symbol
    open_orders_by_symbol = {}
    for symbol in selected_symbols:
        open_orders = client.get_open_orders(symbol=symbol)
        if open_orders:
            open_orders_by_symbol[symbol] = open_orders

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
    
@app.route('/get_hourly_data', methods=['POST'])
def get_hourly_data():
    # Lấy dữ liệu từ request
    data = request.form
    print(data)
    current_date = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    # Convert end date to timestamp
    end_timestamp = pd.to_datetime(current_date).timestamp() * 1000

    # Initialize an empty DataFrame to store the data
    df = pd.DataFrame()

    # Define the interval for each request (1 month)
    interval = timedelta(days=2)

    # Make requests in chunks
    current_start = end_timestamp - interval.total_seconds() * 1000
    while current_start < end_timestamp:
        current_end = min(current_start + interval.total_seconds() * 1000, end_timestamp)

        # Get data from the Binance API
        klines = client.get_klines(symbol=data['symbol'], interval=Client.KLINE_INTERVAL_1HOUR, startTime=int(current_start), endTime=int(current_end))

        # Convert data to DataFrame and append to the main df
        temp_df = pd.DataFrame(klines, columns=['ThoiGian', 'Open', 'High', 'Low', 'Close', 'Volume', 'CloseTime', 'QuoteAssetVolume', 'NumberOfTrades', 'TakerBuyBaseAssetVolume', 'TakerBuyQuoteAssetVolume', 'Ignore'])
        df = pd.concat([df, temp_df], ignore_index=True)

        # Move to the next interval
        current_start = current_end

    # Select the columns to use
    df = df[['ThoiGian', 'Open', 'High', 'Low', 'Close', 'Volume']]

    # Convert data types
    df['ThoiGian'] = pd.to_datetime(df['ThoiGian'], unit='ms')
    df['Open'] = df['Open'].astype(float)
    df['High'] = df['High'].astype(float)
    df['Low'] = df['Low'].astype(float)
    df['Close'] = df['Close'].astype(float)
    df['Volume'] = df['Volume'].astype(float)

    # Chuyển DataFrame thành JSON và trả về
    json_data = df.to_json(orient='records', date_format='iso')
    return jsonify(json_data)

@app.route('/order_book', methods=['POST'])
def order_book():
    # Lấy order book cho một cặp giao dịch cụ thể (ví dụ: BTCUSDT)
    data = request.form
    print(data)
    symbol = data.get('symbol')
    symbol = symbol + "USDT"
    order_book = client.get_order_book(symbol=symbol)

    # Chuẩn bị dữ liệu để xuất dưới dạng JSON
    order_book_json = {
        'bids': order_book['bids'][:5],
        'asks': order_book['asks'][:5]
    }

    return jsonify(order_book_json)


if __name__ == '__main__':
    app.run(host = "0.0.0.0", debug=True)
# Main loop

