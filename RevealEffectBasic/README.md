
Android RevealEffectBasic Sample
===================================

Sample demonstrating circular reveal effect. It covers creating an
[Animator][1] with [ViewAnimationUtils][2] as well as defining the parameters
of the circular reveal including starting position and radius.


[1]: https://developer.android.com/reference/android/animation/Animator.html
[2]: https://developer.android.com/reference/android/view/ViewAnimationUtils.html

Introduction
------------

Sample demonstrating circular reveal effect. Reveal animations can be used to
provide visual continuity when showing or hiding views. With
[ViewAnimationsUtils.createCircularReveal()][1] you can use the startRadius and
endRadius to define a hiding or revealing animation. You can also define the
center of the animation, in this sample the center is x=0, y=0 which defines
the top left of the View as the center.

Press the *Reveal* button to see the the circular reveal.

[1]: http://developer.android.com/reference/android/view/ViewAnimationUtils.html#createCircularReveal(android.view.View, int, int, float, float)

Pre-requisites
--------------

- Android SDK 28
- Android Build Tools v28.0.3
- Android Support Repository

Screenshots
-------------

<img src="screenshots/1-main.png" height="400" alt="Screenshot"/> 

Getting Started
---------------

This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.

Support
-------

- Stack Overflow: http://stackoverflow.com/questions/tagged/android

If you've found an error in this sample, please file an issue:
https://github.com/android/animation

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub. Please see CONTRIBUTING.md for more details.
