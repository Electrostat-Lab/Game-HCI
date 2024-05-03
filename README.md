 # Game-HCI
> Formerly known as "JmeGamepad" and "Superior-Extended-Engine" or "SEE".

## [Demonstration of the library's capabilities.](https://www.youtube.com/watch?v=Gp2JJ-PCI8c) ##

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

## Useful Tags/Code snippets: 

1) `Constraining the game pad stick using a scale factor to the coordinates calculated from the similarity of parallel triangles` : 
        <br/>
	<br/>
	Code : https://github.com/Scrappers-glitch/Superior-Extended-Engine/blob/27c6dbb65e07eb2b3096b0dd07af575cc43c54f0/SuperiorPlugin/src/main/java/com/scrappers/superiorExtendedEngine/gamePad/GameStickView.java#L335 
	<br/>
	https://www.instructables.com/A-Simple-Android-UI-Joystick/
	<br/>
	<b> Linear Interpolation : <b/>
	https://www.mathsisfun.com/data/scatter-xy-plots.html

2) `Converting vector/rectangular coordinates (vector2d) to polar coordinates (angles) :` => Used by `DrivingWheel`
	<br/>
	<br/>
	Code : https://github.com/Scrappers-glitch/Superior-Extended-Engine/blob/27c6dbb65e07eb2b3096b0dd07af575cc43c54f0/SuperiorPlugin/src/main/java/com/scrappers/superiorExtendedEngine/vehicles/DrivingWheelView.java#L213
	<br/>
 	[Trigonometry.pdf](https://github.com/Scrappers-glitch/Superior-Extended-Engine/files/7531994/Trigonometry.pdf)
	<br/>
	Arc of trig functions : https://www.mathsisfun.com/algebra/trig-inverse-sin-cos-tan.html 
	<br/>
	Java atan2 docs : https://developer.android.com/reference/java/lang/Math#atan2(double,%20double)


3) `Using the game rotation vector software sensor (Geomagnetic sensor + Gyroscope) :` => Used by `GameDrivingMatrix`
	<br/>
	<br/>
	Code : https://github.com/Scrappers-glitch/Superior-Extended-Engine/blob/27c6dbb65e07eb2b3096b0dd07af575cc43c54f0/SuperiorPlugin/src/main/java/com/scrappers/superiorExtendedEngine/gamePad/GameStickView.java#L231 
	<br/>
	https://developer.android.com/guide/topics/sensors/sensors_position#java
