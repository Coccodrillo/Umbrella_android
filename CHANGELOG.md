# Changelog
 
Dates and release notes for some production releases of the Umbrella Android app.
 
### 1.0.32
2020-05-06
- Fixed bugs, deprecation warnings and unused methods
- Upgrated code and other dependencies

### 1.0.31
2020-05-03
- Fixed bugs to improve app stability
 
### 1.0.29
2019-02-27
- Added next and submit buttons to keyboard in Advanced search
- Added ability to prevent sleep at initial download
- Fixed layout that made submission in Advanced search difficult
- Fixed mask mode issues
- Other stability improvements

### 1.0.28
2019-11-13
- Added French language
- Fixed bug affecting lesson loading speed

### 1.0.27
2019-10-08
- Fixed issues with database access on certain devices
 
### 1.0.24
2019-09-03
- Added way to only show pathways dialog if pathways are found 
- Fixed issue with database reset when the database file has been corrupted

### 1.0.23
2019-08-20
- Added new translations in Chinese and other languages
- Added share button to checklist dashboard and icons
- Fixed crash on startup when doing the password check
- Fixed some crashes reported on Google Play console
- Fixed layout bugs with checklist and lesson dashboards
- Fixed bug affecting export PDF to different languages
- Removed Crashlytics, Fabric and Firebase

### 1.0.22
2019-07-18
- Added Arabic, Russian and Farsi languages
- Added pathways to help new users find content
- Added warning dialog box for switching language and repo
- Added "select all" button to feed sources
- Changed glossary order alphabetically
- Fixed logout issue
- Fixed some Spanish and Chinese translations
- Fixed issue with switch language warning

### 1.0.12
2019-04-30
- Added new topics such as dealing with online abuse, internet shutdowns, dangerous assignments and much much more.
- Added feature to share lessons as well as checklists
- Added advanced search
- Added feature to export and import your data to a new device
- Added way to change the content source to your own custom content
- Added export option to checklist
- Added ability to keep screen on during refresh from server
- Added option to delete all RSS feeds
- Added ability to change repo URL without adding .git at the end
- Changed navigation for easier use
- Fixed custom checklist issue and HTML conversion for checklist
- Fixed flags and responsivity on smaller screens
- Fixed crashed when searching from RSS tab and calculator crash
- Fixed default language and language switch
- Fixed checklist export bug and make export feature to work consistently
- Fixed bug with some strings not being translated
- Fixed bug when language switching back to default after app restart
- Fixed other bugs and stability 
- Updated of all existing content

### 1.0.11
2019-04-23
- Added hints to advanced search
- Add ability for password to be not asked again in feeds after setting it
- Added logout feature
- Added page to tour screen
- Changed feed settings interaction
- Fixed favorite checklist not being deleted from dashboard
- Fixed switch from server, navigation issues and calculator navigation
- Fixed deeplink and deeplink navigation
- Fixed issue with feed images
- Updated translations
- Removed advanced search in Login screen.

### 1.0.10
2019-04-03
- Added autocomplete function to location for RSS feed
- Added option to delete of RSS feeds added by the user
- Fixed tour screen layout
- Fixed mask issues
- Updated app icon

### 1.0.7
2019-03-22
- Added enhancement that enables device now go in sleep mode when app is idle
- Fixed export data feature and empty views
- Fixed form issues with options, deeplinks and advanced search filters
- Fixed article card, feed names and dialog, duplicated and invalid RSS
- Changed error when adding wrong RSS URL to improve clarity

### 1.0.6
2019-03-20
- Added way to go to the checklist in lesson upon clicking on checklist in dashboard
- Added a progress dialog
- Added performance improvements
- Added dialog to download additional content

### 1.0.3
2019-03-16
- Added dialog to change your language
- Added translated strings for Chinese and Spanish
- Added notification functionality
- Added flags to language setting
- Changed background menu style
- Fixed checklist and database export bugs
- Fixed bug that stops progress from updating when adding items to custom checklist

### 0.9.9
2019-03-01
- Added password validation improvements
- Add option to share database
- Changed form styling
- Fixed lesson icon and custom checklist

### 0.9.0
2019-02-19
- Added deeplinks for Forms, Checklists, Lessons
- Added new icons
- Fixed styling and advanced search implementation
- Updated bottom navigation and other layouts

### 0.7.0
2019-02-05
- Added existing whitelabel development 
- Added toolbars for lessons, article, web view, forms and difficulty
- Added ability to save the preferred topic by user
- Added Glossary to Lessons menu and Checklists to Lessons tab
- Added Checklist progress, Checklist dashboard, Checklist sharing and favorite Checklist features
- Added ability to delete Checklists and check items, and to long-press to edit a Checklist item
- Added "switch server" to Settings page
- Added Feed location autocomplete and delete feed option
- Added options to export lessons as HTML or PDF, add HTML to Checklist items and make links clickable
- Added mask mode and ability to launch SimpleCalc with icon
- Added reset password to Settings
- Added way to quit app with "back" button
- Changed Feed list and Feed sources
- Changed password workflow to improve UX
- Fixed issues with selecting difficulty
- Fixed Markdown and Checklist issues
- Fixed bug with deleting Form and saving Form draft
- Fixed bugs with bookmarks and database import/export
- Fixed bug that prevents app from opening after password reset
- Updated UI for Feeds, Checklists, navigation, Checklist and check items
- Other performance and stability improvements

