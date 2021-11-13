 # Superior Extended Engine 1.4

![](https://github.com/Scrappers-glitch/Superior-Extended-Engine/blob/master/SuperiorPlugin/src/main/java/com/scrappers/superiorExtendedEngine/attachments/LogoMod1.png)

### GamePad Library for Android Games running java/Kotlin ###


## [Video about this Library ](https://www.youtube.com/watch?v=Gp2JJ-PCI8c) ##

##### How to implement it into your Android project :

Step 1. Add it in your root build.gradle at the end of repositories :
```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  ```

Step 2. Add the dependency :
```gradle
	dependencies {
	        implementation 'com.github.Scrappers-glitch:Superior-Extended-Engine:1.x.x'
	}
```

![](https://github.com/Scrappers-glitch/Superior-Extended-Engine/blob/master/SuperiorPlugin/src/main/java/com/scrappers/superiorExtendedEngine/attachments/imageSEEDemo1.png)

![](https://github.com/Scrappers-glitch/Superior-Extended-Engine/blob/master/SuperiorPlugin/src/main/java/com/scrappers/superiorExtendedEngine/attachments/imageSEEDemo2.png)

![](https://github.com/Scrappers-glitch/Superior-Extended-Engine/blob/master/SuperiorPlugin/src/main/java/com/scrappers/superiorExtendedEngine/attachments/imageSEEDemo3.png)

## References : 

1) `Constraining the game pad stick using a scale factor to the coordinates calculated from the similarity of parallel triangles` : 
	https://www.instructables.com/A-Simple-Android-UI-Joystick/

2) `Converting vector coordinates (vector2d) to polar coordinates (angles) :` => Used by `DrivingWheel`
	<br/>
	<br/>
 	[Trigonometry.pdf](https://github.com/Scrappers-glitch/Superior-Extended-Engine/files/7531994/Trigonometry.pdf)


3) `Using the game rotation vector software sensor (Geomagnetic sensor + Gyroscope) :` => Used by `GameDrivingMatrix`

	https://developer.android.com/guide/topics/sensors/sensors_position#java
