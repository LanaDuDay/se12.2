import pandas as pd
import matplotlib.pyplot as plt
import mplfinance as mpf
from binance.client import Client
from datetime import datetime, timedelta

def getHourlyData(symbol, end_date):
    # Replace YOUR_API_KEY and YOUR_API_SECRET with your API information
    api_key = 'YOUR_API_KEY'
    api_secret = 'YOUR_API_SECRET'
    
    # Initialize the Client object
    client = Client(api_key=api_key, api_secret=api_secret)

    # Convert end date to timestamp
    end_timestamp = pd.to_datetime(end_date).timestamp() * 1000

    # Initialize an empty DataFrame to store the data
    df = pd.DataFrame()

    # Define the interval for each request (1 month)
    interval = timedelta(days=2)

    # Make requests in chunks
    current_start = end_timestamp - interval.total_seconds() * 1000
    while current_start < end_timestamp:
        current_end = min(current_start + interval.total_seconds() * 1000, end_timestamp)

        # Get data from the Binance API
        klines = client.get_klines(symbol=symbol, interval=Client.KLINE_INTERVAL_1HOUR, startTime=int(current_start), endTime=int(current_end))

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

    return df

def plotBTCChart(data, save_path=None):
    fig, (ax, ax_volume) = plt.subplots(nrows=2, sharex=True, figsize=(12, 6), gridspec_kw={'height_ratios': [3, 1]})
    
    # Plot candlestick chart
    mpf.plot(data.set_index('ThoiGian'), type='candle', ax=ax, volume=ax_volume, show_nontrading=True)

    plt.title('BTC Hourly Candlestick Chart')
    plt.xlabel('Date')
    ax.set_ylabel('Price (USDT)')
    ax_volume.set_ylabel('Volume')

    # Save the figure if save_path is provided
    if save_path:
        plt.savefig(save_path)

    plt.show()

# Get the current date and time
current_date = datetime.now().strftime('%Y-%m-%d %H:%M:%S')

# Use the function to get BTCUSDT data from the current time to 1 day before
symbol = 'BTCUSDT'
hourly_data = getHourlyData(symbol, current_date)

# Print the DataFrame for debugging
print(hourly_data)

# Plot the chart and save the image
save_path = 'BTC_Hourly_Chart.png'
plotBTCChart(hourly_data, save_path)
