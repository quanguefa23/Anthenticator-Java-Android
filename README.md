# ZA Authenticator

## Function  
- Add new authenticator code (auth-code) via entering key or scanning an QR code  
- Remove auth-code, update detail of auth-code  
- Export your auth-code to an QR code image, other device can use this QR to import auth-code
- Switch between showing or hiding code values in main screen  
- Sync with world time to correct the device lock  
- Sign in via google account to backup and restore your data  
- Pin an auth-code to notification drawer: users can copy code value to clipboard anywhere and anywhen (code is still updated in realtime)  

## Screenshoot

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
