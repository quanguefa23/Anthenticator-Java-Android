# Authenticator

## Features 
- Add new authenticator code (auth-code) by entering key or scanning an QR code  
- Remove auth-code, update detail of auth-code  
- Export your auth-code to an QR code image, other devices can use this QR to import auth-code
- Switch between showing or hiding code values in main screen  
- Sync with world time to correct the device lock  
- Sign in via google account to backup and restore your data  
- Pin an auth-code to notification drawer: users can copy code value to clipboard anywhere and anywhen (code is still updated in realtime)  

## Screenshoots
![1](https://user-images.githubusercontent.com/60953757/89712881-ed35e500-d9bd-11ea-9700-c214aa97f7ce.jpg)  
![5](https://user-images.githubusercontent.com/60953757/89712883-f030d580-d9bd-11ea-9f31-206f920b4911.jpg)  
![9](https://user-images.githubusercontent.com/60953757/89712885-f2932f80-d9bd-11ea-8248-e2ce5c7e68d2.jpg)  
![13](https://user-images.githubusercontent.com/60953757/89712886-f4f58980-d9bd-11ea-824a-50959df2518d.jpg)  

## Main technology  
- MVVM Architecture  
- LiveData  
- ViewModel  
- Room  
- Retrofit  
- Data Binding  
- Dagger2 with ViewModelFactory  
- RecyclerView with DiffUtil and Payload  
- Google Drive API

## Extra technique  
- ZXing framework  
- Foreground Service  
- Notifications    
- MotionLayout  
- Material Design  
- TimerTask (replace later)  
- Handler and HandlerThread (replace TimerTask)  
- ThreadPool - SingleThreadScheduledExecutor  
- Persistent configuration change  
