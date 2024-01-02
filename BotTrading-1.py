import pandas as pd
from binance.client import Client
import time

# Binance API credentials
api_key = 'eifB4BAvzSGJYjZwUe0cwrxwCDoiesdnxOnGsO7MSCiOBxcpO8Ferl25kVLVLrcS'
api_secret = 'BrJsu91t9DCi6z3Dmab8x4M7bbQ5T5Cu9mNyDvu5yg0a7uGOWviiHP71f6eGXcti'

# Tạo một đối tượng Binance client
client = Client(api_key=api_key, api_secret=api_secret, testnet=True)

def getHourlyData(symbol, num_hours=200):
    """
    Lấy dữ liệu lịch sử giá đóng cửa và khối lượng giao dịch theo giờ của một biểu tượng cụ thể từ Binance.
    """
    num_points = num_hours
    frame = pd.DataFrame(client.get_historical_klines(symbol, '1h', num_points))

    frame = frame[[0, 4, 7]]
    frame.columns = ['ThoiGian', 'GiaDong', 'KhoiLuong']

    frame['GiaDong'] = frame['GiaDong'].astype(float)
    frame['KhoiLuong'] = frame['KhoiLuong'].astype(float)
    frame['ThoiGian'] = pd.to_datetime(frame['ThoiGian'], unit='ms')

    return frame

def SMA(df):
    """
    Tính toán Động trung bình đơn giản (SMA) với các cửa sổ khác nhau cho giá đóng cửa của một biểu tượng.
    """
    df['SMA7(7h)'] = df['GiaDong'].rolling(7).mean()
    df['SMA25(25h)'] = df['GiaDong'].rolling(25).mean()
    df['SMA99(99h)'] = df['GiaDong'].rolling(99).mean()

def EMA(df):
    """
    Tính toán Động trung bình mũi tên (EMA) với các cửa sổ khác nhau cho giá đóng cửa của một biểu tượng.
    """
    df['EMA7(7h)'] = df['GiaDong'].ewm(span=7, adjust=False).mean()
    df['EMA25(25h)'] = df['GiaDong'].ewm(span=25, adjust=False).mean()
    df['EMA99(99h)'] = df['GiaDong'].ewm(span=99, adjust=False).mean()

def get_current_server_time():
    """
    Lấy thời gian hiện tại từ máy chủ Binance.
    """
    server_time = client.get_server_time()
    return server_time['serverTime']

def get_current_price(symbol='BTCUSDT'):
    """
    Lấy giá hiện tại của một biểu tượng.
    """
    ticker = client.get_symbol_ticker(symbol=symbol)
    current_price = float(ticker['price'])
    return current_price

def place_buy_order(symbol, quantity, price):
    """
    Đặt một lệnh mua trên sàn giao dịch Binance.
    """
    order = client.create_order(
        symbol=symbol,
        side='BUY',
        type='LIMIT',
        timeInForce='GTC',  # Good 'Til Canceled
        quantity=quantity,
        price=price,
        recvWindow=50000  # Cửa sổ nhận trong mili giây
    )
    return order

def place_sell_order(symbol, quantity, price):
    """
    Đặt một lệnh bán trên sàn giao dịch Binance.
    """
    order = client.create_order(
        symbol=symbol,
        side='SELL',
        type='LIMIT',
        timeInForce='GTC',
        quantity=quantity,
        price=price,
        recvWindow=50000  # Cửa sổ nhận trong mili giây
    )
    return order

def strategy(data):
    """
    Xác định chiến lược giao dịch dựa trên trung bình động và giá hiện tại.
    Dựa trên chiến lược Trend Following (Theo dõi xu hướng):

Mô tả: Mua khi giá và đường trung bình động đều đang trong xu hướng tăng, bán khi chúng đều trong xu hướng giảm.
Tín hiệu Mua (BUY): Khi giá nằm trên đường trung bình động và cả hai đều đang tăng.
Tín hiệu Bán (SELL): Khi giá nằm dưới đường trung bình động và cả hai đều đang giảm.
    """
    current_price = get_current_price()
    Buy_Signal = ((current_price > data['SMA7(7h)']) & (current_price > data['SMA25(25h)']) & 
                  (current_price > data['SMA99(99h)']) & (current_price > data['EMA7(7h)']) & 
                  (current_price > data['EMA25(25h)']) & (current_price > data['EMA99(99h)'])).any()

    Sell_Signal = ((current_price < data['SMA25(25h)']) | (current_price < data['SMA99(99h)']) & 
                   (current_price < data['EMA25(25h)']) | (current_price < data['EMA99(99h)'])).any()
    
    if Buy_Signal:
        return True
    elif Sell_Signal:
        return False
    else:
        return None

def log_transaction(action, symbol, quantity, price, file_path='note.txt'):
    """
    Ghi nhận các giao dịch vào một tệp tin.
    """
    with open(file_path, 'a') as file:
        file.write(f"{action} - Symbol: {symbol}, Quantity: {quantity}, Price: {price}\n")

def create_note_file(file_path='note.txt'):
    """
    Tạo hoặc ghi đè lên tệp tin ghi chú giao dịch.
    """
    with open(file_path, 'w') as file:
        file.write("My Trading Notes\n")
        file.write("================\n")

def get_account_balance(symbol='BTC'):
    """
    Lấy số dư tài khoản cho một biểu tượng cụ thể trên sàn giao dịch Binance.
    """
    server_timestamp = get_current_server_time()
    
    # Thêm một độ trễ nhỏ để tránh vấn đề về thời gian
    time.sleep(10)
    
    account_info = client.get_account(recvWindow=50000)

    for balance in account_info['balances']:
        if balance['asset'] == symbol:
            return float(balance['free'])
    return 0.0

# vòng lặp giao dịch chính
i = 1
create_note_file(file_path='Dữ liệu giao dịch.txt')

while i != 0:
    # Lấy dữ liệu và áp dụng động trung bình
    num_hours = 200
    dataLive = getHourlyData('BTCUSDT', num_hours=200)
    EMA(dataLive)
    SMA(dataLive)
    
    # Sử dụng thời gian máy chủ Binance để đánh dấu thời gian
    server_timestamp = get_current_server_time()
    
    # Thực hiện chiến lược giao dịch
    # So sánh với strategy đã đề ra bên trên đưa ra True và False ứng với mua và bán, None là Hold, đồng thời lưu vào file dữ liệu đã tạo trước đó
    if (strategy(dataLive) == True) & (get_account_balance(symbol='USDT') >= 1):
        place_buy_order('BTCUSDT', get_account_balance(symbol='BTC'), get_current_price())
        log_transaction('BUY', 'BTCUSDT', get_account_balance(symbol='BTC'), get_current_price(), file_path='Dữ liệu giao dịch.txt')
    elif (strategy(dataLive) == False) & ((get_account_balance(symbol='BTCUSDT') * get_current_price) >= 1):
        place_sell_order('BTCUSDT', get_account_balance(symbol='BTC'), get_current_price())
        log_transaction('SELL', 'BTCUSDT', get_account_balance(symbol='BTC'), get_current_price(), file_path='Dữ liệu giao dịch.txt')
    else:
        log_transaction('HOLD', 'BTCUSDT', 0, get_current_price(), file_path='Dữ liệu giao dịch.txt')

