# HM_Android_ARC_Core


Setting up and Building Projects
================================

To the best of knowledge, getting these applications to build should require pretty minimal setup on your part. Once you've got the projects cloned, all that you should need to change are build settings like the Bundle Identifier and Team for iOS, and whatever the equivalent is for Android.


Cloning Projects
----------------

You'll need to clone the application repository and arc_core_android separately. They should both be within the same parent directory, so it should look something like:

some parent dir/
|
|-- core/
|
|-- exr_android/


When you open the application project in Android Studio, it expects to find the core project in the 'core' directory.


Changing Dev/QA/Production Settings
-----------------------------

The Android projects keep similar settings in an Application class that inherits from `com.healthymedium.arc.core.Application`. Changing between different settings is handled by selecting the Build Variant within the Build Variants window in Android Studio.


Application Structure
=====================

Although they achieve it in very different ways, both iOS and Android projects are built with the same ideas in mind: a core project contains all of the basic functionality, and each application is built by customizing certain core classes. 

I will admit now that I am not as familiar with the Android project as I am iOS, and the same goes for Android development in general. 

**com.healthymedium.arc.core.Config**

The `Config` class is a static class that defines many different configuration options. These values are modified based on the needs of the specific application and the current Build Variant.

**Application**

The core `Application` class handles initializing several components, as well as some lifecycle events. 

Each application provides an `Application` subclass, which override the `onCreate()` and `registerStudyComponents()` methods. The `onCreate()` method typically handles customizing the `Config` values for the application.

**StateMachine**

Each application contains a subclass of the `com.healthymedium.arc.study.StateMachineAlpha` class (which itself is a subclass of `com.healthymedium.arc.study.StateMachine`). This class manages pretty much the entire application state. 

There are two key properties on the `StateMachine` object:
- *state*, a `State` object which contains the `lifecycle` and `currentPath` values
- *statecache*, a `StateCache` object which contains a list of upcoming Fragments.

The `decidePath()` method uses the state's current `lifecycle` and `currentPath` values to decide the next value of `currentPath`.
The `setupPath()` method is usually called right after `decidePath()`. Based on the current state, it adds the necessary fragments to the `cache`.

The `StateMachine` contains several other methods that handle moving through items within the `cache`, such as `openNext()`, `skipToNextSegment()`, `moveOn()`, etc.

**Scheduler**

The `Scheduler` class handles creating the participant's test schedule. Applications may subclass this class to implement customizations, such as `initializeCycles()` to implement a custom test cycle schedule.
