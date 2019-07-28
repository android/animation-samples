# OurStreets for Android

Visit StreetView galleries with delightful material design aspects.

## Introduction

<img src="https://cloud.githubusercontent.com/assets/336005/14609752/1429b8f8-0583-11e6-862e-aa4af27396f6.gif" alt="OurStreets transitions" align="right" />

With OurStreets you can experience beautiful views from around the world
in 360 degrees.
All of this with a delightful material design approach throughout the whole interface.

OurStreets demonstrates use of shared element
[transitions](https://developer.android.com/reference/android/transition/Transition.html)
throughout the app.

The data for OurStreets is stored in [Firebase](https://firebase.com) and retrieved at runtime.
This allows for a smaller initial download as well as modifying of data without
having to deploy a new apk.

## [Download the prebuilt sample](https://github.com/googlesamples/android-ourstreets/releases)

## Screenshots

<img src="https://cloud.githubusercontent.com/assets/336005/14610389/a9f9e914-0585-11e6-9bbb-63d535570487.png" width="30%" />
<img src="https://cloud.githubusercontent.com/assets/336005/14610392/ab50ab18-0585-11e6-86d5-0c6580ee0bca.png" width="30%" />
<img src="https://cloud.githubusercontent.com/assets/336005/14610394/acc0edb4-0585-11e6-90c5-98c06bf20360.png" width="30%" />

## Getting started

### Clone and build

This project uses gradle as its build system.
After cloning, enter the top level directory and run <code>./gradlew tasks</code>
to get an overview of all the tasks available for this project.
Probably you'll just want to run

```./gradlew installDebug```

to get OurStreets running on your local device.

### Add a maps api key

To add a Google Maps API key, follow the instructions on the
[developers console](https://goo.gl/JRXyBx).
Copy the API key you've created and paste it into the [`gradle.properties`
file](gradle.properties).

## Support

- Stack Overflow: http://stackoverflow.com/questions/tagged/android

If you've found an error in this sample, please file an issue:

https://github.com/android/animation/issues

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub.