### 0.5.0
2018-07-31
- Fixed bug that affected the lesson deeplink routes with category and difficulty
- Fixed other bugs

### 0.4.9
2018-06-05
- Added Chinese translation phrases
- Fixed search issues and enhanced search ability
- Fixed navigation bug
- Fixed bug that affected abiity to change lesson difficulty
- Fixed bugs on some deeplinks

### 0.4.8
2018-05-22
- Fixed bug that stopped link to lesson by difficult level from working
- Fixed bug that brings you back to the "My Checklists" screen instead of the "Forms" screen after clicking the back arrow when filling out a form
- Refactored code

### 0.4.6
2018-05-17
- Added feature to open Terms and Conditions in app
- Fixed select language and export data bugs
 
### 0.4.5
2018-02-28
- Added internet access error
- Added RSS feed, RSS dialog and way to read RSS article in app
- Added Feed/Articles feature and dialog
- Added default images and new icons
- Added way to share and delete feed list
- Changed background color
- Fixed feed when there's no information to show
- Fixed checklist total done value and other checklist bugs
- Fixed "this address is not valid" error when setting feed location
- Fixed other bugs
- Refactored code
- Updated feed layout

### 0.4.4
2018-01-29
- Added missing words, glossary, background and translations
- Added "Shake to unmask" dialog
- Added feature to share HTML file using Whatsapp
- Change design of masking feature
- Changed UI for editing forms
- Fixed bug that causes log-out to ask to create new password when already set
- Fixed bugs that affected dashboard layout and refresh interval
- Fixed feed list layout and "swipe to refresh" issue
- Fixed location issues, location dialog, empty feed view and location component
- Fixed bug that leads to crash when attempting to share a filled-out form
- Fixed NPE errors, crashes and support libraries
- Fixed lesson difficulty selection issue
- Fixed form, check list items and difficulty selection issues
- Fixed other bugs
- Updated missing Spanish strings

### 0.3.9
2018-11-14
- Added progress bar to checklist
- Added default language selection when no language set
- Fixed lesson difficulty selection issue
- Fixed API bugs
- Fixed check items rendering
- Removed options menu to refresh after language change
- Removed password as a requirement for server refresh

### 0.3.8
2017-11-13
- Fixed localization issue
- Refactored code

### 0.3.7
2017-11-06
- Fixed styling for checkmarks
- Updated layouts

### 0.3.6
2017-06-30
- Added basic forms layouts and ability to view form as HTML
- Added basic PDF generation
- Fixed other layout issues
- Added Multidex config and other compatibility fixes

### 0.3.2
2017-06-06
- Added HTML outputs and ability to attach instead of view HTML
- Fixed issue that that affect content provider in a cache folder
- Fixed layout bugs
- Fixed small bugs that caused crashes

### 0.3.0
2017-04-10
- Added mask mode and ability to shake to mask
- Added ability to prevent unmasking when out of focus
- Added increased sensitivity in masked mode to prevent accidental unmasking
- Fixed bugs for bad token crash and NullPointerException
- Fixed bug that crashes dashboard

### 0.2.4
2017-02-20
- Fixed bug for uncaught SQLite exception before the app has the chance to decrypt

### 0.2.3
2017-02-02
- Added Timber for logging
- Added change password action
- Added login skip password settings
- Added ability to hide navigation after shown first time 
- Added feed sources and language switch
- Added a missing Spanish resource
- Added feature to hide add sources menu temporarily
- Added licences, terms and thank you
- Fixed login glitches
- Fixed notification bugs
- Fixed other bugs
- Fixed bug that affected dashboard updates in the notification tray
- Updated database and refactored code

### 0.2.0
2016-10-06
- Added missing resources
- Added notifications settings
- Added feature to send notification when new feeds are retrieved

### 0.1.9
2016-01-14
- Added feature to allow "panic button" apps to make Umbrella log out
- Fixed bugs

### 0.1.8
2015-12-08
- Changed lessons content
- Fixed bugs reported by Google Play Store Crash and ANR tool 

### 0.1.7
2015-10-10
- Changed inaccurate content link
- Fixed bug on dashboard when there are no results
- Fixed bug when changing country
- Fixed unresponsive button on tour slides

### 0.1.5 (Public beta)
2015-09-29
- Added skip password feature
- Changed lessons and tool guides content
- Fixed bugs
- Removed unused feed sources
- Updated database

### 0.1.4
2015-09-23
- First release
- Checklists, lessons, checklists, dashboard, search, settings, set password
- Categories and content, tool guides
