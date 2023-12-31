import pandas as pd
from binance.client import Client
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_squared_error
from sklearn.impute import SimpleImputer
from scipy.interpolate import lagrange
import time

api_key = 'YOUR_API_KEY'
api_secret = 'YOUR_API_SECRET'
client = Client(api_key=api_key, api_secret=api_secret)

def getHourlyData(symbol, num_hours=200):
    # Calculate the number of data points needed for the last num_hours
    num_points = num_hours

    frame = pd.DataFrame(client.get_historical_klines(symbol, '1h', num_points))

    frame = frame[[0, 4, 7]]
    frame.columns = ['ThoiGian', 'GiaDong', 'KhoiLuong']

    frame['GiaDong'] = frame['GiaDong'].astype(float)
    frame['KhoiLuong'] = frame['KhoiLuong'].astype(float)

    frame['ThoiGian'] = pd.to_datetime(frame['ThoiGian'], unit='ms')

    return frame


def SMA(df):
    df['SMA7(7h)'] = df['GiaDong'].rolling(7).mean()
    df['SMA25(25h)'] = df['GiaDong'].rolling(25).mean()
    df['SMA99(99h)'] = df['GiaDong'].rolling(99).mean()
    
def EMA(df):
    df['EMA7'] = df['GiaDong'].ewm(span=7, adjust=False).mean()
    df['EMA25'] = df['GiaDong'].ewm(span=25, adjust=False).mean()
    df['EMA99'] = df['GiaDong'].ewm(span=99, adjust=False).mean()


def get_current_price(symbol='BTCUSDT'):
    ticker = client.get_symbol_ticker(symbol=symbol)
    current_price = float(ticker['price'])
    return current_price

def place_buy_order(symbol, quantity, price):
    order = client.create_order(
        symbol=symbol,
        side='BUY',
        type='LIMIT',
        timeInForce='GTC',  # Good 'Til Canceled
        quantity=quantity,
        price=price
    )
    return order

def place_sell_order(symbol, quantity, price):
    order = client.create_order(
        symbol=symbol,
        side='SELL',
        type='LIMIT',
        timeInForce='GTC',
        quantity=quantity,
        price=price
    )
    return order

def strategy(data):
    current_price = get_current_price()
    Buy_Signal = (current_price > data['SMA7(7h)']) & (current_price > data['SMA25(25h)']) & (current_price > data['SMA99(99h)']) (current_price > data['EMA7(7h)']) & (current_price > data['EMA25(25h)']) & (current_price > data['EMA99(99h)'])
    Sell_Signal = (current_price < data['SMA25(25h)']) | (current_price < data['SMA99(99h)']) & (current_price < data['EMA25(25h)']) | (current_price < data['EMA99(99h)'])
    if Buy_Signal: return True
    if Sell_Signal: return False
    return None

def log_transaction(action, symbol, quantity, price, file_path='note.txt'):
    # Mở file ở chế độ append ('a')
    with open(file_path, 'a') as file:
        # Ghi thông tin giao dịch vào file
        file.write(f"{action} - Symbol: {symbol}, Quantity: {quantity}, Price: {price}\n")
        
def create_note_file(file_path='note.txt'):
    # Mở file ở chế độ ghi ('w'), nếu file chưa tồn tại sẽ tạo mới, nếu đã tồn tại sẽ bị ghi đè
    with open(file_path, 'w') as file:
        file.write("My Trading Notes\n")  # Ghi một dòng tiêu đề vào file
        file.write("================\n")  # Ghi một dòng phân đoạn

        
def get_account_balance(symbol='BTC'):
    # Lấy thông tin tài khoản
    account_info = client.get_account()

    # Duyệt qua các tài khoản và lấy số dư của đồng coin cụ thể
    for balance in account_info['balances']:
        if balance['asset'] == symbol:
            return float(balance['free'])

    # Trả về 0 nếu không tìm thấy số dư cho đồng coin cụ thể
    return 0.0
i = 0
create_note_file(file_path='Dữ liệu giao dịch.txt')
while i != 0:
    num_hours = 200
    dataLive = getHourlyData('BTCUSDT', num_hours=200)
    EMA(dataLive)
    SMA(dataLive)
    if (strategy(dataLive) == True) & (get_account_balance(symbol='USDT') != 0):
        place_buy_order('BTCUSDT', get_account_balance(symbol='BTC'), get_current_price())
        log_transaction('BUY', 'BTCUSDT', get_account_balance(symbol='BTC'), get_current_price(), file_path='Dữ liệu giao dịch.txt')
    elif (strategy(dataLive) == False) & (get_account_balance(symbol='BTCUSDT') != 0):
        place_sell_order('BTCUSDT', get_account_balance(symbol='BTC'), get_current_price())
        log_transaction('SELL', 'BTCUSDT', get_account_balance(symbol='BTC'), get_current_price(), file_path='Dữ liệu giao dịch.txt')
    else:
        log_transaction('HOLD', 'BTCUSDT', 0, get_current_price(), file_path='Dữ liệu giao dịch.txt')


    


    
    
    
    

