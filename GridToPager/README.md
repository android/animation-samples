# Android Fragment Transitions: RecyclerView to ViewPager

This Android project accompanies the [Continuous Shared Element Transitions: RecyclerView to ViewPager](https://goo.gl/Txqtds ) article. 

The code here provides the implementation for a specific transition between Android Fragments. 
It demonstrates how to implement a transition from an image in a `RecyclerView` into an image in a 
`ViewPager` and back, using ‘Shared Elements’ to determine which views participate in the transition 
and how. It also handles the tricky case of transitioning back to the grid after paging to an item 
that was previously offscreen.

**This is not an officially supported Google product.**

## Launching

* Import the project into Android Studio
* Launch the app

![Demo](doc/demo/app_demo.gif "Grid to Pager demo.")
