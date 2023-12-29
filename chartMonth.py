import pandas as pd
import matplotlib.pyplot as plt
import mplfinance as mpf
from binance.client import Client
from datetime import datetime, timedelta

def getClosingDataByMonth(symbol, start_date, end_date):
    # Replace YOUR_API_KEY and YOUR_API_SECRET with your API information
    api_key = 'YOUR_API_KEY'
    api_secret = 'YOUR_API_SECRET'
    
    # Initialize the Client object
    client = Client(api_key=api_key, api_secret=api_secret)

    # Convert start and end dates to timestamps
    start_timestamp = pd.to_datetime(start_date).timestamp() * 1000
    end_timestamp = pd.to_datetime(end_date).timestamp() * 1000

    # Initialize an empty DataFrame to store the data
    df = pd.DataFrame()

    # Make requests in chunks
    current_start = start_timestamp
    while current_start < end_timestamp:
        current_end = min(current_start + 365 * 24 * 60 * 60 * 1000, end_timestamp)

        # Get data from the Binance API
        klines = client.get_klines(symbol=symbol, interval=Client.KLINE_INTERVAL_1DAY, startTime=int(current_start), endTime=int(current_end))

        # Convert data to DataFrame and append to the main df
        temp_df = pd.DataFrame(klines, columns=['ThoiGian', 'Open', 'High', 'Low', 'Close', 'Volume', 'CloseTime', 'QuoteAssetVolume', 'NumberOfTrades', 'TakerBuyBaseAssetVolume', 'TakerBuyQuoteAssetVolume', 'Ignore'])
        df = pd.concat([df, temp_df], ignore_index=True)

        # Move to the next interval
        current_start = current_end

    # Select the columns to use
    df = df[['ThoiGian', 'Open', 'High', 'Low', 'Close', 'Volume']]

    # Convert data types
    df['ThoiGian'] = pd.to_datetime(df['ThoiGian'], unit='ms')

    # Check and convert columns to numeric values
    numeric_columns = ['Open', 'High', 'Low', 'Close', 'Volume']
    for col in numeric_columns:
        non_numeric_values = df[~pd.to_numeric(df[col], errors='coerce').notna()]
        if not non_numeric_values.empty:
            print(f"Non-numeric values found in '{col}' column:\n{non_numeric_values}")

        # Convert column to numeric values
        df[col] = pd.to_numeric(df[col], errors='coerce')

    # Extract the first day of each month
    df['ThoiGian'] = df['ThoiGian'] + pd.offsets.MonthBegin(0)

    # Resample data to monthly frequency and take the closing value of each month
    monthly_closing_data = df.set_index('ThoiGian').resample('M').last().reset_index()

    return monthly_closing_data

def plotBTCChart(data):
    fig, (ax, ax_volume) = plt.subplots(nrows=2, sharex=True, figsize=(12, 6), gridspec_kw={'height_ratios': [3, 1]})
    
    # Plot candlestick chart
    mpf.plot(data.set_index('ThoiGian'), type='candle', ax=ax, volume=ax_volume, show_nontrading=True, mav=(50, 200))

    plt.title('BTC Monthly Candlestick Chart')
    plt.xlabel('Month')
    
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
    current_month = now.month
    current_year = now.year - 7
    return f"{current_year}-{current_month}-{current_day}"

# Sử dụng hàm để lấy dữ liệu BTCUSDT từ ngày 1/1/2022 đến ngày hiện tại
symbol = 'BTCUSDT'
start_date = last_date_string()
end_date = current_date_string()

monthly_data = getClosingDataByMonth(symbol, start_date, end_date)

# Vẽ biểu đồ
plotBTCChart(monthly_data)
