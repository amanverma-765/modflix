# 🎬 MovieHub - Movie Browser & Downloader App

**MovieHub** is a sleek and modern movie browsing and downloading app built with **Jetpack Compose**. It scrapes movie listings and download/streaming links from **Vegamovies** and fetches detailed movie and series metadata using **IMDb APIs**.

> ⚠️ **Disclaimer:** We do not host or store any movies, videos, or streaming content. All the movie data, links, and streaming sources are scraped from publicly available third-party websites such as **Vegamovies**. This app is for educational purposes only.

---

## ✨ Features

- 🔍 **Browse** latest and trending movies and TV series.
- 🎞️ **Detailed movie pages** with poster, genre, rating, description, cast, and more.
- ⬇️ **Easy download** or **instant streaming** of content using scraped links.
- 🧠 **Movie & Series data** powered by IMDb APIs.
- 🌐 **Scraping** powered by `Jsoup` and network requests handled using `Ktor`.
- 💅 **Modern Android UI** built using Jetpack Compose.

---

## 🛠️ Tech Stack

| Technology       | Usage                                      |
|------------------|--------------------------------------------|
| Kotlin           | Core programming language                  |
| Jetpack Compose  | Declarative UI toolkit                     |
| Ktor             | HTTP client for networking                 |
| Jsoup            | HTML parsing and web scraping              |
| IMDb APIs        | Movie and series metadata                  |

---

## 📸 Screenshots

| Home Screen | Movie Detail | Download Options | Listing Page |
|-------------|--------------|------------------|----------------|
| ![Home Screen](https://github.com/user-attachments/assets/7e9f9251-57fa-4dec-89df-7f2fe1468043) | ![Movie Detail](https://github.com/user-attachments/assets/b6d9535f-c396-443e-9c27-0e0fee0daed1) | ![Download Options](https://github.com/user-attachments/assets/510b5cef-e486-483b-be50-861bc4f913c3) | ![Listing Page](https://github.com/user-attachments/assets/6e417151-db44-44db-b5f0-9556a49ef359) |

---

## 📦 How to Build

To build and run the app locally:

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/MovieHub.git
   cd MovieHub

2. **Open Project in Android Studio**

   * Launch Android Studio
   * Select **"Open an existing project"**
   * Navigate to the cloned `MovieHub` directory and open it

3. **Wait for Gradle to Sync**

   * Ensure all dependencies are downloaded

4. **Run the App**

   * Connect your Android device (or use an emulator)
   * Click the green **Run** ▶️ button, or use:

     ```bash
     ./gradlew installDebug
     ```

> ✅ **Minimum SDK Version**: 21
> 📱 **Target SDK Version**: 33+
> ⚙️ **Build Tool Version**: Compatible with Android Gradle Plugin 8.0+

---

## ⚠️ Disclaimer

This application is intended **solely for educational and research purposes**.
We **do not host, store, or distribute** any movies, videos, or streaming content.
All movie and series information, including streaming and download links, is **scraped from publicly accessible third-party websites** such as **Vegamovies** and **Luxmovies**.

If you are a copyright owner and believe your content is being misused, please contact the original source.

---

## ❤️ Contributing

We welcome contributions from the community!

* Fork the repository
* Create a new branch (`git checkout -b feature-name`)
* Make your changes
* Commit and push (`git commit -m "Add new feature"`)
* Open a Pull Request

---

## 👨‍💻 Developer

Developed with ❤️ using Kotlin and Jetpack Compose.
