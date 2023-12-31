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
num_hours = 200
dataLive = pd.DataFrame(getHourlyData('BTCUSDT', num_hours))
SMA(dataLive)
EMA(dataLive)
print(dataLive)
print(get_current_price('BTCUSDT'))
    


    
    
    
    

