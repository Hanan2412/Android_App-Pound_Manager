# Pound-Manager
A simple Android application designed to help to manage a pound, by providing auto updating information about the animals, personal schedual, arriving list and more
using googles Firebase API.
this is a personal project to present my coding skills and know-how


How To:
The app is devieded between 2 kinds of users: 1)admin 2)volenteer
the admin kind can perform any task possible, including deleting animals(the cards on the main screen), add new users and more.
the volenteer can basically only control what effects them directly and cannot add new users or remove animals from the app.

-deleting an animal is done by swiping it left or right and confirming the action
-search bar at the top will search and display the animal wich is being searched
-the arriving button indicates that the connected user will come to work the following day(or won't if unselected)
-the task tab shows what tasks the user should perform and the system will notify the user few minutes before the task is due to begin
-the user list tab shows all users connected(assuming all users are the workers of the pound). an admin user can add tasks for each user while a volenteer user can only view each users task
-no new user can add themself to the app. only an admin can add users so no register button is avaliable in the first screen(altho the button exists in the code, it is set as View.Gone so it's invisable)
-the app updates automatically, so whenever a new animal is being added to the system. all the app instances will download automatically from Firebase the new data
-the app doesn't run in the background, there is no need for that so it wasn't implemented



-----------------------
the application is not 100% ready and will be worked and refined in the future
-----------------------
This application is to present my coding skills and know-how. it is not to be published.
made by Hanan Dorfman
dorfmanhanan@gmail.com
