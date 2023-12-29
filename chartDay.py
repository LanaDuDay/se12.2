import pandas as pd
import matplotlib.pyplot as plt
import mplfinance as mpf
from binance.client import Client
from datetime import datetime, timedelta

def getDailyData(symbol, start_date, end_date):
    # Thay thế YOUR_API_KEY và YOUR_API_SECRET bằng thông tin API của bạn
    api_key = 'YOUR_API_KEY'
    api_secret = 'YOUR_API_SECRET'
    
    # Khởi tạo đối tượng Client
    client = Client(api_key=api_key, api_secret=api_secret)

    # Chuyển đổi ngày thành timestamp
    start_timestamp = pd.to_datetime(start_date).timestamp() * 1000
    end_timestamp = pd.to_datetime(end_date).timestamp() * 1000

    # Lấy dữ liệu từ API Binance
    klines = client.get_klines(symbol=symbol, interval=Client.KLINE_INTERVAL_1DAY, startTime=int(start_timestamp), endTime=int(end_timestamp))

    # Chuyển đổi dữ liệu thành DataFrame
    df = pd.DataFrame(klines, columns=['ThoiGian', 'Open', 'High', 'Low', 'Close', 'Volume', 'CloseTime', 'QuoteAssetVolume', 'NumberOfTrades', 'TakerBuyBaseAssetVolume', 'TakerBuyQuoteAssetVolume', 'Ignore'])

    # Chọn các cột cần sử dụng
    df = df[['ThoiGian', 'Open', 'High', 'Low', 'Close', 'Volume']]

    # Chuyển đổi kiểu dữ liệu
    df['ThoiGian'] = pd.to_datetime(df['ThoiGian'], unit='ms')
    df['Open'] = df['Open'].astype(float)
    df['High'] = df['High'].astype(float)
    df['Low'] = df['Low'].astype(float)
    df['Close'] = df['Close'].astype(float)
    df['Volume'] = df['Volume'].astype(float)

    return df

def plotBTCChart(data):
    fig, (ax, ax_volume) = plt.subplots(nrows=2, sharex=True, figsize=(12, 6), gridspec_kw={'height_ratios': [3, 1]})
    
    # Plot candlestick chart
    mpf.plot(data.set_index('ThoiGian'), type='candle', ax=ax, volume=ax_volume, show_nontrading=True)

    plt.title('BTC Daily Candlestick Chart')
    plt.xlabel('Date')
    
    # Adjust ylabel for the main candlestick chart
    ax.set_ylabel('Price (USDT)')
    
    # Adjust ylabel for the volume subplot
    ax_volume.set_ylabel('Volume')

    plt.show()

def current_date_string():
    now = datetime.now()
    current_day = now.day
    current_month = now.month
    current_year = now.year
    return f"{current_year}-{current_month}-{current_day}"

def last_date_string():
    now = datetime.now()
    current_day = now.day
    current_month = now.month - 1
    current_year = now.year
    return f"{current_year}-{current_month}-{current_day}"

# Sử dụng hàm để lấy dữ liệu BTCUSDT từ ngày 1/1/2022 đến ngày hiện tại
symbol = 'BTCUSDT'
start_date = last_date_string()
end_date = current_date_string()

daily_data = getDailyData(symbol, start_date, end_date)

# Vẽ biểu đồ
plotBTCChart(daily_data)
