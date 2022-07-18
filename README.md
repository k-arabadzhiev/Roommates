<h1 align="center">Roommates🏠</h1></br>

<p align="center">
  <a href="https://opensource.org/licenses/MIT"><img alt="License" src="https://img.shields.io/badge/License-MIT-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=21"><img alt="API" src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat"/></a>
</p>

A small demo application for finding roommates. The backend is written in Kotlin using Ktor. 

# 🛠️ Tech stack & Open-source libraries
- Minimum SDK level 21
- [Kotlin](https://kotlinlang.org/), [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) and [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/)
- [Hilt](https://dagger.dev/hilt/) for dependency injection.
- JetPack
  -  [Compose](https://developer.android.com/jetpack/compose) - A modern toolkit for building native Android UI.
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - UI related data holder, lifecycle aware.
  - [Room](https://developer.android.com/jetpack/androidx/releases/room) Persistence - abstraction layer for local SQLite database.
  - [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) - store user settings asynchronously with Kotlin Coroutines and Flow.
- MVVM (Model - ViewModel - View) Architecture 
- [Ktor-client](https://ktor.io/docs/welcome.html) - Type-safe HTTP client used to connect to the backend API. 
- [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization/) - JSON parser.
- [Coil](https://coil-kt.github.io/coil/) - loading images from the network.
- [Compose Destinations](https://github.com/raamcosta/compose-destinations) - Annotation processing library for type-safe Jetpack Compose navigation with no boilerplate.
- [Ktor-server](https://ktor.io/docs/welcome.html) - server side Ktor
- [KMongo](https://github.com/Litote/kmongo) - Kotlin toolkit for Mongo
- [Koin](https://insert-koin.io/) - dependency injection for Ktor
 
# 🎬 Preview 
<p align="center">
<img src="previews/chat.gif"/>
</p>

#

### 📅 TODO 
- Add tests
- Implement Firebase Authentication and FCM for push notifications
- Improve stability, fix any bugs

#

### 🌟Huge Thanks
* [Philipp Lackner](https://github.com/philipplackner) - very clear and straight to the point [tutorials](https://www.youtube.com/c/PhilippLackner). 
* [Rafael Costa](https://github.com/raamcosta) - provided help with [Compose Destinations](https://github.com/raamcosta/compose-destinations).
* [Jaewoong Eum](https://github.com/skydoves) - has a ton of amazing articles on medium and open-source libraries. Also this README format. 
* [davideC00](https://github.com/davideC00) - Compose [CardStack](https://github.com/davideC00/CardStack).

<img src="https://user-images.githubusercontent.com/24237865/141674368-6013d77c-d52b-4bb1-afe4-9a57a06be32f.jpg" width="18%" align="right" />


## Contents Credits
All copyrights of the contents, concepts, and phrases used for this open-source project belong to [The Walt Disney Company](https://www.disneyplus.com/).
 

# 📝 License
```xml
MIT License

Copyright (c) 2022 Kostadin Arabadzhiev

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.