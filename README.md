# 🔌 SmartEnergyMeter

A real-time Smart Energy Meter system using **ESP32** that measures **voltage**, **current**, and **power**, and sends live data to a custom **Android app** via Bluetooth.

---

## 📱 Features

- 📊 Measures voltage (V), current (A), and power (W)
- 📡 Sends data to Android app via **Bluetooth Classic**
- 📲 Displays real-time readings in a clean mobile UI
- 🔧 Built with **ESP32**, **Kotlin**, and **Firebase** (optional)
- ⚙️ Calibrated for accuracy and fast refresh rate

---

## 🛠️ Tech Stack

- **Hardware:** ESP32, Current Sensor (e.g., ACS712), Voltage Divider
- **Firmware:** Arduino C++ (ESP32), Serial Bluetooth communication
- **Mobile App:** Kotlin + Android Studio (custom UI)
- **Tools:** Git, Firebase (optional), XML layout

---

## 🚀 How It Works

1. ESP32 reads analog values from sensors.
2. Calculates voltage, current, and power in real-time.
3. Sends the data via Bluetooth to the Android app.
4. App parses and displays the values instantly.


---

## 📦 Installation

1. Flash the ESP32 firmware (in `/firmware` folder)
2. Pair your mobile with the ESP32 over Bluetooth
3. Open the Android app and select the device
4. Start monitoring energy in real-time

---

## 📬 Contact

Have questions or feedback?  
📧 sanjaips.tech@gmail.com

