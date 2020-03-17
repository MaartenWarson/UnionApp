# UnionApp

## General Summary
This goal of this project is to create an Android application to let registered users manage data from members of a union. This could be a music association, youth association, ... Users are able to view, add, update and delete members. It is also possible to directly make a phone call to a member, send a text message to a member or view its location in Google Maps.

This functionalities are wrapped in a polished application built with attention for the visual aspect. Both portrait and landscape mode are supported.

- This application is written in Java.
- minSdkVersion: 28
- targetSdkVersion: 29

## Running the project locally
**Physical Android device**
1. Set device in Developer Mode
2. Enable USB debuggin
3. Connect your Android device to your programming system
4. Run the project

**Emulator**
1. Install an emulator with API-level 29
2. This emulator needs to suppert Play Store
3. Run the project

## Further Information
This project consumes a Firebase backend:
- Firebase Database is used to store data from members
- Firebase Storage is used to store images
- Fireabase Cloud Messaging is used to send notifications to the device
