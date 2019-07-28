
Android BasicTransition Sample
===================================

A basic app showing how to use the Transition framework introduced in
KitKat. The app shows radioboxes to select between different Scenes,
and uses various ways to transition between them.

Introduction
------------

A Scene is an encapsulation of the state of a view hierarchy,
including the views in that hierarchy and the various values
(layout-related and otherwise) that those views have. A scene can be
defined by a layout hierarchy directly or by code which sets up the
scene dynamically as it is entered.

A Transition is a mechanism to automatically animate changes that
occur when a new scene is entered. Some transition capabilities are
automatic. That is, entering a scene may cause animations to run which
fade out views that go away, changeBounds and resize existing views
that change, and fade in views that become visible. There are
additional transitions that can animate other attributes, such as
color changes, and which can optionally be specified to take place
during particular scene changes. Finally, developers can define their
own Transition subclasses which monitor particular property changes
and which run custom animations when those properties change values.

TransitionManager is used to specify custom transitions for particular
scene changes, and to cause scene changes with specific transitions to
take place.

Pre-requisites
--------------

- Android SDK 28
- Android Build Tools v28.0.3
- Android Support Repository

Screenshots
-------------

<img src="screenshots/main.png" height="400" alt="Screenshot"/> 

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
