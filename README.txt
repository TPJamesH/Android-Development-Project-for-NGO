----- Full source code here -------
https://drive.google.com/file/d/1YtN7boqEqJJkzOSioU0IERQMCzGGweLW/view?usp=sharing

------- FUNCTIONALITIES---------------

- A map view that shows nearby all available clean up sites. 
The number of sites to be shown will depend on the zoom level and camera view
(detailed info of the site will be displayed when click into the special marker [trash cleaner])

- In the "volunteering" function, user can use the map to find routes from their current location to the site by clicking onto
one of the markers.

- Filter/search sites based on criteria (in "volunteering" function for users and "see all sites" function for admin).


- Users can join a site by creating an account and sign in 
(can also switch from login screen to sign up or vice versa with a text link)

- Users can create new site and be admin of that site.

- Users can see the site(s) they created, as well as:

	- User can see the list of people who sign up for the site (as the admin of that site).

	- User can modfiy site's details (as the admin of that site)


- User can see list of events/sites they have volunteered to clean up [Site(s) I volunteered]


--------LEFT OUT FUNCTIONALITY----------------

-When an user modify their site's details, the volunteers will not receive any notification
(Attempted to implement through broadcast receiver)


-------TECHNOLOGIES (excluding the provided resources by android studio)---------------

Firebase-Realtime Database => Storing data (e.g user's information and site details)

Google Maps SDK -> Retrieving user's location and site's location (NOTE: NEED TO BE SETUP ON THE RESPECTIVE MACHINE)

Navigation SDK -> Integrate Google Maps navigation with the ability to route user's current location
		  to a site



