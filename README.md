
Buse Celik's Final Project
Overview
This project is an Android application that manages account transactions. It displays account details and lists transactions retrieved from a remote server. The transactions can be saved and managed locally using Room database.

Features
Account Details: Fetch and display account details from a remote server.
Transactions List: Display transactions in a RecyclerView.
Custom Dialog: Show account details in a custom dialog.
Room Database: Save and manage transactions locally.
Picasso: Load images from URLs for transactions.

Technologies Used
Kotlin: Programming language.
Retrofit: HTTP client for Android to consume RESTful web services.
Room: Persistence library for local database management.
Picasso: Image loading library for Android.
RecyclerView: For displaying list of transactions.
Coroutines: For asynchronous programming.

JSON Structure
Example JSON structure with nested objects and arrays:

json
{
  "account": {
    "no": "199199199",
    "balance": 305,
    "name": "Veli Korkmaz",
    "email": "veli@bilkent.edu.tr",
    "accountType": "Verified Account",
    "currency": "TL"
  },
  "transaction": [
    {
      "title": "Deposit",
      "description": "Teknosa",
      "type": 1,
      "amount": 100,
      "date": "08/05/2024 15:15"
    },
    {
      "title": "Other",
      "description": "Papara Card",
      "type": 3,
      "amount": 5,
      "date": "08/05/2024 15:08"
    },
    {
      "title": "Payment",
      "description": "BilEnerji",
      "type": 2,
      "amount": 102,
      "date": "05/05/2024 18:00"
    }
  ]
}

Project Structure
busecelikfin/
├── adapter/
│   ├── TransactionAdapter.kt
├── database/
│   ├── AppDatabase.kt
│   ├── Transaction.kt
│   ├── TransactionDao.kt
├── network/
│   ├── ApiService.kt
│   ├── RetrofitClientInstance.kt
├── MainActivity.kt
├── models/
│   ├── Account.kt
│   ├── AccountResponse.kt
├── res/
│   ├── layout/
│   │   ├── activity_main.xml
│   │   ├── custom_dialog.xml
│   │   ├── recyclerview_transaction_layout.xml
│   │   ├── recyclerview_other_transaction_layout.xml
│   ├── drawable/
│   │   ├── deposit.png
│   │   ├── withdraw.png
│   │   ├── other.png
│   ├── values/
│   │   ├── strings.xml
│   │   ├── colors.xml
│   │   ├── themes.xml

Getting Started

Prerequisites
Android Studio
Kotlin 1.4 or later
Internet connection to fetch account data

Installation
Clone the repository:
git clone https://github.com/yourusername/busecelikfin.git
Open the project in Android Studio.

Sync the project to download all dependencies.

Build and run the project on an emulator or physical device.

Usage
On launch, the app will fetch account data and transactions from the server.
The account number will be displayed at the top.
Transactions will be listed in the RecyclerView.
Click the "Account Detail" button to view account details in a custom dialog.
Click on a transaction to handle it according to its type:
Type 1 (Deposit) and Type 2 (Withdraw): Insert into the marked transactions table and display a toast.
Type 3 (Other): Display a toast message on image click.

Contributing
Fork the repository.
Create your feature branch (git checkout -b feature/featureName).
Commit your changes (git commit -m 'Add some feature').
Push to the branch (git push origin feature/featureName).
Open a pull request.

License
This project is licensed under the MIT License - see the LICENSE file for details.

Contact
Buse Celik - celikb292@icloud.com
